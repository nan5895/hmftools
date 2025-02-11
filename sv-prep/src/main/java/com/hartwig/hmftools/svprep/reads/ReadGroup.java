package com.hartwig.hmftools.svprep.reads;

import static com.hartwig.hmftools.svprep.SpanningReadCache.formChromosomePartition;
import static com.hartwig.hmftools.svprep.reads.ReadFilterType.SOFT_CLIP_LOW_BASE_QUAL;
import static com.hartwig.hmftools.svprep.reads.ReadType.CANDIDATE_SUPPORT;
import static com.hartwig.hmftools.svprep.reads.ReadType.SUPPORT;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.beust.jcommander.internal.Nullable;
import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Lists;
import com.hartwig.hmftools.common.genome.chromosome.HumanChromosome;
import com.hartwig.hmftools.common.samtools.SupplementaryReadData;
import com.hartwig.hmftools.common.utils.sv.ChrBaseRegion;

public class ReadGroup
{
    private final String mReadId;
    private final List<ReadRecord> mReads;

    private ReadGroupStatus mStatus;
    private final Set<String> mRemotePartitions; // given that supplementaries are no longer included, this is now 0 or 1 entries
    private int mExpectedReadCount;
    private Set<Integer> mJunctionPositions;
    private boolean mHasRemoteJunctionReads;

    public ReadGroup(final ReadRecord read, @Nullable final String readId)
    {
        mReadId = readId;
        mReads = Lists.newArrayListWithCapacity(2);
        mStatus = ReadGroupStatus.UNSET;
        mRemotePartitions = Sets.newHashSet();
        mExpectedReadCount = 0;
        mJunctionPositions = null;
        mHasRemoteJunctionReads = false;
        addRead(read);
    }

    public ReadGroup(final ReadRecord read)
    {
        this(read, null);
    }

    public final String id() { return mReadId != null ? mReadId : mReads.get(0).id(); }
    public List<ReadRecord> reads() { return mReads; }
    public int size() { return mReads.size(); }

    public boolean isComplete() { return mStatus == ReadGroupStatus.COMPLETE; }

    public boolean spansPartitions() { return !mRemotePartitions.isEmpty(); }
    public int partitionCount() { return mRemotePartitions.size() + 1; }
    public int expectedReadCount() { return mExpectedReadCount; }
    public Set<String> remotePartitions() { return mRemotePartitions; }

    public Set<Integer> junctionPositions() { return mJunctionPositions; }

    public void addRead(final ReadRecord read)
    {
        mReads.add(read);
    }

    public boolean hasJunctionPosition(int position) { return mJunctionPositions != null && mJunctionPositions.contains(position); }
    public boolean hasJunctionPositions() { return mJunctionPositions != null && !mJunctionPositions.isEmpty(); }

    public void addJunctionPosition(int position)
    {
        if(mJunctionPositions == null)
            mJunctionPositions = Sets.newHashSet();

        mJunctionPositions.add(position);
    }

    public void clearJunctionPositions()
    {
        if(mJunctionPositions != null)
            mJunctionPositions.clear();
    }

    public ReadGroupStatus groupStatus() { return mStatus; }

    public boolean hasRemoteJunctionReads() { return mHasRemoteJunctionReads; }
    public void markHasRemoteJunctionReads() { mHasRemoteJunctionReads = true; }

    public boolean isSimpleComplete()
    {
        // no supplementaries and both reads received or no mate
        if(mReads.size() == 1)
        {
            ReadRecord read = mReads.get(0);
            return !read.hasMate() && !read.hasSuppAlignment();
        }

        return mReads.size() == 2 && mReads.stream().allMatch(x -> !x.hasSuppAlignment() && !x.isSupplementaryAlignment());
    }

    public boolean allNoSupport() { return mReads.stream().allMatch(x -> x.readType() == ReadType.NO_SUPPORT); }

    public boolean hasReadType(final ReadType type) { return mReads.stream().anyMatch(x -> x.readType() == type); }

    public void setGroupState(final ReadGroupStatus status) { mStatus = status; }

    public void setGroupState()
    {
        if(conditionalOnRemoteReads())
        {
            if(mReads.size() == 2)
                mStatus = ReadGroupStatus.PAIRED;
            else
                mStatus = ReadGroupStatus.SUPPLEMENTARY;

            mExpectedReadCount = mReads.size();
            return;
        }

        boolean firstHasSupp = false;
        boolean secondHasSupp = false;
        boolean nonSuppPaired = false;

        for(ReadRecord read : mReads)
        {
            if(!read.isSupplementaryAlignment() && !nonSuppPaired)
            {
                nonSuppPaired = hasReadMate(read);
            }

            if(read.isFirstOfPair())
            {
                if(read.hasSuppAlignment() || read.isSupplementaryAlignment())
                    firstHasSupp = true;
            }
            else
            {
                if(read.hasSuppAlignment() || read.isSupplementaryAlignment())
                    secondHasSupp = true;
            }
        }

        int suppCount = (firstHasSupp ? 1 : 0) + (secondHasSupp ? 1 : 0);
        int mainReadCount = mReads.get(0).hasMate() ? 2 : 1;
        mExpectedReadCount = mainReadCount + suppCount;

        if(mReads.size() >= mExpectedReadCount)
            mStatus = ReadGroupStatus.COMPLETE;
        else if(nonSuppPaired)
            mStatus = ReadGroupStatus.PAIRED;
        else
            mStatus = ReadGroupStatus.INCOMPLETE;
    }

    public boolean conditionalOnRemoteReads()
    {
        // a candidate or supporting SC low-qual read needs to check that its remote mate read supports a junction
        // and for supplementaries needs to check the the remote mate read(s) aren't duplicates
        // an exception is where a supplementary supporting a junction is paired with a non-supp candidate
        if(mReads.stream().allMatch(x -> x.isSupplementaryAlignment()))
            return true;

        if(mReads.stream().allMatch(x -> x.readType() == CANDIDATE_SUPPORT
        || (x.readType() == SUPPORT && ReadFilterType.isSet(x.filters(), SOFT_CLIP_LOW_BASE_QUAL))))
        {
            return true;
        }

        return false;
    }

    public void setPartitionCount(final ChrBaseRegion region, int partitionSize)
    {
        for(ReadRecord read : mReads)
        {
            if(read.isUnmapped())
                continue;

            if(read.hasSuppAlignment())
            {
                SupplementaryReadData suppData = read.supplementaryAlignment();

                if(!supplementaryInRegion(suppData, region))
                {
                    mRemotePartitions.add(formChromosomePartition(suppData.Chromosome, suppData.Position, partitionSize));
                }
            }

            if(read.hasMate() && HumanChromosome.contains(read.MateChromosome) && !region.containsPosition(read.MateChromosome, read.MatePosStart))
            {
                mRemotePartitions.add(formChromosomePartition(read.MateChromosome, read.MatePosStart, partitionSize));
            }
        }
    }

    public boolean hasReadMate(final ReadRecord read)
    {
        if(!read.hasMate())
            return false;

        for(ReadRecord otherRead : mReads)
        {
            if(otherRead == read)
                continue;

            if(read.isFirstOfPair() == otherRead.isFirstOfPair())
                continue;

            if(read.isSupplementaryAlignment() != otherRead.isSupplementaryAlignment())
                continue;

            if(read.MateChromosome.equals(otherRead.Chromosome) && read.MatePosStart == otherRead.start())
                return true;
        }

        return false;
    }

    public String toString()
    {
        return String.format("reads(%d) initRead(%s:%d-%d) id(%s) partitions(%d) state(%s)",
                mReads.size(), mReads.get(0).Chromosome, mReads.get(0).start(), mReads.get(0).end(), id(), partitionCount(), mStatus);
    }

    private static boolean supplementaryInRegion(final SupplementaryReadData suppData, final ChrBaseRegion region)
    {
        return suppData != null && region.containsPosition(suppData.Chromosome, suppData.Position);
    }

    public static void addUniqueReadGroups(final Set<String> readIds, final List<ReadGroup> uniqueGroups, final List<ReadGroup> readGroups)
    {
        for(ReadGroup readGroup : readGroups)
        {
            if(readIds.contains(readGroup.id()))
                continue;

            readIds.add(readGroup.id());
            uniqueGroups.add(readGroup);
        }
    }

    public static class ReadGroupComparator implements Comparator<ReadGroup>
    {
        public int compare(final ReadGroup first, final ReadGroup second)
        {
            return first.reads().get(0).start() - second.reads().get(0).start();
        }
    }
}

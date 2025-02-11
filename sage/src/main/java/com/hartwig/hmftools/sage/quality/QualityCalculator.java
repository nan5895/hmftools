package com.hartwig.hmftools.sage.quality;

import static java.lang.Math.round;

import com.hartwig.hmftools.common.genome.position.GenomePosition;
import com.hartwig.hmftools.sage.common.IndexedBases;
import com.hartwig.hmftools.sage.evidence.ReadContextCounter;

import htsjdk.samtools.SAMRecord;

public class QualityCalculator
{
    private final QualityConfig mConfig;
    private final QualityRecalibrationMap mQualityRecalibrationMap;
    private final IndexedBases mRefBases;

    private static final int MAX_HIGHLY_POLYMORPHIC_GENES_QUALITY = 10;

    public QualityCalculator(
            final QualityConfig config, final QualityRecalibrationMap qualityRecalibrationMap, final IndexedBases refBases)
    {
        mConfig = config;
        mQualityRecalibrationMap = qualityRecalibrationMap;
        mRefBases = refBases;
    }

    public static int modifiedMapQuality(
            final QualityConfig config, final GenomePosition position, int mapQuality, double readEvents, boolean properPairFlag)
    {
        if(config.isHighlyPolymorphic(position))
        {
            return Math.min(MAX_HIGHLY_POLYMORPHIC_GENES_QUALITY, mapQuality - config.FixedPenalty);
        }

        int improperPairPenalty = config.ImproperPairPenalty * (properPairFlag ? 0 : 1);
        int eventPenalty = (int)round(Math.max(0, readEvents - 1) * config.ReadEventsPenalty);
        return mapQuality - config.FixedPenalty - improperPairPenalty - eventPenalty;
    }

    public static double modifiedBaseQuality(final QualityConfig config, double baseQuality, int distanceFromReadEdge)
    {
        return Math.min(baseQuality - config.BaseQualityFixedPenalty, 3 * distanceFromReadEdge - config.DistanceFromReadEdgeFixedPenalty);
    }

    public static double jitterPenalty(final QualityConfig config, int repeatCount)
    {
        return config.JitterPenalty * Math.max(0, repeatCount - config.JitterMinRepeatCount);
    }

    public double calculateQualityScore(
            final ReadContextCounter readContextCounter, int readBaseIndex, final SAMRecord record, double numberOfEvents)
    {
        int distanceFromReadEdge = readDistanceFromEdge(readContextCounter, readBaseIndex, record);
        double baseQuality = baseQuality(readContextCounter, readBaseIndex, record);

        int mapQuality = record.getMappingQuality();
        boolean properPairFlag = record.getReadPairedFlag() && record.getProperPairFlag();
        int modifiedMapQuality = modifiedMapQuality(mConfig, readContextCounter.variant(), mapQuality, numberOfEvents, properPairFlag);
        double modifiedBaseQuality = modifiedBaseQuality(mConfig, baseQuality, distanceFromReadEdge);

        return Math.max(0, Math.min(modifiedMapQuality, modifiedBaseQuality));
    }

    public double baseQuality(final ReadContextCounter readContextCounter, int readBaseIndex, final SAMRecord record)
    {
        return !readContextCounter.variant().isIndel()
                ? baseQuality(readContextCounter, readBaseIndex, record, readContextCounter.variant().ref().length())
                : readContextCounter.readContext().avgCentreQuality(readBaseIndex, record);
    }

    public static double rawBaseQuality(final ReadContextCounter readContextCounter, int readIndex, final SAMRecord record)
    {
        if(readContextCounter.variant().isIndel())
            return readContextCounter.readContext().avgCentreQuality(readIndex, record);

        int varLength = readContextCounter.variant().ref().length();

        double baseQualTotal = 0;

        for(int i = readIndex; i < readIndex + varLength; ++i)
        {
            baseQualTotal += record.getBaseQualities()[i];
        }

        return baseQualTotal / varLength;
    }

    private double baseQuality(final ReadContextCounter readContextCounter, int startReadIndex, final SAMRecord record, int length)
    {
        int maxIndex = Math.min(startReadIndex + length, record.getBaseQualities().length) - 1;
        int maxLength = maxIndex - startReadIndex + 1;

        double quality = Integer.MAX_VALUE;
        for(int i = 0; i < maxLength; i++)
        {
            int refPosition = readContextCounter.position() + i;
            int readIndex = startReadIndex + i;
            byte rawQuality = record.getBaseQualities()[readIndex];

            double recalibratedQual = recalibrateQuality(readContextCounter, refPosition, i, rawQuality);
            quality = Math.min(quality, recalibratedQual);
        }

        return quality;
    }

    private double recalibrateQuality(final ReadContextCounter readContextCounter, int refPosition, int refAltPos, byte rawQuality)
    {
        if(mQualityRecalibrationMap == null)
            return rawQuality;

        byte[] trinucleotideContext = mRefBases.trinucleotideContext(refPosition);

        return mQualityRecalibrationMap.quality(
                (byte) readContextCounter.ref().charAt(refAltPos),
                (byte) readContextCounter.alt().charAt(refAltPos),
                trinucleotideContext, rawQuality);
    }

    private int readDistanceFromEdge(final ReadContextCounter readContextCounter, int readIndex, final SAMRecord record)
    {
        int index = readContextCounter.readContext().readBasesPositionIndex();
        int leftIndex = readContextCounter.readContext().readBasesLeftCentreIndex();
        int rightIndex = readContextCounter.readContext().readBasesRightCentreIndex();

        int leftOffset = index - leftIndex;
        int rightOffset = rightIndex - index;

        int adjustedLeftIndex = readIndex - leftOffset;
        int adjustedRightIndex = readIndex + rightOffset;

        return Math.max(0, Math.min(adjustedLeftIndex, record.getReadBases().length - 1 - adjustedRightIndex));
    }

}

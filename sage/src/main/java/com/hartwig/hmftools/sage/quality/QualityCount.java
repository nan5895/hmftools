package com.hartwig.hmftools.sage.quality;

import org.jetbrains.annotations.NotNull;

public class QualityCount implements QualityRecord, Comparable<QualityCount> {
    private final QualityRecord key;
    private int count;

    public QualityCount(final QualityRecord key) {
        this.key = key;
    }

    public int count() {
        return count;
    }

    public void increment() {
        count++;
    }

    public void increment(int increment) {
        count += increment;
    }

    public int position() {
        return key.position();
    }

    public byte ref() {
        return key.ref();
    }

    public byte alt() {
        return key.alt();
    }

    public byte qual() {
        return key.qual();
    }

    @Override
    public int readIndex() {
        return key.readIndex();
    }

    @Override
    public byte[] trinucleotideContext() {
        return key.trinucleotideContext();
    }

    public boolean firstOfPair() {
        return key.firstOfPair();
    }

    @NotNull
    public QualityRecord key() {
        return key;
    }

    @Override
    public int compareTo(@NotNull final QualityCount o2) {
        int countCompare = Integer.compare(o2.count(), this.count());
        if (countCompare != 0) {
            return countCompare;
        }

        int refCompare = Byte.compare(this.ref(), o2.ref());
        if (refCompare != 0) {
            return refCompare;
        }

        int altCompare = Byte.compare(this.alt(), o2.alt());
        if (altCompare != 0) {
            return altCompare;
        }

        int indexCompare = Integer.compare(this.readIndex(), o2.readIndex());
        if (indexCompare != 0) {
            return indexCompare;
        }

        if (this.trinucleotideContext().length < 3 || o2.trinucleotideContext().length < 3) {
            return 0;
        }

        int triOne = Byte.compare(this.trinucleotideContext()[0], o2.trinucleotideContext()[0]);
        if (triOne != 0) {
            return triOne;
        }

        int triTwo = Byte.compare(this.trinucleotideContext()[1], o2.trinucleotideContext()[1]);
        if (triTwo != 0) {
            return triTwo;
        }

        int triThree = Byte.compare(this.trinucleotideContext()[2], o2.trinucleotideContext()[2]);
        if (triThree != 0) {
            return triThree;
        }

        return 0;
    }
}

package cz.cuni.kubinja.hospitalsystem.core.statistics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Stores a limited number of entries with the greatest values.
 *
 * @param <T> Type of the data associated with each value.
 */
public final class TopX<T> {
    private final int size;
    private final Comparator<? super T> tieBreaker;
    private final List<TopXEntry<T>> entries;

    /**
     * Creates a ranking with the provided maximum size.
     *
     * @param size Maximum number of stored entries.
     * @param tieBreaker Ordering used when entries have equal values.
     */
    public TopX(int size, Comparator<? super T> tieBreaker) {
        if (size < 0) {
            throw new IllegalArgumentException("Ranking size cannot be negative");
        }

        this.size = size;
        this.tieBreaker = Objects.requireNonNull(tieBreaker);
        this.entries = new ArrayList<>(size);
    }

    /**
     * Adds an entry if it belongs in the ranking.
     *
     * @param data Data associated with the value.
     * @param value Value used for ranking.
     */
    public void add(T data, int value) {
        TopXEntry<T> candidate = new TopXEntry<>(data, value);
        int insertionIndex = 0;

        while (
                insertionIndex < entries.size()
                        && compare(entries.get(insertionIndex), candidate) <= 0
        ) {
            insertionIndex++;
        }

        if (insertionIndex >= size) {
            return;
        }

        entries.add(insertionIndex, candidate);
        if (entries.size() > size) {
            entries.removeLast();
        }
    }

    /**
     * Returns the stored entries from greatest to smallest value.
     */
    public List<TopXEntry<T>> entries() {
        return List.copyOf(entries);
    }

    private int compare(TopXEntry<T> first, TopXEntry<T> second) {
        int valueComparison = Integer.compare(second.value(), first.value());
        if (valueComparison != 0) {
            return valueComparison;
        }

        return tieBreaker.compare(first.data(), second.data());
    }

    /**
     * Data and value stored in a ranking.
     *
     * @param data Ranked data.
     * @param value Ranking value.
     * @param <T> Type of the ranked data.
     */
    public record TopXEntry<T>(T data, int value) {}
}

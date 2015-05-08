package com.health;

import java.util.Objects;

/**
 * Represents a column of a {@link Table}.
 *
 * @author Martijn
 */
public class Column {
    private final String name;
    private final int index;
    private final ValueType type;

    /**
     * Constructs a new column with the given name, index and type.
     *
     * @param name
     *            the name of the column.
     * @param index
     *            the index of the column.
     * @param type
     *            the type of the value of the column.
     * @throws NullPointerException
     *             if name is null;
     * @throws IllegalArgumentException
     *             if index is negative.
     */
    public Column(final String name, final int index, final ValueType type) {
        Objects.requireNonNull(name, "Argument name cannot be null.");
        Objects.requireNonNull(type, "Argument type cannot be null.");

        if (index < 0) {
            throw new IllegalArgumentException(
                    "Argument index cannot be negative.");
        }

        this.name = name;
        this.index = index;
        this.type = type;
    }

    /**
     * Gets the name of this column.
     *
     * @return the name of this column.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the index of this column.
     *
     * @return the index of this column.
     */
    public final int getIndex() {
        return this.index;
    }

    /**
     * Gets the type of the value of this column.
     *
     * @return the type of the value of this column.
     */
    public final ValueType getType() {
        return this.type;
    }
}
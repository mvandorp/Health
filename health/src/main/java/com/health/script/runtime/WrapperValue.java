package com.health.script.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a wrapped Java value in the script.
 *
 * @param <T>
 *            the type of the value being wrapped.
 */
public class WrapperValue<T> extends ComplexValue {
    private static Map<Class<?>, ScriptType> types;
    private final T value;

    static {
        WrapperValue.types = new HashMap<Class<?>, ScriptType>();
    }

    /**
     * Creates a new value with the given underlying Java value.
     *
     * @param value
     *            the value.
     */
    public WrapperValue(final T value) {
        super(WrapperValue.getWrapperType(value.getClass()));

        Objects.requireNonNull(value);

        this.value = value;
    }

    /**
     * Gets the underlying value of this wrapper.
     *
     * @return the underlying value of this wrapper.
     */
    public final T getValue() {
        return this.value;
    }

    /**
     * Gets the {@link ScriptType} corresponding to {@link WrapperValue}.
     *
     * @param type
     *            the class of the value.
     * @return the {@link ScriptType} corresponding to {@link WrapperValue}.
     */
    public static synchronized ScriptType getWrapperType(final Class<?> type) {
        if (!types.containsKey(type)) {
            ScriptTypeBuilder builder = new ScriptTypeBuilder();
            builder.setTypeName(type.getName());
            builder.defineConstructor((args) -> null);
            builder.defineMethod(new ScriptMethod("toString",
                    (args) -> {
                        return new StringValue(((WrapperValue<?>) args[0]).value.toString());
                    }));

            types.put(type, builder.buildType());
        }

        return types.get(type);
    }
}

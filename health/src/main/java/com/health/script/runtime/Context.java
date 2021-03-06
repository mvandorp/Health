package com.health.script.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.health.EventList;
import com.health.EventSequence;
import com.health.Table;

/**
 * Represents the runtime environment of the script.
 *
 * @author Martijn
 */
public final class Context {
    private Map<String, ScriptType> types;
    private Map<String, LValue> variables;

    /**
     * Creates a new, empty, context.
     */
    public Context() {
        this.types = new HashMap<String, ScriptType>();
        this.variables = new HashMap<String, LValue>();

        // Declare all the standard type
        this.declareType(WrapperValue.getWrapperType(Object.class));
        this.declareType(WrapperValue.getWrapperType(Double.class));
        this.declareType(WrapperValue.getWrapperType(String.class));
        this.declareType(WrapperValue.getWrapperType(Boolean.class));
        this.declareType(WrapperValue.getWrapperType(Table.class));
        this.declareType(WrapperValue.getWrapperType(EventList.class));
        this.declareType(WrapperValue.getWrapperType(EventSequence.class));
    }

    /**
     * Returns a map containing the local variables declared in this context.
     *
     * @return a map containing the local variables declared in this context.
     */
    public Map<String, LValue> getVariables() {
        return Collections.unmodifiableMap(this.variables);
    }

    /**
     * Returns a map containing the types declared in this context.
     *
     * @return a map containing the types declared in this context.
     */
    public Map<String, ScriptType> getTypes() {
        return Collections.unmodifiableMap(this.types);
    }

    /**
     * Declares a new local variable of the given type.
     *
     * @param symbol
     *            the name of the local variable.
     * @param type
     *            the type of the local variable.
     * @throws ScriptRuntimeException
     *             if a local variable with the given name is already declared
     *             or if the type and value or not compatible.
     */
    public void declareLocal(final String symbol, final ScriptType type) throws ScriptRuntimeException {
        declareLocal(symbol, type, type.makeInstance(new Value[0]));
    }

    /**
     * Declares a new local variable of the given type.
     *
     * @param symbol
     *            the name of the local variable.
     * @param type
     *            the type of the local variable.
     * @param value
     *            the value of the local variable.
     * @throws ScriptRuntimeException
     *             if a local variable with the given name is already declared
     *             or if the type and value or not compatible.
     */
    public void declareLocal(final String symbol, final ScriptType type, final Value value)
            throws ScriptRuntimeException {
        Objects.requireNonNull(symbol);

        if (this.variables.containsKey(symbol)) {
            throw new ScriptRuntimeException(String.format(
                    "A local variable named '%s' is already defined in this scope.", symbol));
        }

        this.variables.put(symbol, new LValue(type, value));
    }

    /**
     * Removes the local variable with the given name.
     *
     * @param symbol
     *            the name of the local variable to remove.
     */
    public void removeLocal(final String symbol) {
        Objects.requireNonNull(symbol);

        if (!this.variables.containsKey(symbol)) {
            throw new ScriptRuntimeException(String.format(
                    "No local variable named '%s' is defined in this scope.", symbol));
        }

        this.variables.remove(symbol);
    }

    /**
     * Declares a given static method in the context.
     *
     * @param symbol
     *            the name of the method.
     * @param function
     *            the actual method.
     */
    public void declareStaticMethod(final String symbol, final ScriptFunction<Value[], Value> function) {
        ScriptMethod method = new ScriptMethod(symbol, function, true);
        ScriptDelegate delegate = method.createDelegate(null);

        this.declareLocal(symbol, delegate.getType(), delegate);
    }

    /**
     * Declares a given type.
     *
     * @param type
     *            the type to declare.
     * @throws ScriptRuntimeException
     *             if a type with the given name is already declared.
     */
    public void declareType(final ScriptType type) throws ScriptRuntimeException {
        Objects.requireNonNull(type);

        String symbol = type.getName();

        if (this.types.containsKey(symbol)) {
            throw new ScriptRuntimeException(String.format(
                    "The current context already contains a definition for '%s'.", symbol));
        }

        this.types.put(symbol, type);
    }

    /**
     * Declares a given type as a (fake) static type.
     *
     * @param type
     *            the type to declare.
     * @throws ScriptRuntimeException
     *             if a type with the given name is already declared.
     */
    public void declareStaticType(final ScriptType type) throws ScriptRuntimeException {
        Objects.requireNonNull(type);

        String symbol = type.getName();

        if (this.variables.containsKey(symbol)) {
            throw new ScriptRuntimeException(String.format(
                    "The current context already contains a definition for '%s'.", symbol));
        }

        this.variables.put(symbol, new NonModifiableLValue(type, type.makeInstance(new Value[0])));
    }

    /**
     * Returns whether a given symbol is defined.
     *
     * @param symbol
     *            the symbol to check for whether it is defined.
     * @return true if the given symbol is defined; otherwise false.
     */
    public boolean isDefined(final String symbol) {
        return isLocalDefined(symbol) || isTypeDefined(symbol);
    }

    /**
     * Returns whether a given local variable is defined.
     *
     * @param symbol
     *            the symbol to check for whether it is defined.
     * @return true if the given symbol is a defined local variable; otherwise
     *         false.
     */
    public boolean isLocalDefined(final String symbol) {
        Objects.requireNonNull(symbol);

        return this.variables.containsKey(symbol);
    }

    /**
     * Returns whether a given type is defined.
     *
     * @param symbol
     *            the symbol to check for whether it is defined.
     * @return true if the given symbol is a defined type; otherwise false.
     */
    public boolean isTypeDefined(final String symbol) {
        Objects.requireNonNull(symbol);

        return this.types.containsKey(symbol);
    }

    /**
     * Retrieves the l-value for a given symbol.
     *
     * @param symbol
     *            the symbol to retrieve the l-value for.
     * @return the l-value for a given symbol.
     * @throws ScriptRuntimeException
     *             if a local variable with the given name is not declared.
     */
    public LValue lookup(final String symbol) throws ScriptRuntimeException {
        Objects.requireNonNull(symbol);

        if (!this.variables.containsKey(symbol)) {
            throw new ScriptRuntimeException(String.format(
                    "The name '%s' does not exist in the current context.", symbol));
        }

        return this.variables.get(symbol);
    }

    /**
     * Retrieves the type for a given symbol.
     *
     * @param symbol
     *            the symbol to retrieve the type for.
     * @return the type for a given symbol.
     * @throws ScriptRuntimeException
     *             if a type with the given name is not declared.
     */
    public ScriptType lookupType(final String symbol) throws ScriptRuntimeException {
        Objects.requireNonNull(symbol);

        if (!this.types.containsKey(symbol)) {
            throw new ScriptRuntimeException(String.format(
                    "The type '%s' does not exist in the current context.", symbol));
        }

        return this.types.get(symbol);
    }
}

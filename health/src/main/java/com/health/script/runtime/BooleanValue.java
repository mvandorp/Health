package com.health.script.runtime;

public final class BooleanValue extends Value {
    private static ScriptType type;
    private boolean value;

    static {
        ScriptTypeBuilder bool = new ScriptTypeBuilder();
        bool.setTypeName("bool");
        bool.defineConstructor((args) -> new BooleanValue());
        bool.defineMethod(new ScriptMethod("toString",
                (args) -> {
                    return new StringValue(Boolean.toString(((BooleanValue) args[0]).getValue()));
                }));

        BooleanValue.type = bool.buildType();
    }

    public BooleanValue() {
        this(false);
    }

    public BooleanValue(final boolean value) {
        super(BooleanValue.type);

        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public static ScriptType getStaticType() {
        return BooleanValue.type;
    }

    public String toString() {
        return Boolean.toString(value);
    }
}

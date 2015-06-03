package com.health.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.health.script.MyScriptBaseVisitor;
import com.health.script.MyScriptParser;
import com.health.script.runtime.BooleanValue;
import com.health.script.runtime.Context;
import com.health.script.runtime.LValue;
import com.health.script.runtime.NullValue;
import com.health.script.runtime.NumberValue;
import com.health.script.runtime.ScriptDelegate;
import com.health.script.runtime.ScriptRuntimeException;
import com.health.script.runtime.StringValue;
import com.health.script.runtime.Value;

public final class ExpressionValueVisitor extends MyScriptBaseVisitor<Value> {
    private Context context;
    private ExpressionLValueVisitor lValueVisitor;

    public ExpressionValueVisitor(final Context context) {
        Objects.requireNonNull(context);

        this.context = context;
    }

    public void setLValueVisitor(ExpressionLValueVisitor visitor) {
        this.lValueVisitor = visitor;
    }

    @Override
    public Value visitBoolLiteral(final MyScriptParser.BoolLiteralContext ctx) {
        if (ctx.TRUE() != null) {
            return new BooleanValue(true);
        } else {
            return new BooleanValue(false);
        }
    }

    @Override
    public Value visitNumberLiteral(final MyScriptParser.NumberLiteralContext ctx) {
        return new NumberValue(Double.parseDouble(ctx.NUMBER().getText()));
    }

    @Override
    public Value visitStringLiteral(final MyScriptParser.StringLiteralContext ctx) {
        String text = ctx.STRING().getText();

        // The string should be longer than two characters because of the quote
        // characters
        assert text.length() >= 2;

        // Strip the quotes
        text = text.substring(1, text.length() - 2);

        return new StringValue();
    }

    @Override
    public Value visitNullLiteral(final MyScriptParser.NullLiteralContext ctx) {
        return new NullValue();
    }

    @Override
    public Value visitLookupExpression(final MyScriptParser.LookupExpressionContext ctx) {
        return this.lValueVisitor.visit(ctx).get();
    }

    @Override
    public Value visitParenthesizedExpression(final MyScriptParser.ParenthesizedExpressionContext ctx) {
        return super.visit(ctx.expression());
    }

    @Override
    public Value visitMemberAccessExpression(final MyScriptParser.MemberAccessExpressionContext ctx) {
        return this.lValueVisitor.visit(ctx).get();
    }

    @Override
    public Value visitInvocationExpression(final MyScriptParser.InvocationExpressionContext ctx) {
        Value value = super.visit(ctx.primaryExpression());

        if (!(value instanceof ScriptDelegate)) {
            throw new ScriptRuntimeException("Tried to invoke expression that was not a function.");
        }

        ScriptDelegate delegate = (ScriptDelegate) value;

        // Create argument list
        Value[] args = evaluateArgumentList(ctx.argumentList());

        return delegate.invoke(args);
    }

    @Override
    public Value visitAssignmentExpression(final MyScriptParser.AssignmentExpressionContext ctx) {
        LValue left = this.lValueVisitor.visit(ctx.primaryExpression());
        Value right = super.visit(ctx.expression());

        left.set(right);

        return right;
    }

    @Override
    public Value visitChunkExpression(final MyScriptParser.ChunkExpressionContext ctx) {
        return null;
    }

    /**
     * Evaluates the arguments for a given {@link ArgumentList}.
     *
     * @param arguments
     *            the argument list to evaluate.
     * @param context
     *            the runtime environment.
     * @throws ScriptRuntimeException
     *             if any script runtime errors occur.
     */
    private Value[] evaluateArgumentList(final MyScriptParser.ArgumentListContext ctx) throws ScriptRuntimeException {
        // Create a list for the arguments
        List<Value> values = new ArrayList<Value>();

        if (ctx != null) {
            // Evaluate the arguments in the argument list and add them to the
            // list
            this.evaluateArguments(ctx, values);
        }

        // Convert the list to an array
        Value[] args = new Value[values.size()];
        values.toArray(args);

        return args;
    }

    private void evaluateArguments(final MyScriptParser.ArgumentListContext ctx, final List<Value> values)
            throws ScriptRuntimeException {
        // Evaluate the preceding arguments
        if (ctx.argumentList() != null) {
            this.evaluateArguments(ctx.argumentList(), values);
        }

        // Evaluate the argument and add it to the list
        values.add(super.visit(ctx.expression()));
    }
}

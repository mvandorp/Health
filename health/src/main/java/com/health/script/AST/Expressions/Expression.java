package com.health.script.AST.Expressions;

import com.health.Utils;
import com.health.script.TokenReader;

public interface Expression {
    public static Expression parse(final TokenReader reader) {
        return Utils.firstNonNull(
                () -> NonAssignmentExpression.parse(reader),
                () -> Assignment.parse(reader));
    }
}

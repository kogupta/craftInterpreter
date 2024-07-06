package org.kogu.lox.ch5_ast;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kogu.lox.ch5_ast.BinaryOperator.*;
import static org.kogu.lox.ch5_ast.Expr.*;
import static org.kogu.lox.ch5_ast.ExprPrinter.*;
import static org.kogu.lox.ch5_ast.UnaryOperator.Negative;

class ExprPrinterTest {
    @Test
    void asInfixLiteral() {
        Expr e = Expr.binary(literal(1), Eq, literal(2));
        assertEquals("1 == 2", asInfix(e));

        e = Expr.binary(literal(1), NotEq, literal(2));
        assertEquals("1 != 2", asInfix(e));

        e = Expr.binary(literal(1), LessThan, literal(2));
        assertEquals("1 < 2", asInfix(e));

        e = Expr.binary(literal(1), GreaterThan, literal(2));
        assertEquals("1 > 2", asInfix(e));

        e = Expr.binary(literal(1), LessThanEq, literal(2));
        assertEquals("1 <= 2", asInfix(e));

        e = Expr.binary(literal(1), GreaterThanEq, literal(2));
        assertEquals("1 >= 2", asInfix(e));

        e = Expr.binary(literal(1), Add, literal(2));
        assertEquals("1 + 2", asInfix(e));

        e = Expr.binary(literal(1), Subtract, literal(2));
        assertEquals("1 - 2", asInfix(e));

        e = Expr.binary(literal(1), Multiply, literal(2));
        assertEquals("1 * 2", asInfix(e));

        e = Expr.binary(literal(1), Divide, literal(2));
        assertEquals("1 / 2", asInfix(e));

        e = Expr.binary(literal("repeatMe"), Multiply, literal(3));
        assertEquals("\"repeatMe\" * 3", asInfix(e));

        e = Expr.binary(literal(true), Eq, literal(false));
        assertEquals("\"true\" == \"false\"", asInfix(e));
    }

    @Test
    void infixTest() {
        Expr minus123 = unary(Negative, literal(123));
        Expr group = grouping(binary(literal(23), Add, literal(45)));
        Expr e = binary(minus123, Multiply, group);
        assertEquals("-123 * (23 + 45)", asInfix(e));
    }

    @Test
    void printExpr() {
        Expr a = binary(literal(1), Add, literal(2));
        Expr b = binary(literal(4), Subtract, literal(3));

        assertEquals("(1 + 2) * (4 - 3)", asInfix(binary(grouping(a), Multiply, grouping(b))));

        assertEquals("1 2 + 4 3 - *", asRPN(binary(a, Multiply, b)));

        assertEquals("(* (+ 1 2) (- 4 3))", lispy(binary(a, Multiply, b)));
    }

    @Test
    void lispyPrint() {
        Expr e = binary(unary(Negative, literal(123)), Multiply, grouping(literal(45.67)));
        assertEquals("(* (- 123) (group 45.67))", lispy(e));
    }
}
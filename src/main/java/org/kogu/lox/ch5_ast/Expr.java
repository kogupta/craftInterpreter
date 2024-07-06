package org.kogu.lox.ch5_ast;


import static org.kogu.lox.ch5_ast.Literal.Bool.False;
import static org.kogu.lox.ch5_ast.Literal.Bool.True;

public sealed interface Expr permits Expr.Binary, Expr.Grouping, Expr.Unary, Literal {
    record Binary(Expr lhs, BinaryOperator op, Expr rhs) implements Expr {}

    record Unary(UnaryOperator op, Expr expr) implements Expr {}

    record Grouping(Expr expr) implements Expr {}

    static Expr binary(Expr lhs, BinaryOperator op, Expr rhs) {
        return new Binary(lhs, op, rhs);
    }

    static Expr unary(UnaryOperator op, Expr expr) {
        return new Unary(op, expr);
    }

    static Expr grouping(Expr expr) {
        return new Grouping(expr);
    }

    static Expr literal(java.lang.String s) {return new Literal.String(s);}

    static Expr literal(int n) {return new Literal.Int(n);}

    static Expr literal(double d) {return new Literal.Double(d);}

    static Expr literal(boolean b) {return b ? True : False;}

    static Expr nil() {return new Literal.Nil();}

}

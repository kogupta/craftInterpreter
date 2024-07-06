package org.kogu.lox.ch5_ast;


sealed interface Expr {
    record BinaryExpr(Expr lhs, BinaryOperator op, Expr rhs) implements Expr {}
    record UnaryExpr(UnaryOperator op, Expr expr) implements Expr {}
}

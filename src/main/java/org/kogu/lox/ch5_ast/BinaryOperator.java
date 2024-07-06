package org.kogu.lox.ch5_ast;

public enum BinaryOperator {
    Eq("=="),
    NotEq("!="),

    LessThan("<"),
    GreaterThan(">"),
    LessThanEq("<="),
    GreaterThanEq(">="),

    Add("+"),
    Subtract("-"),
    Multiply("*"),
    Divide("/"),
    ;

    public final String symbol;
    BinaryOperator(String symbol) {this.symbol = symbol;}
}

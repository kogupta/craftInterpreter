package org.kogu.lox.ch5_ast;

public enum UnaryOperator {
    Negative("-"),
    Not("!");

    public final String symbol;
    UnaryOperator(String symbol) {this.symbol = symbol;}
}

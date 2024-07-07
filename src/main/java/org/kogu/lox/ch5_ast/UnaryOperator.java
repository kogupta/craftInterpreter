package org.kogu.lox.ch5_ast;

import org.kogu.lox.ch4_scanning.TokenType;

public enum UnaryOperator {
    Negative("-"),
    Not("!");

    public final String symbol;
    UnaryOperator(String symbol) {this.symbol = symbol;}

    public static UnaryOperator from(TokenType tokenType) {
        return switch (tokenType) {
            case MINUS -> Negative;
            case BANG -> Not;
            default -> throw new IllegalArgumentException("Unknown token type: " + tokenType);
        };
    }
}

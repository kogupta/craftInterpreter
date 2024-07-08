package org.kogu.lox.ch5_ast;

import org.kogu.lox.ch4_scanning.TokenType;

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

    Or("or"),
    And("and")
    ;

    public final String symbol;
    BinaryOperator(String symbol) {this.symbol = symbol;}

    public static BinaryOperator from(TokenType type) {
        return switch (type) {
            case TokenType.EQUAL_EQUAL -> Eq;
            case TokenType.BANG_EQUAL -> NotEq;
            case TokenType.LESS -> LessThan;
            case TokenType.GREATER -> GreaterThan;
            case TokenType.LESS_EQUAL -> LessThanEq;
            case TokenType.GREATER_EQUAL -> GreaterThanEq;
            case TokenType.PLUS -> Add;
            case TokenType.MINUS -> Subtract;
            case TokenType.STAR -> Multiply;
            case TokenType.SLASH -> Divide;
            default -> throw new IllegalArgumentException("No binary operator mapped to: " + type);
        };
    }
}

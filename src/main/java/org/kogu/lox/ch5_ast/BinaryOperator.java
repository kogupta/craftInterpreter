package org.kogu.lox.ch5_ast;

import org.kogu.lox.ch4_scanning.Token;
import org.kogu.lox.ch4_scanning.TokenType;

import static org.kogu.lox.ch4_scanning.Token.of;
import static org.kogu.lox.ch4_scanning.TokenType.*;

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
            case EQUAL_EQUAL -> Eq;
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

    public static Token asToken(BinaryOperator op) {
        TokenType t = switch(op) {
            case Eq -> EQUAL_EQUAL;
            case NotEq -> BANG_EQUAL;
            case LessThan -> LESS;
            case GreaterThan -> GREATER;
            case LessThanEq -> LESS_EQUAL;
            case GreaterThanEq -> GREATER_EQUAL;
            case Add -> PLUS;
            case Subtract -> MINUS;
            case Multiply -> STAR;
            case Divide -> SLASH;
            case Or -> OR;
            case And -> AND;
        };

        return of(t, op.symbol, -1); // TODO: unknown line number
    }
}

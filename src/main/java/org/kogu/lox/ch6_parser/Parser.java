package org.kogu.lox.ch6_parser;

import org.kogu.lox.ch4_scanning.Token;
import org.kogu.lox.ch4_scanning.TokenType;
import org.kogu.lox.ch5_ast.BinaryOperator;
import org.kogu.lox.ch5_ast.Expr;
import org.kogu.lox.ch5_ast.UnaryOperator;

import java.util.List;

import static org.kogu.lox.ch4_scanning.TokenType.*;

public final class Parser {
    private final List<Token> tokens;
    private int current;

    public Parser(List<Token> tokens) {this.tokens = tokens;}

    // expression -> equality ;
    private Expr expression() {
        return equality();
    }

    // equality -> comparison ( ( "!=" | "==" ) comparison )*
    private Expr equality() {
        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token op = previous();
            Expr right = comparison();
            expr = Expr.binary(expr, BinaryOperator.from(op.tokenType()), right);
        }

        return expr;
    }

    // comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    private Expr comparison() {
        Expr expr = term();
        while (matchAny(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token op = previous();
            Expr right = term();
            expr = Expr.binary(expr, BinaryOperator.from(op.tokenType()), right);
        }

        return expr;
    }

    // term -> factor ( ( "-" | "+" ) factor )* ;
    private Expr term() {
        Expr expr = factor();
        while (match(MINUS, PLUS)) {
            Token op = previous();
            Expr right = factor();
            expr = Expr.binary(expr, BinaryOperator.from(op.tokenType()), right);
        }

        return expr;
    }

    // factor -> unary ( ( "/" | "*" ) unary )* ;
    private Expr factor() {
        Expr expr = unary();
        while (match(SLASH, STAR)) {
            Token op = previous();
            Expr right = unary();
            expr = Expr.binary(expr, BinaryOperator.from(op.tokenType()), right);
        }

        return expr;
    }

    // unary -> ( "!" | "-" ) unary | primary
    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token op = previous();
            Expr expr = primary();
            return Expr.unary(UnaryOperator.from(op.tokenType()), expr);
        }

        return primary();
    }

    // primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
    private Expr primary() {
        if (match(TRUE)) return Expr.literal(true);
        if (match(FALSE)) return Expr.literal(false);
        if (match(NIL)) return Expr.nil();

        if (match(STRING, NUMBER)) {
            Token token = previous();
            if (token instanceof Token.LiteralToken t) {
                return switch (t.literal()) {
                    case String s -> Expr.literal(s);
                    case Integer i -> Expr.literal(i);
                    case Double v -> Expr.literal(v);
                    case null, default -> throw new IllegalArgumentException("Unrecognized literal: " + t.literal());
                };
            }
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private Token consume(TokenType type, String message) {
        Token t = peek();
        if (t.tokenType() == type) {
            if (isNotEnd()) current++;
            return t;
        }
        throw error(t, message);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }

        return false;
    }

    private boolean match(TokenType a, TokenType b) {
        if (check(a) || check(b)) {
            advance();
            return true;
        }

        return false;
    }

    private boolean matchAny(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        return isNotEnd() && peek().tokenType() == type;
    }

    private void advance() {if (isNotEnd()) current++;}

    private Token peekAndAdvance() {
        Token token = peek();
        advance();
        return token;
    }

    private boolean isNotEnd() {return peek().tokenType() != EOF;}

    private Token peek() {return tokens.get(current);}

    private Token previous() {return tokens.get(current - 1);}

    public static final class ParseError extends RuntimeException {}
}
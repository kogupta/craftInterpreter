package org.kogu.lox.ch6_parser;

import org.kogu.lox.ch4_scanning.Token;

import static org.kogu.lox.ch4_scanning.TokenType.*;

public final class Tokens {
    public static Token leftBrace() {
        return Token.of(LEFT_BRACE, "{", 1);
    }

    public static Token rightBrace() {
        return Token.of(RIGHT_BRACE, "}", 1);
    }

    public static Token comma() {
        return Token.of(COMMA, ",", 1);
    }

    public static Token print() {
        return Token.of(PRINT, "print", 1);
    }

    public static Token var() {
        return Token.of(VAR, "var", 1);
    }

    public static Token fun() {
        return Token.of(FUN, "fun", 1);
    }

    public static Token identifier(String lexeme) {
        return Token.of(IDENTIFIER, lexeme, 1);
    }

    public static Token _if() {
        return Token.of(IF, "if", 1);
    }

    public static Token _else() {
        return Token.of(ELSE, "else", 1);
    }

    public static Token _while() {
        return Token.of(WHILE, "while", 1);
    }

    public static Token _for() {
        return Token.of(FOR, "for", 1);
    }

    public static Token or() {
        return Token.of(OR, "or", 1);
    }

    public static Token and() {
        return Token.of(AND, "and", 1);
    }

    public static Token greater() {
        return Token.of(GREATER, ">", 1);
    }

    public static Token greaterEqual() {
        return Token.of(GREATER_EQUAL, ">=", 1);
    }

    public static Token less() {
        return Token.of(LESS, "<", 1);
    }

    public static Token lessEqual() {
        return Token.of(LESS_EQUAL, "<=", 1);
    }

    public static Token leftParen() {
        return Token.of(LEFT_PAREN, "(", 1);
    }

    public static Token rightParen() {
        return Token.of(RIGHT_PAREN, ")", 1);
    }

    public static Token bang() {
        return Token.of(BANG, "!", 1);
    }

    public static Token bangEqual() {
        return Token.of(BANG_EQUAL, "!=", 1);
    }

    public static Token equal() {
        return Token.of(EQUAL, "=", 1);
    }

    public static Token equalEqual() {
        return Token.of(EQUAL_EQUAL, "==", 1);
    }

    public static Token _false() {
        return Token.of(FALSE, "false", 1);
    }

    public static Token _true() {
        return Token.of(TRUE, "true", 1);
    }

    public static Token nil() {
        return Token.of(NIL, "nil", 1);
    }

    public static Token plus() {
        return Token.of(PLUS, "+", 1);
    }

    public static Token minus() {
        return Token.of(MINUS, "-", 1);
    }

    public static Token star() {
        return Token.of(STAR, "*", 1);
    }

    public static Token slash() {
        return Token.of(SLASH, "/", 1);
    }

    public static Token one() {
        return Token.of(NUMBER, "1", 1, 1);
    }

    public static Token two() {
        return Token.of(NUMBER, "2", 2, 1);
    }

    public static Token integer(String integer) {
        return Token.of(NUMBER, integer, Double.valueOf(integer), 1);
    }

    public static Token pi() {
        return Token.of(NUMBER, "3.14", 3.14, 1);
    }

    public static Token _return() {
        return Token.of(RETURN, "return", 1);
    }

    public static Token _class() {
        return Token.of(CLASS, "class", 1);
    }

    public static Token _this() {
        return Token.of(THIS, "this", 1);
    }

    public static Token _super() {
        return Token.of(SUPER, "super", 1);
    }

    public static Token dot() {
        return Token.of(DOT, ".", 1);
    }

    public static Token semicolon() {
        return Token.of(SEMICOLON, ";", 1);
    }

    public static Token eof() {
        return Token.eof(1);
    }
}
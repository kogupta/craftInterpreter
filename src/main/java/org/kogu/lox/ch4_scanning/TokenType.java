package org.kogu.lox.ch4_scanning;

import java.util.HashMap;
import java.util.Map;

enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords.
    AND, CLASS, ELSE, FALSE, FOR,
    FUN, IF, NIL, OR, PRINT,
    RETURN, SUPER, THIS, TRUE, VAR,
    WHILE,

    EOF;

    private static final Map<String, TokenType> keywords = _keywords();

    static TokenType keywordOrIdentifier(String s) {
        return keywords.getOrDefault(s, IDENTIFIER);
    }

    private static Map<String, TokenType> _keywords() {
        TokenType[] keywords = {
            AND, CLASS, ELSE, FALSE, FOR,
            FUN, IF, NIL, OR, PRINT,
            RETURN, SUPER, THIS, TRUE, VAR,
            WHILE,
        };

        Map<String, TokenType> result = new HashMap<>();
        for (TokenType keyword : keywords)
            result.put(keyword.name().toLowerCase(), keyword);

        return result;
    }
}

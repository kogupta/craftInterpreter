package org.kogu.lox.ch4_scanning;

sealed public interface Token {
    TokenType tokenType();
    String lexeme();
    int line();

    record SimpleToken(TokenType tokenType,
                       String lexeme,
                       int line) implements Token {}

    record LiteralToken(TokenType tokenType,
                        String lexeme,
                        Object literal,
                        int line) implements Token {}

    static Token of(TokenType tokenType, String lexeme, int line) {
        return new SimpleToken(tokenType, lexeme, line);
    }

    static Token of(TokenType tokenType, String lexeme, Object literal, int line) {
        return new LiteralToken(tokenType, lexeme, literal, line);
    }

    static Token eof(int line) {
        return new SimpleToken(TokenType.EOF, "", line);
    }

}

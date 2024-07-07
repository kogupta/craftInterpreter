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

    static Token eof(int line) {
        return new SimpleToken(TokenType.EOF, "", line);
    }

}

package org.kogu.lox.ch4_scanning;

import org.kogu.lox.ch4_scanning.Token.LiteralToken;
import org.kogu.lox.ch4_scanning.Token.SimpleToken;

import java.util.ArrayList;
import java.util.List;

import static org.kogu.lox.ch4_scanning.TokenType.*;

public final class Scanner {
    private final String source;
    private final List<Token> tokens;

    public final List<ScanError> errors;
    private int start = 0, current = 0, line = 1;

    public Scanner(String src) {
        this.source = src;
        this.tokens = new ArrayList<>();
        errors = new ArrayList<>();
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(Token.eof(line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(DOT);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(STAR);

            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);

            case '/' -> {
                if (match('/')) {
                    // a comment starting with //
                    while (peek() != '\n' && !isAtEnd())
                        advance();
                } else
                    addToken(SLASH);
            }

            // whitespace
            case ' ', '\r', '\t' -> {}

            // line ending
            case '\n' -> line++;

            case '"' -> string();

            default -> {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character: " + c);
                    mergeOrAddError(errors, new ScanError(line, start, current, "unexpected char(s)"));
                }
            }
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();

        String s = source.substring(start, current);
        addToken(keywordOrIdentifier(s));
    }

    private void number() {
        while (isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())) {
            do {
                advance();
            } while (isDigit(peek()));
        }
        double v = Double.parseDouble(source.substring(start, current));
        addToken(NUMBER, v);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean match(char expected) {
        if (!isAtEnd() && source.charAt(current) == expected) {
            current++;
            return true;
        } else
            return false;
    }

    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(current);
    }

    private char peekNext() {
        return current + 1 >= source.length() ? '\0' : source.charAt(current + 1);
    }

    private void addToken(TokenType type) {
        String lexeme = source.substring(start, current);
        Token token = new SimpleToken(type, lexeme, line);
        tokens.add(token);
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = source.substring(start, current);
        Token token = new LiteralToken(type, lexeme, literal, line);
        tokens.add(token);
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    private static boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    public record ScanError(int line, int start, int end, String message) {}

    private static void mergeOrAddError(List<ScanError> errors, ScanError error) {
        if (errors.isEmpty()) {
            errors.add(error);
            return;
        }

        ScanError last = errors.getLast();
        // error on same line - consecutive - merge
        if (last.line() == error.line() &&
            last.end() == error.start() &&
            last.message().equals(error.message())) {
            ScanError merged = new ScanError(error.line(), last.start(), error.end(), last.message());
            errors.removeLast();
            errors.add(merged);
        }
    }
}

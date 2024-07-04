package org.kogu.lox.ch4_scanning;

import org.kogu.lox.ch4_scanning.Token.LiteralToken;
import org.kogu.lox.ch4_scanning.Token.SimpleToken;

import java.util.ArrayList;
import java.util.List;

import static org.kogu.lox.ch4_scanning.TokenType.*;

final class Scanner {
    private final String source;
    private final List<Token> tokens;

    private List<ScanError> errors;
    private int start = 0, current = 0, line = 1;

    Scanner(String src) {
        this.source = src;
        this.tokens = new ArrayList<>();
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
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;

            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;

            case '/':
                if (match('/')) {
                    // a comment starting with //
                    while (peek() != '\n' && !isAtEnd())
                        advance();
                } else
                    addToken(SLASH);
                break;

            // whitespace
            case ' ':
            case '\r':
            case '\t': break;

            // line ending
            case '\n': line++; break;

            case '"': string(); break;

            default:
                Lox.error(line, "Unexpected character: " + c);
                if (errors == null) errors = new ArrayList<>();
                mergeOrAddError(errors, new ScanError(line, start, current, "unexpected char(s)"));
                break;
        }


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

    private boolean isAtEnd() {return current >= source.length();}

    private char advance() {return source.charAt(current++);}

    private boolean match(char expected) {
        if (!isAtEnd() && source.charAt(current) == expected) {
            current++;
            return true;
        } else
            return false;
    }

    private char peek() {return isAtEnd() ? '\0' : source.charAt(current);}

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

    record ScanError(int line, int start, int end, String message) {}

    private void mergeOrAddError(List<ScanError> errors, ScanError error) {
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

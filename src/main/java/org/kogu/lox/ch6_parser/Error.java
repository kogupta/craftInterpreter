package org.kogu.lox.ch6_parser;

public interface Error {
    @Override
    String toString();

    record ParseError(String msg) implements Error {}

    static Error parseError(String msg) {
        return new ParseError(msg);
    }
}

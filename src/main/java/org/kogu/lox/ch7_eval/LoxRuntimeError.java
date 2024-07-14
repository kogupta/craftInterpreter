package org.kogu.lox.ch7_eval;

import org.kogu.lox.ch4_scanning.Token;

public final class LoxRuntimeError extends RuntimeException {
    public final Token token;

    public LoxRuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}

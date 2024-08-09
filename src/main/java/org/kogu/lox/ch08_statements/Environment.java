package org.kogu.lox.ch08_statements;

import org.kogu.lox.ch04_scanning.Token;
import org.kogu.lox.ch07_eval.LoxRuntimeError;

import java.util.HashMap;
import java.util.Map;

public final class Environment {
    private final Map<String, Object> values;

    public Environment() {values = new HashMap<>();}

    void define(String name, Object value) {
        values.put(name, value);
    }

    Object get(Token name) {
        Object o = values.get(name.lexeme());
        if (o != null) return o;

        throw new LoxRuntimeError(name, "Undefined variable '" + name.lexeme() + "'.");
    }
}

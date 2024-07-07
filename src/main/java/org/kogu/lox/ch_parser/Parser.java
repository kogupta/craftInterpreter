package org.kogu.lox.ch_parser;

import org.kogu.lox.ch4_scanning.Token;
import org.kogu.lox.ch4_scanning.TokenType;
import org.kogu.lox.ch5_ast.BinaryOperator;
import org.kogu.lox.ch5_ast.Expr;

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
        while (matchAny(BANG_EQUAL, EQUAL_EQUAL)) {
            Token op = previous();
            Expr right = comparison();
            expr = Expr.binary(expr, BinaryOperator.from(op.tokenType()), right);
        }
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

    private Token advance() {
        Token token = peek();
        if (isNotEnd()) current++;

        return token;
    }

    private boolean isNotEnd() {
        return peek().tokenType() != EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}

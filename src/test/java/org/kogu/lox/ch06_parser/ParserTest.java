package org.kogu.lox.ch06_parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.kogu.lox.ch04_scanning.Token;
import org.kogu.lox.ch05_ast.BinaryOperator;
import org.kogu.lox.ch05_ast.Expr;
import org.kogu.lox.ch05_ast.Literal;
import org.kogu.lox.ch05_ast.UnaryOperator;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kogu.lox.ch04_scanning.TokenType.NIL;
import static org.kogu.lox.ch04_scanning.TokenType.STRING;
import static org.kogu.lox.ch05_ast.BinaryOperator.And;
import static org.kogu.lox.ch05_ast.BinaryOperator.Or;
import static org.kogu.lox.ch05_ast.Expr.*;
import static org.kogu.lox.ch05_ast.UnaryOperator.Not;
import static org.kogu.lox.ch06_parser.Tokens.*;

class ParserTest {
    private ErrorReporter.FakeErrorReporter fakeErrorReporter;

    @BeforeEach
    void setUp() {
        fakeErrorReporter = new ErrorReporter.FakeErrorReporter();
    }

    @Nested
    class LiteralExpressions {

        @Test
        void trueToken() {
            Optional<Expr> expr = parseTokens(_true(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(literal(true));
        }

        @Test
        void falseToken() {
            Optional<Expr> expr = parseTokens(_false(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(literal(false));
        }

        @Test
        void nilToken() {
            Optional<Expr> expr = parseTokens(Token.of(NIL, "nil", 1), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(Expr.nil());
        }

        @Test
        void integerNumberToken() {
            Optional<Expr> expr = parseTokens(one(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).matches(e -> e instanceof Literal.Int(int n) && n == 1);
        }

        @Test
        void floatingPointNumberToken() {
            Optional<Expr> expr = parseTokens(pi(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).matches(e -> e instanceof Literal.Double(double d) && d == 3.14);
        }

        @Test
        void stringToken() {
            Optional<Expr> expr = parseTokens(Token.of(STRING, "\"Hello\"", "Hello", 1), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).matches(e -> e instanceof Literal.String(String s) && s.equals("Hello"));
        }
    }

    @Nested
    class GroupingExpressions {

        @Test
        void leftAndRightParenWithLiteralSubExpression() {
            Optional<Expr> expr = parseTokens(leftParen(), one(), rightParen(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).matches(e -> e instanceof Expr.Grouping(Literal.Int(int n)) && n == 1);
        }
    }

    @Nested
    class VariableExpressions {

        @Test
        void identifierToken() {
            Optional<Expr> expr = parseTokens(identifier("a"), semicolon(), eof());
            assertThat(expr).isEmpty();
            // no support for identifier yet
        }
    }

    @Nested
    class UnaryExpressions {

        @Test
        void bangTokenFollowedByLiteralToken() {
            Optional<Expr> expr = parseTokens(bang(), _false(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(unary(Not, literal(false)));
        }

        @Test
        void minusTokenFollowedByLiteralToken() {
            Optional<Expr> expr = parseTokens(minus(), one(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(unary(UnaryOperator.Negative, literal(1)));
        }

        @Test
        void multipleUnaryOperations() {
            Optional<Expr> expr = parseTokens(bang(), bang(), bang(), _false(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(unary(Not, unary(Not, unary(Not, literal(false)))));
        }
    }

    @Nested
    class BinaryExpressions {

        @Test
        void slashTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), slash(), two(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(binary(literal(1), BinaryOperator.Divide, literal(2)));
        }

        @Test
        void starTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), star(), two(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(binary(literal(1), BinaryOperator.Multiply, literal(2)));
        }

        @Test
        void minusTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), minus(), two(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(binary(literal(1), BinaryOperator.Subtract, literal(2)));
        }

        @Test
        void plusTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), plus(), two(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(binary(literal(1), BinaryOperator.Add, literal(2)));
        }

        @Test
        void greaterTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), greater(), two(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(binary(literal(1), BinaryOperator.GreaterThan, literal(2)));
        }

        @Test
        void greaterEqualTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), greaterEqual(), two(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(binary(literal(1), BinaryOperator.GreaterThanEq, literal(2)));
        }

        @Test
        void lessTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), less(), two(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(binary(literal(1), BinaryOperator.LessThan, literal(2)));
        }

        @Test
        void lessEqualTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), lessEqual(), two(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(binary(literal(1), BinaryOperator.LessThanEq, literal(2)));
        }

        @Test
        void bangEqualTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), bangEqual(), two(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(binary(literal(1), BinaryOperator.NotEq, literal(2)));
        }

        @Test
        void equalEqualTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), equalEqual(), two(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(binary(literal(1), BinaryOperator.Eq, literal(2)));
        }

        @Test
        void multipleBinaryOperand() {
            Optional<Expr> expr = parseTokens(one(), star(), two(), plus(), pi(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(
                binary(
                    binary(literal(1), BinaryOperator.Multiply, literal(2)),
                    BinaryOperator.Add,
                    literal(3.14)
                )
            );
        }
    }

    @Nested
    class LogicalExpressions {

        @Test
        void orOperatorWithBothOperands() {
            Optional<Expr> expr = parseTokens(_true(), or(), _false(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(binary(literal(true), Or, literal(false)));
        }

        @Test
        void andOperatorWithBothOperands() {
            Optional<Expr> expr = parseTokens(_true(), and(), _false(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(binary(literal(true), And, literal(false)));
        }
    }

    private Optional<Expr> parseTokens(Token... tokens) {
        return parseTokens(List.of(tokens));
    }

    private Optional<Expr> parseTokens(List<Token> tokens) {
        return Parser.parse(tokens, fakeErrorReporter);
    }
}
package org.kogu.lox.ch6_parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.kogu.lox.ch4_scanning.Token;
import org.kogu.lox.ch5_ast.Expr;
import org.kogu.lox.ch5_ast.Literal;

import java.beans.Expression;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kogu.lox.ch4_scanning.TokenType.STRING;
import static org.kogu.lox.ch6_parser.Tokens.*;

class ParserTest {
    private ErrorReporter.FakeErrorReporter fakeErrorReporter;

    @BeforeEach
    void setUp() {
        fakeErrorReporter = new ErrorReporter.FakeErrorReporter();
    }

    @Nested
    class DegenerateCases {

        @Test
        void noTokens() {

            Optional<Expr> statements = parseTokens((List) null);

            assertThat(statements).isEmpty();
            assertThat(fakeErrorReporter.receivedError()).isFalse();
        }

        @Test
        void emptyListOfTokens() {
            var statements = parseTokens(emptyList());

            assertThat(statements).isEmpty();
            assertThat(fakeErrorReporter.receivedError()).isFalse();
        }
    }

    @Nested
    class LiteralExpressions {

        @Test
        void trueToken() {
            Optional<Expr> expr = parseTokens(_true(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(Expr.literal(true));
        }

        @Test
        void falseToken() {
            Optional<Expr> expr = parseTokens(_false(), semicolon(), eof());
            assertThat(expr).isPresent();
            assertThat(expr.get()).isEqualTo(Expr.literal(false));
        }

        @Test
        void nilToken() {
            Optional<Expr> expr = parseTokens(nil(), semicolon(), eof());
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
            assertThat(expr).isPresent();
            assertThat(expr.get()).matches(e -> e instanceof Expr.Grouping(Literal.Int(int n)) && n == 1);

            var expression = extractOnlyExpressionFrom(parser);

            assertVariableExpression(expression, identifier("a"));
        }
    }

    @Nested
    class UnaryExpressions {

        @Test
        void bangTokenFollowedByLiteralToken() {
            Optional<Expr> expr = parseTokens(bang(), _false(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            var unaryExpression = castTo(expression, Expression.Unary.class);

            assertThat(unaryExpression.operator()).isEqualToComparingFieldByField(bang());
            assertLiteralExpression(unaryExpression.right(), false);
        }

        @Test
        void minusTokenFollowedByLiteralToken() {
            Optional<Expr> expr = parseTokens(minus(), one(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            var unaryExpression = castTo(expression, Expression.Unary.class);

            assertThat(unaryExpression.operator()).isEqualToComparingFieldByField(minus());
            assertLiteralExpression(unaryExpression.right(), 1.0);
        }

        @Test
        void multipleUnaryOperations() {
            Optional<Expr> expr = parseTokens(bang(), bang(), bang(), _false(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            var farLeftUnaryExpression = castTo(expression, Expression.Unary.class);
            assertThat(farLeftUnaryExpression.operator()).isEqualToComparingFieldByField(bang());

            var leftUnaryExpression = castTo(farLeftUnaryExpression.right(), Expression.Unary.class);
            assertThat(leftUnaryExpression.operator()).isEqualToComparingFieldByField(bang());

            var unaryExpression = castTo(leftUnaryExpression.right(), Expression.Unary.class);
            assertThat(unaryExpression.operator()).isEqualToComparingFieldByField(bang());
            assertLiteralExpression(unaryExpression.right(), false);
        }
    }

    @Nested
    class BinaryExpressions {

        @Test
        void slashTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), slash(), two(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            assertBinaryExpression(expression, 1.0, slash(), 2.0);
        }

        @Test
        void starTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), star(), two(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            assertBinaryExpression(expression, 1.0, star(), 2.0);
        }

        @Test
        void minusTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), minus(), two(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            assertBinaryExpression(expression, 1.0, minus(), 2.0);
        }

        @Test
        void plusTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), plus(), two(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            assertBinaryExpression(expression, 1.0, plus(), 2.0);
        }

        @Test
        void greaterTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), greater(), two(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            assertBinaryExpression(expression, 1.0, greater(), 2.0);
        }

        @Test
        void greaterEqualTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), greaterEqual(), two(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            assertBinaryExpression(expression, 1.0, greaterEqual(), 2.0);
        }

        @Test
        void lessTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), less(), two(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            assertBinaryExpression(expression, 1.0, less(), 2.0);
        }

        @Test
        void lessEqualTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), lessEqual(), two(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            assertBinaryExpression(expression, 1.0, lessEqual(), 2.0);
        }

        @Test
        void bangEqualTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), bangEqual(), two(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            assertBinaryExpression(expression, 1.0, bangEqual(), 2.0);
        }

        @Test
        void equalEqualTokenWithLeftAndRightOperands() {
            Optional<Expr> expr = parseTokens(one(), equalEqual(), two(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            assertBinaryExpression(expression, 1.0, equalEqual(), 2.0);
        }

        @Test
        void multipleBinaryOperand() {
            Optional<Expr> expr = parseTokens(one(), star(), two(), plus(), pi(), semicolon(), eof());

            var statements = parser;

            var firstBinaryExpression = castTo(extractOnlyExpressionFrom(statements), Expression.Binary.class);
            assertThat(firstBinaryExpression.operator()).isEqualToComparingFieldByField(plus());
            assertLiteralExpression(firstBinaryExpression.right(), 3.14);

            var secondBinaryExpression = castTo(firstBinaryExpression.left(), Expression.Binary.class);
            assertLiteralExpression(secondBinaryExpression.left(), 1.0);
            assertThat(secondBinaryExpression.operator()).isEqualToComparingFieldByField(star());
            assertLiteralExpression(secondBinaryExpression.right(), 2.0);
        }
    }

    @Nested
    class LogicalExpressions {

        @Test
        void orOperatorWithBothOperands() {
            Optional<Expr> expr = parseTokens(_true(), or(), _false(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            assertLogicalExpression(expression, true, or(), false);
        }

        @Test
        void andOperatorWithBothOperands() {
            Optional<Expr> expr = parseTokens(_true(), and(), _false(), semicolon(), eof());
            assertThat(expr).isPresent();

            var expression = extractOnlyExpressionFrom(parser);

            assertLogicalExpression(expression, true, and(), false);
        }
    }

//    @Nested
//    class ThisExpression {
//
//        @Test
//        void thisToken() {
//            Optional<Expr> expr = createParser(_this(), semicolon(), eof());
//
//            var expression = extractOnlyExpressionFrom(parser);
//
//            assertThat(castTo(expression, Expression.This.class).keyword()).isEqualToComparingFieldByField(_this());
//        }
//    }

//    @Nested
//    class SuperExpression {
//
//        @Test
//        void superToken() {
//            Optional<Expr> expr = createParser(_super(), dot(), identifier("method"), semicolon(), eof());
//
//            var expression = extractOnlyExpressionFrom(parser);
//
//            var aSuper = castTo(expression, Expression.Super.class);
//            assertThat(aSuper.keyword()).isEqualToComparingFieldByField(_super());
//            assertThat(aSuper.method()).isEqualToComparingFieldByField(identifier("method"));
//        }
//    }

//    @Nested
//    class PrintStatement {
//
//        @Test
//        void printLiteralExpressionBoolean() {
//            Optional<Expr> expr = createParser(print(), _false(), semicolon(), eof());
//
//            var statement = extractOnlyStatementFrom(parser);
//
//            assertPrintStatement(statement, false);
//        }
//
//        @Test
//        void printLiteralExpressionNumber() {
//            Optional<Expr> expr = createParser(print(), pi(), semicolon(), eof());
//
//            var statement = extractOnlyStatementFrom(parser);
//
//            assertPrintStatement(statement, 3.14);
//        }
//
//        @Test
//        void printLiteralExpressionString() {
//            Optional<Expr> expr = createParser(print(), new Token(STRING, "\"Hello\"", "Hello", 1), semicolon(), eof());
//
//            var statement = extractOnlyStatementFrom(parser);
//
//            assertPrintStatement(statement, "Hello");
//        }
//
//    }

//    @Nested
//    class VariableDeclaration {
//
//        @Test
//        void variableWithoutInitializer() {
//            Optional<Expr> expr = createParser(var(), identifier("a"), semicolon(), eof());
//
//            var statement = extractOnlyStatementFrom(parser);
//
//            assertUninitializedVariable(statement, identifier("a"));
//        }
//
//        @Test
//        void variableWithInitializer() {
//            Optional<Expr> expr = createParser(var(), identifier("a"), equal(), one(), semicolon(), eof());
//
//            var statements = parser;
//
//            assertVariableStatement(extractOnlyStatementFrom(statements), identifier("a"), 1.0);
//        }
//    }

//    @Nested
//    class AssignmentExpressions {
//
//        @Test
//        void assignUninitializedVariable() {
//            Optional<Expr> expr = createParser(
//                var(), identifier("a"), semicolon(),
//                identifier("a"), equal(), one(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(2);
//            // Ignore the first statement, it has been tested elsewhere.
//            assertAssignedWithValue(statements.get(1), identifier("a"), 1.0);
//        }
//
//        @Test
//        void reassignVariable() {
//            Optional<Expr> expr = createParser(
//                var(), identifier("a"), equal(), one(), semicolon(),
//                identifier("a"), equal(), two(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(2);
//            // Ignore the first statement, it has been tested elsewhere.
//            assertAssignedWithValue(statements.get(1), identifier("a"), 2.0);
//        }
//
//        private void assertAssignedWithValue(Statement statement, Token name, Object expected) {
//            var assignExpression = castTo(extractExpressionFrom(statement), Expression.Assign.class);
//
//            assertThat(assignExpression.name()).isEqualToComparingFieldByField(name);
//            assertLiteralExpression(assignExpression.value(), expected);
//        }
//    }

//    @Nested
//    class BlockStatement {
//
//        @Test
//        void blockWithOneStatement() {
//            Optional<Expr> expr = createParser(
//                leftBrace(),
//                var(), identifier("a"), equal(), one(), semicolon(),
//                rightBrace(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1);
//            var block = castTo(statements.get(0), Statement.Block.class);
//            var innerBlockStatement = extractOnlyStatementFrom(block.statements());
//
//            assertVariableStatement(innerBlockStatement, identifier("a"), 1.0);
//        }
//
//        @Test
//        void blockWithMultipleStatements() {
//            Optional<Expr> expr = createParser(
//                leftBrace(),
//                var(), identifier("a"), equal(), one(), semicolon(),
//                print(), one(), semicolon(),
//                rightBrace(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1);
//            var block = castTo(statements.get(0), Statement.Block.class);
//            assertThat(block.statements()).hasSize(2);
//            assertVariableStatement(block.statements().get(0), identifier("a"), 1.0);
//            assertPrintStatement(block.statements().get(1), 1.0);
//        }
//    }

//    @Nested
//    class IfStatement {
//
//        @Test
//        void ifOnly() {
//            Optional<Expr> expr = createParser(
//                _if(), leftParen(), _true(), rightParen(),
//                print(), one(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//            assertIfStatementPrints(statements.get(0), true, 1.0);
//        }
//
//        @Test
//        void ifElse() {
//            Optional<Expr> expr = createParser(
//                _if(), leftParen(), _true(), rightParen(),
//                print(), one(), semicolon(),
//                _else(),
//                print(), two(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//            assertIfElseStatementPrints(statements.get(0), true, 1.0, 2.0);
//        }
//
//        @Test
//        void danglingElseProblem() {
//            Optional<Expr> expr = createParser(
//                _if(), leftParen(), _true(), rightParen(),
//                _if(), leftParen(), _true(), rightParen(),
//                print(), one(), semicolon(),
//                _else(), // Does this else belong to the first or second else?! --> it should belong to the nearest if.
//                print(), two(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//            var ifStatement = castTo(statements.get(0), Statement.If.class);
//
//            assertLiteralExpression(ifStatement.condition(), true);
//            assertIfElseStatementPrints(ifStatement.thenBranch(), true, 1.0, 2.0);
//            assertThat(ifStatement.elseBranch()).isNull();
//        }
//    }

//    @Nested
//    class WhileStatement {
//
//        @Test
//        void whileLoop() {
//            Optional<Expr> expr = createParser(
//                _while(), leftParen(), _true(), rightParen(),
//                print(), one(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//            assertWhileStatementPrints(statements.get(0), true, 1.0);
//        }
//    }

//    @Nested
//    class ForStatement {
//
//        @Test
//        void fullForLoop() {
//            // for (var i = 0; i < 10; i = i + 1) print 1;
//            Optional<Expr> expr = createParser(
//                _for(), leftParen(),
//                var(), identifier("i"), equal(), integer("0"), semicolon(),
//                identifier("i"), less(), integer("10"), semicolon(),
//                identifier("i"), equal(), identifier("i"), plus(), one(),
//                rightParen(),
//                print(), one(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            //  {
//            //      var i = 0;
//            //      while (i < 10) {
//            //          print 1;
//            //          i = i + 1;
//            //      }
//            //  }
//            assertForLoop(statements);
//        }
//
//        @Test
//        void forLoopWithExpressionStatement() {
//            // var i; for (i = 0; ; ) print 1;
//            Optional<Expr> expr = createParser(
//                var(), identifier("i"), semicolon(),
//                _for(), leftParen(), identifier("i"), equal(), integer("0"), semicolon(), semicolon(), rightParen(),
//                print(), one(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            //  var i;
//            //  {
//            //      i = 0;
//            //      while (true) {
//            //          print 1;
//            //      }
//            //  }
//            assertThat(statements).hasSize(2).doesNotContainNull();
//
//            assertUninitializedVariable(statements.get(0), identifier("i"));
//
//            var block = castTo(statements.get(1), Statement.Block.class);
//
//            assertThat(block.statements()).hasSize(2).doesNotContainNull();
//
//            var expression = extractExpressionFrom(block.statements().get(0));
//            var assignment = castTo(expression, Expression.Assign.class);
//            assertThat(assignment.name()).isEqualToComparingFieldByField(identifier("i"));
//            assertLiteralExpression(assignment.value(), 0.0);
//
//            assertWhileStatementPrints(block.statements().get(1), true, 1.0);
//        }
//
//        @Test
//        void infiniteForLoop() {
//            // for (; ;) print 1;
//            Optional<Expr> expr = createParser(
//                _for(), leftParen(), semicolon(), semicolon(), rightParen(),
//                print(), one(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            //  while (true) {
//            //      print 1;
//            //  }
//            assertThat(statements).hasSize(1).doesNotContainNull();
//            assertWhileStatementPrints(statements.get(0), true, 1.0);
//        }
//
//        private void assertForLoop(List<Statement> statements) {
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var block = castTo(statements.get(0), Statement.Block.class);
//
//            assertThat(block.statements()).hasSize(2).doesNotContainNull();
//
//            assertVariableStatement(block.statements().get(0), identifier("i"), 0.0);
//
//            var whileStatement = castTo(block.statements().get(1), Statement.While.class);
//            assertWhileCondition(whileStatement.condition(), identifier("i"), less(), 10.0);
//            assertWhileBody(whileStatement.body());
//        }
//
//        private void assertWhileCondition(Expression condition, Token left, Token operator, double right) {
//            var binaryCondition = castTo(condition, Expression.Binary.class);
//
//            assertVariableExpression(binaryCondition.left(), left);
//            assertThat(binaryCondition.operator()).isEqualToComparingFieldByField(operator);
//            assertLiteralExpression(binaryCondition.right(), right);
//        }
//
//        private void assertWhileBody(Statement body) {
//            var block = castTo(body, Statement.Block.class);
//
//            assertThat(block.statements()).hasSize(2);
//            assertPrintStatement(block.statements().get(0), 1.0);
//            assertAssignmentStatement(block.statements().get(1), "i");
//        }
//
//        private void assertAssignmentStatement(Statement statement, String variableName) {
//            var assignment = castTo(extractExpressionFrom(statement), Expression.Assign.class);
//            assertThat(assignment.name()).isEqualToComparingFieldByField(identifier(variableName));
//
//            var binaryExpression = castTo(assignment.value(), Expression.Binary.class);
//            assertVariableExpression(binaryExpression.left(), identifier(variableName));
//            assertThat(binaryExpression.operator()).isEqualToComparingFieldByField(plus());
//            assertLiteralExpression(binaryExpression.right(), 1.0);
//        }
//    }

//    @Nested
//    class CallFunction {
//
//        @Test
//        void callNoArgumentsFunction() {
//            Optional<Expr> expr = createParser(
//                identifier("get"), leftParen(), rightParen(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var statementExpression = castTo(statements.get(0), Statement.Expression.class);
//            var call = castTo(statementExpression.expression(), Expression.Call.class);
//
//            assertVariableExpression(call.callee(), identifier("get"));
//            assertThat(call.paren()).isEqualToComparingFieldByField(rightParen());
//            assertThat(call.arguments()).isEmpty();
//        }
//
//        @Test
//        void callSingleArgumentFunction() {
//            Optional<Expr> expr = createParser(
//                identifier("set"), leftParen(), one(), rightParen(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var statementExpression = castTo(statements.get(0), Statement.Expression.class);
//            var call = castTo(statementExpression.expression(), Expression.Call.class);
//
//            assertVariableExpression(call.callee(), identifier("set"));
//            assertThat(call.paren()).isEqualToComparingFieldByField(rightParen());
//            assertThat(call.arguments()).hasSize(1);
//            assertLiteralExpression(call.arguments().get(0), 1.0);
//        }
//
//        @Test
//        void callMultiArgumentFunction() {
//            Optional<Expr> expr = createParser(
//                identifier("sum"), leftParen(), one(), comma(), two(), rightParen(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var statementExpression = castTo(statements.get(0), Statement.Expression.class);
//            var call = castTo(statementExpression.expression(), Expression.Call.class);
//
//            assertVariableExpression(call.callee(), identifier("sum"));
//            assertThat(call.paren()).isEqualToComparingFieldByField(rightParen());
//            assertThat(call.arguments()).hasSize(2);
//            assertLiteralExpression(call.arguments().get(0), 1.0);
//            assertLiteralExpression(call.arguments().get(1), 2.0);
//        }
//    }

//    @Nested
//    class CallProperty {
//
//        @Test
//        void callGetProperty() {
//            Optional<Expr> expr = createParser(
//                identifier("point"), dot(), identifier("x"), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var statementExpression = castTo(statements.get(0), Statement.Expression.class);
//            assertGetExpression(statementExpression.expression(), identifier("point"), identifier("x"));
//        }
//
//        @Test
//        void callGetNestedProperty() {
//            Optional<Expr> expr = createParser(
//                identifier("square"), dot(), identifier("point"), dot(), identifier("x"), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var statementExpression = castTo(statements.get(0), Statement.Expression.class);
//
//            var nestedProperty = castTo(statementExpression.expression(), Expression.Get.class);
//            assertGetExpression(nestedProperty.object(), identifier("square"), identifier("point"));
//            assertThat(nestedProperty.name()).isEqualToComparingFieldByField(identifier("x"));
//        }
//
//        @Test
//        void callSetProperty() {
//            Optional<Expr> expr = createParser(
//                identifier("point"), dot(), identifier("x"), equal(), one(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var statementExpression = castTo(statements.get(0), Statement.Expression.class);
//            assertSetExpressionToLiteral(statementExpression.expression(), identifier("point"), identifier("x"), 1.0);
//        }
//
//        @Test
//        void callSetNestedProperty() {
//            Optional<Expr> expr = createParser(
//                identifier("square"), dot(), identifier("point"), dot(), identifier("x"), equal(), one(), semicolon(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var statementExpression = castTo(statements.get(0), Statement.Expression.class);
//
//            var nestedProperty = castTo(statementExpression.expression(), Expression.Set.class);
//            assertGetExpression(nestedProperty.object(), identifier("square"), identifier("point"));
//            assertThat(nestedProperty.name()).isEqualToComparingFieldByField(identifier("x"));
//            assertLiteralExpression(nestedProperty.value(), 1.0);
//        }
//    }

//    @Nested
//    class FunctionDeclaration {
//
//        @Test
//        void noParametersFunction() {
//            Optional<Expr> expr = createParser(
//                fun(), identifier("print1"), leftParen(), rightParen(), leftBrace(),
//                print(), one(), semicolon(),
//                rightBrace(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            assertFunctionDeclarationWithBodyPrints(statements.get(0), "print1", 1.0);
//        }
//
//        @Test
//        void singleParameterFunction() {
//            Optional<Expr> expr = createParser(
//                fun(), identifier("set"), leftParen(), identifier("a"), rightParen(), leftBrace(),
//                print(), one(), semicolon(),
//                rightBrace(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            assertFunctionDeclarationWithBodyPrints(statements.get(0), "set", List.of("a"), 1.0);
//        }
//
//        @Test
//        void multipleParametersFunction() {
//            Optional<Expr> expr = createParser(
//                fun(), identifier("set"), leftParen(), identifier("a"), comma(), identifier("b"), rightParen(), leftBrace(),
//                print(), one(), semicolon(),
//                rightBrace(),
//                eof()
//            );
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var function = castTo(statements.get(0), Statement.Function.class);
//            assertFunctionDeclarationWithBodyPrints(statements.get(0), "set", List.of("a", "b"), 1.0);
//        }
//    }

//    @Nested
//    class ReturnStatement {
//
//        @Test
//        void emptyReturn() {
//            Optional<Expr> expr = createParser(_return(), semicolon(), eof());
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var returnStatement = castTo(statements.get(0), Statement.Return.class);
//            assertThat(returnStatement.keyword()).isEqualToComparingFieldByField(_return());
//            assertThat(returnStatement.value()).isNull();
//        }
//
//        @Test
//        void returnValue() {
//            Optional<Expr> expr = createParser(_return(), one(), semicolon(), eof());
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var returnStatement = castTo(statements.get(0), Statement.Return.class);
//            assertThat(returnStatement.keyword()).isEqualToComparingFieldByField(_return());
//            assertLiteralExpression(returnStatement.value(), 1.0);
//        }
//    }

//    @Nested
//    class ClassStatement {
//
//        @Test
//        void emptyClass() {
//            Optional<Expr> expr = createParser(_class(), identifier("EmptyClass"), leftBrace(), rightBrace(), eof());
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var classStatement = castTo(statements.get(0), Statement.Class.class);
//            assertThat(classStatement.name()).isEqualToComparingFieldByField(identifier("EmptyClass"));
//            assertThat(classStatement.superclass()).isNull();
//            assertThat(classStatement.methods()).isEmpty();
//        }
//
//        @Test
//        void classWithMultipleMethods() {
//            Optional<Expr> expr = createParser(_class(), identifier("Printer"), leftBrace(),
//                identifier("print1"), leftParen(), rightParen(), leftBrace(), print(), one(), semicolon(), rightBrace(),
//                identifier("print2"), leftParen(), rightParen(), leftBrace(), print(), two(), semicolon(), rightBrace(),
//                rightBrace(), eof());
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var classStatement = castTo(statements.get(0), Statement.Class.class);
//            assertThat(classStatement.name()).isEqualToComparingFieldByField(identifier("Printer"));
//            assertThat(classStatement.superclass()).isNull();
//            assertThat(classStatement.methods()).hasSize(2);
//
//            assertFunctionDeclarationWithBodyPrints(classStatement.methods().get(0), "print1", 1.0);
//            assertFunctionDeclarationWithBodyPrints(classStatement.methods().get(1), "print2", 2.0);
//        }
//
//        @Test
//        void classWithSuperClassMultipleMethods() {
//            Optional<Expr> expr = createParser(_class(), identifier("Circle"), less(), identifier("Shape"), leftBrace(),
//                rightBrace(), eof());
//
//            var statements = parser;
//
//            assertThat(statements).hasSize(1).doesNotContainNull();
//
//            var classStatement = castTo(statements.get(0), Statement.Class.class);
//            assertThat(classStatement.name()).isEqualToComparingFieldByField(identifier("Circle"));
//            assertVariableExpression(classStatement.superclass(), identifier("Shape"));
//            assertThat(classStatement.methods()).isEmpty();
//        }
//    }

    @Nested
    class ErrorCases {

        @Test
        void onlyEOFToken() {
            Optional<Expr> expr = parseTokens(semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void bangEqualWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(bangEqual(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme("!=");
        }

        @Test
        void equalEqualWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(equalEqual(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme("==");
        }

        @Test
        void leftParenWithoutRightParen() {
            Optional<Expr> expr = parseTokens(leftParen(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void leftParenAndLiteralButNoRightParen() {
            Optional<Expr> expr = parseTokens(leftParen(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertError("[line 1] SyntaxError: at ';' expect ')' after expression.");
        }

        @Test
        void leftAndRightParenWithoutSubExpression() {
            Optional<Expr> expr = parseTokens(leftParen(), rightParen(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(")");
        }

        @Test
        void rightParenWithoutLeftParen() {
            Optional<Expr> expr = parseTokens(rightParen(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(")");
        }

        @Test
        void rightParenAndLiteralButNoLeftParen() {
            Optional<Expr> expr = parseTokens(rightParen(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(")");
        }

        @Test
        void bangTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(bang(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void minusTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(minus(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void slashTokenWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(slash(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme("/");
        }

        @Test
        void slashTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(one(), slash(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void starTokenWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(star(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme("*");
        }

        @Test
        void starTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(one(), star(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void plusTokenWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(plus(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme("+");
        }

        @Test
        void plusTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(one(), plus(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void greaterTokenWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(greater(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(">");
        }

        @Test
        void greaterTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(one(), greater(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void greaterEqualTokenWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(greaterEqual(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(">=");
        }

        @Test
        void greaterEqualTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(one(), greaterEqual(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void lessTokenWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(less(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme("<");
        }

        @Test
        void lessTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(one(), less(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void lessEqualTokenWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(lessEqual(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme("<=");
        }

        @Test
        void lessEqualTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(one(), lessEqual(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void bangEqualTokenWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(bangEqual(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme("!=");
        }

        @Test
        void bangEqualTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(one(), bangEqual(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void equalEqualTokenWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(equalEqual(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme("==");
        }

        @Test
        void equalEqualTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(one(), equalEqual(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void orTokenWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(or(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme("or");
        }

        @Test
        void orTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(one(), or(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void andTokenWithoutLeftOperand() {
            Optional<Expr> expr = parseTokens(and(), one(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme("and");
        }

        @Test
        void andTokenWithoutRightOperand() {
            Optional<Expr> expr = parseTokens(one(), and(), semicolon(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme(";");
        }

        @Test
        void bangTokenWithoutRightOperandNextTokenIsEOF() {
            Optional<Expr> expr = parseTokens(bang(), eof());

            var statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertErrorAtLexeme("end");
        }

        @Test
        void assignmentTokenWithInvalidTarget() {
            Optional<Expr> expr = parseTokens(
                var(), identifier("a"), semicolon(),
                var(), identifier("b"), semicolon(),
                identifier("a"), plus(), identifier("b"), equal(), identifier("c"), semicolon(),
                eof()
            );

            var statements = parser;

            assertThat(statements).hasSize(3);
            // Ignore the first 2 statements.

            var assignmentStatement = statements.get(2);
            var statementExpression = castTo(assignmentStatement, Statement.Expression.class);
            var binaryExpression = castTo(statementExpression.expression(), Expression.Binary.class);

            assertVariableExpression(binaryExpression.left(), identifier("a"));
            assertThat(binaryExpression.operator()).isEqualToComparingFieldByField(plus());
            assertVariableExpression(binaryExpression.right(), identifier("b"));

            assertError("[line 1] SyntaxError: at '=' invalid assignment target.");
        }

        @Test
        void leftBraceButRightBraceMissing() {
            Optional<Expr> expr = parseTokens(
                leftBrace(),
                var(), identifier("a"), equal(), one(), semicolon(),
                // Error: Missing right brace!
                eof()
            );

            List<Statement> statements = parser;

            assertThat(statements).hasSize(1).containsOnlyNulls();
            assertError("[line 1] SyntaxError: at 'end' expect '}' after block.");
        }

        @Test
        void rightBraceButLeftBraceMissing() {
            Optional<Expr> expr = parseTokens(
                print(), one(), semicolon(),
                rightBrace(),
                eof()
            );

            List<Statement> statements = parser;

            assertThat(statements).hasSize(2);
            assertPrintStatement(statements.get(0), 1.0);
            assertThat(statements.get(1)).isNull();
            assertErrorAtLexeme("}");
        }

        @Nested
        class If {

            @Test
            void ifWithoutRightParen() {
                Optional<Expr> expr = parseTokens(
                    _if(), leftParen(), _true(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at 'print' expect ')' after if condition.");
            }

            @Test
            void ifWithoutCondition() {
                Optional<Expr> expr = parseTokens(
                    _if(), leftParen(), rightParen(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);
                assertThat(statements.get(0)).isNull();
                assertErrorAtLexeme(")");

                assertPrintStatement(statements.get(1), 1.0);
            }

            @Test
            void ifWithoutLeftParen() {
                Optional<Expr> expr = parseTokens(
                    _if(), _true(), rightParen(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);
                assertThat(statements.get(0)).isNull();
                assertError("[line 1] SyntaxError: at 'true' expect '(' after 'if'.");

                assertPrintStatement(statements.get(1), 1.0);
            }

            @Test
            void ifElseWithoutElseBranch() {
                Optional<Expr> expr = parseTokens(
                    _if(), leftParen(), _true(), rightParen(),
                    print(), one(), semicolon(),
                    _else(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertErrorAtLexeme("end");
            }
        }

        @Nested
        class While {

            @Test
            void whileWithoutRightParen() {
                Optional<Expr> expr = parseTokens(
                    _while(), leftParen(), _true(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at 'print' expect ')' after while condition.");
            }

            @Test
            void whileWithoutCondition() {
                Optional<Expr> expr = parseTokens(
                    _while(), leftParen(), rightParen(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);
                assertThat(statements.get(0)).isNull();
                assertErrorAtLexeme(")");

                assertPrintStatement(statements.get(1), 1.0);
            }

            @Test
            void whileWithoutLeftParen() {
                Optional<Expr> expr = parseTokens(
                    _while(), _true(), rightParen(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);
                assertThat(statements.get(0)).isNull();
                assertError("[line 1] SyntaxError: at 'true' expect '(' after 'while'.");

                assertPrintStatement(statements.get(1), 1.0);
            }
        }

        @Nested
        class For {

            @Test
            void forWithoutRightParen() {
                Optional<Expr> expr = parseTokens(
                    _for(), leftParen(), semicolon(), semicolon(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at 'print' expect expression.");
            }

            @Test
            void forWithoutLeftParen() {
                Optional<Expr> expr = parseTokens(
                    _for(), semicolon(), semicolon(), rightParen(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(4);
                assertThat(statements.get(0)).isNull();
                assertThat(statements.get(1)).isNull();
                assertThat(statements.get(2)).isNull();
                assertThat(statements.get(3)).isInstanceOf(Statement.Print.class); // Recover to the print statement, this test does not care!
                assertError("[line 1] SyntaxError: at ')' expect expression.");
            }

            @Test
            void forWithOneMissingSemiColon() {
                Optional<Expr> expr = parseTokens(
                    _for(), leftParen(), var(), identifier("i"), equal(), one(), semicolon(), rightParen(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);
                assertThat(statements.get(0)).isNull();
                assertThat(statements.get(1)).isInstanceOf(Statement.Print.class); // Recover to the print statement, this test does not care!
                assertError("[line 1] SyntaxError: at ')' expect expression.");
            }

            @Test
            void forWithTwoMissingSemiColons() {
                Optional<Expr> expr = parseTokens(
                    _for(), leftParen(), var(), identifier("i"), equal(), one(), rightParen(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);
                assertThat(statements.get(0)).isNull();
                assertThat(statements.get(1)).isInstanceOf(Statement.Print.class); // Recover to the print statement, this test does not care!
                assertError("[line 1] SyntaxError: at ')' expect ';' after variable declaration.");
            }
        }

        @Nested
        class CallFunction {

            @Test
            void callFunctionWithoutLeftParen() {
                Optional<Expr> expr = parseTokens(
                    identifier("get"), rightParen(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at ')' expect ';' after value.");
            }

            @Test
            void callFunctionWithoutRightParen() {
                Optional<Expr> expr = parseTokens(
                    identifier("get"), leftParen(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at ';' expect expression.");
            }

            @Test
            void callFunctionWithMissingCommaInArgumentList() {
                Optional<Expr> expr = parseTokens(
                    identifier("sum"), leftParen(), one(), two(), rightParen(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at '2' expect ')' after arguments.");
            }

            @Test
            void callFunctionWithTooManyArguments() {
                Optional<Expr> expr = parseTokens(
                    identifier("sum"), leftParen(),
                    one(), comma(),
                    one(), comma(),
                    one(), comma(),
                    one(), comma(),
                    one(), comma(),
                    one(), comma(),
                    one(), comma(),
                    one(), comma(),
                    one(),
                    rightParen(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).doesNotContainNull(); // Still parses the call!
                assertThat(statements.get(0)).isInstanceOf(Statement.Expression.class);
                assertError("[line 1] SyntaxError: at '1' cannot have more than 8 arguments.");
            }
        }

        @Nested
        class CallGetProperty {

            @Test
            void callGetPropertyWithoutObject() {
                Optional<Expr> expr = parseTokens(
                    dot(), identifier("x"), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at '.' expect expression.");
            }

            @Test
            void callGetPropertyWithoutName() {
                Optional<Expr> expr = parseTokens(
                    identifier("point"), dot(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at ';' expect property name after '.'.");
            }

            @Test
            void callSetPropertyWithoutObject() {
                Optional<Expr> expr = parseTokens(
                    dot(), identifier("x"), equal(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at '.' expect expression.");
            }

            @Test
            void callSetPropertyWithoutName() {
                Optional<Expr> expr = parseTokens(
                    identifier("point"), dot(), equal(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at '=' expect property name after '.'.");
            }

            @Test
            void callSetPropertyWithoutEqual() {
                Optional<Expr> expr = parseTokens(
                    identifier("point"), dot(), identifier("x"), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at '1' expect ';' after value.");
            }

            @Test
            void callSetPropertyWithoutValue() {
                Optional<Expr> expr = parseTokens(
                    identifier("point"), dot(), identifier("x"), equal(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at ';' expect expression.");
            }
        }

        @Nested
        class Function {

            @Test
            void functionWithoutIdentifier() {
                Optional<Expr> expr = parseTokens(
                    fun(), leftParen(), rightParen(), leftBrace(),
                    print(), one(), semicolon(),
                    rightBrace(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(3);
                assertThat(statements.get(0)).isNull();
                assertThat(statements.get(1)).isInstanceOf(Statement.Print.class); // Still parses some of the code.
                assertThat(statements.get(2)).isNull();
                assertError("[line 1] SyntaxError: at '}' expect expression.");
            }

            @Test
            void functionWithoutLeftParen() {
                Optional<Expr> expr = parseTokens(
                    fun(), identifier("get"), rightParen(), leftBrace(),
                    print(), one(), semicolon(),
                    rightBrace(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(3);
                assertThat(statements.get(0)).isNull();
                assertThat(statements.get(1)).isInstanceOf(Statement.Print.class); // Still parses some of the code.
                assertThat(statements.get(2)).isNull();
                assertError("[line 1] SyntaxError: at '}' expect expression.");
            }

            @Test
            void functionWithoutRightParen() {
                Optional<Expr> expr = parseTokens(
                    fun(), identifier("get"), leftParen(), leftBrace(),
                    print(), one(), semicolon(),
                    rightBrace(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(3);
                assertThat(statements.get(0)).isNull();
                assertThat(statements.get(1)).isInstanceOf(Statement.Print.class); // Still parses some of the code.
                assertThat(statements.get(2)).isNull();
                assertError("[line 1] SyntaxError: at '}' expect expression.");
            }

            @Test
            void functionWithMissingCommaInParameterList() {
                Optional<Expr> expr = parseTokens(
                    fun(), identifier("get"), leftParen(), identifier("a"), identifier("b"), rightParen(), leftBrace(),
                    print(), one(), semicolon(),
                    rightBrace(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(3);
                assertThat(statements.get(0)).isNull();
                assertThat(statements.get(1)).isInstanceOf(Statement.Print.class); // Still parses some of the code.
                assertThat(statements.get(2)).isNull();
                assertError("[line 1] SyntaxError: at '}' expect expression.");
            }

            @Test
            void functionWithMoreThan8Parameters() {
                Optional<Expr> expr = parseTokens(
                    fun(), identifier("get"), leftParen(),
                    identifier("a1"), comma(),
                    identifier("a2"), comma(),
                    identifier("a3"), comma(),
                    identifier("a4"), comma(),
                    identifier("a5"), comma(),
                    identifier("a6"), comma(),
                    identifier("a7"), comma(),
                    identifier("a8"), comma(),
                    identifier("a9"), rightParen(),
                    leftBrace(),
                    print(), one(), semicolon(),
                    rightBrace(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).doesNotContainNull(); // Still parses the call!
                assertThat(statements.get(0)).isInstanceOf(Statement.Function.class);
                assertError("[line 1] SyntaxError: at 'a9' cannot have more than 8 parameters.");
            }

            @Test
            void functionWithoutLeftBrace() {
                Optional<Expr> expr = parseTokens(
                    fun(), identifier("get"), leftParen(), rightParen(),
                    print(), one(), semicolon(),
                    rightBrace(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at '}' expect expression.");
            }

            @Test
            void functionWithoutRightBrace() {
                Optional<Expr> expr = parseTokens(
                    fun(), identifier("get"), leftParen(), rightParen(), leftBrace(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at 'end' expect '}' after block.");
            }
        }

        @Nested
        class Return {

            @Test
            void emptyReturnWithoutSemicolon() {
                Optional<Expr> expr = parseTokens(_return(), eof());

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at 'end' expect expression.");
            }

            @Test
            void returnValueWithoutSemicolon() {
                Optional<Expr> expr = parseTokens(_return(), one(), eof());

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at 'end' expect ';' after return value.");
            }
        }

        @Nested
        class Class {

            @Test
            void classWithMissingLeftBrace() {
                Optional<Expr> expr = parseTokens(_class(), identifier("Foo"), rightBrace(), eof());

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at '}' expect '{' before class body.");
            }

            @Test
            void classWithMissingRightBrace() {
                Optional<Expr> expr = parseTokens(_class(), identifier("Foo"), leftBrace(), eof());

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at 'end' expect '}' after class body.");
            }

            @Test
            void classWithLessTokenButMissingSuperClassIdentifier() {
                Optional<Expr> expr = parseTokens(_class(), identifier("Foo"), less(), leftBrace(), rightBrace(), eof());

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at '{' expected super class name.");
            }
        }

        @Nested
        class Super {

            @Test
            void superWithMissingDot() {
                Optional<Expr> expr = parseTokens(_super(), identifier("method"), semicolon(), eof());

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at 'method' expect '.' after 'super'.");
            }

            @Test
            void superWithMissingMethod() {
                Optional<Expr> expr = parseTokens(_super(), dot(), semicolon(), eof());

                var statements = parser;

                assertThat(statements).hasSize(1).containsOnlyNulls();
                assertError("[line 1] SyntaxError: at ';' expect superclass method name.");
            }
        }

        @Nested
        class SynchronizeCases {

            @Test
            void afterErrorRecoversToNextSemiColon() {
                Optional<Expr> expr = parseTokens(
                    bangEqual(), two(), semicolon(),    // Error: bangEqual without left operand
                    one(), bangEqual(), two(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);

                assertThat(statements.get(0)).isNull();
                assertErrorAtLexeme("!=");

                assertBinaryExpression(extractExpressionFrom(statements.get(1)), 1.0, bangEqual(), 2.0);
            }

            @Test
            void afterErrorRecoversToNextVariableDeclarationEvenWhenSemiColonIsMissing() {
                Optional<Expr> expr = parseTokens(
                    bangEqual(), two(), // Error: bangEqual without left operand
                    var(), identifier("a"), equal(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);

                assertThat(statements.get(0)).isNull();
                assertErrorAtLexeme("!=");

                assertVariableStatement(statements.get(1), identifier("a"), 1.0);
            }

            @Test
            void afterErrorRecoversToNextPrintDeclarationEvenWhenSemiColonIsMissing() {
                Optional<Expr> expr = parseTokens(
                    bangEqual(), two(), // Error: bangEqual without left operand
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);

                assertThat(statements.get(0)).isNull();
                assertErrorAtLexeme("!=");

                assertPrintStatement(statements.get(1), 1.0);
            }

            @Test
            void afterErrorRecoversToNextIfDeclarationEvenWhenSemiColonIsMissing() {
                Optional<Expr> expr = parseTokens(
                    bangEqual(), two(), // Error: bangEqual without left operand
                    _if(), leftParen(), _true(), rightParen(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);

                assertThat(statements.get(0)).isNull();
                assertErrorAtLexeme("!=");

                assertIfStatementPrints(statements.get(1), true, 1.0);
            }

            @Test
            void afterErrorRecoversToNextWhileDeclarationEvenWhenSemiColonIsMissing() {
                Optional<Expr> expr = parseTokens(
                    bangEqual(), two(), // Error: bangEqual without left operand
                    _while(), leftParen(), _true(), rightParen(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);

                assertThat(statements.get(0)).isNull();
                assertErrorAtLexeme("!=");

                assertWhileStatementPrints(statements.get(1), true, 1.0);
            }

            @Test
            void afterErrorRecoversToNextForDeclarationEvenWhenSemiColonIsMissing() {
                Optional<Expr> expr = parseTokens(
                    bangEqual(), two(), // Error: bangEqual without left operand
                    _for(), leftParen(), semicolon(), semicolon(), rightParen(),
                    print(), one(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);

                assertThat(statements.get(0)).isNull();
                assertErrorAtLexeme("!=");

                // "for (;;)" is the same as "while(true)"
                assertWhileStatementPrints(statements.get(1), true, 1.0);
            }

            @Test
            void afterErrorRecoversToNextFunctionDeclarationEvenWhenSemiColonIsMissing() {
                Optional<Expr> expr = parseTokens(
                    bangEqual(), two(), // Error: bangEqual without left operand
                    fun(), identifier("print1"), leftParen(), rightParen(), leftBrace(),
                    print(), one(), semicolon(),
                    rightBrace(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);

                assertThat(statements.get(0)).isNull();
                assertErrorAtLexeme("!=");

                assertFunctionDeclarationWithBodyPrints(statements.get(1), "print1", 1.0);
            }

            @Test
            void afterErrorRecoversToNextReturnEvenWhenSemiColonIsMissing() {
                Optional<Expr> expr = parseTokens(
                    bangEqual(), two(), // Error: bangEqual without left operand
                    _return(), semicolon(),
                    eof()
                );

                var statements = parser;

                assertThat(statements).hasSize(2);

                assertThat(statements.get(0)).isNull();
                assertErrorAtLexeme("!=");

                var returnStatement = castTo(statements.get(1), Statement.Return.class);
                assertThat(returnStatement.keyword()).isEqualToComparingFieldByField(_return());
                assertThat(returnStatement.value()).isNull();
            }

            @Test
            void afterErrorRecoversToNextClassEvenWhenSemiColonIsMissing() {

                var statements = parseTokens(
                    bangEqual(), two(), // Error: bangEqual without left operand
                    _class(), identifier("EmptyClass"), leftBrace(), rightBrace(),
                    eof()
                );

                assertThat(statements).hasSize(2);

                assertThat(statements.get(0)).isNull();
                assertErrorAtLexeme("!=");

                var classStatement = castTo(statements.get(1), Statement.Class.class);
                assertThat(classStatement.name()).isEqualToComparingFieldByField(identifier("EmptyClass"));
                assertThat(classStatement.superclass()).isNull();
                assertThat(classStatement.methods()).isEmpty();
            }
        }
    }

    private <T> T castTo(Object o, Class<T> clazz) {
        assertThat(o).isInstanceOf(clazz);

        return clazz.cast(o);
    }

    private Optional<Expr> parseTokens(Token... tokens) {
        return parseTokens(List.of(tokens));
    }

    private Optional<Expr> parseTokens(List<Token> tokens) {
        return Parser.parse(tokens, fakeErrorReporter);
    }

    private Expression extractOnlyExpressionFrom(List<Expr> statements) {
        return extractExpressionFrom(extractOnlyStatementFrom(statements));
    }

    private Expression extractExpressionFrom(Statement statement) {
        return castTo(statement, Statement.Expression.class).expression();
    }

    private Expr extractOnlyStatementFrom(List<Expr> statements) {
        assertThat(statements).hasSize(1);

        return statements.get(0);
    }

    private void assertLiteralExpression(Expression expression, Object expected) {
        assertThat(castTo(expression, Expression.Literal.class).value()).isEqualTo(expected);
    }

    private void assertErrorAtLexeme(String lexeme) {
        assertError("[line 1] SyntaxError: at '" + lexeme + "' expect expression.");
    }

    private void assertError(String message) {
        assertThat(fakeErrorReporter.receivedError()).isTrue();
        assertThat(fakeErrorReporter.getError()).hasToString(message);
    }

    private void assertBinaryExpression(Expression expression, Object left, Token operator, Object right) {
        var binaryExpression = castTo(expression, Expression.Binary.class);

        assertLiteralExpression(binaryExpression.left(), left);
        assertThat(binaryExpression.operator()).isEqualToComparingFieldByField(operator);
        assertLiteralExpression(binaryExpression.right(), right);
    }

    private void assertLogicalExpression(Expression expression, Object left, Token operator, Object right) {
        var logicalExpression = castTo(expression, Expression.Logical.class);

        assertLiteralExpression(logicalExpression.left(), left);
        assertThat(logicalExpression.operator()).isEqualToComparingFieldByField(operator);
        assertLiteralExpression(logicalExpression.right(), right);
    }

    private void assertVariableExpression(Expression expression, Token expected) {
        assertThat(castTo(expression, Expression.Variable.class).name()).isEqualToComparingFieldByField(expected);
    }

    private void assertUninitializedVariable(Statement statement, Token name) {
        var variableDeclaration = castTo(statement, Statement.Variable.class);

        assertThat(variableDeclaration.name()).isEqualToComparingFieldByField(name);
        assertThat(variableDeclaration.initializer()).isNull();
    }

    private void assertVariableStatement(Statement statement, Token name, Object expected) {
        var variableDeclaration = castTo(statement, Statement.Variable.class);

        assertThat(variableDeclaration.name()).isEqualToComparingFieldByField(name);
        assertLiteralExpression(variableDeclaration.initializer(), expected);
    }

    private void assertIfElseStatementPrints(Statement statement, boolean condition, Object thenPrints, Object elsePrints) {
        var ifStatement = castTo(statement, Statement.If.class);

        assertLiteralExpression(ifStatement.condition(), condition);
        assertPrintStatement(ifStatement.thenBranch(), thenPrints);
        assertPrintStatement(ifStatement.elseBranch(), elsePrints);
    }

    private void assertGetExpression(Expression expression, Token object, Token name) {
        var getExpression = castTo(expression, Expression.Get.class);

        assertVariableExpression(getExpression.object(), object);
        assertThat(getExpression.name()).isEqualToComparingFieldByField(name);
    }

    private void assertSetExpressionToLiteral(Expression expression, Token object, Token name, Object expectedValue) {
        var setExpression = castTo(expression, Expression.Set.class);

        assertVariableExpression(setExpression.object(), object);
        assertThat(setExpression.name()).isEqualToComparingFieldByField(name);
        assertLiteralExpression(setExpression.value(), expectedValue);
    }

}
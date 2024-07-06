package org.kogu.lox.ch5_ast;

public final class ExprPrinter {
    public static String asInfix(Expr expr) {
        return _infixHelper(expr, new StringBuilder()).toString();
    }

    public static String asRPN(Expr expr) {
        return _rpnHelper(expr, new StringBuilder()).toString();
    }

    public static String lispy(Expr expr) {
        return _lispyHelper(expr, new StringBuilder()).toString();
    }

    private static StringBuilder _infixHelper(Expr expr, StringBuilder acc) {
        return switch (expr) {
            case Expr.Binary(var a, var op, var b) -> {
                _infixHelper(a, acc);
                acc.append(' ').append(op.symbol).append(' ');
                yield _infixHelper(b, acc);
            }
            case Expr.Grouping(var exp) -> {
                acc.append('(');
                _infixHelper(exp, acc);
                yield acc.append(')');
            }
            case Expr.Unary u -> {
                acc.append(u.op().symbol);
                yield _infixHelper(u.expr(), acc);
            }
            case Literal literal -> printLiteral(acc, literal);
        };
    }

    private static StringBuilder _rpnHelper(Expr expr, StringBuilder acc) {
        return switch (expr) {
            case Expr.Binary(var a, var op, var b) -> {
                _rpnHelper(a, acc);
                acc.append(' ');
                _rpnHelper(b, acc);
                yield acc.append(' ').append(op.symbol);
            }
            case Expr.Grouping(var exp) -> {
                acc.append('(');
                _rpnHelper(exp, acc);
                yield acc.append(')');
            }
            case Expr.Unary u -> {
                acc.append(u.op().symbol);
                yield _rpnHelper(u.expr(), acc);
            }
            case Literal literal -> printLiteral(acc, literal);
        };
    }

    private static StringBuilder _lispyHelper(Expr expr, StringBuilder acc) {
        return switch (expr) {
            case Expr.Binary(var a, var op, var b) -> {
                acc.append('(').append(op.symbol).append(' ');
                _lispyHelper(a, acc);

                if (a instanceof Literal)
                    acc.append(' ');

                _lispyHelper(b, acc);

                yield acc.append(')');
            }
            case Expr.Grouping(var exp) -> {
                acc.append('(');
                _lispyHelper(exp, acc);
                yield acc.append(')');
            }
            case Expr.Unary u -> {
                acc.append(u.op().symbol);
                yield _lispyHelper(u.expr(), acc);
            }
            case Literal literal -> printLiteral(acc, literal);
        };
    }

    private static StringBuilder printLiteral(StringBuilder acc, Literal literal) {
        return switch (literal) {
            case Literal.Bool b -> withinQuote(b.b() ? "true" : "false", acc);
            case Literal.Nil _ -> withinQuote("nil", acc);
            case Literal.Int n -> acc.append(n.n());
            case Literal.Double n -> acc.append(n.d());
            case Literal.String s -> withinQuote(s.s(), acc);
        };
    }

    private static StringBuilder withinQuote(String s, StringBuilder acc) {
        return acc.append('"').append(s).append('"');
    }
}

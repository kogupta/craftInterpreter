package org.kogu.lox.ch07_eval;

import org.kogu.lox.ch05_ast.Expr;
import org.kogu.lox.ch05_ast.Literal;

import java.util.Objects;

import static org.kogu.lox.ch05_ast.BinaryOperator.Divide;
import static org.kogu.lox.ch05_ast.BinaryOperator.asToken;

public final class Interpreter {

    public static Object eval(Expr expr) {
        return switch (expr) {
            case Expr.Binary b -> evalBinary(b);
            case Expr.Grouping(var e) -> eval(e);
            case Expr.Unary u -> evalUnary(u);
            case Literal literal -> literal.value();
        };
    }

    private static Object evalUnary(Expr.Unary expr) {
        Object o = eval(expr.expr());
        return switch (expr.op()) {
            case Negative -> o instanceof Integer n ? -n : -((double) o);
            case Not -> !toBoolean(o);
        };
    }

    private static Object evalBinary(Expr.Binary expr) {
        Object a = eval(expr.lhs());
        return switch (expr.op()) {
            case Eq -> Objects.equals(a, eval(expr.rhs()));
            case NotEq -> !Objects.equals(a, eval(expr.rhs()));
            case LessThan -> {
                Object b = eval(expr.rhs());
                double da = (Double) a;
                double db = (Double) b;
                yield da < db;
            }
            case GreaterThan -> {
                Object b = eval(expr.rhs());
                double da = (Double) a;
                double db = (Double) b;
                yield da > db;
            }
            case LessThanEq -> {
                Object b = eval(expr.rhs());
                double da = (Double) a;
                double db = (Double) b;
                yield da <= db;
            }
            case GreaterThanEq -> {
                Object b = eval(expr.rhs());
                double da = (Double) a;
                double db = (Double) b;
                yield da >= db;
            }
            case Add -> {
                Object b = eval(expr.rhs());
                if (a instanceof Integer ia && b instanceof Integer ib) yield ia + ib;
                if (a instanceof String || b instanceof String)   yield a.toString() + b.toString();

                double da = (Double) a;
                double db = (Double) b;
                yield da + db;
            }
            case Subtract -> {
                Object b = eval(expr.rhs());
                if (a instanceof Integer ia && b instanceof Integer ib) yield ia - ib;
                double da = (Double) a;
                double db = (Double) b;
                yield da - db;
            }
            case Multiply -> {
                Object b = eval(expr.rhs());
                if (a instanceof Integer ia && b instanceof Integer ib) yield ia * ib;
                double da = (Double) a;
                double db = (Double) b;
                yield da * db;
            }
            case Divide -> {
                Object b = eval(expr.rhs());
                if (b instanceof Number ib && ib.doubleValue() == 0) {
                    throw new LoxRuntimeError(asToken(Divide), "Cannot divide by zero");
                }
                if (a instanceof Integer ia && b instanceof Integer ib) yield ia / ib;
                double da = (Double) a;
                double db = (Double) b;
                yield da / db;
            }
            case Or -> Objects.equals(a, Boolean.TRUE) ? Boolean.TRUE : eval(expr.rhs());
            case And -> Objects.equals(a, Boolean.FALSE) ? Boolean.FALSE : eval(expr.rhs());
        };
    }

    // null or false -> falsey
    // everything else -> truthy
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean toBoolean(Object o) {
        if (o == null) return false;
        if (o instanceof Boolean) return (Boolean) o;
        return true;
    }
}

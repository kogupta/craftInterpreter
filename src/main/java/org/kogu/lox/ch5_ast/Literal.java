package org.kogu.lox.ch5_ast;


public sealed interface Literal extends Expr {
    Object value();

    record Int(int n) implements Literal {
        @Override
        public Object value() {return n;}
    }

    record Double(double d) implements Literal {
        @Override
        public Object value() {return d;}
    }

    record String(java.lang.String value) implements Literal {}

    record Bool(boolean b) implements Literal {
        public static final Literal True = new Bool(true);
        public static final Literal False = new Bool(false);

        @Override
        public Object value() {
            return b ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    record Nil() implements Literal {
        public static final Literal Instance = new Nil();

        @Override
        public Object value() {
            return null;
        }
    }
}

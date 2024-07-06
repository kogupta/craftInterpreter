package org.kogu.lox.ch5_ast;


public sealed interface Literal extends Expr {
    record Int(int n) implements Literal {}

    record Double(double d) implements Literal {}

    record String(java.lang.String s) implements Literal {}

    record Bool(boolean b) implements Literal {
        public static final Literal True = new Bool(true);
        public static final Literal False = new Bool(false);
    }

    record Nil() implements Literal {
        public static final Literal Nil = new Nil();
    }
}

package org.kogu.lox.ch7_eval;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        System.out.println((int)'a');
        System.out.println((short)'a');
        test(10.0f, 'a', (short) 1);
    }

    public static void test(float f, char c, short s) {
        // 1. division
        // 2. cast
        // 3. addition
        System.out.println((int) f + c / s);

        System.out.println(f + c / s);

        System.out.println((short) f + c / s);
    }
}

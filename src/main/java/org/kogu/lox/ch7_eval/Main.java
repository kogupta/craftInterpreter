package org.kogu.lox.ch7_eval;

public final class Main {
    private Main() {}

    public static void main() {
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

    public static void test(int i, float f, double d, char c, short s, byte b) {
        System.out.println(i + c/b);
        System.out.println(f/d + c*f);
        System.out.println(i + f - b * c);
        System.out.println((f / i) * c + s);
        System.out.println(i + f - c + b / d);
        System.out.println(i / c + f / b);
    }
}

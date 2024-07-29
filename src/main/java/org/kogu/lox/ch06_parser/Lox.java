package org.kogu.lox.ch06_parser;

import org.kogu.lox.ch04_scanning.Token;
import org.kogu.lox.ch04_scanning.TokenType;
import org.kogu.lox.ch05_ast.Expr;
import org.kogu.lox.ch05_ast.ExprPrinter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SuppressWarnings("DuplicatedCode")
public final class Lox {
    private static boolean hadError = false;

    public static void main(String... args) throws IOException {
        int len = args.length;
        switch (len) {
            case 0 -> runPrompt();
            case 1 -> runFile(args[0]);
            default -> {
                System.out.println("Usage: jlox [script]");
                System.exit(64);
            }
        }
    }

    private static void runFile(String filePath) throws IOException {
        String src = Files.readString(Paths.get(filePath), StandardCharsets.US_ASCII);
        run(src);
        if (hadError)
            System.exit(65);
    }

    private static void runPrompt() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine();
                if (line == null) break;

                run(line);
                hadError = false;
            }
        }
    }

    private static void run(String src) {
        org.kogu.lox.ch04_scanning.Scanner scanner = new org.kogu.lox.ch04_scanning.Scanner(src);
        List<Token> tokens = scanner.scanTokens();

        Optional<Expr> maybeExpr = Parser.parse(tokens);

        if (hadError)
            System.err.println(scanner.errors);
        else maybeExpr.ifPresent(e -> System.out.println(ExprPrinter.lispy(e)));
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

     public static void error(Token token, String message) {
        if (token.tokenType() == TokenType.EOF) {
            report(token.line(), " at end", message);
        } else {
            report(token.line(), " at '" + token.lexeme() + "'", message);
        }
    }

    private static void report(int line, String where, String message) {
        System.err.printf("[line %d] Error%s: %s%n", line, where, message);
        hadError = true;
    }

}

package org.kogu.lox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class Lox {
    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }

        if (args.length == 0) runFile(args[0]);
        else runPrompt();
    }

    private static void runFile(String filePath) throws IOException {
        String src = Files.readString(Paths.get(filePath), StandardCharsets.US_ASCII);
        run(src);
    }

    private static void runPrompt() throws IOException {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String line = scanner.nextLine();
                if (line == null) break;

                run(line);
            }
        }
    }

    private static void run(String src) throws IOException {

    }

    record Token(TokenType type, String lexeme, Object literal, int line) {
        public static Token eof(int line) {
            return new Token(TokenType.EOF, "", null, line);
        }
    }

    private static final class SrcScanner {
        private final String src;
        private final List<Token> tokens;
        private int start = 0, current = 0, line = 1;

        private SrcScanner(String src) {
            this.src = src;
            this.tokens = new ArrayList<>();
        }

        public List<Token> scanTokens() {
            while (!isAtEnd()) {
                start = current;
                scanToken();
            }
        }

        private boolean isAtEnd() {return current >= src.length();}

    }

}

package org.kogu.lox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public final class Main {
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
}

package org.kogu.lox.ch04_scanning;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;

class LoxTest {
    private String[] snippets = {
        """
        var a = 20;
        """,
        """
        print "Hello, world!";
        """,
        """
        fun addPair(a, b) {
          return a + b;
        }
        fun identity(a) {
          return a;
        }
        print identity(addPair)(1, 2);
        """,
        """
        class Breakfast {
          cook() {
            print "Eggs a-fryin'!";
          }
        
          serve(who) {
            print "Enjoy your breakfast, " + who + ".";
          }
        }
        """,
        """
        class Breakfast {
          init(meat, bread) {
            this.meat = meat;
            this.bread = bread;
          }
        }
        """,
        """
        class Brunch < Breakfast {
          init(meat, bread, drink) {
            super.init(meat, bread);
            this.drink = drink;
          }
        }
        """,
    };
    private String[] snippetsWithError = {
        """
        var a = 0x20;
        """,
        """
        var !!!! a = 0x20;
        """,
        """
        val x = "Hello, world!";
        """,
        """
        var x = "Hello, world!
        """
    };

    @Test
    void runFile() throws IOException {
        for (String snippet : snippets) {
            Path p = Paths.get("/tmp/test.lox");
            Files.writeString(p, snippet, StandardCharsets.US_ASCII, CREATE, WRITE, TRUNCATE_EXISTING);
            Lox.main(p.toFile().getAbsolutePath());
        }
    }

    @Test
    void runFileWithError() throws IOException {
        for (String s : snippetsWithError) {
            Path p = Paths.get("/tmp/testError.lox");
            Files.writeString(p, s, StandardCharsets.US_ASCII, CREATE, WRITE, TRUNCATE_EXISTING);
            Lox.main(p.toFile().getAbsolutePath());
        }

    }
}
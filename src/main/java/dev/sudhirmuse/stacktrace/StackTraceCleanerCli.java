/* Copyright 2026 Sudhir Mishra. SPDX-License-Identifier: Apache-2.0 */
package dev.sudhirmuse.stacktrace;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class StackTraceCleanerCli {
    private StackTraceCleanerCli() {}

    public static void main(String[] args) {
        int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    static int run(String[] args) {
        boolean markdown = false;
        boolean collapse = true;
        List<String> positional = new ArrayList<>();
        for (String arg : args) {
            switch (arg) {
                case "--markdown" -> markdown = true;
                case "--keep-framework" -> collapse = false;
                case "--help", "-h" -> {
                    printHelp();
                    return 0;
                }
                default -> positional.add(arg);
            }
        }
        if (positional.size() > 1) {
            System.err.println("Expected at most one input file.");
            return 2;
        }
        try {
            String input = positional.isEmpty()
                ? new String(System.in.readAllBytes(), StandardCharsets.UTF_8)
                : Files.readString(Path.of(positional.getFirst()), StandardCharsets.UTF_8);
            System.out.println(new StackTraceCleaner().clean(input, new CleanerOptions(collapse, markdown)));
            return 0;
        } catch (IOException | RuntimeException exception) {
            System.err.println("Unable to clean stack trace: " + exception.getMessage());
            return 1;
        }
    }

    private static void printHelp() {
        System.out.println("Usage: stacktrace-cleaner [--markdown] [--keep-framework] [file]");
    }
}


/* Copyright 2026 Sudhir Mishra. SPDX-License-Identifier: Apache-2.0 */
package dev.sudhirmuse.stacktrace;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class StackTraceCleaner {
    private static final Pattern WINDOWS_HOME = Pattern.compile("(?i)(?:[A-Z]:\\\\Users\\\\)[^\\\\\s]+(?=\\\\)");
    private static final Pattern UNIX_HOME = Pattern.compile("/(?:home|Users)/[^/\\s]+(?=/)");
    private static final Pattern BEARER = Pattern.compile("(?i)(Bearer\\s+)[A-Za-z0-9._~+/=-]+");
    private static final Pattern SECRET = Pattern.compile("(?i)(password|passwd|api[_-]?key|secret|token)(\\s*[=:]\\s*)([^\\s,;]+)");
    private static final Pattern FRAME = Pattern.compile("^\\s*at\\s+([\\w.$]+)\\(.*$");
    private static final List<String> FRAMEWORK_PREFIXES = List.of(
        "java.", "jdk.", "sun.", "org.springframework.", "org.apache.",
        "org.junit.", "org.gradle.", "reactor.", "io.netty."
    );

    public String clean(String input, CleanerOptions options) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String normalized = input.replace("\r\n", "\n").replace('\r', '\n');
        List<String> output = options.collapseFrameworkFrames()
            ? collapseFrameworkFrames(List.of(normalized.split("\n", -1)))
            : new ArrayList<>(List.of(normalized.split("\n", -1)));
        String cleaned = String.join("\n", output).stripTrailing();
        cleaned = redact(cleaned);
        return options.markdown() ? "```text\n" + cleaned + "\n```" : cleaned;
    }

    private String redact(String text) {
        String result = WINDOWS_HOME.matcher(text).replaceAll("C:\\\\Users\\\\<user>");
        result = UNIX_HOME.matcher(result).replaceAll("/home/<user>");
        result = BEARER.matcher(result).replaceAll("$1<redacted>");
        return SECRET.matcher(result).replaceAll("$1$2<redacted>");
    }

    private List<String> collapseFrameworkFrames(List<String> lines) {
        List<String> output = new ArrayList<>();
        int hidden = 0;
        for (String line : lines) {
            if (isFrameworkFrame(line)) {
                hidden++;
            } else {
                appendHiddenCount(output, hidden);
                hidden = 0;
                output.add(line);
            }
        }
        appendHiddenCount(output, hidden);
        return output;
    }

    private boolean isFrameworkFrame(String line) {
        var matcher = FRAME.matcher(line);
        if (!matcher.matches()) {
            return false;
        }
        String className = matcher.group(1);
        return FRAMEWORK_PREFIXES.stream().anyMatch(className::startsWith);
    }

    private void appendHiddenCount(List<String> output, int hidden) {
        if (hidden > 0) {
            output.add("\t... " + hidden + " framework frame" + (hidden == 1 ? "" : "s") + " collapsed");
        }
    }
}


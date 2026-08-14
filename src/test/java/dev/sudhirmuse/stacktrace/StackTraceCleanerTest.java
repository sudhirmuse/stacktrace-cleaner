/* Copyright 2026 Sudhir Mishra. SPDX-License-Identifier: Apache-2.0 */
package dev.sudhirmuse.stacktrace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StackTraceCleanerTest {
    private final StackTraceCleaner cleaner = new StackTraceCleaner();

    @Test
    void collapsesConsecutiveFrameworkFramesButKeepsApplicationFrames() {
        String input = """
            java.lang.IllegalStateException: failed
            \tat com.example.PaymentService.charge(PaymentService.java:42)
            \tat org.springframework.aop.Proxy.invoke(Proxy.java:10)
            \tat java.base.Thread.run(Thread.java:1)
            Caused by: java.io.IOException: downstream
            \tat com.example.Client.call(Client.java:12)
            """;

        String result = cleaner.clean(input, CleanerOptions.defaults());

        assertTrue(result.contains("PaymentService.java:42"));
        assertTrue(result.contains("... 2 framework frames collapsed"));
        assertTrue(result.contains("Client.java:12"));
        assertFalse(result.contains("org.springframework.aop.Proxy"));
    }

    @Test
    void redactsPathsAndSecrets() {
        String input = "C:\\Users\\sudhir\\work\\App.java password=hunter2 Authorization: Bearer abc.def.ghi";

        String result = cleaner.clean(input, CleanerOptions.defaults());

        assertEquals("C:\\Users\\<user>\\work\\App.java password=<redacted> Authorization: Bearer <redacted>", result);
    }

    @Test
    void wrapsMarkdownWhenRequested() {
        assertEquals("```text\nboom\n```", cleaner.clean("boom", new CleanerOptions(false, true)));
    }
}

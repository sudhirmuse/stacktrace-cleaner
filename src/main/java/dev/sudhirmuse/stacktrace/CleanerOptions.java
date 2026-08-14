/* Copyright 2026 Sudhir Mishra. SPDX-License-Identifier: Apache-2.0 */
package dev.sudhirmuse.stacktrace;

public record CleanerOptions(boolean collapseFrameworkFrames, boolean markdown) {
    public static CleanerOptions defaults() {
        return new CleanerOptions(true, false);
    }
}


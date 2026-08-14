# StackTrace Cleaner

A small, dependency-free Java CLI that makes stack traces safe and readable before they are pasted into issues, chats, or incident reports.

## Features

- Redacts Windows and Unix home-directory paths.
- Redacts bearer tokens, passwords, API keys, and common secret assignments.
- Collapses consecutive framework stack frames while preserving application frames.
- Produces plain text or fenced Markdown.
- Reads from a file or standard input.
- Runs on Java 21 on Windows, macOS, and Linux.

## Build

```bash
./gradlew build
```

On Windows:

```powershell
.\gradlew.bat build
```

## Usage

```bash
java -jar build/libs/stacktrace-cleaner.jar trace.txt
cat trace.txt | java -jar build/libs/stacktrace-cleaner.jar --markdown
```

Options:

```text
--markdown       Wrap output in a Markdown code fence
--keep-framework Do not collapse framework frames
--help           Show help
```

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).

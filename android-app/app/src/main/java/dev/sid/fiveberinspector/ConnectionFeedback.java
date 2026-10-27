package dev.sid.fiveberinspector;
/** Only fixed, non-sensitive connection messages cross the bootstrap boundary. */
final class ConnectionFeedback {
  static String code(Throwable failure) {
    for (int depth = 0; failure != null && depth < 12; depth++, failure = failure.getCause()) {
      String message = failure.getMessage();
      if (message == null) continue;
      if (message.contains("Start connection from the app first")) return "WINDOW_CLOSED";
      if (message.contains("App version mismatch") || message.contains("artifact mismatch")) return "APP_CHANGED";
      if (message.contains("Bootstrap session mismatch") || message.contains("Invalid bootstrap challenge")) return "WINDOW_CHANGED";
      if (message.contains("App lifetime unavailable")) return "APP_CLOSED";
    }
    return "START_FAILED";
  }
  static boolean allowed(String code) {
    return "STARTING".equals(code) || "WINDOW_CLOSED".equals(code) || "APP_CHANGED".equals(code)
        || "WINDOW_CHANGED".equals(code) || "APP_CLOSED".equals(code) || "START_FAILED".equals(code)
        || "TIMEOUT".equals(code);
  }
  static String message(String code) {
    if ("STARTING".equals(code)) return "Computer detected. Starting the local service…";
    if ("WINDOW_CLOSED".equals(code)) return "Open 8bercueLa → Connect using computer (or Start connection again), then run the command within two minutes.";
    if ("APP_CHANGED".equals(code)) return "The app changed during connection. Reopen 8bercueLa, start a new connection window and run the command again.";
    if ("WINDOW_CHANGED".equals(code)) return "The connection window changed. Use Start connection again, then rerun the command.";
    if ("APP_CLOSED".equals(code)) return "The app closed during connection. Reopen 8bercueLa and start the connection again.";
    if ("TIMEOUT".equals(code)) return "Connection was not confirmed. Check the app before retrying; a late connection may still appear.";
    return "The local service could not start. Keep 8bercueLa open, start a new connection window and retry. If it repeats, check system restrictions in Settings → Compatibility check.";
  }
  static String countdown(long remainingMillis) {
    long seconds = Math.max(0, (remainingMillis + 999) / 1000);
    return seconds == 0 ? "Connection window expired" : String.format(java.util.Locale.ROOT,
        "Ready for command · %d:%02d remaining", seconds / 60, seconds % 60);
  }
  private ConnectionFeedback() {}
}

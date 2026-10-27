package dev.sid.fiveberinspector;
/** Resolve the installed, package-manager-owned APK each time; no downloaded script. */
final class ConnectionCommand {
  static String forApk(String path) {
    if (!validApk(path)) throw new IllegalArgumentException("Unexpected installed APK path");
    return "adb shell 'p=$(pm path dev.sid.fiveberinspector | head -n 1); CLASSPATH=${p#package:} app_process / dev.sid.fiveberinspector.Connect'";
  }
  static boolean validApk(String path) {
    return path != null && !path.contains("/../") && !path.contains("/./")
        && path.matches("/data/app/[A-Za-z0-9_~./+=-]+/base\\.apk")
        && path.contains("/dev.sid.fiveberinspector-");
  }
  private ConnectionCommand() {}
}

package dev.sid.fiveberinspector;

import android.os.Binder;
import android.os.Process;
import java.io.File;
import java.util.concurrent.TimeUnit;

/** Short-lived Shizuku UserService. Reuses the complete authenticated ADB bootstrap,
 * including owner-user, APK, nonce, app-lifetime and read-only recovery checks.
 * It never handles card operations or accepts commands from its caller. */
public final class ShizukuBootstrapService extends IShizukuBootstrap.Stub {
    public ShizukuBootstrapService() {}

    @Override public synchronized boolean connect() {
        try {
            ServiceCaller.requireApp();
            String apk = NativeLpacBundle.installedApk();
            if (!ConnectionCommand.validApk(apk)) return false;
            ProcessBuilder launch = new ProcessBuilder("/system/bin/app_process", "/",
                    "dev.sid.fiveberinspector.Connect", "--shizuku");
            launch.environment().put("CLASSPATH", apk);
            launch.redirectInput(new File("/dev/null"));
            launch.redirectOutput(new File("/dev/null"));
            launch.redirectError(new File("/dev/null"));
            java.lang.Process client = launch.start();
            try {
                return client.waitFor(80, TimeUnit.SECONDS) && client.exitValue() == 0;
            } finally {
                // This is only the status client. Its authenticated management child
                // has its own app lifetime and must never be killed during a write.
                if (client.isAlive()) client.destroyForcibly();
            }
        } catch (Exception failure) { return false; }
    }

    @Override public synchronized void destroy() {
        // Shizuku calls this transaction itself when removing the UserService.
        if (Binder.getCallingUid() != Process.SHELL_UID) {
            try { ServiceCaller.requireApp(); }
            catch (Exception denied) { throw new SecurityException("Unauthorized shutdown"); }
        }
        System.exit(0);
    }
}

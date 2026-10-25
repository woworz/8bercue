package dev.sid.fiveberinspector;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import rikka.shizuku.Shizuku;

/** Activity-owned authorization attempt. Only a verified provider handoff is success. */
final class ShizukuConnection implements AutoCloseable {
    interface Listener {
        void starting();
        void connected();
        void failed(int message, CompatibilityDiagnostics.Failure reason);
    }
    private static final java.util.concurrent.atomic.AtomicInteger REQUESTS = new java.util.concurrent.atomic.AtomicInteger(8501);
    private static final ExecutorService WORK = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private final Listener listener;
    private final Shizuku.UserServiceArgs args;
    private volatile boolean closed, active;
    private boolean binding, requesting;
    private volatile int generation;
    private int requestCode;
    private ServiceConnection connection;
    private IBinder previous;
    private final Runnable timeout = () -> fail(R.string.shizuku_timeout,
            CompatibilityDiagnostics.Failure.BOOTSTRAP_TIMEOUT);
    private final Shizuku.OnBinderReceivedListener received = () -> {
        if (active && !binding && !requesting) authorize();
    };
    private final Shizuku.OnBinderDeadListener dead = () -> fail(R.string.shizuku_not_running,
            CompatibilityDiagnostics.Failure.SHIZUKU_NOT_RUNNING);
    private final Shizuku.OnRequestPermissionResultListener permission = (code, result) -> {
        if (code != requestCode || !active || !requesting) return;
        requesting = false;
        if (result == PackageManager.PERMISSION_GRANTED) authorize();
        else fail(R.string.shizuku_denied, CompatibilityDiagnostics.Failure.SHIZUKU_PERMISSION_DENIED);
    };

    ShizukuConnection(Context context, Listener listener) {
        this.listener = listener;
        args = new Shizuku.UserServiceArgs(new ComponentName(context.getPackageName(),
                ShizukuBootstrapService.class.getName()))
                .tag("8bercue-shizuku-bootstrap").processNameSuffix("shizuku-bootstrap")
                .version(BuildConfig.VERSION_CODE).daemon(false).debuggable(BuildConfig.DEBUG);
        Shizuku.addBinderReceivedListenerSticky(received, main);
        Shizuku.addBinderDeadListener(dead, main);
        Shizuku.addRequestPermissionResultListener(permission, main);
    }

    boolean busy() { return active; }

    void start() {
        if (closed || active) return;
        active = true;
        requestCode = REQUESTS.incrementAndGet();
        IProfileService existing = CompanionBinderProvider.management();
        previous = existing == null ? null : existing.asBinder();
        generation++;
        CompatibilityDiagnostics.started(CompatibilityDiagnostics.Transport.SHIZUKU);
        main.postDelayed(timeout, 90000);
        authorize();
    }

    private void authorize() {
        if (closed || !active || binding || requesting) return;
        try {
            if (!Shizuku.pingBinder()) {
                fail(R.string.shizuku_not_running, CompatibilityDiagnostics.Failure.SHIZUKU_NOT_RUNNING);
            } else if (Shizuku.isPreV11() || Shizuku.getVersion() < 12) {
                fail(R.string.shizuku_update, CompatibilityDiagnostics.Failure.SHIZUKU_BIND_FAILED);
            } else if (Shizuku.getUid() != android.os.Process.SHELL_UID) {
                fail(R.string.shizuku_shell_required, CompatibilityDiagnostics.Failure.DEVICE_UNSUPPORTED);
            } else if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                bind();
            } else if (Shizuku.shouldShowRequestPermissionRationale()) {
                fail(R.string.shizuku_denied, CompatibilityDiagnostics.Failure.SHIZUKU_PERMISSION_DENIED);
            } else {
                requesting = true;
                Shizuku.requestPermission(requestCode);
            }
        } catch (RuntimeException unavailable) {
            fail(R.string.shizuku_failed, CompatibilityDiagnostics.Failure.SHIZUKU_BIND_FAILED);
        }
    }

    private void bind() {
        listener.starting();
        binding = true;
        final int attempt = generation;
        connection = new ServiceConnection() {
            private boolean submitted;
            @Override public void onServiceConnected(ComponentName name, IBinder binder) {
                if (closed || !active || generation != attempt || submitted) return;
                submitted = true;
                WORK.execute(() -> {
                    // The shared executor can still hold this task after a retry
                    // or Activity destruction. Discard it before privileged IPC.
                    if (closed || !active || generation != attempt) return;
                    boolean ok = false;
                    try {
                        if (binder != null && binder.pingBinder()
                                && IShizukuBootstrap.Stub.DESCRIPTOR.equals(binder.getInterfaceDescriptor()))
                            ok = IShizukuBootstrap.Stub.asInterface(binder).connect();
                    } catch (Exception ignored) { /* Fixed UI text, never raw shell output. */ }
                    final boolean result = ok;
                    main.post(() -> {
                        if (closed || !active || generation != attempt) return;
                        if (hasNewService()) {
                            finish();
                            listener.connected();
                        } else {
                            fail(result ? R.string.shizuku_timeout : R.string.shizuku_failed,
                                    CompatibilityDiagnostics.Failure.SHIZUKU_BIND_FAILED);
                        }
                    });
                });
            }
            @Override public void onServiceDisconnected(ComponentName name) {
                if (!closed && active && generation == attempt)
                    fail(R.string.shizuku_failed, CompatibilityDiagnostics.Failure.SHIZUKU_BIND_FAILED);
            }
        };
        Shizuku.bindUserService(args, connection);
    }

    private void fail(int message, CompatibilityDiagnostics.Failure reason) {
        if (closed || !active) return;
        // A late successful handoff wins over timeout/disconnect callbacks.
        if (hasNewService()) {
            finish();
            listener.connected();
            return;
        }
        finish();
        CompatibilityDiagnostics.failed(CompatibilityDiagnostics.Transport.SHIZUKU,
                reason == CompatibilityDiagnostics.Failure.SHIZUKU_PERMISSION_DENIED
                        ? CompatibilityDiagnostics.Stage.AUTHORIZATION : CompatibilityDiagnostics.Stage.BOOTSTRAP,
                reason);
        listener.failed(message, reason);
    }

    void cancel() { if (!closed) finish(); }

    private boolean hasNewService() {
        IProfileService service = CompanionBinderProvider.management();
        return service != null && !service.asBinder().equals(previous);
    }

    private void finish() {
        active = false;
        requesting = false;
        generation++;
        main.removeCallbacks(timeout);
        if (binding) {
            binding = false;
            try {
                // Remove the callback separately: remove=true does not clear the
                // API client's connection registry. The bootstrap owns no card worker.
                Shizuku.unbindUserService(args, connection, false);
            } catch (RuntimeException ignored) { /* Binder may already have died. */ }
            try { Shizuku.unbindUserService(args, connection, true); }
            catch (RuntimeException ignored) { /* Still attempt removal if detaching failed. */ }
            connection = null;
        }
    }

    @Override public void close() {
        if (closed) return;
        finish();
        closed = true;
        Shizuku.removeBinderReceivedListener(received);
        Shizuku.removeBinderDeadListener(dead);
        Shizuku.removeRequestPermissionResultListener(permission);
    }
}

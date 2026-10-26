# Build the corresponding release source

Shizuku integration uses API/provider 13.1.5 from Maven Central with locked
dependencies and SHA-256 verification. Run `bash android-app/tools/run_shizuku_tests.sh`
for the production authorization/lifecycle state machine regressions. See
[Shizuku connection](docs/SHIZUKU.md) for setup and the real-device verification checklist.

This branch builds the 8bercueLa 0.19.1-beta.4 source, versionCode 49. Debug-only laboratory tools, private device records, activation credentials and signing keys are excluded; none are compiled into the release APK. The public compatibility regressions run with `bash android-app/tools/run_compatibility_tests.sh`. UI/ADB command regressions run with `bash android-app/tools/run_ui_tests.sh`. Provider authentication/state regressions run with `bash android-app/tools/run_bootstrap_tests.sh`. Read-only recovery/worker exclusion checks run with `bash android-app/tools/run_recovery_tests.sh`. Bilingual resource, placeholder and fixed-error coverage runs with `python3 android-app/tools/test_localization.py`. Reconciliation identity-composition checks run with `python3 android-app/tools/test_reconcile_identity.py`. The private laboratory suite is not included.

Use the versions in release/toolchain.json. Set JAVA_HOME to JDK 17+, ANDROID_HOME to the SDK with platform 36 and NDK 27.3.13750724. The Gradle wrapper downloads its pinned distribution; Maven dependencies are resolved from the repositories in settings.gradle with dependency locks and verification metadata. An existing populated Gradle cache supports --offline.

Run `bash android-app/tools/build_android_lpac.sh` to rebuild the native payload from the bundled complete upstream source archives and local patch. Run `bash android-app/gradlew -p android-app :app:assembleRelease :app:lintRelease --console=plain` to build an unsigned APK. The native shared library can be modified and rebuilt using the same script; no publisher key is required to build or run a self-signed derivative.

To sign your own build, supply all four FIVEBER_RELEASE_KEYSTORE, FIVEBER_RELEASE_STORE_PASSWORD, FIVEBER_RELEASE_KEY_ALIAS and FIVEBER_RELEASE_KEY_PASSWORD environment variables. Use your own private key; the publisher key is intentionally absent. Android permits same-package upgrades only with a compatible signing identity. Review release/SIGNING_AND_MIGRATION.md before replacing an existing installation and preserve unresolved safety records.

Install your signed APK with `adb -s YOUR_DEVICE install path/to/app-release.apk`. Open the app, choose Connect using computer and copy the stable ADB command shown in the app and run it while its two-minute timer is active. The foreground client resolves the installed APK and confirms the authenticated Binder handoff; it exits nonzero on an unconfirmed connection. Expired windows require Start connection again, then rerunning the same command. No Python runtime or separately downloaded bootstrap is required. Review every device/profile selection before card operations. The companion requires authorized ADB access. No fixed card identity is required by this release.

The repository SHA256.json records this release source snapshot, excluding the manifest itself. The Release SHA256SUMS records downloadable assets. Native source archives and license notices remain unmodified. See LICENSING.md, COPYING and android-app/third_party.

Remote feedback and crash reporting have been removed. No reporting SDK or DSN configuration is required.

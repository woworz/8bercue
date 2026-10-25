# Shizuku connection

Install Shizuku from https://shizuku.rikka.app/download/ and start it with wireless
debugging or ADB. Open 8bercue, select **Connect your card → Connect using Shizuku**
(also available in Settings), then allow the authorization prompt. If permission
was permanently denied, allow 8bercue in Shizuku's Authorized applications and retry.

This build requires Shizuku API v12 or later running as Shell (UID 2000).
Root-started Shizuku and Sui are explicitly rejected. Existing filesystem ownership,
service caller checks and recovery process identity depend on the Shell identity.
Changing those checks to accept root would require a separate migration design.

The short-lived Shizuku UserService exposes only `connect()` and the standard
destroy transaction. It accepts no commands, activation codes, card IDs or APK paths.
It authenticates the real calling app UID and installed artifact before launching
this installed APK's `Connect --shizuku` entry point. That entry point performs the
same main-user check, fresh two-minute challenge, version/hash validation, typed
Binder handoff, app-lifetime binding and stale-worker recovery as the ADB entry point.
The exported ADB provider still accepts only Shell UID callers.

The resulting `ProfileUserService` is identical for ADB and Shizuku. Profile reading,
card selection, rename, enable/disable, delete, download, notification cleanup and
recovery review retain their existing gates and one-use operation plans. Authorization
alone does not establish device/card compatibility or authorize a blocked operation.
Connection metadata and Settings identify Shizuku when it started the service.

The Shizuku UserService is detached and removed after bootstrap. The management
process has the same lifetime as an ADB connection: it survives stopping Shizuku or
revoking Shizuku permission, and ends when its app-lifetime binder dies and card work
is idle. This avoids interrupting an in-flight card write. A reboot, app update or
management service termination requires a new connection; no writes are replayed.
The connection screen explains this behavior.

## Verification

Run the normal UI, bootstrap authentication and localization regressions plus
`bash android-app/tools/run_shizuku_tests.sh`. Assemble and lint the release with the
normal Gradle dependency verification enabled. Dependency locks and SHA-256 metadata
include Shizuku API/provider 13.1.5 and their transitive dependencies.

On an ARM64 phone with a compatible eUICC, verify these cases before release:

1. Shizuku absent, stopped, outdated, Root mode; permission denied and permanently
   denied; allow permission in the manager and retry.
2. Grant permission, connect, verify Settings shows Shizuku, run Compatibility check,
   compare a fresh profile list and card selection with the same device using ADB.
3. With a test eSIM, verify rename, enable/disable, confirmed delete and download
   through both methods under the same compatibility/policy conditions.
4. Background/recreate the Activity during permission and bootstrap, stop Shizuku,
   retry after timeout, reboot/update the app and reconnect. No callback may replay
   card work or declare connection without the authenticated Binder.
5. Verify an existing management session is retained, an active operation prevents
   changing connection, and read-only recovery preserves unresolved records.

Offline tests and compilation do not establish device compatibility or successful
eSIM mutations. Real-device verification is required for those claims.

Official references: https://github.com/RikkaApps/Shizuku-API and
https://github.com/RikkaApps/Shizuku/blob/master/README.md.

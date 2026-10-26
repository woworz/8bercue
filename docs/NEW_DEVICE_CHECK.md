# Check a new phone

Use the signed APK from the candidate delivery. Copy its connection command from the installed app; no separate bootstrap download is required. The published Beta retains older compatibility limits. The current candidate installs on ARM64 Android 9+; actual card access depends on the phone's interfaces and permissions. No Shizuku app is required or offered.

## Connect once using USB ADB

1. Enable USB debugging on the target phone, connect it to a trusted computer and accept that computer's authorization prompt. Run `adb devices -l`; identify the target's exact serial and require state `device`. This bootstrap currently supports the owner user (user 0).
2. Install the candidate on that target, for example:

       adb -s SERIAL install -r 8bercue-0.18.1-rc11-33.apk

   Replace SERIAL with the target serial. If Android reports a signing conflict, stop and preserve the existing app/data/safety history; do not uninstall or clear data as a shortcut. Package signatures are shared across Android user profiles; any additional work/private-space registration must be included in an explicitly approved migration scope.
   Some ROMs reject `adb install` with `INSTALL_FAILED_USER_RESTRICTED` without showing a prompt. That is an installation policy, not a card-access result. You can copy the same APK to Downloads and open it in the normal system package installer. Preserve its hash/signature; do not remove an existing installation or disable security protections to hide a failure.
3. Open 8bercueLa and choose **Connect using computer**. Keep **Waiting for computer** open, then choose **Copy ADB command** and run it in the computer terminal while the visible two-minute timer is active. The terminal must confirm connection; if the window expires, choose **Start connection again** and rerun the same command. If several phones are attached, use `adb -s SERIAL shell` for the intended phone.

   If ADB is not in PATH, replace the initial `adb` with its absolute executable path. The connection command does not install the app, change profiles or enable wireless debugging.
4. If several readable cards are found, choose **Choose card** and verify the physical slot/port and masked EID suffix before selecting the intended eUICC. The built-in card and removable card are distinct assets. A choice lasts only for the current Shell service session; a missing or changed selected card stops access instead of selecting an alternative.
5. Keep the app open until the profile read finishes. A successful Binder handoff only proves the app-to-Shell connection; it does not prove card access.

The computer starts a local authenticated Shell service. It is not a permanent privilege grant. Reconnect after reboot, APK update, or app/companion process termination. A normal background/foreground return can reuse an existing live connection. No failed card operation is automatically replayed.

## Decide what actually passed

- **Installed/launched:** the app opens. This alone says nothing about card management.
- **Connected:** the authenticated local service is attached. This alone says nothing about card access.
- **Card/profile read verified:** Home shows a selected card channel and a freshly read profile list. Confirm those profiles belong to the intended card, not another built-in eUICC.
- **Write operations:** not established by compatibility checks or results from a different phone/APK. Obtain device/card-specific authorization and protect the baseline before testing a disposable inactive profile. Do not consume activation codes, switch a live subscription or delete anything just to test discovery.

Open **Compatibility check** from Home or Settings. The app tries Secure Element AIDL/HIDL HAL first, then Telephony, then OMAPI if needed. It records service discovery/binding, card presence, fixed ISD-R opening, full GetEID verification and owned-channel cleanup. **Run read-only check** repeats only reads.

The report distinguishes absent/inaccessible services, unavailable interface classes/methods, denied access, no reported card, unavailable management applet/channel, invalid identity response, unreachable service, invalid reader inventory and multiple readable cards. An API may not distinguish an unregistered service from blocked lookup; that uncertainty is shown. Multiple readable cards, invalid inventory or uncertain cleanup stop automatic selection.

The Telephony path remains an experimental framework-policy workaround and can be blocked by another ROM. HIDL is read-only in this build. A HAL service name, Android version or phone brand cannot establish compatibility by itself.

## Save a useful result

In Settings → **Recovery & advanced** → **Export safe diagnostics**, save the report with the candidate APK version/hash, phone model and ROM version. Do not send activation credentials, complete EID/ICCID or raw protocol logs. A complete read failure report is useful evidence; it does not authorize another privilege method or a write.

See [automatic compatibility checks](AUTOMATIC_COMPATIBILITY.md) for the exact detection and evidence boundaries. Tests from a Pixel, Moto or earlier Xiaomi candidate do not qualify a new target.

# Device compatibility

Installing the APK and accessing an eUICC are separate requirements. Minimum SDK is 28 (Android 9), with ARM64 native components built for API 28. Compile/target SDK remains 36.

The published v0.18.0-beta.1 APK (0.18.0-rc1 / 22) rejects management below API 36. The unpublished 0.18.1-rc11 / 33 candidate checks actual capabilities instead and includes an **experimental Shell Telephony fallback with scoped management protocols**. This is not general Android 13 qualification.

## Transports

1. Prefer registered hardware AIDL instances using interface classes from the trusted system SecureElement APK.
2. Otherwise check declared HIDL HAL readers with real service/channel/GetEID observations. HIDL is read-only in this build; no HAL init/reset is called.
3. If no card was verified through HAL, try the experimental Shell Telephony backend.
4. Otherwise try framework OMAPI, retaining its applet access rules. Never hide ambiguity or failed channel cleanup by falling back.

[Automatic compatibility checks](AUTOMATIC_COMPATIBILITY.md) describe the per-channel report, explicit missing conditions and read-only rerun in Home/Settings. No brand/model or Android 16 list is consulted.

Every backend retains caller authentication, complete EID identity binding and bounded APDU policies. Reader names include the transport namespace. A backend change invalidates an existing management selection. No path initializes/resets the HAL or closes another client's channels.

The fallback depends on an Android framework **security-check defect**: a case-sensitive comparison of the ISD-R hex string, despite case-insensitive interpretation of the encoded bytes. It uses the real Shell UID and package, with the existing MODIFY_PHONE_STATE permission. It does not impersonate the system LPA. A patched ROM may reject this path. See [rootless investigation](ROOTLESS_ANDROID13.md).

Default Telephony channels remain read-only: GetEID, GetProfilesInfo and optional GetEuiccInfo2. Dedicated channels enforce one exact confirmed lifecycle command, the existing staged ES10b download state machine, or a sequence-bound notification exchange. ListNotification is opt-in and read-only. Lost Binder replies poison that connection; no automatic mutation or HTTP retry is added.

Caller authentication, one-use confirmation tokens, complete card identity, exact inventory binding, fsynced journals and independent postflight checks remain mandatory. An optional expiring Shell-owned owner scope can further restrict a session to its original inventory and one independently identified disposable profile. It grants no new permission and an invalid existing scope fails closed.

On the tested Android 13 Xiaomi, holding a notification read channel prevents opening a second usable ISD-R channel for identity verification. The candidate closes its owned read channel, re-identifies the complete card, then opens a dedicated acknowledged-removal channel. It never removes another client's channel. Cleanup failures are propagated to the parent coordinator and quarantine further writes.

For profiles installed by this app with a delivered installation receipt, deletion now snapshots notification metadata before the write. After independently verified deletion, only a new deletion notification matching that complete profile identity, approved recipient and transaction may be posted once. Its sequence and kind are durably bound; removal follows operator acknowledgment. Profiles without a trusted installation receipt are not automatically assigned a notification recipient.

## Current candidate and new-phone qualification

Version 32 removed Shizuku. This source now restores an explicit Shizuku authorization choice alongside the computer connection. Shizuku must run as Shell UID 2000 (wireless debugging or ADB); it starts the same authenticated management service and does not change card qualification. Reconnect after reboot, APK update or service termination. See [Shizuku connection](SHIZUKU.md) for lifecycle, requirements and verification limits.

A fresh install on Redmi 26021RN18C / mist, HyperOS OS3.0.305.0.WPUCNXM, Android 16 / API 36 passed installation through the normal system package installer, launch, one ADB bootstrap and actual AIDL HAL SIM1 card access. Streamed ADB installation was rejected by HyperOS; this installation policy did not block the subsequent Shell-to-HAL connection. Native and Java reads agreed on an empty inventory, with complete card identity stable. UI read-only recheck, card information and background return passed. No fallback ROM exception, profile write, activation credential or notification delivery was used. That initial stage did not test writes. A subsequent authorized cycle on the identical APK passed public BetterRoaming installation, rename/restore and deletion, including both installation/deletion notifications; final inventory returned to empty. Activation, switching, network service and other backends remain untested on this target. See the [write-cycle record](ANDROID16_PROFILE_CYCLE.md) and [APK-bound initial record](AUTOMATIC_COMPATIBILITY.md#fresh-install-candidate-result-20-october-2026).

## Actual Android 13 results

Device: Xiaomi 2109119BC / mona, ROM V816.0.2.0.TKVCNXM, Android 13 / API 33, ARM64. ADB uses the local USB target only; no Shizuku, root, ROM/SELinux change, reboot or global communication-setting change was used.

| APK | Results bound to that APK |
| --- | --- |
| 0.18.1-rc4 / 26 | Same-signer install, launch, computer connection, complete card identity and independent native/Java inventory passed. Disabled SMC nickname was changed and restored to its original empty value; full original inventory restored, povo remained enabled. |
| 0.18.1-rc5 / 27 | Public BetterRoaming installation passed: exactly one new disabled profile, original three unchanged. Operator acknowledged its installation notification; local cleanup exposed the single-channel incompatibility. Speedtest's public token was refused at AuthenticateClient. |
| 0.18.1-rc6 / 28 | Recovered acknowledged installation-notification cleanup, then deleted only that disabled disposable profile and independently restored the original inventory. Repeated directory/official public entries were refused by the server; these are not Android API failures or successful installs. |
| 0.18.1-rc7 / 29 | Completed a fresh public BetterRoaming install, temporary activation, povo restoration and disposable deletion through normal UI. Both installation and deletion notifications were acknowledged and removed locally. Full original inventory was restored and Android reported povo active. Also repaired the exact inherited deletion notification. See the [profile-cycle record](ANDROID13_PROFILE_CYCLE.md). |

Network registration, mobile-data traffic, enable/disable notification delivery, standalone disable, reboot persistence and other ROMs remain unqualified. The successful public BetterRoaming entry is not evidence that arbitrary operator activation codes work.

Version 31 completed same-signer upgrade, launch, normal computer connection, Home/Settings compatibility details, explicit read-only recheck and independent native/Java three-profile read on the same Xiaomi. AIDL returned no accessible instances; declared HIDL services could not be reached; Telephony slot 0 could not open ISD-R and slot 1 / port 0 verified the card. Full original inventory, operation/notification journals, UID and default-data setting were preserved. No version-31 write or network-service acceptance is claimed. HIDL successful card reads remain synthetic-only here because no declared HIDL service could be reached through this candidate.

Version 31 signed APK SHA-256: e07490486bb24a7cf298ecb3941f843e67535b00438b604f1d3cb11b44ef5abc. See [automatic compatibility checks](AUTOMATIC_COMPATIBILITY.md).

Version 29 signed APK SHA-256: e83838ffc26ac1de0ea3ed7dd37f4a23590a0b17161a1681b37130e6e7d3e3b3. Same-signer upgrades preserve application data, UID and safety history. Earlier Moto/Beta results do not qualify this candidate.

Server 8.2.6 / 3.8 means MatchingID refused. It does not prove the underlying campaign quota, code-consumption history, or general device eligibility. Refused attempts remain in original immutable history. After same-card/full-inventory readback and operator review, separate abandonment resolutions permit a different authorized test asset without declaring the old code unused. Do not blindly retry refused codes.

Computer connection uses the normal Connect using computer challenge, then one authenticated ADB Shell companion bootstrap. Reconnect after reboot, app update or companion termination. MIUI denies SDK input injection; temporary standard UHID touch/keyboard devices can operate the normal UI with existing Shell permissions and no setting changes.

Run the public synthetic compatibility and safety regressions:

    bash android-app/tools/run_compatibility_tests.sh

These include protocol separation, unknown-result poisoning, one-channel notification cleanup, card-swap and cleanup-failure refusal, protected original assets and deletion-notification identity/recipient/operation binding. Synthetic passes do not qualify a live install, switch or network service.

## Version admission versus actual access

- APK and native payload minimum: Android 9 / API 28, ARM64. Target/compile SDK stays 36.
- Public [OMAPI SEService](https://developer.android.com/reference/android/se/omapi/SEService) starts at API 28.
- Public [Telephony logical channels](https://developer.android.com/reference/android/telephony/TelephonyManager#iccOpenLogicalChannel(java.lang.String)) start at API 21; the P2 overload starts at API 26. These older subscription APIs are not the physical-slot/port backend implemented here and do not grant applet access by themselves.
- The current fallback probes the Android 13 [IccLogicalChannelRequest and physical-port Binder API](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-13.0.0_r1/telephony/java/com/android/internal/telephony/ITelephony.aidl). It fails closed if those exact classes/methods or permissions are absent. Android 9–12 Telephony variants have not been implemented or tested.
- The original hardware [Secure Element AIDL is already present in AOSP Android 14](https://android.googlesource.com/platform/hardware/interfaces/+/refs/tags/android-14.0.0_r1/secure_element/aidl/android/hardware/secure_element/ISecureElement.aidl). It is a vendor HAL contract, not a public SDK guarantee or an Android 16 requirement. Android 13 on this phone exposes HIDL instead; its Shell lookup is denied.

One adapter can serve multiple ROMs exposing the same interface and access behavior; this does not require a ROM-name branch for every device. Each claimed device/ROM still needs actual qualification. ROMs that patch the ISD-R comparison or deny Shell access may have no usable rootless route under these constraints. No further lowering below API 28 is claimed without rebuilding native components, auditing APIs and testing older devices.

## Version 32 Android 13 follow-up

The identical APK 32 subsequently passed normal computer connection, automatic Telephony selection, original three-profile reads, inactive SMC rename/restore, and public BetterRoaming install/test rename/restore/delete. All six card transactions verified and the exact original inventory returned, with povo enabled throughout. Installation notification 68 remains delivery_unknown; deletion notification 69 delivered. The overall notification loop is therefore partial, not qualified by the older version-29 success or the Android-16 AIDL success. No application code or APK changed. See the [current Telephony regression and unresolved receipt](ANDROID13_PROFILE_CYCLE.md#same-apk-telephony-regression-0181-rc10--32).

## Version 33 explicit card choice

The candidate adds an authenticated read-only choice when more than one eUICC is reachable. It never chooses by brand, slot order or removable status. The choice pins a complete card identity for the current Shell session and invalidates old operation previews; a missing or changed selected card stops access without fallback. Full identity/inventory checks, one-use write confirmations and unknown-result journals remain mandatory. See [selection boundaries](AUTOMATIC_COMPATIBILITY.md). Earlier APK 32 physical results do not qualify APK 33.

The exact version-33 Pixel 10 Pro XL / Android 17 two-card qualification, approved signing migration and removable-card write/receipt results are recorded [separately](PIXEL10_MULTI_CARD.md). These do not qualify version 33 on Android 13 or resolve the older Xiaomi receipt.

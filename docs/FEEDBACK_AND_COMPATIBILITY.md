# Local diagnostics and compatibility

8bercueLa keeps compatibility checks, bounded connection stages and normalized
failure codes on the phone. It has no problem-report form, email-report action,
remote compatibility submission, crash-report setting or reporting SDK.

Choose **Settings → Save diagnostic ZIP** to select a local destination. The
operation records and advanced recovery screens use the same ZIP export.
The ZIP contains only allowlisted app/device details, local breadcrumbs and,
when available, the last connection failure. It excludes raw card identifiers,
profiles, activation codes, APDUs, logcat and private files. Saving does not
upload anything; review the archive before sharing it yourself.

Removing reporting does not change ADB/Shizuku authorization, card operations,
transaction journals or recovery protections. A missing Binder handshake alone
does not prove ROM incompatibility.

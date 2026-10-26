# Reporting removed

8bercueLa no longer includes problem-report submission, crash reporting or the
Sentry SDK. Builds do not use `glitchtipDsn` / `GLITCHTIP_DSN`, and artifact
verification no longer accepts `--require-reporting`.

The app retains local diagnostic ZIP export and compatibility checks. Saving a
ZIP does not upload it. Existing published releases and their historical notes
remain unchanged.

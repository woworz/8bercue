#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
out="$(mktemp -d)"
trap 'rm -rf "$out"' EXIT
src=app/src/main/java/dev/sid/fiveberinspector
mapfile -t stubs < <(find tools/bootstrap-stubs tools/shizuku-stubs -name '*.java' -print)
"${JAVA_HOME:+$JAVA_HOME/bin/}javac" -encoding UTF-8 -d "$out" "${stubs[@]}" \
 "$src"/{OperationTokens,ConnectionFeedback,CompanionBinderProvider,ShizukuConnection}.java tools/ShizukuConnectionOfflineTest.java
"${JAVA_HOME:+$JAVA_HOME/bin/}java" -cp "$out" dev.sid.fiveberinspector.ShizukuConnectionOfflineTest

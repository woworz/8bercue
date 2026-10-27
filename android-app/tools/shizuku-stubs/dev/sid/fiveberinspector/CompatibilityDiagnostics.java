package dev.sid.fiveberinspector;
final class CompatibilityDiagnostics {
 enum Transport {SHIZUKU}
 enum Stage {AUTHORIZATION,BOOTSTRAP}
 enum Failure {BOOTSTRAP_TIMEOUT,SHIZUKU_NOT_RUNNING,SHIZUKU_PERMISSION_DENIED,SHIZUKU_BIND_FAILED,DEVICE_UNSUPPORTED}
 static long started(Transport t){return 1;}
 static void failed(Transport t,Stage s,Failure f){}
}

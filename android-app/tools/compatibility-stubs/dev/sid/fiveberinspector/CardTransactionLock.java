package dev.sid.fiveberinspector;
/** Only models a discovery coordinator for hermetic tests. Never compiled into the APK. */
final class CardTransactionLock {
 static boolean active,quarantined;
 static boolean isActive(){return active;}
 static void quarantine(){if(!active)throw new SecurityException("No test owner");quarantined=true;}
}

package dev.sid.fiveberinspector;

// Starts only this installed APK's authenticated connection flow. No shell input.
interface IShizukuBootstrap {
    boolean connect() = 1;
    void destroy() = 16777114;
}

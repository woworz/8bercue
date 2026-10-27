package dev.sid.fiveberinspector;
public interface IShizukuBootstrap {
 boolean connect();
 abstract class Stub {
  public static final String DESCRIPTOR="shizuku-bootstrap";
  public static IShizukuBootstrap asInterface(android.os.IBinder binder){return (IShizukuBootstrap)binder;}
 }
}

package dev.sid.fiveberinspector;
/** The explicit toggle or Save click authorizes one authenticated prepare/execute pair. */
final class ProfileActionPolicy {
  static boolean executesDirectly(String operation){return "switch".equals(operation)||"disable".equals(operation)||"rename".equals(operation);}
  private ProfileActionPolicy(){}
}

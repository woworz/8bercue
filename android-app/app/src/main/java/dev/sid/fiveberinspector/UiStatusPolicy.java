package dev.sid.fiveberinspector;
/** Keep everyday connected UI quiet without hiding failures, selection or recovery. */
final class UiStatusPolicy {
 static boolean visible(boolean running,boolean pending,boolean hasNotice,boolean connected,String title){
  return running||pending||hasNotice||!connected||!("Card connected".equals(title)||"Check complete".equals(title)||"Card selected".equals(title)||"Connected".equals(title));
 }
 private UiStatusPolicy(){}
}

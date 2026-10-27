package dev.sid.fiveberinspector;
/** Ephemeral UI correlation only; never an operation authorization or exported diagnostic. */
final class ProfileUiIdentity {
 static String key(String session,String card,String iccid)throws Exception{
  if(!OperationTokens.isValid(session)||card==null||!card.matches("[a-f0-9]{64}")||iccid==null||!iccid.matches("[0-9]{19,20}"))throw new SecurityException("Complete identity required");
  return IntentDigests.of("profile-ui:"+session+":"+card+":"+iccid);
 }
 private ProfileUiIdentity(){}
}

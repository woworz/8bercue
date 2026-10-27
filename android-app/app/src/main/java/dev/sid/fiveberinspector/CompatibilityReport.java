package dev.sid.fiveberinspector;

import java.util.*;

/** Fixed-vocabulary read-only observations. Never contains a card identity or vendor message. */
final class CompatibilityReport {
 enum Backend {
  HAL_AIDL("Secure Element HAL (AIDL)"), HAL_HIDL("Secure Element HAL (HIDL)"),
  TELEPHONY("Telephony"), OMAPI("OMAPI");
  final String label;
  Backend(String label){this.label=label;}
 }
 enum Stage { ENUMERATION, SERVICE_LOOKUP, CARD_PRESENCE, MANAGEMENT_CHANNEL, EID_READ, CHANNEL_CLOSE, SELECTION }
 static final class Attempt {
  final Backend backend; final String reader; final Stage stage; final SecureElementFailure.Reason reason;
  Attempt(Backend backend,String reader,Stage stage,SecureElementFailure.Reason reason){
   this.backend=backend;this.reader=reader;this.stage=stage;this.reason=reason;
  }
 }
 private final List<Attempt> attempts=new ArrayList<>();
 private Backend selected;
 private String selectedReader="";
 void record(Backend backend,String reader,Stage stage,SecureElementFailure.Reason reason){
  if(backend==null||stage==null||attempts.size()>=160||(!reader.isEmpty()&&!ReaderDescriptor.valid(reader)))throw new SecurityException("Invalid compatibility observation");
  attempts.add(new Attempt(backend,reader,stage,reason));
 }
 void selected(Backend backend,String reader){selected=backend;selectedReader=reader;}
 List<Attempt> attempts(){return Collections.unmodifiableList(attempts);}
 String selectedBackend(){return selected==null?"":selected.name();}
 String selectedReader(){return selectedReader;}
 String route(){return selected==null?"No verified card connection":selected.label+" / "+selectedReader;}
 String summary(){
  StringBuilder result=new StringBuilder();
  for(Backend backend:Backend.values()){
   List<Attempt> group=new ArrayList<>();for(Attempt attempt:attempts)if(attempt.backend==backend)group.add(attempt);
   if(group.isEmpty())continue;
   if(result.length()>0)result.append("\n\n");result.append(backend.label);
   for(Attempt attempt:group){
    result.append("\n");if(!attempt.reader.isEmpty())result.append(attempt.reader).append(": ");
    result.append(description(attempt));
   }
  }
  return result.toString();
 }
 private static String description(Attempt a){
  if(a.reason==null)return "Management channel opened, complete eUICC identity verified and owned channel closed.";
  String stage=switch(a.stage){
   case ENUMERATION -> "Service/reader discovery";case SERVICE_LOOKUP -> "Service lookup";
   case CARD_PRESENCE -> "Card presence";case MANAGEMENT_CHANNEL -> "Opening ISD-R";
   case EID_READ -> "Reading eUICC identity";case CHANNEL_CLOSE -> "Closing owned channel";case SELECTION -> "Card selection";
  };
  String reason=switch(a.reason){
   case NO_SERVICE -> "no accessible service/readers found";
   case SYSTEM_INTERFACE_UNAVAILABLE -> "required interface classes or methods are unavailable";
   case ACCESS_DENIED -> "Shell access denied by system policy or card access rules";
   case INVALID_READER_INVENTORY -> "reader inventory is invalid or exceeds this build's bounds; automatic selection stopped";
   case NO_CARD -> "no card reported on this reader";
   case APPLET_UNAVAILABLE -> "management applet unavailable or channel could not be opened";
   case INVALID_EID_RESPONSE -> "response did not verify a complete eUICC identity";
   case SERVICE_UNREACHABLE -> "service could not be reached; it may be absent or lookup may be blocked";
   case SELECTED_CARD_CHANGED -> "the selected card identity or reader changed; no automatic fallback";
   case AMBIGUOUS_EUICC -> "multiple readable eUICCs; automatic selection stopped";
   case CHANNEL_CLEANUP_FAILED -> "cleanup could not be verified; all fallback stopped";
   default -> "check could not complete";
  };
  return stage+": "+reason+".";
 }
}

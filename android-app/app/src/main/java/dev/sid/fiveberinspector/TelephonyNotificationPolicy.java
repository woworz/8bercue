package dev.sid.fiveberinspector;
import java.util.Arrays;
/** Exact sequence policy; removal-only mode is opened after persisted acknowledgment and re-identification. */
final class TelephonyNotificationPolicy {
 private final NotificationSessionPolicy policy;
 private final long sequence;
 private final boolean removalOnly;
 private boolean submitted,outstanding;
 private int expectedLe=-1,frames,bytes;
 TelephonyNotificationPolicy(long sequence){this(sequence,false);}
 TelephonyNotificationPolicy(long sequence,boolean removalOnly){policy=new NotificationSessionPolicy(sequence);this.sequence=sequence;this.removalOnly=removalOnly;}
 void authorize(byte[] command,int channel){
  if(!removalOnly){if(!Arrays.equals(policy.next(channel),command))throw new SecurityException("Notification command differs from bound sequence");return;}
  if(outstanding||frames>=260||channel<1||channel>3)throw new SecurityException("Notification removal response budget");
  byte[] expected;
  if(!submitted){byte[] body=NotificationCodec.command(sequence,true);expected=new byte[body.length+5];expected[0]=(byte)(0x80|channel);expected[1]=(byte)0xe2;expected[2]=(byte)0x91;expected[4]=(byte)body.length;System.arraycopy(body,0,expected,5,body.length);}
  else{if(expectedLe<0)throw new SecurityException("Notification removal already submitted");expected=new byte[]{(byte)(0x80|channel),(byte)0xc0,0,0,(byte)expectedLe};}
  if(!Arrays.equals(command,expected))throw new SecurityException("Notification removal differs from acknowledged sequence");
  submitted=true;outstanding=true;frames++;
 }
 void acceptResponse(byte[] response){
  if(!removalOnly){policy.accept(response);return;}
  if(!outstanding||response==null||response.length<2||response.length>8192||bytes+response.length-2>NotificationCodec.MAX_BYTES)throw new SecurityException("Invalid notification removal response");
  bytes+=response.length-2;outstanding=false;int sw1=response[response.length-2]&255,sw2=response[response.length-1]&255;
  expectedLe=sw1==0x61?sw2:-1;
  if(sw1!=0x61&&(sw1!=0x90||sw2!=0))throw new SecurityException("Notification removal rejected");
 }
 void acknowledged(){if(removalOnly)throw new SecurityException("Removal connection already scoped");policy.acknowledged();}
}

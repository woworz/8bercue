package dev.sid.fiveberinspector;
import java.util.Arrays;
/** Exact, short read APDUs only. No caller-selected AID, basic channel or SELECT APDU. */
final class TelephonyChannelCodec {
 static final String ISDR="A0000005591010FFFFFFFF8900000100";
 static String aid(byte[] bytes){
  if(!Arrays.equals(bytes,unhex(ISDR)))throw new SecurityException("Only ISD-R is supported");
  // Android 13's special LPA check compares text case-sensitively. This is an
  // explicitly experimental policy workaround, not a new permission grant.
  return ISDR.toLowerCase(java.util.Locale.ROOT);
 }
 static String hex(byte[] bytes,int offset){
  StringBuilder out=new StringBuilder();
  for(int i=offset;i<bytes.length;i++){out.append(Character.forDigit((bytes[i]>>>4)&15,16));out.append(Character.forDigit(bytes[i]&15,16));}
  return out.toString();
 }
 static byte[] unhex(String value){
  if(value==null||value.length()<4||value.length()>16384||(value.length()&1)!=0||!value.matches("[0-9A-Fa-f]+"))throw new SecurityException("Invalid telephony response");
  byte[] bytes=new byte[value.length()/2];
  for(int i=0;i<bytes.length;i++)bytes[i]=(byte)Integer.parseInt(value.substring(2*i,2*i+2),16);
  return bytes;
 }
 private TelephonyChannelCodec(){}
}

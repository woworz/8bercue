package dev.sid.fiveberinspector;

import java.lang.reflect.*;
import java.util.*;

/** Existing initialized HIDL service only. Never init/reset or replace the system callback. */
final class HidlSecureElementTransport implements SecureElementTransport {
 private final Object hal;
 private final Class<?> api,callback;
 private int owned=-1;
 private CardReadApduPolicy policy;
 HidlSecureElementTransport(Object hal,Class<?> api,Class<?> callback){
  if(hal==null)throw new SecureElementFailure(SecureElementFailure.Reason.SERVICE_UNREACHABLE);
  this.hal=hal;this.api=api;this.callback=callback;
 }
 private static ArrayList<Byte> vector(byte[] bytes){ArrayList<Byte> result=new ArrayList<>();for(byte b:bytes)result.add(b);return result;}
 public boolean isCardPresent()throws Exception{return (Boolean)api.getMethod("isCardPresent").invoke(hal);}
 public int openLogicalChannel(byte[] aid,byte p2)throws Exception{
  if(owned>0||p2!=0||!Arrays.equals(aid,TelephonyChannelCodec.unhex(TelephonyChannelCodec.ISDR)))throw new SecurityException("Only owned ISD-R channels supported");
  int[] calls={0},status={-1},number={-1};
  Object result=Proxy.newProxyInstance(callback.getClassLoader(),new Class<?>[]{callback},(proxy,method,args)->{
   if(!method.getName().equals("onValues")||args==null||args.length!=2||++calls[0]!=1)throw new SecurityException("Invalid HIDL callback");
   status[0]=((Number)args[1]).intValue();number[0]=((Number)args[0].getClass().getField("channelNumber").get(args[0])).intValue();return null;
  });
  try{api.getMethod("openLogicalChannel",ArrayList.class,byte.class,callback).invoke(hal,vector(aid),p2,result);}
  catch(Exception uncertain){throw new SecureElementFailure(SecureElementFailure.Reason.CHANNEL_CLEANUP_FAILED);}
  if(calls[0]!=1)throw new SecureElementFailure(SecureElementFailure.Reason.CHANNEL_CLEANUP_FAILED);
  if(number[0]>0){owned=number[0];if(status[0]!=0||owned>3){closeChannel(owned);throw new SecureElementFailure(SecureElementFailure.Reason.APPLET_UNAVAILABLE);}}
  if(status[0]!=0)throw new SecureElementFailure(SecureElementFailure.Reason.APPLET_UNAVAILABLE);
  if(owned<1)throw new SecureElementFailure(SecureElementFailure.Reason.CHANNEL_CLEANUP_FAILED);
  policy=new CardReadApduPolicy(owned,true);return owned;
 }
 public byte[] transmit(byte[] command)throws Exception{
  if(owned<1||command==null||command.length<5||(command[0]&3)!=owned)throw new SecurityException("Foreign channel");
  policy.authorize(command);
  Object value=api.getMethod("transmit",ArrayList.class).invoke(hal,vector(command));
  if(!(value instanceof List<?>)||((List<?>)value).size()>65538)throw new SecurityException("Invalid HIDL response");
  List<?> bytes=(List<?>)value;byte[] result=new byte[bytes.size()];for(int i=0;i<result.length;i++)result[i]=(Byte)bytes.get(i);policy.acceptResponse(result);return result;
 }
 public void closeChannel(int channel)throws Exception{
  if(channel<1||channel!=owned)throw new SecurityException("Foreign channel close");
  try{if(((Number)api.getMethod("closeChannel",byte.class).invoke(hal,(byte)channel)).intValue()!=0)throw new SecurityException("HIDL close rejected");owned=-1;}
  catch(Exception unsafe){throw new SecureElementFailure(SecureElementFailure.Reason.CHANNEL_CLEANUP_FAILED);}
 }
}

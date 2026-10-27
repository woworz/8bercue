package dev.sid.fiveberinspector;
import java.util.Arrays;
/** One confirmed lifecycle command, then at most six response continuations. No retry. */
final class TelephonyLifecyclePolicy {
 private final String id,nickname;
 private final ProfileLifecycleGuard.Operation operation;
 private boolean submitted,awaiting,continuation;
 private int expectedLe=-1,frames,bytes;
 TelephonyLifecyclePolicy(ProfileLifecycleGuard.Operation operation,String id,String nickname){
  if(operation==null)throw new SecurityException("Operation required");this.operation=operation;
  ProfileLifecycleGuard.command(operation,id,nickname,1);
  this.id=id;this.nickname=nickname;
 }
 void authorize(byte[] command,int channel){
  if(awaiting||frames>=7)throw new SecurityException("Lifecycle response budget exhausted");
  byte[] expected=!submitted?ProfileLifecycleGuard.command(operation,id,nickname,channel):
   new byte[]{(byte)(0x80|channel),(byte)0xC0,0,0,(byte)expectedLe};
  if((submitted&&!continuation)||!Arrays.equals(expected,command))throw new SecurityException("Command outside confirmed lifecycle operation");
  submitted=true;awaiting=true;continuation=false;frames++;
 }
 void acceptResponse(byte[] reply){
  if(!awaiting||reply==null||reply.length<2||reply.length>8192||bytes+reply.length-2>32768)throw new SecurityException("Malformed lifecycle response");
  awaiting=false;bytes+=reply.length-2;continuation=(reply[reply.length-2]&255)==0x61;
  expectedLe=continuation?reply[reply.length-1]&255:-1;
 }
}

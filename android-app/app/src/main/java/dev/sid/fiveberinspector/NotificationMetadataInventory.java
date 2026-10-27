package dev.sid.fiveberinspector;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
/** Read-only notification metadata. Full identifiers and recipients stay inside the Shell service. */
final class NotificationMetadataInventory {
 static final class Entry {final long sequence;final String iccid,host;final int operation;Entry(long sequence,String iccid,String host,int operation){this.sequence=sequence;this.iccid=iccid;this.host=host;this.operation=operation;}}
 private static String iccid(byte[] b){if(b.length!=10)throw new SecurityException("Notification ICCID length");StringBuilder id=new StringBuilder();for(int i=0;i<b.length;i++){int lo=b[i]&15,hi=(b[i]>>>4)&15;if(lo>9)throw new SecurityException("Invalid notification ICCID");id.append(lo);if(hi<10)id.append(hi);else if(hi!=15||i!=b.length-1)throw new SecurityException("Invalid notification ICCID");}return id.toString();}
 static List<Entry> decode(byte[] bytes){
  List<Entry> result=new ArrayList<>();Set<Long> sequences=new HashSet<>();
  for(NotificationCodec.Node metadata:NotificationCodec.root(bytes,0xbf28).one(0xa0).children()){
   if(metadata.tag!=0xbf2f||result.size()>=128)throw new SecurityException("Notification metadata bounds");
   long seq=NotificationCodec.number(metadata.one(0x80));if(!sequences.add(seq))throw new SecurityException("Duplicate notification sequence");
   byte[] op=metadata.one(0x81).value();if(op.length!=2||op[0]<0||op[0]>7||((op[1]&255)&((1<<op[0])-1))!=0)throw new SecurityException("Notification operation encoding");
   String host=ActivationCode.validateHost(new String(metadata.one(0x0c).value(),StandardCharsets.UTF_8));String id="";
   for(NotificationCodec.Node child:metadata.children())if(child.tag==0x5a){if(!id.isEmpty())throw new SecurityException("Duplicate notification ICCID");id=iccid(child.value());}
   result.add(new Entry(seq,id,host,op[1]&255));
  }
  return Collections.unmodifiableList(result);
 }
 static List<Entry> list(String reader,String identity)throws Exception{
  SystemHalAdapter.requireShell();FiveberReaderSelector.Target before=FiveberReaderSelector.discover();
  if(!reader.equals(before.reader)||!identity.equals(before.identityHash))throw new SecurityException("Notification inventory card changed");
  SecureElementTransport hal=null;int channel=-1;byte[] complete=null;
  try{
   hal=SystemHalAdapter.connectForNotificationMetadata(reader);channel=hal.openLogicalChannel(TelephonyChannelCodec.unhex(TelephonyChannelCodec.ISDR),(byte)0);
   CardReadApduPolicy policy=new CardReadApduPolicy(channel,false,true);ByteArrayOutputStream all=new ByteArrayOutputStream();
   byte[] command=new byte[]{(byte)(0x80|channel),(byte)0xe2,(byte)0x91,0,3,(byte)0xbf,0x28,0};
   for(int frame=0;frame<24;frame++){
    policy.authorize(command);byte[] response=hal.transmit(command);policy.acceptResponse(response);all.write(response,0,response.length-2);int sw=response[response.length-2]&255;
    if(sw==0x61){command=new byte[]{(byte)(0x80|channel),(byte)0xc0,0,0,response[response.length-1]};continue;}
    if(sw!=0x90||response[response.length-1]!=0)throw new SecurityException("Notification metadata rejected");complete=all.toByteArray();break;
   }
   if(complete==null)throw new SecurityException("Notification metadata continuation limit");
  }finally{if(channel>0&&hal!=null)try{hal.closeChannel(channel);}catch(Exception failure){throw new SecureElementFailure(SecureElementFailure.Reason.CHANNEL_CLEANUP_FAILED);}}
  FiveberReaderSelector.Target after=FiveberReaderSelector.discover();if(!reader.equals(after.reader)||!identity.equals(after.identityHash))throw new SecurityException("Card changed after notification inventory");
  return decode(complete);
 }
 private NotificationMetadataInventory(){}
}

package dev.sid.fiveberinspector;

/** Shell transport: read-only by default; explicitly scoped lifecycle, ES10b or notification modes. */
final class TelephonySecureElementTransport implements SecureElementTransport {
 private final Object phone,token;
 private final Class<?> api,requestApi;
 private final int slot,port;
 private int channel=-1;
 private CardReadApduPolicy policy;
 private TelephonyLifecyclePolicy lifecycle;
 private DownloadSessionPolicy download;
 private TelephonyNotificationPolicy notification;
 private int expectedLe=-1;
 private boolean outstanding;
 private boolean opened;
 private boolean notificationMetadata;
 TelephonySecureElementTransport(Object phone,Class<?> api,Class<?> requestApi,int slot,int port,Object token){
  this.phone=phone;this.api=api;this.requestApi=requestApi;this.slot=slot;this.port=port;this.token=token;
 }
 TelephonySecureElementTransport(Object phone,Class<?> api,Class<?> requestApi,int slot,int port,Object token,String id,String nickname){
  this(phone,api,requestApi,slot,port,token,ProfileLifecycleGuard.Operation.RENAME,id,nickname);
 }
 TelephonySecureElementTransport(Object phone,Class<?> api,Class<?> requestApi,int slot,int port,Object token,ProfileLifecycleGuard.Operation operation,String id,String nickname){
  this(phone,api,requestApi,slot,port,token);lifecycle=new TelephonyLifecyclePolicy(operation,id,nickname);
 }
 TelephonySecureElementTransport(Object phone,Class<?> api,Class<?> requestApi,int slot,int port,Object token,DownloadSessionPolicy download){
  this(phone,api,requestApi,slot,port,token);if(download==null)throw new SecurityException("Download policy required");this.download=download;
 }
 TelephonySecureElementTransport(Object phone,Class<?> api,Class<?> requestApi,int slot,int port,Object token,long sequence){
  this(phone,api,requestApi,slot,port,token,sequence,false);
 }
 TelephonySecureElementTransport(Object phone,Class<?> api,Class<?> requestApi,int slot,int port,Object token,long sequence,boolean acknowledgedRemoval){
  this(phone,api,requestApi,slot,port,token);notification=new TelephonyNotificationPolicy(sequence,acknowledgedRemoval);
 }
 void bindDownloadTransaction(String id){if(download==null)throw new SecurityException("Wrong protocol");download.bindTransaction(id);}
 void authorizeDownloadHttp(String operation){if(download==null)throw new SecurityException("Wrong protocol");download.authorizeHttp(operation);}
 void downloadHttpCompleted(String operation,int status){if(download==null)throw new SecurityException("Wrong protocol");download.httpCompleted(operation,status);}
 void authorizeCancellationHttp(String id){if(download==null)throw new SecurityException("Wrong protocol");download.authorizeCancellationHttp(id);}
 void acknowledgeNotification(){if(notification==null)throw new SecurityException("Wrong protocol");notification.acknowledged();}
 TelephonySecureElementTransport(Object phone,Class<?> api,Class<?> requestApi,int slot,int port,Object token,boolean notificationMetadata){
  this(phone,api,requestApi,slot,port,token);this.notificationMetadata=notificationMetadata;
 }
 private Object request()throws Exception{
  Object request=requestApi.getConstructor().newInstance();
  requestApi.getField("subId").setInt(request,-1);
  requestApi.getField("slotIndex").setInt(request,slot);requestApi.getField("portIndex").setInt(request,port);
  requestApi.getField("callingPackage").set(request,"com.android.shell");
  requestApi.getField("binder").set(request,token);
  return request;
 }
 public boolean isCardPresent() {return true;} // Inventoried active slot; SELECT and GetEID verify the card.
 public int openLogicalChannel(byte[] aid,byte p2)throws Exception{
  if(channel>0||p2!=0||((lifecycle!=null||download!=null||notification!=null)&&opened))throw new SecurityException("Unexpected logical channel open");
  Object request=request();requestApi.getField("aid").set(request,TelephonyChannelCodec.aid(aid));
  requestApi.getField("p2").setInt(request,0);
  Object result=api.getMethod("iccOpenLogicalChannel",requestApi).invoke(phone,request);
  if(result==null)throw new IllegalStateException("No channel result");
  int status=(Integer)result.getClass().getMethod("getStatus").invoke(result);
  if(status!=1)throw new java.util.NoSuchElementException("ISD-R channel unavailable");
  channel=(Integer)result.getClass().getMethod("getChannel").invoke(result);
  if(channel<1||channel>3){
   if(channel>0)closeChannel(channel);
   throw new SecurityException("Unsupported logical channel");
  }
  opened=true;policy=new CardReadApduPolicy(channel,true,notificationMetadata);
  return channel;
 }
 public byte[] transmit(byte[] command)throws Exception{
  if(channel<1||policy==null)throw new SecurityException("No owned channel");
  if(outstanding)throw new SecurityException("Previous transmission result unknown");
  if(command==null||command.length<5)throw new SecurityException("Malformed command");
  if((command[1]&255)==0xC0&&(expectedLe<0||command.length!=5||(command[4]&255)!=expectedLe))throw new SecurityException("Unbound response continuation");
  if(lifecycle!=null)lifecycle.authorize(command,channel);
  else if(download!=null)download.authorize(command,channel);
  else if(notification!=null)notification.authorize(command,channel);
  else policy.authorize(command);
  // Every protocol consumes authorization before Binder; uncertain sends are not retried.
  outstanding=true;int lc=command[4]&255;
  if(command.length!=5&&command.length!=5+lc)throw new SecurityException("Unsupported APDU shape");
  String result=(String)api.getMethod("iccTransmitApduLogicalChannelByPort",int.class,int.class,int.class,int.class,int.class,int.class,int.class,int.class,String.class)
   .invoke(phone,slot,port,channel,command[0]&0xFC,command[1]&255,command[2]&255,command[3]&255,lc,TelephonyChannelCodec.hex(command,5));
  byte[] response=TelephonyChannelCodec.unhex(result);
  expectedLe=(response[response.length-2]&255)==0x61?response[response.length-1]&255:-1;
  if(lifecycle!=null)lifecycle.acceptResponse(response);
  else if(download!=null)download.acceptResponse(command,channel,response);
  else if(notification!=null)notification.acceptResponse(response);
  else policy.acceptResponse(response);
  outstanding=false;return response;
 }
 public void closeChannel(int number)throws Exception{
  if(channel<1)return;
  if(number!=channel)throw new SecurityException("Foreign logical channel");
  Object request=request();requestApi.getField("channel").setInt(request,channel);
  if(!(Boolean)api.getMethod("iccCloseLogicalChannel",requestApi).invoke(phone,request))
   throw new SecureElementFailure(SecureElementFailure.Reason.CHANNEL_CLEANUP_FAILED);
  channel=-1;policy=null;
 }
}

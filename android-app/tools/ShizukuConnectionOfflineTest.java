package dev.sid.fiveberinspector;

import android.os.*;
import rikka.shizuku.Shizuku;

/** Executes the production connection state machine with controlled Shizuku/Binder adapters. */
public final class ShizukuConnectionOfflineTest {
 static int checks;
 static void check(boolean ok,String message){checks++;if(!ok)throw new AssertionError(message);}
 static final class Events implements ShizukuConnection.Listener {
  int starting,connected,failed,message;
  public void starting(){starting++;}
  public void connected(){connected++;}
  public void failed(int message,CompatibilityDiagnostics.Failure reason){failed++;this.message=message;}
 }
 static ShizukuConnection fresh(Events events){Shizuku.reset();return new ShizukuConnection(new android.content.Context(),events);}
 static class Bootstrap extends Binder implements IShizukuBootstrap {
  final boolean publish,result;
  Bootstrap(boolean publish,boolean result){this.publish=publish;this.result=result;}
  @Override public String getInterfaceDescriptor(){return IShizukuBootstrap.Stub.DESCRIPTOR;}
  public boolean connect(){if(publish)publish();return result;}
 }
 static void publish(){
  CompanionBinderProvider.beginManagementBootstrap();
  CompanionBinderProvider provider=new CompanionBinderProvider();
  Bundle payload=provider.call(CompanionBinderProvider.CHALLENGE_METHOD,null,new Bundle());
  payload.putString("apkSha256","expected-apk-sha");payload.putString("connectionMethod","shizuku");
  payload.putBinder("managementBinder",new Binder());
  provider.call(CompanionBinderProvider.MANAGEMENT_METHOD,null,payload);
 }
 static void clear(){IProfileService service=CompanionBinderProvider.management();if(service!=null)((Binder)service.asBinder()).alive=false;}
 static void settle(Events e)throws Exception {
  long until=System.nanoTime()+java.util.concurrent.TimeUnit.SECONDS.toNanos(3);
  while(e.failed+e.connected==0&&System.nanoTime()<until){Handler.drain();Thread.sleep(5);}
  Handler.drain();check(e.failed+e.connected==1,"Exactly one terminal callback");
 }
 public static void main(String[] args)throws Exception {
  Events e=new Events();ShizukuConnection c=fresh(e);Shizuku.running=false;c.start();
  check(e.message==R.string.shizuku_not_running&&!c.busy()&&Shizuku.binds==0,"Stopped Shizuku cannot bind");c.close();
  e=new Events();c=fresh(e);Shizuku.version=11;c.start();check(e.message==R.string.shizuku_update,"Old API rejected");c.close();
  e=new Events();c=fresh(e);Shizuku.uid=0;c.start();check(e.message==R.string.shizuku_shell_required&&Shizuku.binds==0,"Root never bypasses Shell policy");c.close();
  e=new Events();c=fresh(e);Shizuku.permission=-1;Shizuku.rationale=true;c.start();
  check(e.message==R.string.shizuku_denied&&Shizuku.requests==0,"Permanent denial does not loop prompts");c.close();

  e=new Events();c=fresh(e);Shizuku.permission=-1;c.start();int oldCode=Shizuku.requestCode;c.start();
  check(c.busy()&&Shizuku.requests==1&&Shizuku.binds==0,"One permission request per attempt");
  Shizuku.permissions.get(0).onRequestPermissionResult(oldCode,-1);check(e.failed==1&&!c.busy(),"Denial ends attempt");
  c.start();int nextCode=Shizuku.requestCode;check(nextCode!=oldCode,"Retries have distinct permission request IDs");
  Shizuku.permissions.get(0).onRequestPermissionResult(oldCode,0);check(Shizuku.binds==0,"Stale grant cannot authorize retry");
  Shizuku.permission=0;Shizuku.permissions.get(0).onRequestPermissionResult(nextCode,0);
  check(Shizuku.binds==1&&e.starting==1,"Grant binds exactly one bootstrap");c.close();
  check(Shizuku.received.isEmpty()&&Shizuku.dead.isEmpty()&&Shizuku.permissions.isEmpty(),"Activity close unregisters every listener");
  check(Shizuku.detached==1&&Shizuku.removed==1,"Callbacks detach and bootstrap is removed");

  e=new Events();c=fresh(e);c.start();Shizuku.connection.onServiceConnected(null,new Bootstrap(false,true));settle(e);
  check(e.connected==0&&e.failed==1,"Bootstrap success without authenticated management Binder is rejected");c.close();
  e=new Events();c=fresh(e);c.start();Shizuku.connection.onServiceConnected(null,new Binder());settle(e);
  check(e.connected==0,"Wrong bootstrap interface rejected");c.close();
  e=new Events();c=fresh(e);c.start();Shizuku.connection.onServiceConnected(null,new Bootstrap(true,true));settle(e);
  check(e.connected==1&&CompanionBinderProvider.usesShizuku(),"Verified Shizuku handoff connects");c.close();clear();
  e=new Events();c=fresh(e);c.start();Shizuku.connection.onServiceConnected(null,new Bootstrap(true,false));settle(e);
  check(e.connected==1,"Authenticated late connection wins over status-client failure");c.close();

  e=new Events();c=fresh(e);c.start();Shizuku.connection.onServiceConnected(null,new Bootstrap(false,true));settle(e);
  check(e.connected==0&&e.failed==1,"Old live Binder cannot claim successful recovery reconnect");c.close();clear();
  e=new Events();c=fresh(e);c.start();android.content.ServiceConnection stale=Shizuku.connection;Handler.expire();
  check(e.message==R.string.shizuku_timeout&&!c.busy(),"Binding has a bounded timeout");
  stale.onServiceConnected(null,new Bootstrap(true,true));Handler.drain();check(CompanionBinderProvider.management()==null,"Stale service callback cannot launch bootstrap");c.close();
  e=new Events();c=fresh(e);Shizuku.permission=-1;c.start();Shizuku.dead.get(0).onBinderDead();
  check(e.message==R.string.shizuku_not_running&&!c.busy(),"Binder death ends authorization attempt");c.close();
  e=new Events();c=fresh(e);c.start();stale=Shizuku.connection;c.close();stale.onServiceConnected(null,new Bootstrap(true,true));Handler.drain();
  check(e.connected==0&&e.failed==0&&CompanionBinderProvider.management()==null,"Destroyed Activity cannot receive a connection or submit bootstrap");
  System.out.println("PASS "+checks+" Shizuku authorization/lifecycle assertions (production state machine; simulated Android/Shizuku)");
  System.exit(0);
 }
}

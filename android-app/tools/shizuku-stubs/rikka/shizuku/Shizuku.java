package rikka.shizuku;
import android.content.*;
import android.os.*;
import java.util.*;
public class Shizuku {
 public interface OnBinderReceivedListener {void onBinderReceived();}
 public interface OnBinderDeadListener {void onBinderDead();}
 public interface OnRequestPermissionResultListener {void onRequestPermissionResult(int code,int result);}
 public static boolean running=true,rationale=false;
 public static int version=13,uid=2000,permission=0,requests,requestCode,binds,removed,detached;
 public static ServiceConnection connection;
 public static final List<OnBinderReceivedListener> received=new ArrayList<>();
 public static final List<OnBinderDeadListener> dead=new ArrayList<>();
 public static final List<OnRequestPermissionResultListener> permissions=new ArrayList<>();
 public static void reset(){running=true;rationale=false;version=13;uid=2000;permission=0;requests=binds=removed=detached=0;connection=null;}
 public static boolean pingBinder(){return running;}
 public static boolean isPreV11(){return version<11;}
 public static int getVersion(){return version;}
 public static int getUid(){return uid;}
 public static int checkSelfPermission(){return permission;}
 public static boolean shouldShowRequestPermissionRationale(){return rationale;}
 public static void requestPermission(int code){requests++;requestCode=code;}
 public static void addBinderReceivedListenerSticky(OnBinderReceivedListener listener,Handler handler){received.add(listener);}
 public static void addBinderDeadListener(OnBinderDeadListener listener,Handler handler){dead.add(listener);}
 public static void addRequestPermissionResultListener(OnRequestPermissionResultListener listener,Handler handler){permissions.add(listener);}
 public static void removeBinderReceivedListener(OnBinderReceivedListener listener){received.remove(listener);}
 public static void removeBinderDeadListener(OnBinderDeadListener listener){dead.remove(listener);}
 public static void removeRequestPermissionResultListener(OnRequestPermissionResultListener listener){permissions.remove(listener);}
 public static void bindUserService(UserServiceArgs args,ServiceConnection conn){binds++;connection=conn;}
 public static void unbindUserService(UserServiceArgs args,ServiceConnection conn,boolean remove){if(remove)removed++;else detached++;}
 public static class UserServiceArgs {
  public UserServiceArgs(ComponentName name){}
  public UserServiceArgs tag(String tag){return this;}
  public UserServiceArgs processNameSuffix(String suffix){return this;}
  public UserServiceArgs version(int version){return this;}
  public UserServiceArgs daemon(boolean daemon){if(daemon)throw new AssertionError("Bootstrap must follow app lifetime");return this;}
  public UserServiceArgs debuggable(boolean debug){return this;}
 }
}

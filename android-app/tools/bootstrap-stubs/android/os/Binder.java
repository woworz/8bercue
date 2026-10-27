package android.os;
public class Binder implements IBinder {public static int uid=2000;public static int getCallingUid(){return uid;}public boolean alive=true;public boolean pingBinder(){return alive;}public String getInterfaceDescriptor()throws RemoteException{return "profiles";}public void linkToDeath(Runnable r,int f)throws RemoteException{}}

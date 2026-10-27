package android.os;
public interface IBinder {boolean pingBinder(); String getInterfaceDescriptor() throws RemoteException;void linkToDeath(Runnable r,int flags)throws RemoteException;}

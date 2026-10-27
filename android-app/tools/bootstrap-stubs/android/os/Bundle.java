package android.os;
public class Bundle extends java.util.HashMap<String,Object> {
 public Bundle(){} public Bundle(Bundle b){super(b);} public void putString(String k,String v){put(k,v);} public String getString(String k){return (String)get(k);}
 public void putInt(String k,int v){put(k,v);} public int getInt(String k){return containsKey(k)?(Integer)get(k):0;}
 public void putBoolean(String k,boolean v){put(k,v);} public boolean getBoolean(String k){return Boolean.TRUE.equals(get(k));}
 public void putBinder(String k,IBinder v){put(k,v);} public IBinder getBinder(String k){return (IBinder)get(k);}
}

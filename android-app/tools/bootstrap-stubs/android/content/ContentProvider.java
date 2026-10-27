package android.content;
public abstract class ContentProvider {
 public Context getContext(){return new Context();}public abstract boolean onCreate();public android.os.Bundle call(String m,String a,android.os.Bundle e){return null;}
 public abstract android.database.Cursor query(android.net.Uri u,String[] p,String s,String[] a,String o);public abstract String getType(android.net.Uri u);
 public abstract android.net.Uri insert(android.net.Uri u,ContentValues v);public abstract int delete(android.net.Uri u,String s,String[] a);public abstract int update(android.net.Uri u,ContentValues v,String s,String[] a);
}

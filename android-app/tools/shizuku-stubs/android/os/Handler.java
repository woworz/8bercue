package android.os;
import java.util.*;
import java.util.concurrent.*;
public class Handler {
 private static final Queue<Runnable> posted=new ConcurrentLinkedQueue<>();
 private static final List<Runnable> delayed=new ArrayList<>();
 public Handler(Looper looper){}
 public boolean post(Runnable work){posted.add(work);return true;}
 public boolean postDelayed(Runnable work,long delay){delayed.add(work);return true;}
 public void removeCallbacks(Runnable work){delayed.remove(work);posted.remove(work);}
 public static void drain(){Runnable work;while((work=posted.poll())!=null)work.run();}
 public static void expire(){List<Runnable> copy=new ArrayList<>(delayed);delayed.clear();for(Runnable work:copy)work.run();drain();}
}

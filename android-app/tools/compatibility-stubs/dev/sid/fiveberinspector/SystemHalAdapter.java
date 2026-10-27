package dev.sid.fiveberinspector;
import java.util.*;
/** Test-only registry. It is never included in the application source set. */
final class SystemHalAdapter {
 static final Map<String,SecureElementTransport> transports=new LinkedHashMap<>();
 static final Map<String,SecureElementTransport> fallback=new LinkedHashMap<>();
 static void requireShell(){if(android.os.Process.myUid()!=2000)throw new SecurityException("Shell required");}
 static SecureElementTransport connectForNotificationMetadata(String reader){return connect(reader);}
 static int fallbackCalls;
 static final Map<CompatibilityReport.Backend,SecureElementFailure.Reason> failures=new EnumMap<>(CompatibilityReport.Backend.class);
 static final List<CompatibilityReport.Backend> order=new ArrayList<>();
 static List<String> readers(CompatibilityReport.Backend backend){
  order.add(backend);if(failures.containsKey(backend))throw new SecureElementFailure(failures.get(backend));
  if(backend==CompatibilityReport.Backend.TELEPHONY)return telephonyReaders();
  List<String> result=new ArrayList<>();
  for(String reader:transports.keySet()){
   CompatibilityReport.Backend kind=reader.startsWith("omapi.")?CompatibilityReport.Backend.OMAPI:reader.startsWith("hidl.")?CompatibilityReport.Backend.HAL_HIDL:CompatibilityReport.Backend.HAL_AIDL;
   if(kind==backend)result.add(reader);
  }
  return result;
 }
 static SecureElementTransport notification,removal;
 static SecureElementTransport connectForNotification(String reader,long sequence){return notification;}
 static SecureElementTransport connectForNotificationRemoval(String reader,long sequence){return removal;}
 static List<String> telephonyReaders(){fallbackCalls++;return new ArrayList<>(fallback.keySet());}
 static List<String> readers(){return new ArrayList<>(transports.keySet());}
 static SecureElementTransport connect(String reader){return transports.containsKey(reader)?transports.get(reader):fallback.get(reader);}
}

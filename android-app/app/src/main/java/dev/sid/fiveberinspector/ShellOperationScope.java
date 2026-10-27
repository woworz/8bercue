package dev.sid.fiveberinspector;
import java.io.*;
import java.nio.file.*;
import java.util.*;
/** Shell-owned, expiring restrictions; an invalid existing scope always fails closed. */
final class ShellOperationScope {
 private static final Path FILE=Paths.get("/data/local/tmp/8bercue-operation-scope/scope.properties");
 private static OwnerOperationScope read()throws Exception{
  if(!Files.exists(FILE,LinkOption.NOFOLLOW_LINKS))return null;
  if(android.os.Process.myUid()!=2000||Files.isSymbolicLink(FILE)||!FILE.toFile().getCanonicalFile().equals(FILE.toFile())||Files.size(FILE)>4096)throw new SecurityException("Unsafe owner scope");
  android.system.StructStat stat=android.system.Os.lstat(FILE.toString());
  if(stat.st_uid!=2000||(stat.st_mode&0777)!=0600)throw new SecurityException("Invalid owner scope permissions");
  Properties config=new Properties();try(InputStream in=Files.newInputStream(FILE,LinkOption.NOFOLLOW_LINKS)){config.load(in);}
  long now=System.currentTimeMillis(),created=Long.parseLong(config.getProperty("createdAt")),expires=Long.parseLong(config.getProperty("expiresAt"));
  if(now<created||now>=expires||expires<=created||expires-created>7200000||!IntentDigests.of(android.os.Build.FINGERPRINT).equals(config.getProperty("device")))throw new SecurityException("Owner scope expired or device changed");
  return new OwnerOperationScope(config);
 }
 static void validateMutation(String card,ProfileLifecycleGuard.Operation op,List<ProfileLifecycleGuard.Row> rows,String target)throws Exception{OwnerOperationScope scope=read();if(scope!=null)scope.mutation(card,op,rows,target);}
 static void validateDownload(String card,List<ProfileLifecycleGuard.Row> rows,String activation)throws Exception{OwnerOperationScope scope=read();if(scope!=null)scope.download(card,rows,activation);}
 private ShellOperationScope(){}
}

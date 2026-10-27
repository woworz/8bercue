package dev.sid.fiveberinspector;

import java.util.*;

/** Fixed-scope recovery policy. Never terminates a native card worker or another app. */
final class RecoveryProcessGuard {
 static final String NAME="8bercue-management-v1";
 static final class Worker {
  final int pid,uid,parent; final String name,exe,classpath,start;
  Worker(int pid,int uid,int parent,String name,String exe,String classpath,String start){
   this.pid=pid;this.uid=uid;this.parent=parent;this.name=name;this.exe=exe;this.classpath=classpath;this.start=start;
  }
  boolean management(){return NAME.equals(name);}
  boolean nativeCard(){return "lpac".equals(name)||exe.startsWith("/data/local/tmp/fiveber-lpac-lab/");}
  boolean same(Worker w){return w!=null&&pid==w.pid&&uid==w.uid&&parent==w.parent&&name.equals(w.name)&&exe.equals(w.exe)&&classpath.equals(w.classpath)&&start.equals(w.start);}
  void validate(){
   if(pid<2||uid!=2000||!management()||!("/system/bin/app_process64".equals(exe)||"/system/bin/app_process32".equals(exe))
     ||!ConnectionCommand.validApk(classpath)||!start.matches("[0-9]{1,20}"))throw new SecurityException("RECOVERY_IDENTITY_UNVERIFIED");
  }
 }
 interface Host {
  List<Worker> snapshot() throws Exception;
  void terminate(Worker expected) throws Exception;
  void pause() throws Exception;
 }
 static String[] processRow(String line){
  String[] fields=line.trim().split("\\s+",4);
  if(fields.length!=4)throw new SecurityException("RECOVERY_IDENTITY_UNVERIFIED");
  if(!fields[0].equals("UID"))for(int i=0;i<3;i++)if(!fields[i].matches("[0-9]{1,10}"))throw new SecurityException("RECOVERY_IDENTITY_UNVERIFIED");
  return fields;
 }
 static void quiescent(List<Worker> workers,int self){
  for(Worker w:workers)if(w.uid==2000&&(w.nativeCard()||(w.management()&&w.pid!=self)))
   throw new SecurityException("RECOVERY_WORKER_ACTIVE");
 }
 static int retire(Host host,int self)throws Exception{
  List<Worker> first=host.snapshot(),peers=new ArrayList<>();
  for(Worker w:first){
   if(w.uid==2000&&w.nativeCard())throw new SecurityException("RECOVERY_WORKER_ACTIVE");
   if(w.management()&&w.pid!=self){w.validate();peers.add(w);}
  }
  // Validate the complete set and every child before sending any signal.
  for(Worker w:peers)for(Worker child:first)if(child.parent==w.pid)throw new SecurityException("RECOVERY_WORKER_ACTIVE");
  for(Worker w:peers){
   List<Worker> now=host.snapshot();Worker current=null;
   for(Worker candidate:now){
    if(candidate.uid==2000&&candidate.nativeCard())throw new SecurityException("RECOVERY_WORKER_ACTIVE");
    if(candidate.parent==w.pid)throw new SecurityException("RECOVERY_WORKER_ACTIVE");
    if(candidate.pid==w.pid)current=candidate;
   }
   if(current==null)continue;
   if(!w.same(current))throw new SecurityException("RECOVERY_IDENTITY_UNVERIFIED");
   host.terminate(w);
  }
  for(int i=0;i<20;i++){
   try{quiescent(host.snapshot(),self);host.pause();quiescent(host.snapshot(),self);return peers.size();}
   catch(SecurityException busy){if(!"RECOVERY_WORKER_ACTIVE".equals(busy.getMessage()))throw busy;host.pause();}
  }
  throw new SecurityException("RECOVERY_WORKER_ACTIVE");
 }
 private RecoveryProcessGuard(){}
}

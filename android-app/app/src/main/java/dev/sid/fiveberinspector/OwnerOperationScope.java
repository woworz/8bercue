package dev.sid.fiveberinspector;
import java.util.*;
/** Optional owner restrictions. This only reduces existing management permissions. */
final class OwnerOperationScope {
 private final Properties config;
 OwnerOperationScope(Properties properties){
  config=new Properties();config.putAll(properties);
  if(!"1".equals(config.getProperty("schema"))||!digest(config.getProperty("card"))||!digest(config.getProperty("active"))||!digest(config.getProperty("activation")))throw new SecurityException("Incomplete owner scope");
  Set<String> original=originals();if(!original.contains(config.getProperty("active"))||original.isEmpty()||original.size()>16)throw new SecurityException("Invalid protected inventory");
  for(String id:original)if(!digest(id)||!digest(config.getProperty("meta."+id))||!config.getProperty("state."+id,"").matches("true|false"))throw new SecurityException("Incomplete protected profile");
  String disposable=config.getProperty("disposable","");if(!disposable.isEmpty()&&(!digest(disposable)||original.contains(disposable)))throw new SecurityException("Disposable profile overlaps protected inventory");
 }
 private static boolean digest(String s){return s!=null&&s.matches("[a-f0-9]{64}");}
 private Set<String> originals(){String value=config.getProperty("baseline","");Set<String> ids=new HashSet<>(Arrays.asList(value.split(",",-1)));if(ids.size()!=value.split(",",-1).length)throw new SecurityException("Duplicate protected profile");return ids;}
 static String metadata(ProfileLifecycleGuard.Row row)throws Exception{return ProfileLifecycleGuard.fingerprint(List.of(new ProfileLifecycleGuard.Row(row.id,false,row.nickname,row.provider,row.name)));}
 private void baseline(String card,List<ProfileLifecycleGuard.Row> rows,boolean temporarySwitch)throws Exception{
  if(!config.getProperty("card").equals(card))throw new SecurityException("Owner scope card mismatch");ProfileLifecycleGuard.fingerprint(rows);
  Set<String> found=new HashSet<>();
  for(ProfileLifecycleGuard.Row row:rows){String id=IntentDigests.of(row.id);if(!originals().contains(id))continue;found.add(id);
   if(!metadata(row).equals(config.getProperty("meta."+id)))throw new SecurityException("Protected profile metadata changed");
   if(!(temporarySwitch&&id.equals(config.getProperty("active")))&&row.enabled!=Boolean.parseBoolean(config.getProperty("state."+id)))throw new SecurityException("Protected profile state changed");
  }
  if(!found.equals(originals()))throw new SecurityException("Protected profile missing");
 }
 void download(String card,List<ProfileLifecycleGuard.Row> rows,String activation)throws Exception{
  baseline(card,rows,false);
  if(rows.size()!=originals().size()||!config.getProperty("disposable","").isEmpty()||!IntentDigests.of(activation).equals(config.getProperty("activation")))throw new SecurityException("Download outside owner scope");
 }
 void mutation(String card,ProfileLifecycleGuard.Operation op,List<ProfileLifecycleGuard.Row> rows,String target)throws Exception{
  boolean maySwitch="true".equals(config.getProperty("switchAllowed"));baseline(card,rows,maySwitch);
  String selected=IntentDigests.of(target),active=config.getProperty("active"),disposable=config.getProperty("disposable","");
  if(!digest(disposable)||rows.size()!=originals().size()+1)throw new SecurityException("No verified disposable profile");
  for(ProfileLifecycleGuard.Row row:rows)if(!originals().contains(IntentDigests.of(row.id))&&!disposable.equals(IntentDigests.of(row.id)))throw new SecurityException("Unexpected additional profile");
  ProfileLifecycleGuard.Row activeRow=null,testRow=null;
  for(ProfileLifecycleGuard.Row row:rows){String id=IntentDigests.of(row.id);if(id.equals(active))activeRow=row;if(id.equals(disposable))testRow=row;}
  if(activeRow==null||testRow==null)throw new SecurityException("Scope profile missing");
  if(op==ProfileLifecycleGuard.Operation.ENABLE){
   if(!maySwitch||(!selected.equals(disposable)&&!selected.equals(active)))throw new SecurityException("Switch outside owner scope");
   if(selected.equals(active)&&(!testRow.enabled||activeRow.enabled))throw new SecurityException("Restoration state changed");
   if(selected.equals(disposable)&&(!activeRow.enabled||testRow.enabled))throw new SecurityException("Test switch state changed");
  }else if((op==ProfileLifecycleGuard.Operation.DELETE||op==ProfileLifecycleGuard.Operation.RENAME)&&selected.equals(disposable)){
   if(!activeRow.enabled||testRow.enabled)throw new SecurityException("Restore protected active profile first");
  }else throw new SecurityException("Protected profile operation blocked");
 }
}

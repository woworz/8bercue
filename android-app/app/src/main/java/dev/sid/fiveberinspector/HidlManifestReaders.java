package dev.sid.fiveberinspector;

import java.io.StringReader;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

/** Instance names from platform-merged VINTF declarations; declarations never prove access. */
final class HidlManifestReaders {
 private static List<Element> children(Element parent,String tag){
  List<Element> result=new ArrayList<>();
  for(Node node=parent.getFirstChild();node!=null;node=node.getNextSibling())
   if(node instanceof Element&&node.getNodeName().equals(tag))result.add((Element)node);
  return result;
 }
 private static String text(Element parent,String tag){List<Element> nodes=children(parent,tag);return nodes.size()==1?nodes.get(0).getTextContent().trim():"";}
 static List<String> parse(String[] documents)throws Exception{
  if(documents==null||documents.length>16)throw new SecureElementFailure(SecureElementFailure.Reason.INVALID_READER_INVENTORY);
  Set<String> names=new TreeSet<>();int bytes=0;
  for(String xml:documents){
   if(xml==null||(bytes+=xml.length())>1048576||xml.contains("<!DOCTYPE")||xml.contains("<!ENTITY"))throw new SecureElementFailure(SecureElementFailure.Reason.INVALID_READER_INVENTORY);
   Element root;
   try{root=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml))).getDocumentElement();}
   catch(Exception malformed){throw new SecureElementFailure(SecureElementFailure.Reason.INVALID_READER_INVENTORY);}
   if(!root.getNodeName().equals("manifest"))continue; // Compatibility matrices are requirements, not devices.
   for(Element hal:children(root,"hal")){
    if(!text(hal,"name").equals("android.hardware.secure_element")||(!hal.getAttribute("format").isEmpty()&&!hal.getAttribute("format").equals("hidl")))continue;
    for(Element fq:children(hal,"fqname")){
     String value=fq.getTextContent().trim();
     if(!value.matches("@1\\.[0-9]+::ISecureElement/[A-Za-z0-9_.-]{1,64}"))throw new SecureElementFailure(SecureElementFailure.Reason.SYSTEM_INTERFACE_UNAVAILABLE);
     names.add(value.substring(value.indexOf('/')+1));
    }
    if(!children(hal,"interface").isEmpty()){
     boolean compatible=false;for(Element version:children(hal,"version"))compatible|=version.getTextContent().trim().matches("1\\.[0-9]+(?:-[0-9]+)?");
     if(!compatible)throw new SecureElementFailure(SecureElementFailure.Reason.SYSTEM_INTERFACE_UNAVAILABLE);
     for(Element api:children(hal,"interface"))if(text(api,"name").equals("ISecureElement"))for(Element instance:children(api,"instance"))names.add(instance.getTextContent().trim());
    }
    if(names.size()>32)throw new SecureElementFailure(SecureElementFailure.Reason.INVALID_READER_INVENTORY);
   }
  }
  return ReaderDescriptor.hidlInstances(new ArrayList<>(names));
 }
 private HidlManifestReaders(){}
}

package dev.sid.fiveberinspector;
import java.util.*;
/** Opaque, one-use, short-lived choices bound to complete verified card identities. */
final class CardChoicePlan {
 static final long TTL=90_000L;
 static final class Choice {
  final String token;final FiveberReaderSelector.Target target;
  Choice(String token,FiveberReaderSelector.Target target){this.token=token;this.target=target;}
 }
 final List<Choice> choices;private final long created;private boolean consumed;
 CardChoicePlan(List<FiveberReaderSelector.Target> targets,long now){
  if(targets==null||targets.isEmpty()||targets.size()>16||now<0)throw new SecurityException("Invalid card choices");
  created=now;List<Choice> entries=new ArrayList<>();Set<String> readers=new HashSet<>();
  for(FiveberReaderSelector.Target target:targets){
   if(target==null||!ReaderDescriptor.valid(target.reader)||!target.identityHash.matches("[a-f0-9]{64}")||!readers.add(target.reader))throw new SecurityException("Invalid card choice identity");
   entries.add(new Choice(OperationTokens.create(),target));
  }
  choices=Collections.unmodifiableList(entries);
 }
 synchronized FiveberReaderSelector.Target consume(String token,long now){
  if(consumed)throw new SecurityException("Card choice already used");consumed=true;
  if(now<created||now-created>TTL||!OperationTokens.isValid(token))throw new SecurityException("Card choice expired or invalid");
  for(Choice choice:choices)if(choice.token.equals(token))return choice.target;
  throw new SecurityException("Card choice not offered");
 }
}

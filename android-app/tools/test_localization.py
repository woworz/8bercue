#!/usr/bin/env python3
"""Offline Android resource/format/error-coverage contract checks; no device success claim."""
from pathlib import Path
import re,xml.etree.ElementTree as ET
root=Path(__file__).resolve().parents[1]
res=root/'app/src/main/res';java=root/'app/src/main/java/dev/sid/fiveberinspector'
checks=0
def check(ok,message):
 global checks
 checks+=1
 if not ok:raise AssertionError(message)
def load(path):
 nodes=ET.parse(path).getroot().findall('string');names=[n.attrib['name'] for n in nodes]
 check(len(names)==len(set(names)),f'No duplicate resources: {path.name}')
 return {n.attrib['name']:n.text or '' for n in nodes}
en=load(res/'values/strings.xml');zh=load(res/'values-b+zh+Hans/strings.xml')
check(set(en)==set(zh),'English and Simplified Chinese have identical resource coverage')
for key in en:
 check(bool(zh[key].strip('" ')),f'Nonempty Chinese translation: {key}')
 def placeholders(text):return sorted(re.findall(r'%(\d+)\$([-+0 #]*\d*(?:\.\d+)?[sdf])',text))
 check(placeholders(en[key])==placeholders(zh[key]),f'Format positions/types match: {key}')
 check(en[key].count('\\n')==zh[key].count('\\n'),f'Preserve message boundaries: {key}')
for p in java.glob('*.java'):
 for key in re.findall(r'R\.string\.(\w+)',p.read_text()):check(key in en,f'Resource exists for {p.name}: {key}')
known=set(re.findall(r'case ("(?:[^"\\]|\\.)*"):',(java/'UiMessages.java').read_text()))
for name in ['SecureElementFailure','NotificationPresentation','ConnectionFeedback']:
 source=(java/(name+'.java')).read_text()
 if name=='ConnectionFeedback':source=source.split('static String message(',1)[1].split('static String countdown(',1)[0]
 for literal in re.findall(r'return ("(?:[^"\\]|\\.)*")\s*;',source):
  if literal!='""':check(literal in known,f'Translate owned backend message from {name}')
for literal in re.findall(r'new IllegalArgumentException\(("(?:[^"\\]|\\.)*")\)',(java/'ActivationCode.java').read_text()):check(literal in known,'Activation validation has translated recovery text')
locales=ET.parse(res/'xml/locales_config.xml').getroot();ns='{http://schemas.android.com/apk/res/android}'
check([n.attrib[ns+'name'] for n in locales]==['en','zh-Hans'],'Supported per-app language list')
check('legal/privacy-zh-Hans.txt' in (java/'MainActivity.java').read_text(),'Chinese privacy page is selected explicitly')
check((root/'app/src/main/assets/legal/privacy-zh-Hans.txt').stat().st_size>1000,'Complete local translated privacy document')
print(f'PASS {checks} localization resource/placeholder/error coverage assertions ({len(en)} bilingual strings; offline only)')

# Protocol operation IDs must never be translated when dispatching writes.
ui=(java/"MainActivity.java").read_text()
nonlabels=re.sub(r"private String operationLabel\(String op\).*?private String countdown", "private String countdown", ui, flags=re.S)
assert "s(R.string.operation_" not in nonlabels, "Localized operation ID entered protocol dispatch"
assert 'return "Card connected"' in ui, "Canonical status key must remain independent of locale"

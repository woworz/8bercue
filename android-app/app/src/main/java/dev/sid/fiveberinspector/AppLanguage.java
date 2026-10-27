package dev.sid.fiveberinspector;

import android.app.Activity;
import android.app.LocaleManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;
import java.util.Locale;

/** Per-app display preference only. Never changes system locale, protocol, or card state. */
// Both languages are packaged in the APK; bundle language splitting is also disabled.
@android.annotation.SuppressLint("AppBundleLocaleChanges")
final class AppLanguage {
  static Context wrap(Context base){
    if(Build.VERSION.SDK_INT>=33)return base; // Framework handles per-app locales on Android 13+.
    String tag=base.getSharedPreferences("display",0).getString("language","");
    if(tag.isEmpty())return base;
    Configuration config=new Configuration(base.getResources().getConfiguration());
    config.setLocales(LocaleList.forLanguageTags(tag));return base.createConfigurationContext(config);
  }
  static String selected(Context context){
    if(Build.VERSION.SDK_INT>=33){LocaleManager manager=context.getSystemService(LocaleManager.class);return manager==null?"":manager.getApplicationLocales().toLanguageTags();}
    return context.getSharedPreferences("display",0).getString("language","");
  }
  static boolean isChinese(Context context){return "zh".equals(context.getResources().getConfiguration().getLocales().get(0).getLanguage());}
  static String label(Context context){String value=selected(context);return context.getString(value.isEmpty()?R.string.language_system:value.startsWith("zh")?R.string.language_chinese:R.string.language_english);}
  static void select(Activity activity,String tag){
    if(!tag.isEmpty()&&!tag.equals("en")&&!tag.equals("zh-Hans"))throw new IllegalArgumentException("Unsupported app language");
    if(Build.VERSION.SDK_INT>=33){
      LocaleManager manager=activity.getSystemService(LocaleManager.class);
      if(manager!=null)manager.setApplicationLocales(LocaleList.forLanguageTags(tag));
    }else{activity.getSharedPreferences("display",0).edit().putString("language",tag).apply();activity.recreate();}
  }
  private AppLanguage(){}
}

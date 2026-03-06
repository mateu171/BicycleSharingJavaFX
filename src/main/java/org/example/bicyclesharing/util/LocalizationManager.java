package org.example.bicyclesharing.util;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LocalizationManager {

  private static Locale locale = new  Locale("uk");
  private static Preferences prefs = Preferences.userNodeForPackage(LocalizationManager.class);
  private static final String LANG_KEY = "lang";

  public static ResourceBundle getBundle()
  {
       return ResourceBundle.getBundle(
      "org.example.bicyclesharing.localization.bundle",
      getLocale()
  );
  }

  public static Locale getLocale()
  {
    locale = Locale.forLanguageTag(prefs.get(LANG_KEY,"uk"));
    return locale;
  }

  public static void setLocale(String tag)
  {
    locale = Locale.forLanguageTag(tag);
    prefs.put(LANG_KEY,tag);
  }

  public static String getStringByKey(String key)
  {
    return getBundle().getString(key);
  }

}

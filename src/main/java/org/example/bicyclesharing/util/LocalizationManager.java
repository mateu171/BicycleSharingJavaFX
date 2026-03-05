package org.example.bicyclesharing.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {

  private static Locale locale = Locale.forLanguageTag("ua");
  public static ResourceBundle getBundle()
  {
       return ResourceBundle.getBundle(
      "org.example.bicyclesharing.localization.bundle",
      locale
  );
  }

  public static void setLocale(String tag)
  {
   locale = Locale.forLanguageTag(tag);
  }

  public static String getStringByKey(String key)
  {
    return getBundle().getString(key);
  }
}

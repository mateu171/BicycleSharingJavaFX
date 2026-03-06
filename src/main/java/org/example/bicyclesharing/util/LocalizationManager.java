package org.example.bicyclesharing.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LocalizationManager {

  private static Locale locale = new Locale("uk");
  private static Preferences prefs = Preferences.userNodeForPackage(LocalizationManager.class);
  private static final String LANG_KEY = "lang";

  private static ResourceBundle bundle = ResourceBundle.getBundle(
      "org.example.bicyclesharing.localization.bundle",
      locale
  );

  private static final Map<String, StringProperty> strings = new HashMap<>();

  public static void initKeys(String... keys) {
    for (String key : keys) {
      strings.put(key, new SimpleStringProperty(getStringByKey(key)));
    }
  }

  public static StringProperty getStringProperty(String key) {
    return strings.get(key);
  }

  public static void setLocale(String tag) {
    locale = Locale.forLanguageTag(tag);
    prefs.put(LANG_KEY, tag);
    bundle = ResourceBundle.getBundle("org.example.bicyclesharing.localization.bundle", locale);
    strings.forEach((k, prop) -> prop.set(bundle.getString(k)));
  }

  public static String getStringByKey(String key) {
    return bundle.getString(key);
  }

  public static Locale getLocale() {
    return locale;
  }
}
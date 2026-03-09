package org.example.bicyclesharing.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LocalizationManager {

  private static Preferences prefs = Preferences.userNodeForPackage(LocalizationManager.class);
  private static final String LANG_KEY = "lang";

  private static final ObjectProperty<Locale> locale =
      new SimpleObjectProperty<>(Locale.forLanguageTag(prefs.get(LANG_KEY,"en")));

  private static ResourceBundle bundle = ResourceBundle.getBundle(
      "org.example.bicyclesharing.localization.bundle",
      locale.get()
  );
  private static final Map<String, StringProperty> strings = new HashMap<>();

  public static StringProperty getStringProperty(String key) {

    if (!strings.containsKey(key)) {
      strings.put(key, new SimpleStringProperty(getStringByKey(key)));
    }

    return strings.get(key);
  }

  public static void setLocale(String tag) {
    Locale newLocale = Locale.forLanguageTag(tag);

    prefs.put(LANG_KEY, tag);

    ResourceBundle.clearCache();

    bundle = ResourceBundle.getBundle(
        "org.example.bicyclesharing.localization.bundle",
        newLocale
    );

    strings.forEach((k, prop) -> prop.set(bundle.getString(k)));

    locale.set(newLocale);
  }

  public static String getStringByKey(String key) {
    if (key == null || key.isEmpty()) return "";
    return bundle.getString(key);
  }

  public static Locale getLocale() {
    return locale.get();
  }

  public static ObjectProperty<Locale> localeProperty() {
    return locale;
  }
}
package org.example.bicyclesharing.util;

import java.util.prefs.Preferences;

public class ThemeManager {

  private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
  private static final String THEME_KEY = "theme";

  public static String getSavedTheme()
  {
    String savedTheme = prefs.get(THEME_KEY, "light");
    if ("dark".equals(savedTheme)) {
          return "/org/example/bicyclesharing/css/dark-theme.css";
    } else {
          return "/org/example/bicyclesharing/css/style.css";
    }
  }

  public static void saveTheme(String tag)
  {
    prefs.put(THEME_KEY,tag);
  }

  public static String getCurrentTheme()
  {
    return prefs.get(THEME_KEY,"light");
  }
}

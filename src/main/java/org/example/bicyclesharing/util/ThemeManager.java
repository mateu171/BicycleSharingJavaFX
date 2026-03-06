package org.example.bicyclesharing.util;

import java.util.prefs.Preferences;
import org.example.bicyclesharing.controller.SettingsController;

public class ThemeManager {

  private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
  private static final String THEME_KEY = "theme";

}

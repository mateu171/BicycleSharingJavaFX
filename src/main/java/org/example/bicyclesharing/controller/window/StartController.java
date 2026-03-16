package org.example.bicyclesharing.controller.window;

import java.util.Locale;
import org.example.bicyclesharing.util.LocalizationManager;

public class StartController extends BaseWindowController{
  public void showRegister() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/RegisterView.fxml");
  }

  public void closeWindow()
  {
    windowUtil.close();
  }

  public void minimizeWindow()
  {
    windowUtil.minimize();
  }

  public void switchLanguage() {

    Locale current = LocalizationManager.getLocale();

    if (current.getLanguage().equals("uk")) {
      LocalizationManager.setLocale("en");
    } else {
      LocalizationManager.setLocale("uk");
    }
  }
}

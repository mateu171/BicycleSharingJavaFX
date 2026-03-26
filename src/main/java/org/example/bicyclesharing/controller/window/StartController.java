package org.example.bicyclesharing.controller.window;

import java.util.Locale;
import javafx.fxml.FXML;
import org.example.bicyclesharing.util.LocalizationManager;

public class StartController extends BaseWindowController{
  private void showRegister() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/RegisterView.fxml");
  }

  @Override
  @FXML
  protected void initialize()
  {
    super.initialize();
    showRegister();
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

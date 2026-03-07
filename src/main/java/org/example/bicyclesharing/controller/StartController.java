package org.example.bicyclesharing.controller;

import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.bicyclesharing.services.NavigationService;
import org.example.bicyclesharing.util.LocalizationManager;

public class StartController {

  @FXML
  private StackPane contentPane;
  @FXML
  private Button closeButton;
  @FXML
  private Button minimizeButton;

  private NavigationService navigation;

  public void showLogin() {
    navigation.load("/org/example/bicyclesharing/presentation/LoginView.fxml");
  }

  public void showRegister() {
    navigation.load("/org/example/bicyclesharing/presentation/RegisterView.fxml");
  }

  @FXML
  private void initialize() {
    navigation = new NavigationService(contentPane);

    LocalizationManager.initKeys(
        "login.title",
        "login.login",
        "login.password",
        "login.noAccount",
        "login.signIn",
        "register.title",
        "register.login",
        "register.password",
        "register.email",
        "register.confirmEmail",
        "register.confirmButton",
        "register.alreadyAccount",
        "register.code",
        "register.signUp",
        "profile.title",
        "profile.login",
        "profile.password",
        "profile.email",
        "profile.update",
        "profile.success",
        "balance.title",
        "balance.yourBalance",
        "balance.chooseAmount",
        "balance.topUpAmount",
        "balance.topUp",
        "balance.recharged",
        "menu.map",
        "menu.profile",
        "menu.balance",
        "menu.history",
        "menu.transactions",
        "menu.guide",
        "menu.settings",
        "history.title",
        "history.bike",
        "history.start",
        "history.end",
        "history.active",
        "history.total",
        "transactions.title",
        "transaction.top_up",
        "transaction.rental_fee",
        "settings.title",
        "settings.language",
        "settings.theme",
        "settings.light",
        "settings.dark",
        "settings.save",
        "error.login.empty",
        "error.login.length",
        "error.password.empty",
        "error.password.length",
        "error.email.empty",
        "error.email.invalid",
        "error.login.exists",
        "error.email.send_failed",
        "error.email.code_invalid",
        "error.auth.invalid",
        "lang.uk",
        "lang.en"
    );
  }

  public void closeWindow()
  {
    Stage stage = (Stage) closeButton.getScene().getWindow();
    stage.close();
  }

  public void minimizeWindow()
  {
    Stage stage = (Stage) minimizeButton.getScene().getWindow();
    stage.setIconified(true);
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

package org.example.bicyclesharing.controller;

import java.io.IOException;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.bicyclesharing.domain.Impl.User;

public class MainMenuController {

  @FXML
  private Button closeButton;
  @FXML
  private Button minimizeButton;
  @FXML
  private Button fullSizeButton;
  @FXML
  private VBox sidebar;
  @FXML
  private StackPane contentPane;

  private User currentUser;

  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
  }

  @FXML
  public void initialize()
  {
    double expandedWidth = 180;
    double collapsedWidth = 60;

    sidebar.setOnMouseEntered(e -> {
      Timeline expand = new Timeline(
          new KeyFrame(Duration.millis(300),
              new KeyValue(sidebar.prefWidthProperty(), expandedWidth, Interpolator.EASE_BOTH)
          )
      );
      expand.play();
    });
    sidebar.setOnMouseExited(e -> {
      Timeline collapse = new Timeline(
          new KeyFrame(Duration.millis(300),
              new KeyValue(sidebar.prefWidthProperty(), collapsedWidth, Interpolator.EASE_BOTH)
          )
      );
      collapse.play();
    });
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

  public void fullSize()
  {
    Stage stage = (Stage) fullSizeButton.getScene().getWindow();
    stage.setMaximized(!stage.isMaximized());
  }

  public void onShowProfile()
  {
    load("/org/example/bicyclesharing/presentation/ProfileView.fxml");
  }
  public void onShowBalance() {
    load("/org/example/bicyclesharing/presentation/BalanceView.fxml");
  }
  public void onShowRideHistory()
  {
    load("/org/example/bicyclesharing/presentation/RideHistoryView.fxml");
  }
  public void onShowTransactions()
  {
    load("/org/example/bicyclesharing/presentation/TransactionView.fxml");
  }

  public void onShowSettings()
  {
    load("/org/example/bicyclesharing/presentation/SettingsView.fxml");
  }

  private void load(String path) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
      Parent view = loader.load();

      Object controller = loader.getController();
      if (controller instanceof ProfileController profileController) {
        profileController.setCurrentUser(currentUser);
      }
      if (controller instanceof BalanceController balanceController) {
        balanceController.setCurrentUser(currentUser);
      }
      if (controller instanceof RideHistoryController rideHistoryController) {
        rideHistoryController.setCurrentUser(currentUser);
      }
      if (controller instanceof TransactionController transactionController) {
        transactionController.setCurrentUser(currentUser);
      }

      contentPane.getChildren().setAll(view);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}

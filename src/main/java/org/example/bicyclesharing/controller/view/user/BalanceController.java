package org.example.bicyclesharing.controller.view.user;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.user.BalanceViewModel;

public class BalanceController extends BaseController {

  @FXML private Label title;
  @FXML private Label balanceLabel;
  @FXML private Label yourBalance;
  @FXML private Label selectTopUp;
  @FXML private Label topUp;
  @FXML private TextField topUpField;
  @FXML private HBox topUpButtons;
  @FXML private Button replenishButton;

  private User currentUser;
  private BalanceViewModel viewModel;

  @Override
  public void setCurrentUser(User user) {
    this.currentUser = user;
    this.viewModel = new BalanceViewModel(AppConfig.userService(),AppConfig.transactionService(), currentUser);
    balanceLabel.textProperty().bind(viewModel.balanceProperty().asString("%.2f₴"));
    title.textProperty().bind(viewModel.titleText);
    yourBalance.textProperty().bind(viewModel.yourBalanceText);
    replenishButton.textProperty().bind(viewModel.topUpButtonText);
    selectTopUp.textProperty().bind(viewModel.chooseAmountText);
    topUp.textProperty().bind(viewModel.topUpAmountText);
    topUpField.textProperty().bindBidirectional(viewModel.amount);
  }

  @FXML
  public void initialize() {
    for (var node : topUpButtons.getChildren()) {
      if (node instanceof Button btn) {
        btn.setOnAction(event -> {
          topUpButtons.getChildren().forEach(n -> n.getStyleClass().remove("menu-button-active"));
          btn.getStyleClass().add("menu-button-active");

          switch (btn.getText()) {
            case "100₴" -> topUpField.setText("100");
            case "200₴" -> topUpField.setText("200");
            case "500₴" -> topUpField.setText("500");
          }
        });
      }
    }

    topUpField.setTextFormatter(new TextFormatter<>(change -> {
      String newText = change.getControlNewText();
      if (newText.matches("\\d*")) {
        return change;
      }
      return null;
    }));

    replenishButton.setOnAction(event -> {
        viewModel.addBalance();
    });
  }

}
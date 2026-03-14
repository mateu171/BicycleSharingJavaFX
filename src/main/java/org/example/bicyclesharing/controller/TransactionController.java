package org.example.bicyclesharing.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import org.example.bicyclesharing.domain.Impl.Transaction;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.TransactionType;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.TransactionViewModel;

import java.time.format.DateTimeFormatter;

public class TransactionController extends BaseController {

  @FXML
  private ListView<Transaction> transactionListView;
  @FXML
  private Label title;

  private TransactionViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    this.viewModel = new TransactionViewModel(AppConfig.transactionService(),currentUser);
    transactionListView.setItems(viewModel.getTransactions());
    setupCellFactory();
  }

  private void setupCellFactory() {
    title.textProperty().bind(viewModel.titleText);
    transactionListView.setItems(viewModel.getTransactions());
    transactionListView.getStyleClass().add("rental-list");
    transactionListView.setSelectionModel(null);

    transactionListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(Transaction item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
          setGraphic(null);
          return;
        }

        VBox card = new VBox(6);
        card.getStyleClass().add("transaction-card");

        Label title = new Label(item.getType().getLocalizedName());
        title.getStyleClass().add("transaction-title");

        Label description = new Label(LocalizationManager.getStringByKey(item.getDescription()));

        Label date = new Label(
            item.getTimestamp()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(LocalizationManager.getLocale()))
        );
        date.getStyleClass().add("transaction-date");

        Label amount = new Label();

        if (item.getType() == TransactionType.TOP_UP) {
          amount.setText("+ UAH " + item.getAmount());
          amount.getStyleClass().add("amount-positive");
        } else {
          amount.setText("- UAH " + item.getAmount());
          amount.getStyleClass().add("amount-negative");
        }

        HBox bottomRow = new HBox(amount);
        bottomRow.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(title, description, date, bottomRow);

        setGraphic(card);
      }
    });
  }

}
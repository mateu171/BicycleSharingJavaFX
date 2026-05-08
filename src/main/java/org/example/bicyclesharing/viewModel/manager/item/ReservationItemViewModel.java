package org.example.bicyclesharing.viewModel.manager.item;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Reservation;

public class ReservationItemViewModel {

  private final Reservation reservation;

  private final StringProperty customerText = new SimpleStringProperty();
  private final StringProperty bicycleText = new SimpleStringProperty();
  private final StringProperty periodText = new SimpleStringProperty();
  private final StringProperty documentText = new SimpleStringProperty();
  private final StringProperty depositText = new SimpleStringProperty();
  private final StringProperty statusText = new SimpleStringProperty();

  private final BooleanProperty canIssue = new SimpleBooleanProperty();
  private final BooleanProperty canCancel = new SimpleBooleanProperty();

  public ReservationItemViewModel(
      Reservation reservation,
      String customer,
      String bicycle,
      String period,
      String document,
      String deposit,
      String status,
      boolean canIssue,
      boolean canCancel
  ) {
    this.reservation = reservation;

    customerText.set(customer);
    bicycleText.set(bicycle);
    periodText.set(period);
    documentText.set(document);
    depositText.set(deposit);
    statusText.set(status);

    this.canIssue.set(canIssue);
    this.canCancel.set(canCancel);
  }

  public Reservation getReservation() {
    return reservation;
  }

  public StringProperty customerTextProperty() {
    return customerText;
  }

  public StringProperty bicycleTextProperty() {
    return bicycleText;
  }

  public StringProperty periodTextProperty() {
    return periodText;
  }

  public StringProperty documentTextProperty() {
    return documentText;
  }

  public StringProperty depositTextProperty() {
    return depositText;
  }

  public StringProperty statusTextProperty() {
    return statusText;
  }

  public BooleanProperty canIssueProperty() {
    return canIssue;
  }

  public BooleanProperty canCancelProperty() {
    return canCancel;
  }
}
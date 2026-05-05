package org.example.bicyclesharing.viewModel.manager.modalViewModal;

import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.util.LocalizationManager;

public class FinishRentalDialogViewModel {

  private final RentalService rentalService;
  private final Rental rental;
  private final User currentUser;

  public final StringProperty titleText =
      LocalizationManager.getStringProperty("manager.rentals.finish.title");
  public final StringProperty finishButtonText =
      LocalizationManager.getStringProperty("button.finish");
  public final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("cancel.button");

  public final StringProperty problemText =
      LocalizationManager.getStringProperty("manager.rentals.finish.problem");
  public final StringProperty technicalProblemText =
      LocalizationManager.getStringProperty("manager.rentals.finish.technical");

  public final StringProperty problemTypeLabelText =
      LocalizationManager.getStringProperty("ride.problem.type.label");
  public final StringProperty commentLabelText =
      LocalizationManager.getStringProperty("ride.problem.comment.label");

  public final BooleanProperty hasProblem = new SimpleBooleanProperty(false);
  public final BooleanProperty technicalProblem = new SimpleBooleanProperty(false);

  public final StringProperty selectedProblemType = new SimpleStringProperty("");
  public final StringProperty comment = new SimpleStringProperty("");

  public final StringProperty problemTypeError = new SimpleStringProperty("");
  public final StringProperty commentError = new SimpleStringProperty("");
  public final StringProperty generalError = new SimpleStringProperty("");

  private double finalPrice;

  public FinishRentalDialogViewModel(
      User currentUser,
      Rental rental,
      RentalService rentalService
  ) {
    this.currentUser = currentUser;
    this.rental = rental;
    this.rentalService = rentalService;
  }

  public List<String> getProblemTypes() {
    return List.of(
        LocalizationManager.getStringByKey("ride.problem.type.damage"),
        LocalizationManager.getStringByKey("ride.problem.type.brakes"),
        LocalizationManager.getStringByKey("ride.problem.type.wheel"),
        LocalizationManager.getStringByKey("ride.problem.type.other")
    );
  }

  public double getFinalPrice() {
    return finalPrice;
  }

  public boolean finishRental() {
    clearErrors();

    try {
      finalPrice = rentalService.finishRental(
          rental,
          hasProblem.get(),
          selectedProblemType.get(),
          comment.get(),
          technicalProblem.get()
      );

      return true;

    } catch (CustomEntityValidationExeption e) {
      applyValidationErrors(e);
      return false;

    } catch (BusinessException e) {
      generalError.set(LocalizationManager.getStringByKey(e.getMessage()));
      return false;
    }
  }

  private void applyValidationErrors(CustomEntityValidationExeption e) {
    e.getErrors().forEach((field, messages) -> {
      String text = messages.stream()
          .map(LocalizationManager::getStringByKey)
          .collect(Collectors.joining("\n"));

      switch (field) {
        case "problemType" -> problemTypeError.set(text);
        case "comment" -> commentError.set(text);
        default -> generalError.set(text);
      }
    });
  }

  private void clearErrors() {
    problemTypeError.set("");
    commentError.set("");
    generalError.set("");
  }
}
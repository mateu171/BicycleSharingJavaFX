package org.example.bicyclesharing.viewModel.manager.modalViewModal;

import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("manager.rentals.finish.title");

  private final StringProperty finishButtonText =
      LocalizationManager.getStringProperty("button.finish");

  private final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("cancel.button");

  private final StringProperty problemText =
      LocalizationManager.getStringProperty("manager.rentals.finish.problem");

  private final StringProperty technicalProblemText =
      LocalizationManager.getStringProperty("manager.rentals.finish.technical");

  private final StringProperty problemTypeLabelText =
      LocalizationManager.getStringProperty("ride.problem.type.label");

  private final StringProperty commentLabelText =
      LocalizationManager.getStringProperty("ride.problem.comment.label");

  private final BooleanProperty hasProblem =
      new SimpleBooleanProperty(false);

  private final BooleanProperty technicalProblem =
      new SimpleBooleanProperty(false);

  private final StringProperty selectedProblemType =
      new SimpleStringProperty("");

  private final StringProperty comment =
      new SimpleStringProperty("");

  private final StringProperty problemTypeError =
      new SimpleStringProperty("");

  private final StringProperty commentError =
      new SimpleStringProperty("");

  private final StringProperty generalError =
      new SimpleStringProperty("");

  private final DoubleProperty finalPrice =
      new SimpleDoubleProperty(0);

  private final List<String> problemTypes = List.of(
      LocalizationManager.getStringByKey("ride.problem.type.damage"),
      LocalizationManager.getStringByKey("ride.problem.type.brakes"),
      LocalizationManager.getStringByKey("ride.problem.type.wheel"),
      LocalizationManager.getStringByKey("ride.problem.type.other")
  );

  public FinishRentalDialogViewModel(
      Rental rental,
      RentalService rentalService
  ) {
    this.rental = rental;
    this.rentalService = rentalService;

    hasProblem.addListener((obs, oldVal, newVal) -> {
      if (!newVal) {
        clearProblemFields();
      }
    });

    selectedProblemType.addListener((obs, oldVal, newVal) -> problemTypeError.set(""));
    comment.addListener((obs, oldVal, newVal) -> commentError.set(""));
  }

  public boolean finishRental() {
    clearErrors();

    try {
      double price = rentalService.finishRental(
          rental,
          hasProblem.get(),
          selectedProblemType.get(),
          comment.get(),
          technicalProblem.get()
      );

      finalPrice.set(price);
      return true;

    } catch (CustomEntityValidationExeption e) {
      applyValidationErrors(e);
      return false;

    } catch (BusinessException e) {
      generalError.set(LocalizationManager.getStringByKey(e.getMessage()));
      return false;
    }
  }

  private void clearProblemFields() {
    selectedProblemType.set("");
    technicalProblem.set(false);
    comment.set("");
    problemTypeError.set("");
    commentError.set("");
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

  public List<String> getProblemTypes() {
    return problemTypes;
  }

  public double getFinalPrice() {
    return finalPrice.get();
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty finishButtonTextProperty() {
    return finishButtonText;
  }

  public StringProperty cancelButtonTextProperty() {
    return cancelButtonText;
  }

  public StringProperty problemTextProperty() {
    return problemText;
  }

  public StringProperty technicalProblemTextProperty() {
    return technicalProblemText;
  }

  public StringProperty problemTypeLabelTextProperty() {
    return problemTypeLabelText;
  }

  public StringProperty commentLabelTextProperty() {
    return commentLabelText;
  }

  public BooleanProperty hasProblemProperty() {
    return hasProblem;
  }

  public BooleanProperty technicalProblemProperty() {
    return technicalProblem;
  }

  public StringProperty selectedProblemTypeProperty() {
    return selectedProblemType;
  }

  public StringProperty commentProperty() {
    return comment;
  }

  public StringProperty problemTypeErrorProperty() {
    return problemTypeError;
  }

  public StringProperty commentErrorProperty() {
    return commentError;
  }

  public StringProperty generalErrorProperty() {
    return generalError;
  }

  public DoubleProperty finalPriceProperty() {
    return finalPrice;
  }
}
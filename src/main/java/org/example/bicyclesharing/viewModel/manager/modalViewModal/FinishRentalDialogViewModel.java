package org.example.bicyclesharing.viewModel.manager.modalViewModal;

import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.BikeIssueService;
import org.example.bicyclesharing.services.CustomerService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.util.LocalizationManager;

public class FinishRentalDialogViewModel {

  private final RentalService rentalService;
  private final CustomerService customerService;
  private final BicycleService bicycleService;
  private final BikeIssueService bikeIssueService;

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

  private double finalPrice;

  public FinishRentalDialogViewModel(
      User currentUser,
      Rental rental,
      RentalService rentalService,
      CustomerService customerService,
      BicycleService bicycleService,
      BikeIssueService bikeIssueService
  ) {
    this.currentUser = currentUser;
    this.rental = rental;
    this.rentalService = rentalService;
    this.customerService = customerService;
    this.bicycleService = bicycleService;
    this.bikeIssueService = bikeIssueService;
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

    Bicycle bicycle = bicycleService.getById(rental.getBicycleId()).orElse(null);
    Customer customer = customerService.getById(rental.getCustomerId()).orElse(null);

    if (bicycle == null) {
      throw new BusinessException("error.bicycle.not_found");
    }

    if (customer == null) {
      throw new BusinessException("error.customer.not_found");
    }

    if (rental.getEnd() != null) {
      throw new BusinessException("error.rental.already_finished");
    }

    if (hasProblem.get()) {
      if (selectedProblemType.get() == null || selectedProblemType.get().trim().isEmpty()) {
        problemTypeError.set(
            LocalizationManager.getStringByKey("ride.problem.validation.type.required")
        );
        return false;
      }

      try {
        new BikeIssue(
            rental.getId(),
            bicycle.getId(),
            selectedProblemType.get(),
            comment.get(),
            technicalProblem.get()
        );
      } catch (CustomEntityValidationExeption e) {
        e.getErrors().forEach((field, messages) -> {
          String text = messages.stream()
              .map(LocalizationManager::getStringByKey)
              .collect(Collectors.joining("\n"));

          switch (field) {
            case "problemType" -> problemTypeError.set(text);
            case "comment" -> commentError.set(text);
          }
        });
        return false;
      }
    }

    finalPrice = rentalService.finishRental(rental);

    if (hasProblem.get()) {
      bicycle.setState(StateBicycle.NEEDS_INSPECTION);
      bicycleService.update(bicycle);

      BikeIssue issue = new BikeIssue(
          rental.getId(),
          bicycle.getId(),
          selectedProblemType.get(),
          comment.get(),
          technicalProblem.get()
      );
      bikeIssueService.add(issue);
    } else {
      bicycle.setState(StateBicycle.AVAILABLE);
      bicycleService.update(bicycle);
    }

    return true;
  }

  private void clearErrors() {
    problemTypeError.set("");
    commentError.set("");
  }
}
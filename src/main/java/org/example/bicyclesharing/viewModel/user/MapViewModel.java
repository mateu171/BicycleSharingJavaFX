package org.example.bicyclesharing.viewModel.user;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class MapViewModel extends BaseViewModel {

  public final StringProperty titleText = LocalizationManager.getStringProperty("map.title");
  public final StringProperty rentButtonText = LocalizationManager.getStringProperty("button.rent");
  public final StringProperty finishButtonText = LocalizationManager.getStringProperty("button.finish");
  public final StringProperty labelPrice = LocalizationManager.getStringProperty("label.price");

  private final ObservableList<Bicycle> bicycles = FXCollections.observableArrayList();
  private final BicycleService bicycleService = AppConfig.bicycleService();
  private final RentalService rentalService = AppConfig.rentalService();
  private final Map<Bicycle, StringProperty> rentalTimeProps = new HashMap<>();
  private final Map<Bicycle, Timeline> timelines = new HashMap<>();


  public MapViewModel(User currentUser) {
    super(currentUser);
    loadBicycles();
  }

  private void loadBicycles()
  {
    bicycles.clear();
    bicycles.addAll(bicycleService.getAll());
  }

  public Rental rentBike(Bicycle bike)
  {
    Rental rental = new Rental(currentUser.getId(),bike.getId());
    return rentalService.add(rental);
  }

  public void finishRental(Bicycle bike) {
    Rental rental = rentalService.getByUserId(currentUser.getId())
        .stream()
        .filter(r -> r.getBicycleId().equals(bike.getId()) && r.getEnd() == null)
        .findFirst()
        .orElse(null);

    if (rental != null) {
      rentalService.finishRental(rental);
    }
  }

  public Rental getActiveRental(Bicycle bicycle)
  {
    return  rentalService.getByUserId(currentUser.getId())
        .stream()
        .filter(r -> r.getBicycleId().equals(bicycle.getId()) && r.getEnd() == null)
        .findFirst()
        .orElse(null);
  }

public Bicycle getBicycleById(String id)
{
  for (Bicycle bike : bicycles) {
    if (bike.getId().toString().equals(id)) {
      return bike;
    }
  }
  return null;
}

  public StringProperty getRentalDurationProperty(Bicycle bike) {
    return rentalTimeProps.computeIfAbsent(bike, b -> new SimpleStringProperty("00:00"));
  }

  public void startRentalTimer(Bicycle bike, Rental rental) {
    stopRentalTimer(bike);

    Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> {
      updateRentalDuration(bike, rental);
    }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();

    timelines.put(bike, timeline);
    updateRentalDuration(bike, rental);
  }

  public void stopRentalTimer(Bicycle bike) {
    Timeline timeline = timelines.remove(bike);
    if (timeline != null) timeline.stop();

    StringProperty prop = rentalTimeProps.get(bike);
    if (prop != null) prop.set("00:00");
  }

  private void updateRentalDuration(Bicycle bike, Rental rental) {
    if (rental == null) return;

    Duration duration = Duration.between(rental.getStart(), LocalDateTime.now());
    long seconds = duration.getSeconds();
    long mins = seconds / 60;
    long secs = seconds % 60;

    getRentalDurationProperty(bike).set(String.format("%02d:%02d", mins, secs));
  }

  public ObservableList<Bicycle> getBicycles() {
    return bicycles;
  }
}
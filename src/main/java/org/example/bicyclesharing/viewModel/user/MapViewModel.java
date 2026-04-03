package org.example.bicyclesharing.viewModel.user;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.BikeIssueService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class MapViewModel extends BaseViewModel {

  public final StringProperty titleText = LocalizationManager.getStringProperty("map.title");
  public final StringProperty rentButtonText = LocalizationManager.getStringProperty("button.rent");
  public final StringProperty finishButtonText = LocalizationManager.getStringProperty("button.finish");
  public final StringProperty labelPrice = LocalizationManager.getStringProperty("label.price");
  public final StringProperty stationTitleText = LocalizationManager.getStringProperty("map.station");

  private final ObservableList<Station> stations = FXCollections.observableArrayList();
  private final ObservableList<Bicycle> bicycleForSelectedStation = FXCollections.observableArrayList();
  private final StationService stationService = AppConfig.stationService();
  private final BicycleService bicycleService = AppConfig.bicycleService();
  private final RentalService rentalService = AppConfig.rentalService();
  private final BikeIssueService bikeIssueService = AppConfig.bikeIssueService();

  private final Map<Bicycle, StringProperty> rentalTimeProps = new HashMap<>();
  private final Map<Bicycle, Timeline> timelines = new HashMap<>();

  private Station selectedStation;

  public MapViewModel(User currentUser) {
    super(currentUser);
    loadStations();
  }

  public void loadStations()
  {
    stations.setAll(stationService.getAll());
    if(!stations.isEmpty())
    {
      selecStation(stations.get(0));
    }
  }
  public void selecStation(Station station)
  {
    selectedStation = station;
    bicycleForSelectedStation.clear();

    if(station == null)
    {
      return;
    }

    bicycleForSelectedStation.setAll(
        bicycleService.getAll().stream()
            .filter(b -> station.getId().equals(b.getStationId()))
            .toList()
    );
  }

  public ObservableList<Station> getStations()
  {
      return stations;
  }

  public ObservableList<Bicycle> getBicyclesForSelectedStation()
  {
    return bicycleForSelectedStation;
  }

  public Station getStationById(String id) {
    try {
      UUID uuid = UUID.fromString(id);
      return stations.stream()
          .filter(station -> station.getId().equals(uuid))
          .findFirst()
          .orElse(null);
    } catch (Exception e) {
      return null;
    }
  }

  public Rental rentBike(Bicycle bike)
  {
    Rental rental = new Rental(currentUser.getId(),bike.getId());
    bike.setState(StateBicycle.RENTED);
    bicycleService.update(bike);
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
      bike.setState(StateBicycle.AVAILABLE);
      bicycleService.update(bike);
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

  public void handleNegativeRideFeedback(Bicycle bike, String problemType, String comment) {
    Rental rental = getActiveOrLastRentalForBike(bike);

    if (rental == null) {
      return;
    }

    boolean isTechnicalProblem =
        "Велосипед мав несправність".equals(problemType) ||
            "Проблема з гальмами".equals(problemType) ||
            "Проблема з колесом".equals(problemType) ||
            "Проблема з сидінням".equals(problemType);

    BikeIssue issue = new BikeIssue(
        rental.getId(),
        bike.getId(),
        currentUser.getId(),
        problemType,
        comment,
        isTechnicalProblem
    );

    bikeIssueService.add(issue);

    if (isTechnicalProblem) {
      bike.setState(StateBicycle.NEEDS_INSPECTION);
      bike.setIssueId(issue.getId());
      bicycleService.update(bike);
    }
  }

  private Rental getActiveOrLastRentalForBike(Bicycle bike) {
    return rentalService.getByUserId(currentUser.getId())
        .stream()
        .filter(r -> r.getBicycleId().equals(bike.getId()))
        .sorted((r1, r2) -> r2.getStart().compareTo(r1.getStart()))
        .findFirst()
        .orElse(null);
  }
}
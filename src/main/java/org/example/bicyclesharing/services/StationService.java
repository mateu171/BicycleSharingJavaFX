package org.example.bicyclesharing.services;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.repository.Repository;
import org.example.bicyclesharing.repository.StationRepository;

public class StationService extends BaseService<Station, UUID>{

  private final StationRepository repository;
  private final BicycleService bicycleService;
  public StationService(StationRepository repository, BicycleService bicycleService) {
    this.repository = repository;
    this.bicycleService = bicycleService;
  }

  @Override
  protected Repository<Station, UUID> getRepository() {
    return repository;
  }

  public  Station getById(UUID id)
  {
    return repository.getById(id);
  }

  public List<Station> findByFilters(String search) {
    return repository.findByFilters(search);
  }

  public void deleteStation(UUID stationId) {

    Station station = getById(stationId);
    if (station == null) {
      throw new BusinessException("error.station.not_found");
    }

    executeInTransaction(() -> {
      for (UUID bicycleId : station.getBicyclesId()) {
        System.out.println("Processing bicycle: " + bicycleId);
        bicycleService.getById(bicycleId).ifPresent(bicycle -> {
          bicycle.setStationId(null);
          bicycleService.update(bicycle);
          System.out.println("Bicycle " + bicycleId + " detached");
        });
      }

      boolean deleted = repository.deleteById(stationId);

      if (!deleted) {
        throw new BusinessException("error.station.delete.failed");
      }
    });
  }
}

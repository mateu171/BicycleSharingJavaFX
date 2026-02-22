package org.example.bicyclesharing.services;

import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.repository.Repository;
import org.example.bicyclesharing.repository.StationRepository;

public class StationService extends BaseService<Station, UUID> {

  private final StationRepository stationRepository;

  public StationService(StationRepository stationRepository) {
    this.stationRepository = stationRepository;
  }

  @Override
  protected Repository<Station, UUID> getRepository() {
    return stationRepository;
  }

  public Station getByName(String name) {
    return stationRepository.findByName(name);
  }
}

package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Station;

public interface StationRepository extends Repository<Station, UUID> {
  Station getById(UUID id);
  List<Station> findByFilters(String search);
}

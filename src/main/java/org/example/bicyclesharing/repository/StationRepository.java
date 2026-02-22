package org.example.bicyclesharing.repository;

import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Station;

public interface StationRepository extends Repository<Station, UUID> {

  Station findByName(String name);
}

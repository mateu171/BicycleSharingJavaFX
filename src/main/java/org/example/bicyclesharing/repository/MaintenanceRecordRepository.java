package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;

public interface MaintenanceRecordRepository extends Repository<MaintenanceRecord, UUID>{
  public List<MaintenanceRecord> findByBicycleId(UUID bicycleId);
}

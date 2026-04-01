package org.example.bicyclesharing.services;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;
import org.example.bicyclesharing.repository.MaintenanceRecordRepository;
import org.example.bicyclesharing.repository.Repository;

public class MaintenanceRecordService extends BaseService<MaintenanceRecord, UUID>{

  private final MaintenanceRecordRepository maintenanceRecordRepository;

  public MaintenanceRecordService(MaintenanceRecordRepository maintenanceRecordRepository) {
    this.maintenanceRecordRepository = maintenanceRecordRepository;
  }

  public List<MaintenanceRecord> getByBicycleId(UUID bikeId)
  {
    return maintenanceRecordRepository.findByBicycleId(bikeId);
  }

  @Override
  protected Repository<MaintenanceRecord, UUID> getRepository() {
    return maintenanceRecordRepository;
  }
}

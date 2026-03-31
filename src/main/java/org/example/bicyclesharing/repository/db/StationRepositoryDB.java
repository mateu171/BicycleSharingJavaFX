package org.example.bicyclesharing.repository.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.repository.StationRepository;
import org.springframework.jdbc.core.RowMapper;

public class StationRepositoryDB extends BaseRepositoryDB<Station, UUID> implements StationRepository {

  @Override
  public List<Station> getByName(String name) {
    String sql = "SELECT * FROM STATIONS WHERE LOWER(name) LIKE LOWER(?)";
    return jdbcTemplate.query(sql, rowMapper(), "%" + name + "%");
  }

  @Override
  public Station getById(UUID id) {
    String sql = "SELECT * FROM STATIONS WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, rowMapper(), id.toString());
  }

  @Override
  protected String getCreateTableSQL() {
    return """
        CREATE TABLE IF NOT EXISTS STATIONS (
            id VARCHAR(36) PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            latitude DOUBLE NOT NULL,
            longitude DOUBLE NOT NULL,
            bicycles_id TEXT,
            employee_id VARCHAR(36)
        )
        """;
  }

  @Override
  protected String getTableName() {
    return "STATIONS";
  }

  @Override
  protected UUID getId(Station entity) {
    return entity.getId();
  }

  @Override
  protected RowMapper<Station> rowMapper() {
    return (rs, rowNum) -> {
      UUID id = UUID.fromString(rs.getString("id"));
      String name = rs.getString("name");
      double latitude = rs.getDouble("latitude");
      double longitude = rs.getDouble("longitude");

      String bicyclesRaw = rs.getString("bicycles_id");
      List<UUID> bicyclesId = parseUuidList(bicyclesRaw);

      String employeeRaw = rs.getString("employee_id");
      UUID employeeId = employeeRaw != null && !employeeRaw.isBlank()
          ? UUID.fromString(employeeRaw)
          : null;

      return Station.fromDatabase(
          id,
          name,
          latitude,
          longitude,
          bicyclesId,
          employeeId
      );
    };
  }

  @Override
  protected Object[] getInsertValues(Station entity) {
    return new Object[] {
        entity.getId().toString(),
        entity.getName(),
        entity.getLatitude(),
        entity.getLongitude(),
        toCsv(entity.getBicyclesId()),
        entity.getEmployeeId() != null ? entity.getEmployeeId().toString() : null
    };
  }

  @Override
  protected Object[] getUpdateValues(Station entity) {
    return new Object[] {
        entity.getName(),
        entity.getLatitude(),
        entity.getLongitude(),
        toCsv(entity.getBicyclesId()),
        entity.getEmployeeId() != null ? entity.getEmployeeId().toString() : null,
        entity.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[] {
        "name",
        "latitude",
        "longitude",
        "bicycles_id",
        "employee_id"
    };
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }

  private String toCsv(List<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return "";
    }

    return ids.stream()
        .map(UUID::toString)
        .collect(Collectors.joining(","));
  }

  private List<UUID> parseUuidList(String raw) {
    if (raw == null || raw.isBlank()) {
      return new ArrayList<>();
    }

    return Arrays.stream(raw.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(UUID::fromString)
        .collect(Collectors.toList());
  }
}
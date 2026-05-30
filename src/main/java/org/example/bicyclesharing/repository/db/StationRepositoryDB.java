package org.example.bicyclesharing.repository.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.repository.StationRepository;
import org.springframework.jdbc.core.RowMapper;

public class StationRepositoryDB extends BaseRepositoryDB<Station, UUID> implements StationRepository {

  public StationRepositoryDB() {
    super();
  }

  public StationRepositoryDB(DataSource dataSource) {
    super(dataSource);
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
                manager_id VARCHAR(36),
                is_deleted BOOLEAN DEFAULT FALSE NOT NULL
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
      UUID managerId = UUID.fromString(rs.getString("manager_id"));

      String bicyclesRaw = rs.getString("bicycles_id");
      List<UUID> bicyclesId = parseUuidList(bicyclesRaw);

      return Station.fromDatabase(
          id,
          name,
          latitude,
          longitude,
          bicyclesId,
          managerId
          );
    };
  }

  @Override
  protected Object[] getInsertValues(Station entity) {
    return new Object[]{
        entity.getId().toString(),
        entity.getName(),
        entity.getLatitude(),
        entity.getLongitude(),
        toCsv(entity.getBicyclesId()),
        entity.getManagerId(),
        false
    };
  }

  @Override
  protected Object[] getUpdateValues(Station entity) {
    return new Object[]{
        entity.getName(),
        entity.getLatitude(),
        entity.getLongitude(),
        toCsv(entity.getBicyclesId()),
        entity.getManagerId(),
        entity.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[]{"name", "latitude", "longitude", "bicycles_id","manager_id"};
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


  @Override
  public Station getById(UUID id) {
    String sql = "SELECT * FROM STATIONS WHERE is_deleted = FALSE AND id = ?";
    return jdbcTemplate.queryForObject(sql, rowMapper(), id.toString());
  }

  @Override
  public List<Station> findByFilters(String search) {
    QueryData query = new QueryData("SELECT * FROM STATIONS WHERE is_deleted = FALSE");

    query.addLikeCondition("name", search);
    query.addOrderBy("name", "ASC");

    return jdbcTemplate.query(query.getSql(), rowMapper(), query.getParams());
  }

  @Override
  public Station findByManagerId(UUID managerId) {
    String sql = """
      SELECT * FROM STATIONS
      WHERE manager_id = ?
        AND is_deleted = FALSE
      """;

    List<Station> result = jdbcTemplate.query(sql, rowMapper(), managerId.toString());

    return result.isEmpty() ? null : result.get(0);
  }
}
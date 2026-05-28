package org.example.bicyclesharing.repository.db;

import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.example.bicyclesharing.dto.LatestInspectionInfo;
import org.example.bicyclesharing.repository.BicycleRepository;
import org.springframework.jdbc.core.RowMapper;

public class BicycleRepositoryDB
    extends BaseRepositoryDB<Bicycle, UUID>
    implements BicycleRepository {

  public BicycleRepositoryDB() {
    super();
  }

  public BicycleRepositoryDB(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected String getTableName() {
    return "bicycles";
  }

  @Override
  protected String getCreateTableSQL() {
    return """
            CREATE TABLE IF NOT EXISTS bicycles (
                id VARCHAR(36) PRIMARY KEY,
                model VARCHAR(255) NOT NULL,
                type_bicycle VARCHAR(50) NOT NULL,
                state VARCHAR(50) NOT NULL,
                price_per_minute DOUBLE NOT NULL,
                station_id VARCHAR(36),
                image_path VARCHAR(255),
                is_deleted BOOLEAN DEFAULT FALSE NOT NULL
            )
            """;
  }

  @Override
  protected UUID getId(Bicycle entity) {
    return entity.getId();
  }

  @Override
  protected RowMapper<Bicycle> rowMapper() {
    return (rs, rowNum) -> {

      String stationId = rs.getString("station_id");

      return Bicycle.fromDatabase(
          UUID.fromString(rs.getString("id")),
          rs.getString("model"),
          TypeBicycle.valueOf(rs.getString("type_bicycle")),
          StateBicycle.valueOf(rs.getString("state")),
          rs.getDouble("price_per_minute"),
          stationId != null ? UUID.fromString(stationId) : null,
          rs.getString("image_path")
      );
    };
  }

  @Override
  protected Object[] getInsertValues(Bicycle entity) {
    return new Object[]{
        entity.getId().toString(),
        entity.getModel(),
        entity.getTypeBicycle().name(),
        entity.getState().name(),
        entity.getPricePerMinute(),
        entity.getStationId() != null ? entity.getStationId().toString() : null,
        entity.getImagePath(),
        false
    };
  }

  @Override
  protected Object[] getUpdateValues(Bicycle entity) {
    return new Object[]{
        entity.getModel(),
        entity.getTypeBicycle().name(),
        entity.getState().name(),
        entity.getPricePerMinute(),
        entity.getStationId() != null
            ? entity.getStationId().toString()
            : null,
        entity.getImagePath(),
        entity.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[]{
        "model",
        "type_bicycle",
        "state",
        "price_per_minute",
        "station_id",
        "image_path"
    };
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }

  @Override
  public List<Bicycle> findByFilters(String search, StateBicycle state) {
    QueryData query = new QueryData("SELECT * FROM bicycles WHERE is_deleted = FALSE");
    query.addLikeCondition("model", search);
    if (state != null) {
      query.addEqualsCondition("state", state.name());
    }
    query.addOrderBy("model", "ASC");
    return jdbcTemplate.query(query.getSql(), rowMapper(), query.getParams());
  }

  @Override
  public List<Bicycle> findByState(StateBicycle state) {
    return jdbcTemplate.query(
        "SELECT * FROM bicycles WHERE is_deleted = FALSE AND state = ?",
        rowMapper(),
        state.name()
    );
  }

  @Override
  public long countByState(StateBicycle state) {
    Long result = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM bicycles WHERE is_deleted = FALSE AND state = ?",
        Long.class,
        state.name()
    );
    return result == null ? 0 : result;
  }

  @Override
  public LatestInspectionInfo getLatestInspectionInfo() {
    String sql = """
            SELECT model 
            FROM bicycles 
            WHERE is_deleted = FALSE AND state = ? 
            ORDER BY model ASC LIMIT 1
            """;

    List<LatestInspectionInfo> list = jdbcTemplate.query(
        sql,
        (rs, rowNum) -> new LatestInspectionInfo(rs.getString("model")),
        StateBicycle.NEEDS_INSPECTION.name()
    );

    return list.isEmpty() ? null : list.getFirst();
  }
}
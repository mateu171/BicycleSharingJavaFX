package org.example.bicyclesharing.repository.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.repository.RentalRepository;

public class RentalRepositoryDB extends BaseRepositoryDB<Rental, UUID> implements RentalRepository {

  @Override
  public List<Rental> findByUserId(UUID id) {
    return findAll().stream()
        .filter(r -> r.getUserId().equals(id))
        .collect(Collectors.toList());
  }

  @Override
  protected String getTableName() {
    return "RENTALS";
  }

  @Override
  protected UUID getId(Rental entity) {
    return entity.getId();
  }

  @Override
  protected Rental mapRow(ResultSet rs) throws SQLException {
    Rental rental = new Rental();
    rental.setId(UUID.fromString(rs.getString("id")));
    rental.setUserId(UUID.fromString(rs.getString("userId")));
    rental.setBicycleId(UUID.fromString(rs.getString("bicycleId")));
    rental.setStart(rs.getTimestamp("start").toLocalDateTime());

    Timestamp endTs = rs.getTimestamp("endTime");
    if (endTs != null) {
      rental.setEnd(endTs.toLocalDateTime());
    }

    rental.setTotalCost(rs.getDouble("totalCost"));
    return rental;
  }

  @Override
  protected Object[] getInsertValues(Rental entity) {
    return new Object[]{
        entity.getId(),
        entity.getUserId(),
        entity.getBicycleId(),
        Timestamp.valueOf(entity.getStart()),
        entity.getEnd() != null ? Timestamp.valueOf(entity.getEnd()) : null,
        entity.getTotalCost()
    };
  }

  @Override
  protected Object[] getUpdateValues(Rental entity) {
    return new Object[]{
        entity.getUserId(),
        entity.getBicycleId(),
        Timestamp.valueOf(entity.getStart()),
        entity.getEnd() != null ? Timestamp.valueOf(entity.getEnd()) : null,
        entity.getTotalCost(),
        entity.getId()
    };
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[]{
        "userId",
        "bicycleId",
        "start",
        "endTime",
        "totalCost"
    };
  }
}
package org.example.bicyclesharing.repository.db;

import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRepositoryDB extends BaseRepositoryDB<User, UUID> implements UserRepository {

  @Override
  protected String getTableName() {
    return "USERS";
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }

  @Override
  protected UUID getId(User entity) {
    return entity.getId();
  }

  @Override
  protected User mapRow(ResultSet rs) throws SQLException {
    return User.fromDatabase(
        UUID.fromString(rs.getString("id")),
        rs.getString("login"),
        rs.getString("password"),
        rs.getString("email"),
        Role.valueOf(rs.getString("role"))
    );
  }

  @Override
  protected Object[] getInsertValues(User entity) {
    if (entity == null) return new Object[]{"id","login","password","email","role"};
    return new Object[]{entity.getId(), entity.getLogin(), entity.getPassword(), entity.getEmail(), entity.getRole().name()};
  }

  @Override
  protected Object[] getUpdateValues(User entity) {
    return new Object[]{entity.getLogin(), entity.getPassword(), entity.getEmail(), entity.getRole().name(), entity.getId()};
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[]{"login","password","email","role"};
  }

  @Override
  public User findByLogin(String login) {
    return findAll().stream()
        .filter(u -> u.getLogin().equals(login))
        .findFirst().orElse(null);
  }
}
package org.example.bicyclesharing.repository.db;

import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.repository.UserRepository;
import org.springframework.jdbc.core.RowMapper;

public class UserRepositoryDB extends BaseRepositoryDB<User, UUID> implements UserRepository {

  public UserRepositoryDB() {
    super();
  }

  public UserRepositoryDB(DataSource dataSource) {
    super(dataSource);
  }

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
  protected RowMapper<User> rowMapper() {
    return (rs, rowNum) -> User.fromDatabase(
        UUID.fromString(rs.getString("id")),
        rs.getString("login"),
        rs.getString("password"),
        rs.getString("email"),
        Role.valueOf(rs.getString("role")),
        rs.getString("image_path")
    );
  }

  @Override
  protected Object[] getInsertValues(User entity) {
    return new Object[]{
        entity.getId().toString(),
        entity.getLogin(),
        entity.getHashedPassword(),
        entity.getEmail(),
        entity.getRole().name(),
        entity.getImagePath(),
        false
    };
  }

  @Override
  protected Object[] getUpdateValues(User entity) {
    return new Object[]{
        entity.getLogin(),
        entity.getHashedPassword(),
        entity.getEmail(),
        entity.getRole().name(),
        entity.getImagePath(),
        entity.isDeleted(),
        entity.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[]{
        "login",
        "password",
        "email",
        "role",
        "image_path",
        "is_deleted"
    };
  }

  @Override
  protected String getCreateTableSQL() {
    return "CREATE TABLE IF NOT EXISTS USERS (" +
        "id VARCHAR(36) PRIMARY KEY," +
        "login VARCHAR(255) NOT NULL UNIQUE," +
        "password VARCHAR(255) NOT NULL," +
        "email VARCHAR(255) NOT NULL," +
        "role VARCHAR(50) NOT NULL," +
        "image_path VARCHAR(255)," +
        "is_deleted BOOLEAN DEFAULT FALSE NOT NULL" +
        ")";
  }


  @Override
  public User findByLogin(String login) {
    String sql = "SELECT * FROM USERS WHERE is_deleted = FALSE AND login = ?";
    List<User> users = jdbcTemplate.query(sql, rowMapper(), login);
    return users.isEmpty() ? null : users.get(0);
  }

  @Override
  public boolean existsByLoginActive(String login) {
    String sql = """
            SELECT COUNT(*) FROM USERS 
            WHERE is_deleted = FALSE AND login = ?
            """;
    Long count = jdbcTemplate.queryForObject(sql, Long.class, login);
    return count != null && count > 0;
  }

  @Override
  public List<User> findByFilters(String search, Role role) {
    QueryData query = new QueryData("SELECT * FROM USERS WHERE is_deleted = FALSE");

    if (search != null && !search.isBlank()) {
      String pattern = "%" + search.trim() + "%";
      query.addCondition("(LOWER(login) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?))",
          pattern, pattern);
    }

    if (role != null) {
      query.addEqualsCondition("role", role.name());
    }

    query.addOrderBy("login", "ASC");

    return jdbcTemplate.query(query.getSql(), rowMapper(), query.getParams());
  }
}
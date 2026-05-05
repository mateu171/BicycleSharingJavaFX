package org.example.bicyclesharing.repository.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserRepositoryDBTest extends AbstractRepositoryTest{
  private  UserRepositoryDB repository;

  @BeforeEach
  void setUpRepository()
  {
    repository = new UserRepositoryDB(dataSource);
  }

  @Test
  void save_shouldInsertNewUser_whenValidData()
  {
    User user = User.fromDatabase(
        UUID.randomUUID(),
        "admin",
        "hashed_password",
        "admin@gmail.com",
        Role.ADMIN,
        "admin.png"
    );

    repository.save(user);

    Optional<User> loaded = repository.findById(user.getId());
    assertThat(loaded).isPresent();
    assertThat(loaded.get().getLogin()).isEqualTo("admin");
    assertThat(loaded.get().getHashedPassword()).isEqualTo("hashed_password");
    assertThat(loaded.get().getEmail()).isEqualTo("admin@gmail.com");
    assertThat(loaded.get().getRole()).isEqualTo(Role.ADMIN);
    assertThat(loaded.get().getImagePath()).isEqualTo("admin.png");
    assertThat(countRowsInTable("USERS")).isEqualTo(1);
  }

  @Test
  void save_shouldThrowException_whenDuplicateLogin()
  {
    User user1 = User.fromDatabase(UUID.randomUUID(),
    "admin",
        "password1",
        "admin1@gmail.com",
        Role.ADMIN,
        null);

    User user2 = User.fromDatabase(
        UUID.randomUUID(),
        "admin",
        "password2",
        "admin2@gmail.com",
        Role.ADMIN,
        null
    );

    repository.save(user1);
    assertThatThrownBy(() -> repository.save(user2)).hasMessageContaining("Unique");
  }

  @Test
  void findById_shouldReturnUser_whenExists()
  {
    User user = User.fromDatabase(
        UUID.randomUUID(),
        "Олександер",
        "hashed_password",
        "akimsergij702@gmail.com",
        Role.MECHANIC,
        null
    );

    repository.save(user);

    Optional<User> result = repository.findById(user.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getLogin()).isEqualTo("Олександер");
  }

  @Test
  void findById_shouldReturnEmpty_whenNotExists()
  {
    UUID nonExistentId = UUID.randomUUID();

    Optional<User> result = repository.findById(nonExistentId);

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_shouldReturnAllUsers_whenMultipleUsersExist()
  {
    User admin = User.fromDatabase(
        UUID.randomUUID(),
        "admin",
        "password",
        "admin@gmail.com",
        Role.ADMIN,
        null
    );

    User mechanic = User.fromDatabase(
        UUID.randomUUID(),
        "Олександер",
        "password",
        "client@gmail.com",
        Role.MECHANIC,
        null
    );

    repository.save(admin);
    repository.save(mechanic);

    List<User> users = repository.findAll();

    assertThat(users).hasSize(2);

    assertThat(users).extracting(User::getLogin)
        .containsExactlyInAnyOrder("admin","Олександер");
  }

  @Test
  void findAll_shouldReturnEmptyList_whenTableEmpty() {

    List<User> users = repository.findAll();

    assertThat(users).isEmpty();
  }

  @Test
  void update_shouldModifyAllFields_whenUserExists() {
    UUID id = UUID.randomUUID();

    User user = User.fromDatabase(
        id,
        "old_login",
        "old_password",
        "old@gmail.com",
        Role.MECHANIC,
        null
    );

    repository.save(user);

    User updated = User.fromDatabase(
        id,
        "new_login",
        "new_password",
        "new@gmail.com",
        Role.ADMIN,
        "new.png"
    );

    repository.update(updated);

    User loaded = repository.findById(id).orElseThrow();

    assertThat(loaded.getLogin()).isEqualTo("new_login");
    assertThat(loaded.getHashedPassword()).isEqualTo("new_password");
    assertThat(loaded.getEmail()).isEqualTo("new@gmail.com");
    assertThat(loaded.getRole()).isEqualTo(Role.ADMIN);
    assertThat(loaded.getImagePath()).isEqualTo("new.png");
  }
  @Test
  void deleteById_shouldRemoveUser_whenExists() {
    User user = User.fromDatabase(
        UUID.randomUUID(),
        "delete_me",
        "password",
        "delete@gmail.com",
        Role.MANAGER,
        null
    );

    repository.save(user);

    boolean deleted = repository.deleteById(user.getId());

    assertThat(deleted).isTrue();
    assertThat(repository.findById(user.getId())).isEmpty();
    assertThat(countRowsInTable("USERS")).isEqualTo(0);
  }

  @Test
  void deleteById_shouldReturnFalse_whenNotExists() {
    UUID nonExistentId = UUID.randomUUID();

    boolean deleted = repository.deleteById(nonExistentId);

    assertThat(deleted).isFalse();
  }

  @Test
  void findByLogin_shouldReturnUser_whenLoginExists() {
    User user = User.fromDatabase(
        UUID.randomUUID(),
        "manager",
        "password",
        "manager@gmail.com",
        Role.ADMIN,
        null
    );

    repository.save(user);

    User result = repository.findByLogin("manager");

    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo("manager@gmail.com");
  }

  @Test
  void findByFilters_shouldReturnMatchingUsers_whenSearchAndRoleProvided() {
    repository.save(User.fromDatabase(
        UUID.randomUUID(),
        "admin_user",
        "password",
        "admin@gmail.com",
        Role.ADMIN,
        null
    ));

    repository.save(User.fromDatabase(
        UUID.randomUUID(),
        "client_user",
        "password",
        "client@gmail.com",
        Role.MECHANIC,
        null
    ));

    List<User> result = repository.findByFilters("admin", Role.ADMIN);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getLogin()).isEqualTo("admin_user");
  }
}

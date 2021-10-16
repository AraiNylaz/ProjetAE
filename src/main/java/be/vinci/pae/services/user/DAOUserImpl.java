package be.vinci.pae.services.user;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.domain.user.UserFactory;
import be.vinci.pae.services.DalBackendServices;
import be.vinci.pae.services.address.DAOAddress;
import be.vinci.pae.utils.FatalException;
import be.vinci.pae.utils.ValueLink.UserType;
import jakarta.inject.Inject;

public class DAOUserImpl implements DAOUser {

  private String querySelectUserByUsername;
  private String querySelectUserById;
  private String querySelectUserByEmail;
  private String queryAddUser;
  private String queryValidateUser;
  private String querySelectUnvalidatedUsers;
  private String querySelectAllUsername;
  private String querySelectUsersFiltered;

  @Inject
  private UserFactory userFactory;

  @Inject
  private DAOAddress daoAddress;

  @Inject
  private DalBackendServices dalBackendServices;

  /**
   * Implementation of User selected by Id and User selected by username.
   */
  public DAOUserImpl() {
    querySelectUserByUsername =
        "SELECT u.user_id, u.username, u.password, u.last_name, u.first_name, u.address, u.email, "
            + "u.registration_date, u.valid_registration, u.user_type FROM project.users u "
            + "WHERE u.username = ?";
    querySelectUserById = "SELECT u.user_id, u.username, u.password, u.last_name, u.first_name, "
        + "u.address, u.email,u.registration_date, u.valid_registration, u.user_type "
        + "FROM project.users u WHERE u.user_id = ?";
    querySelectUserByEmail = "SELECT u.user_id, u.username, u.password, u.last_name, u.first_name, "
        + "u.address, u.email, u.registration_date, u.valid_registration, u.user_type "
        + "FROM project.users u WHERE u.email = ?";
    queryAddUser = "INSERT INTO project.users (user_id, username, last_name, first_name, "
        + "email, password, address, registration_date, valid_registration) "
        + "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)";
    queryValidateUser = "UPDATE project.users " + "SET user_type = ?, valid_registration = ? "
        + "WHERE user_id = ?";
    querySelectUnvalidatedUsers = "SELECT u.user_id, u.username, u.password, u.last_name, "
        + "u.first_name, u.address, u.email, u.registration_date, u.valid_registration, u.user_type"
        + " FROM project.users u WHERE u.valid_registration = false";
    querySelectAllUsername = "SELECT DISTINCT u.last_name FROM project.users u";
    querySelectUsersFiltered = "SELECT u.user_id, u.username, u.password, u.last_name, "
        + "u.first_name, u.address, u.email, u.registration_date, u.valid_registration, "
        + "u.user_type FROM project.addresses a, project.users u WHERE u.address = a.address_id "
        + "AND lower(u.last_name) LIKE lower(?) AND lower(a.postcode) LIKE lower(?) "
        + "AND lower(a.commune) LIKE lower(?)";
  }

  @Override
  public UserDTO getUserByUsername(String username) {
    try {
      PreparedStatement selectUserByUsername =
          this.dalBackendServices.getPreparedStatement(querySelectUserByUsername);
      selectUserByUsername.setString(1, username);
      try (ResultSet rs = selectUserByUsername.executeQuery()) {
        return createUser(rs);
      }
    } catch (SQLException e) {
      throw new FatalException("Database error : getUserByUsername", e);
    }
  }

  @Override
  public UserDTO getUserByEmail(String email) {
    try {
      PreparedStatement selectUserByEmail =
          this.dalBackendServices.getPreparedStatement(querySelectUserByEmail);
      selectUserByEmail.setString(1, email);
      try (ResultSet rs = selectUserByEmail.executeQuery()) {
        return createUser(rs);
      }
    } catch (SQLException e) {
      throw new FatalException("Database error : getUserByEmail", e);
    }
  }

  @Override
  public UserDTO getUserById(int id) {
    try {
      PreparedStatement selectUserById =
          this.dalBackendServices.getPreparedStatement(querySelectUserById);
      selectUserById.setInt(1, id);
      try (ResultSet rs = selectUserById.executeQuery()) {
        return createUser(rs);
      }
    } catch (SQLException e) {
      throw new FatalException("Database error : getUserById", e);
    }
  }

  @Override
  public boolean validateUser(int id, int type) {
    try {
      PreparedStatement validateUser =
          this.dalBackendServices.getPreparedStatement(queryValidateUser);
      validateUser.setInt(1, type);
      validateUser.setBoolean(2, true);
      validateUser.setInt(3, id);
      return validateUser.executeUpdate() == 1;
    } catch (SQLException e) {
      throw new FatalException("Database error : validateUser", e);
    }
  }

  @Override
  public List<UserDTO> getUnvalidatedUsers() {
    try {
      PreparedStatement selectUnvalidatedUsers =
          this.dalBackendServices.getPreparedStatement(querySelectUnvalidatedUsers);
      List<UserDTO> unvalidatedUsers = new ArrayList<>();
      try (ResultSet rs = selectUnvalidatedUsers.executeQuery()) {
        UserDTO user;
        do {
          user = createUser(rs);
          unvalidatedUsers.add(user);
        } while (user != null);
        unvalidatedUsers.remove(unvalidatedUsers.size() - 1);
        return unvalidatedUsers;
      }
    } catch (SQLException e) {
      throw new FatalException("Database error : getUnvalidatedUsers", e);
    }
  }

  @Override
  public List<String> getAllLastnames() {
    try {
      PreparedStatement selectAllUsers =
          this.dalBackendServices.getPreparedStatement(querySelectAllUsername);
      List<String> allUsers = new ArrayList<>();
      try (ResultSet rs = selectAllUsers.executeQuery()) {
        while (rs.next()) {
          allUsers.add(rs.getString(1));
        }
        return allUsers;
      }
    } catch (SQLException e) {
      throw new FatalException("Database error : getAllUsers", e);
    }
  }

  @Override
  public List<UserDTO> getUsersFiltered(String username, String postcode, String commune) {
    try {
      PreparedStatement selectUsersFiltered =
          this.dalBackendServices.getPreparedStatement(querySelectUsersFiltered);
      selectUsersFiltered.setString(1, username + "%");
      selectUsersFiltered.setString(2, postcode + "%");
      selectUsersFiltered.setString(3, commune + "%");
      List<UserDTO> allUsers = new ArrayList<>();
      try (ResultSet rs = selectUsersFiltered.executeQuery()) {
        UserDTO user;
        do {
          user = createUser(rs);
          allUsers.add(user);
        } while (user != null);
        allUsers.remove(allUsers.size() - 1);
        return allUsers;
      }
    } catch (SQLException e) {
      throw new FatalException("Database error : getUsersFiltered", e);
    }
  }

  @Override
  public int addUser(UserDTO user) {
    try {
      PreparedStatement addUser = this.dalBackendServices.getPreparedStatementAdd(queryAddUser);
      addUser.setString(1, user.getUsername());
      addUser.setString(2, user.getLastName());
      addUser.setString(3, user.getFirstName());
      addUser.setString(4, user.getEmail());
      addUser.setString(5, user.getPassword());
      addUser.setInt(6, user.getAddress().getId());
      addUser.setTimestamp(7, Timestamp.from(user.getRegistrationDate().toInstant()));
      addUser.setBoolean(8, user.isValidRegistration());

      addUser.execute();

      try (ResultSet rs = addUser.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
        return -1;
      }
    } catch (SQLException e) {
      throw new FatalException("Database error : addUser", e);
    }
  }

  private UserDTO createUser(ResultSet rs) throws SQLException {
    UserDTO user = null;
    if (rs.next()) {
      user = this.userFactory.getUser();
      user.setId(rs.getInt("user_id"));
      user.setUsername(rs.getString("username"));
      user.setPassword(rs.getString("password"));
      user.setLastName(rs.getString("last_name"));
      user.setFirstName(rs.getString("first_name"));
      user.setAddress(daoAddress.getAddressById(rs.getInt("address")));
      user.setEmail(rs.getString("email"));
      user.setRegistrationDate(Date.from(rs.getTimestamp("registration_date").toInstant()));
      user.setValidRegistration(rs.getBoolean("valid_registration"));
      user.setUserType(UserType.values()[rs.getInt("user_type")]);
    }
    return user;
  }
}

package be.vinci.pae.domain.user;

import be.vinci.pae.utils.ValueLink.UserType;
import java.util.List;

public interface UserUCC {

  UserDTO login(String username, String password);

  UserDTO register(UserDTO newUser);

  boolean validateUser(int id, UserType type);

  List<UserDTO> getUnvalidatedUsers();

  UserDTO getUserById(int id);

  List<String> getAllLastnames();

  List<UserDTO> getUsersFiltered(String username, String postcode, String commune);

  UserDTO getUserByEmail(String email);
}


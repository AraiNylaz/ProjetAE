package be.vinci.pae.domain.user;

public interface User extends UserDTO {

  boolean checkPassword(String password);

  String hashPassword(String password);

}

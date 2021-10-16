package be.vinci.pae.domain.user;

public class UserFactoryImpl implements UserFactory {

  @Override
  public UserDTO getUser() {
    return new UserImpl();
  }

}

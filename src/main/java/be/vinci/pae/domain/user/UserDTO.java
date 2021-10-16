package be.vinci.pae.domain.user;

import java.util.Date;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import be.vinci.pae.domain.address.AddressDTO;
import be.vinci.pae.utils.ValueLink.UserType;

@JsonDeserialize(as = UserImpl.class)
public interface UserDTO {

  int getId();

  void setId(int id);

  String getUsername();

  void setUsername(String username);

  String getLastName();

  void setLastName(String lastName);

  String getFirstName();

  void setFirstName(String firstName);

  String getEmail();

  void setEmail(String email);

  UserType getUserType();

  void setUserType(UserType userType);

  AddressDTO getAddress();

  void setAddress(AddressDTO address);

  Date getRegistrationDate();

  void setRegistrationDate(Date registrationDate);

  boolean isValidRegistration();

  void setValidRegistration(boolean validRegistration);

  String getPassword();

  void setPassword(String password);

}

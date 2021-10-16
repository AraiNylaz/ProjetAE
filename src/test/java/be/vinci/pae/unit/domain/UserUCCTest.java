package be.vinci.pae.unit.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import be.vinci.pae.domain.address.AddressDTO;
import be.vinci.pae.domain.address.AddressFactory;
import be.vinci.pae.domain.user.User;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.domain.user.UserFactory;
import be.vinci.pae.domain.user.UserUCCImpl;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.address.DAOAddress;
import be.vinci.pae.services.type.DAOType;
import be.vinci.pae.services.user.DAOUser;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.ValueLink;
import be.vinci.pae.utils.ValueLink.UserType;

public class UserUCCTest {

  UserFactory userFact;
  AddressFactory addressFactory;

  @Mock
  DalServices dalServices;

  @Mock
  DAOUser daoUser;

  @Mock
  DAOType daoType;
  @Mock
  DAOAddress daoAddress;

  @InjectMocks
  UserUCCImpl userUCC;

  @BeforeEach
  void initAll() {
    MockitoAnnotations.initMocks(this);
    ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinder());
    // this.userUCC = locator.getService(UserUCC.class);
    this.userFact = locator.getService(UserFactory.class);
    this.addressFactory = locator.getService(AddressFactory.class);

  }

  @DisplayName("test if user is null")
  @Test
  public void userUCCNotNullTest() {
    assertNotNull(this.userUCC);
  }

  @DisplayName("test login with both good username and password")
  @Test
  public void loginGoodLoginGoodPasswordTest() {

    UserDTO userToConnect = userFact.getUser();
    userToConnect.setPassword(((User) userToConnect).hashPassword("test"));
    userToConnect.setUsername("test");

    Mockito.when(daoUser.getUserByUsername("test")).thenReturn(userToConnect);
    UserDTO user = userUCC.login("test", "test");
    assertEquals(userToConnect, user);
  }

  @DisplayName("test login with bad username and good password")
  @Test
  public void loginBadLoginGoodPasswordTest() {
    UserDTO userToConnect = userFact.getUser();
    userToConnect.setPassword(((User) userToConnect).hashPassword("test"));
    userToConnect.setUsername("test");
    Mockito.when(daoUser.getUserByUsername("test")).thenReturn(userToConnect);
    assertThrows(BusinessException.class, () -> userUCC.login("chouquettes", "test"));
  }

  @DisplayName("test login with good username and bad password")
  @Test
  public void loginGoodLoginBadPasswordTest() {
    UserDTO userToConnect = userFact.getUser();
    userToConnect.setPassword(((User) userToConnect).hashPassword("test"));
    userToConnect.setUsername("test");
    Mockito.when(daoUser.getUserByUsername("test")).thenReturn(userToConnect);
    assertThrows(BusinessException.class, () -> userUCC.login("login", "bad"));
  }

  @DisplayName("test registerUser good parameters")
  @Test
  public void registerUserAllGood() {
    UserDTO userAlreadyRegistered = userFact.getUser();
    userAlreadyRegistered.setUsername("test");
    userAlreadyRegistered.setEmail("test@gmail.com");
    Mockito.when(daoUser.getUserByUsername("test")).thenReturn(userAlreadyRegistered);
    UserDTO userToRegister = userFact.getUser();
    userToRegister.setUsername("test2");
    userToRegister.setEmail("test2@gmail.com");
    AddressDTO address = addressFactory.getAddress();
    address.setUnitNumber("test");
    userToRegister.setAddress(address);
    Mockito.when(daoAddress.selectAddressID(address)).thenReturn(1);
    assertTrue(userUCC.register(userToRegister) != null);
    Mockito.when(daoAddress.selectAddressID(address)).thenReturn(-1);
    Mockito.when(daoAddress.addAddress(address)).thenReturn(1);
    assertTrue(userUCC.register(userToRegister) != null);

    Mockito.when(daoUser.addUser(userToRegister)).thenReturn(1);
    assertTrue(userUCC.register(userToRegister) != null);
    // meme si adduser ko user != null
    Mockito.when(daoUser.addUser(userToRegister)).thenReturn(-1);
    assertNotNull(userUCC.register(userToRegister));

  }

  @DisplayName("test registerUser with bad username")
  @Test
  public void registerUserBadUsername() {
    UserDTO userAlreadyRegistered = userFact.getUser();
    userAlreadyRegistered.setUsername("test");
    Mockito.when(daoUser.getUserByUsername("test")).thenReturn(userAlreadyRegistered);
    UserDTO userToRegistered = userFact.getUser();
    userToRegistered.setUsername("test");
    assertThrows(BusinessException.class, () -> userUCC.register(userToRegistered));

  }

  @DisplayName("test registerUser with bad email")
  @Test
  public void registerUserBadEmail() {
    UserDTO userAlreadyRegistered = userFact.getUser();
    userAlreadyRegistered.setEmail("test@gmail.com");
    Mockito.when(daoUser.getUserByEmail("test@gmail.com")).thenReturn(userAlreadyRegistered);
    UserDTO userToRegistered = userFact.getUser();
    userToRegistered.setEmail("test@gmail.com");
    assertThrows(BusinessException.class, () -> userUCC.register(userToRegistered));
  }

  @DisplayName("test validateUser with good parameters")
  @Test
  public void validateUserAllGood() {
    UserDTO userToValidate = userFact.getUser();
    userToValidate.setId(1);
    Mockito.when(daoUser.getUserById(1)).thenReturn(userToValidate);
    UserType client = ValueLink.UserType.client;
    UserType admin = ValueLink.UserType.admin;
    Mockito.when(daoUser.validateUser(userToValidate.getId(), client.ordinal())).thenReturn(true);
    assertTrue(userUCC.validateUser(userToValidate.getId(), client));
    assertFalse(userUCC.validateUser(userToValidate.getId(), admin));
    assertFalse(daoUser.getUnvalidatedUsers().contains(userToValidate));
  }

  @DisplayName("test validateUser with bad user")
  @Test
  public void validateUserBadUser() {
    UserDTO userToValidate = userFact.getUser();
    // user n'existe pas encore
    UserType client = ValueLink.UserType.client;
    assertThrows(BusinessException.class,
        () -> userUCC.validateUser(userToValidate.getId(), client));
    assertFalse(daoUser.getUnvalidatedUsers().contains(userToValidate));
    Mockito.when(daoUser.getUserById(1)).thenReturn(userToValidate);
    // if user is already validate
    userToValidate.setValidRegistration(true);
    userToValidate.setUserType(client);
    assertTrue(userToValidate.isValidRegistration());
    UserType admin = ValueLink.UserType.admin;
    assertThrows(BusinessException.class, () -> userUCC.validateUser(1, client));
    assertThrows(BusinessException.class, () -> userUCC.validateUser(1, admin));
    // test if some error occured during process
    UserDTO userTemp = userFact.getUser();
    Mockito.when(daoUser.getUserById(2)).thenReturn(userTemp);
    Mockito.when(daoUser.validateUser(2, client.ordinal())).thenReturn(false);
    assertFalse(userUCC.validateUser(2, client));

    // pas de bad type car pas possible de se tromper de type --> userType

  }

  @DisplayName("test getUnvalidatedUsers ")
  @Test
  public void getUnvalidatedUsersTest() {
    List<UserDTO> list = new ArrayList<UserDTO>();
    assertNotNull(userUCC.getUnvalidatedUsers());
    assertTrue(userUCC.getUnvalidatedUsers().isEmpty());
    UserDTO user = userFact.getUser();
    list.add(user);
    Mockito.when(daoUser.getUnvalidatedUsers()).thenReturn(list);
    assertEquals(list, userUCC.getUnvalidatedUsers());
    assertTrue(userUCC.getUnvalidatedUsers().contains(user));
    assertEquals(1, userUCC.getUnvalidatedUsers().size());
  }

  @DisplayName("test getUsersById ")
  @Test
  public void getUsersByIdTest() {
    assertEquals(null, userUCC.getUserById(1));
    UserDTO user = userFact.getUser();
    Mockito.when(daoUser.getUserById(1)).thenReturn(user);
    assertEquals(user, userUCC.getUserById(1));
  }

  @DisplayName("test getAllLastnames")
  @Test
  public void getAllLastnamesTest() {
    List<String> list = new ArrayList<String>();
    assertNotNull(userUCC.getAllLastnames());
    assertTrue(userUCC.getAllLastnames().isEmpty());
    Mockito.when(daoUser.getAllLastnames()).thenReturn(list);
    assertEquals(list, userUCC.getAllLastnames());
  }

  @DisplayName("test getUserByEmail ")
  @Test
  public void getUserByEmailTest() {
    assertEquals(null, userUCC.getUserByEmail("test@test.be"));
    UserDTO user = userFact.getUser();
    Mockito.when(daoUser.getUserByEmail("test@test.be")).thenReturn(user);
    assertEquals(user, userUCC.getUserByEmail("test@test.be"));
  }

  @DisplayName("test getUsersFiltered")
  @Test
  public void getUsersFilteredTest() {
    List<UserDTO> list = new ArrayList<UserDTO>();
    assertNotNull(userUCC.getUsersFiltered("username", "postcode", "commune"));
    assertTrue(userUCC.getUsersFiltered("username", "postcode", "commune").isEmpty());
    Mockito.when(daoUser.getUsersFiltered("username", "postcode", "commune")).thenReturn(list);
    assertEquals(list, userUCC.getUsersFiltered("username", "postcode", "commune"));
  }

}

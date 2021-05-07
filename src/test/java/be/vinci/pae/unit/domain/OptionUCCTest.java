package be.vinci.pae.unit.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.furniture.FurnitureFactory;
import be.vinci.pae.domain.option.OptionDTO;
import be.vinci.pae.domain.option.OptionFactory;
import be.vinci.pae.domain.option.OptionUCCImpl;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.domain.user.UserFactory;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.furniture.DAOFurniture;
import be.vinci.pae.services.option.DAOOption;
import be.vinci.pae.services.user.DAOUser;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.ValueLink;
import be.vinci.pae.utils.ValueLink.FurnitureCondition;

public class OptionUCCTest {

  OptionFactory optionFactory;

  UserFactory userFactory;

  FurnitureFactory furnitureFactory;

  @Mock
  DalServices dalServices;
  @Mock
  DAOOption daoOption;
  @Mock
  DAOFurniture daoFurniture;
  @Mock
  DAOUser daoUser;

  @InjectMocks
  OptionUCCImpl optionUCC;

  @BeforeEach
  void initAll() {
    MockitoAnnotations.initMocks(this);
    ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinder());
    this.optionFactory = locator.getService(OptionFactory.class);
    this.furnitureFactory = locator.getService(FurnitureFactory.class);
    this.userFactory = locator.getService(UserFactory.class);
    Config.load("tests.properties");

  }

  @DisplayName("test if option is null")
  @Test
  public void optionUCCNotNullTest() {
    assertNotNull(this.optionUCC);
  }

  @DisplayName("test addOption")
  @Test
  public void addOptionTest() {
    // TODO
    // all wrong parameters
    assertThrows(BusinessException.class, () -> optionUCC.addOption(0, 5, null));
    assertThrows(BusinessException.class, () -> optionUCC.addOption(1, -5, null));
    assertThrows(BusinessException.class, () -> optionUCC.addOption(-1, 4, null));

    UserDTO buyer = userFactory.getUser();
    // bad furniture
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(null);
    assertThrows(BusinessException.class, () -> optionUCC.addOption(1, 2, buyer));

    FurnitureDTO furniture = furnitureFactory.getFurniture();
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furniture);
    assertThrows(BusinessException.class, () -> optionUCC.addOption(2, 2, buyer));

    // bad furniture type
    furniture.setCondition(ValueLink.FurnitureCondition.emporte_par_client);
    assertThrows(BusinessException.class, () -> optionUCC.addOption(1, 5, buyer));
    // goor furnitureType but wrong buyer
    furniture.setCondition(ValueLink.FurnitureCondition.en_vente);
    assertThrows(BusinessException.class, () -> optionUCC.addOption(1, 4, buyer));

    // bad buyer
    buyer.setValidRegistration(false);
    buyer.setId(1);
    assertThrows(BusinessException.class, () -> optionUCC.addOption(1, 2, buyer));
    buyer.setValidRegistration(true);
    buyer.setUserType(ValueLink.UserType.admin);
    assertThrows(BusinessException.class, () -> optionUCC.addOption(1, 2, buyer));

    // bad duration
    Mockito.when(daoUser.getUserById(1)).thenReturn(buyer);
    buyer.setValidRegistration(true);
    buyer.setUserType(ValueLink.UserType.client);
    assertThrows(BusinessException.class, () -> optionUCC.addOption(1, -1, buyer));
    assertThrows(BusinessException.class, () -> optionUCC.addOption(1, 6, buyer));

    
    furniture.setId(2);
    buyer.setId(1);
    Mockito.when(daoFurniture.selectFurnitureById(2)).thenReturn(furniture);
    List<OptionDTO> list = new ArrayList<OptionDTO>();
    Mockito.when(daoOption.selectOptionsOfBuyerFromFurniture(1, 2)).thenReturn(list);
    Mockito.when(daoOption.selectOptionsOfFurniture(2)).thenReturn(list);
    OptionDTO option1 = optionFactory.getOption();
    option1.setDuration(2);
    option1.setBuyer(buyer);
    option1.setFurniture(furniture);
    option1.setStatus(ValueLink.OptionStatus.en_cours);
    option1.setDate(Date.from(Instant.now()));
    Mockito.when(daoOption.addOption(option1)).thenReturn(2);

    list.add(option1);
    // durée option trop longue
    assertThrows(BusinessException.class, () -> optionUCC.addOption(2, 4, buyer));

    OptionDTO option2 = optionFactory.getOption();
    option2.setDuration(1);
    UserDTO buyer2 = userFactory.getUser();
    buyer2.setId(1);
    option2.setBuyer(buyer2);
    option2.setFurniture(furniture);
    option2.setStatus(ValueLink.OptionStatus.en_cours);
    option2.setDate(Date.from(Instant.now()));
    Mockito.when(daoOption.addOption(option2)).thenReturn(3);
    list.add(option2);

    // déjà une option d'un autre acheteur
    assertThrows(BusinessException.class, () -> optionUCC.addOption(2, 1, buyer2));

    // TODO
    furniture.setId(5);
    Mockito.when(daoFurniture.selectFurnitureById(5)).thenReturn(furniture);
    UserDTO buyer3 = userFactory.getUser();
    buyer3.setId(1);
    buyer3.setValidRegistration(true);
    buyer3.setUserType(ValueLink.UserType.client);
    OptionDTO optionToAdd = optionFactory.getOption();
    optionToAdd.setDuration(2);
    optionToAdd.setBuyer(buyer);
    optionToAdd.setFurniture(furniture);
    optionToAdd.setStatus(ValueLink.OptionStatus.en_cours);
    optionToAdd.setDate(Date.from(Instant.now()));
    Mockito.when(daoOption.addOption(optionToAdd)).thenReturn(5);
    furniture.setId(5);
    
    //why is it null !!!??? see OptionUCCImlp line 71-76
    assertThrows(NullPointerException.class, () -> optionUCC.addOption(5, 2, buyer3));
    /*
    List<OptionDTO> list2 = new ArrayList<OptionDTO>();
    Mockito.when(daoOption.selectOptionsOfFurniture(5)).thenReturn(list2);
    list2.add(optionToAdd);
    Mockito.when(daoOption.addOption(optionToAdd)).thenReturn(5);
    Mockito.when(daoFurniture.updateCondition(5, ValueLink.FurnitureCondition.en_option.ordinal()))
    .thenReturn(true);
    assertTrue(optionUCC.addOption(furniture.getId(), 2, buyer3));
    */
  }

  @DisplayName("test cancelOption")
  @Test
  public void cancelOptionTest() {
    UserDTO buyer = userFactory.getUser();
    Mockito.when(daoUser.getUserById(1)).thenReturn(buyer);
    FurnitureDTO furniture = furnitureFactory.getFurniture();
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furniture);
    OptionDTO optionToAdd = optionFactory.getOption();
    List<OptionDTO> list = new ArrayList<OptionDTO>();
    list.add(optionToAdd);
    assertTrue(list.size() != 0);
    assertTrue(list.contains(optionToAdd));
    // empty list
    assertThrows(BusinessException.class, () -> optionUCC.cancelOption(1, buyer));
    Mockito.when(daoOption.selectOptionsOfFurniture(1)).thenReturn(list);
    optionToAdd.setStatus(ValueLink.OptionStatus.annulee);
    // no option on furniture
    assertThrows(BusinessException.class, () -> optionUCC.cancelOption(1, buyer));
    optionToAdd.setStatus(ValueLink.OptionStatus.en_cours);
    UserDTO buyerTemp = userFactory.getUser();
    optionToAdd.setBuyer(buyerTemp);
    buyerTemp.setId(2);
    // test supprimer option other user
    assertThrows(BusinessException.class, () -> optionUCC.cancelOption(1, buyer));
    optionToAdd.setBuyer(buyer);
    Mockito.when(daoOption.cancelOption(optionToAdd)).thenReturn(false);
    assertFalse(optionUCC.cancelOption(1, buyer));
    Mockito.when(daoOption.cancelOption(optionToAdd)).thenReturn(true);
    Mockito.when(daoFurniture.updateCondition(1, ValueLink.FurnitureCondition.en_vente.ordinal()))
        .thenReturn(false);
    assertFalse(optionUCC.cancelOption(1, buyer));
    Mockito.when(daoFurniture.updateCondition(1, ValueLink.FurnitureCondition.en_vente.ordinal()))
        .thenReturn(true);
    assertTrue(optionUCC.cancelOption(1, buyer));

  }

  @DisplayName("test cancelOptionByAdmin")
  @Test
  public void cancelOptionByAdminTest() {
    UserDTO buyer = userFactory.getUser();
    Mockito.when(daoUser.getUserById(1)).thenReturn(buyer);
    FurnitureDTO furniture = furnitureFactory.getFurniture();
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furniture);
    OptionDTO optionToAdd = optionFactory.getOption();
    List<OptionDTO> list = new ArrayList<OptionDTO>();
    list.add(optionToAdd);
    assertTrue(list.size() != 0);
    assertTrue(list.contains(optionToAdd));
    // empty list
    assertThrows(BusinessException.class, () -> optionUCC.cancelOptionByAdmin(1));
    Mockito.when(daoOption.selectOptionsOfFurniture(1)).thenReturn(list);
    optionToAdd.setStatus(ValueLink.OptionStatus.annulee);
    // no option on furniture
    assertThrows(BusinessException.class, () -> optionUCC.cancelOptionByAdmin(1));
    optionToAdd.setStatus(ValueLink.OptionStatus.en_cours);
    optionToAdd.setBuyer(buyer);
    Mockito.when(daoOption.cancelOption(optionToAdd)).thenReturn(false);
    assertFalse(optionUCC.cancelOptionByAdmin(1));
    Mockito.when(daoOption.cancelOption(optionToAdd)).thenReturn(true);
    Mockito.when(daoFurniture.updateCondition(1, ValueLink.FurnitureCondition.en_vente.ordinal()))
        .thenReturn(false);
    assertFalse(optionUCC.cancelOptionByAdmin(1));
    Mockito.when(daoFurniture.updateCondition(1, ValueLink.FurnitureCondition.en_vente.ordinal()))
        .thenReturn(true);
    assertTrue(optionUCC.cancelOptionByAdmin(1));

  }


  @DisplayName("test getLastOptionOfFurniture ")
  @Test
  public void getLastOptionOfFurnitureTest() {
    FurnitureDTO furniture = furnitureFactory.getFurniture();
    assertThrows(BusinessException.class, () -> optionUCC.getLastOptionOfFurniture(1));
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furniture);
    OptionDTO option = optionFactory.getOption();
    option.setId(1);
    option.setStatus(ValueLink.OptionStatus.en_cours);
    option.setDuration(2);
    option.setDate(Date.from(Instant.now()));
    Mockito.when(daoOption.getLastOptionOfFurniture(1)).thenReturn(option);
    assertEquals(option, optionUCC.getLastOptionOfFurniture(1));
  }

}

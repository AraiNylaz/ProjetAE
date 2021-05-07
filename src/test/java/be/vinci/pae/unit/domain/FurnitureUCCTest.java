package be.vinci.pae.unit.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
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
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.furniture.FurnitureFactory;
import be.vinci.pae.domain.furniture.FurnitureUCCImpl;
import be.vinci.pae.domain.option.OptionDTO;
import be.vinci.pae.domain.option.OptionFactory;
import be.vinci.pae.domain.picture.PictureDTO;
import be.vinci.pae.domain.picture.PictureFactory;
import be.vinci.pae.domain.type.TypeDTO;
import be.vinci.pae.domain.type.TypeFactory;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.domain.user.UserFactory;
import be.vinci.pae.domain.visitrequest.VisitRequestDTO;
import be.vinci.pae.domain.visitrequest.VisitRequestFactory;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.furniture.DAOFurniture;
import be.vinci.pae.services.option.DAOOption;
import be.vinci.pae.services.picture.DAOPicture;
import be.vinci.pae.services.type.DAOType;
import be.vinci.pae.services.user.DAOUser;
import be.vinci.pae.services.visitrequest.DAOVisitRequest;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.ValueLink;
import be.vinci.pae.utils.ValueLink.FurnitureCondition;

public class FurnitureUCCTest {

  FurnitureFactory furnitureFactory;
  UserFactory userFactory;
  TypeFactory typeFactory;
  PictureFactory pictureFactory;
  VisitRequestFactory vrFactory;
  OptionFactory optionFactory;
  @Mock
  DalServices dalServices;
  @Mock
  DAOFurniture daoFurniture;
  @Mock
  DAOUser daoUser;
  @Mock
  DAOType daoType;
  @Mock
  DAOVisitRequest daoVR;
  @Mock
  DAOPicture daoPicture;
  @Mock
  DAOOption daoOption;
  @InjectMocks
  FurnitureUCCImpl furnitureUCC;

  @BeforeEach
  void initAll() {
    MockitoAnnotations.initMocks(this);
    ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinder());
    this.furnitureFactory = locator.getService(FurnitureFactory.class);
    this.userFactory = locator.getService(UserFactory.class);
    this.typeFactory = locator.getService(TypeFactory.class);
    this.pictureFactory = locator.getService(PictureFactory.class);
    this.optionFactory = locator.getService(OptionFactory.class);
    this.vrFactory = locator.getService(VisitRequestFactory.class);

  }

  @DisplayName("test if furniture is null")
  @Test
  public void furnitureUCCNotNullTest() {
    assertNotNull(this.furnitureUCC);
  }

  @DisplayName("test getAllFurniture ")
  @Test
  public void getAllFurnitureTest() {
    List<FurnitureDTO> list = new ArrayList<FurnitureDTO>();
    assertNotNull(furnitureUCC.getAllFurniture());
    assertTrue(furnitureUCC.getAllFurniture().isEmpty());
    FurnitureDTO furniture = furnitureFactory.getFurniture();
    list.add(furniture);
    Mockito.when(daoFurniture.selectAllFurniture()).thenReturn(list);
    assertEquals(list, furnitureUCC.getAllFurniture());
    assertTrue(furnitureUCC.getAllFurniture().contains(furniture));
    assertEquals(1, furnitureUCC.getAllFurniture().size());
  }

  @DisplayName("test getFurnitureUsers ")
  @Test
  public void getFurnitureUsersTest() {
    List<FurnitureDTO> list = new ArrayList<FurnitureDTO>();
    assertNotNull(furnitureUCC.getFurnitureUsers());
    assertTrue(furnitureUCC.getFurnitureUsers().isEmpty());
    FurnitureDTO furniture = furnitureFactory.getFurniture();
    list.add(furniture);
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furniture);
    Mockito.when(daoFurniture.selectFurnitureUsers()).thenReturn(list);
    assertEquals(list, furnitureUCC.getFurnitureUsers());
    assertTrue(furnitureUCC.getFurnitureUsers().contains(furniture));
    assertEquals(1, furnitureUCC.getFurnitureUsers().size());
  }

  @DisplayName("test addFurniture good parameters")
  @Test
  public void addFurnitureAllGoodTest() {
    FurnitureDTO furnitureToAdd = furnitureFactory.getFurniture();
    Mockito.when(daoFurniture.insertFurniture(furnitureToAdd)).thenReturn(1);
    FurnitureDTO furniture = furnitureUCC.addFurniture(furnitureToAdd);
    assertEquals(furnitureToAdd, furniture);
  }

  @DisplayName("test addFurniture bad furniture")
  @Test
  public void addFurnitureAllBadTest() {
    FurnitureDTO furnitureToAdd = furnitureFactory.getFurniture();
    Mockito.when(daoFurniture.insertFurniture(furnitureToAdd)).thenReturn(-1);
    FurnitureDTO furniture = furnitureUCC.addFurniture(furnitureToAdd);
    assertFalse(daoFurniture.selectAllFurniture().contains(furniture));
    assertEquals(-1, daoFurniture.insertFurniture(furniture));
  }

  @DisplayName("test modifyCondition")
  @Test
  public void modifyConditionTest() {

    FurnitureDTO furniture = furnitureFactory.getFurniture();
    OptionDTO option = optionFactory.getOption();
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furniture);
    Mockito.when(daoOption.selectOptionByFurnitureId(1)).thenReturn(option);

    assertThrows(NullPointerException.class,
        () -> furnitureUCC.modifyCondition(2, ValueLink.FurnitureCondition.ne_convient_pas));
    assertThrows(NullPointerException.class, () -> furnitureUCC.modifyCondition(1, null));

    FurnitureCondition condition = ValueLink.FurnitureCondition.propose;
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));
    condition = ValueLink.FurnitureCondition.ne_convient_pas;
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));
    condition = ValueLink.FurnitureCondition.achete;
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));
    condition = ValueLink.FurnitureCondition.emporte_par_client;
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));
    condition = ValueLink.FurnitureCondition.emporte_par_patron;
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));
    condition = ValueLink.FurnitureCondition.en_restauration;
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));
    condition = ValueLink.FurnitureCondition.en_magasin;
    Mockito.when(daoFurniture.updateDepositDate(1, Instant.now())).thenReturn(true);
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));
    condition = ValueLink.FurnitureCondition.en_vente;
    Mockito.when(daoFurniture.updateSellingDate(1, Instant.now())).thenReturn(true);
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));
    condition = ValueLink.FurnitureCondition.retire_de_vente;
    Mockito.when(daoPicture.updateScrollingPictureFalseByFurnitureId(1)).thenReturn(true);
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));
    condition = ValueLink.FurnitureCondition.en_option;
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));
    condition = ValueLink.FurnitureCondition.vendu;
    //TODO doit être true but is false
    UserDTO buyer = userFactory.getUser();
    buyer.setUserType(ValueLink.UserType.antiquaire);
    furniture.setBuyer(buyer);
    Mockito.when(daoOption.selectOptionByFurnitureId(1)).thenReturn(option);
    Mockito.when(daoOption.finishOption(1)).thenReturn(true);
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));
    condition = ValueLink.FurnitureCondition.reserve;
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));
    condition = ValueLink.FurnitureCondition.livre;
    Mockito.when(daoFurniture.updateCondition(1, condition.ordinal())).thenReturn(true);
    assertTrue(furnitureUCC.modifyCondition(1, condition));

  }

  @DisplayName("test modifyType")
  @Test
  public void modifyTypeTest() {
    FurnitureDTO furniture = furnitureFactory.getFurniture();
    TypeDTO type = typeFactory.getType();
    Mockito.when(daoType.selectTypeById(1)).thenReturn(type);
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furniture);
    Mockito.when(daoFurniture.updateType(1, 1)).thenReturn(true);
    assertTrue(furnitureUCC.modifyType(1, 1));
    Mockito.when(daoType.selectTypeById(1)).thenReturn(null);
    assertThrows(BusinessException.class, () -> furnitureUCC.modifyType(1, 1));
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(null);
    assertThrows(BusinessException.class, () -> furnitureUCC.modifyType(1, 1));
    Mockito.when(daoType.selectTypeById(1)).thenReturn(type);
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furniture);
    Mockito.when(daoFurniture.updateType(1, 1)).thenReturn(false);
    assertThrows(BusinessException.class, () -> furnitureUCC.modifyType(1, 1));
  }

  @DisplayName("test modifyPurchasePrice")
  @Test
  public void modifyPurchasePriceTest() {
    Mockito.when(daoFurniture.updatePurchasePrice(1, 200.00)).thenReturn(true);
    assertTrue(furnitureUCC.modifyPurchasePrice(1, 200));
    Mockito.when(daoFurniture.updatePurchasePrice(1, 200.00)).thenReturn(false);
    assertThrows(BusinessException.class, () -> furnitureUCC.modifyPurchasePrice(1, 200));
  }

  @DisplayName("test modifySellingPrice")
  @Test
  public void modifySellingPriceTest() {
    Mockito.when(daoFurniture.updateSellingPrice(1, 200.00)).thenReturn(true);
    assertTrue(furnitureUCC.modifySellingPrice(1, 200));
    Mockito.when(daoFurniture.updateSellingPrice(1, 200.00)).thenReturn(false);
    assertThrows(BusinessException.class, () -> furnitureUCC.modifySellingPrice(1, 200));
  }

  @DisplayName("test modifySpecialSalePrice")
  @Test
  public void modifySpecialSaleTest() {
    Mockito.when(daoFurniture.updateSpecialSalePrice(1, 200.00)).thenReturn(true);
    assertTrue(furnitureUCC.modifySpecialSalePrice(1, 200));
    Mockito.when(daoFurniture.updateSpecialSalePrice(1, 200.00)).thenReturn(false);
    assertThrows(BusinessException.class, () -> furnitureUCC.modifySpecialSalePrice(1, 200));
  }

  @DisplayName("test modifyDescription")
  @Test
  public void modifyDescriptionTest() {
    Mockito.when(daoFurniture.updateDescription(1, "test")).thenReturn(true);
    assertTrue(furnitureUCC.modifyDescription(1, "test"));
    Mockito.when(daoFurniture.updateDescription(1, "test")).thenReturn(false);
    assertThrows(BusinessException.class, () -> furnitureUCC.modifyDescription(1, "test"));
  }

  @DisplayName("test modifyWithdrawalDateToCustomer")
  @Test
  public void modifyWithdrawalDateToCustomerTest() {
    Mockito.when(daoFurniture.updateWithdrawalDateToCustomer(1,
        Timestamp.valueOf(LocalDate.now().atTime(LocalTime.NOON)))).thenReturn(true);
    assertTrue(furnitureUCC.modifyWithdrawalDateToCustomer(1, LocalDate.now()));
    Mockito.when(daoFurniture.updateWithdrawalDateToCustomer(1,
        Timestamp.valueOf(LocalDate.now().atTime(LocalTime.NOON)))).thenReturn(false);
    assertThrows(BusinessException.class,
        () -> furnitureUCC.modifyWithdrawalDateToCustomer(1, LocalDate.now()));
  }

  @DisplayName("test modifyWithdrawalDateFromCustomer")
  @Test
  public void modifyWithdrawalDateFromCustomerTest() {
    Mockito.when(daoFurniture.updateWithdrawalDateFromCustomer(1,
        Timestamp.valueOf(LocalDate.now().atTime(LocalTime.NOON)))).thenReturn(true);
    assertTrue(furnitureUCC.modifyWithdrawalDateFromCustomer(1, LocalDate.now()));
    Mockito.when(daoFurniture.updateWithdrawalDateFromCustomer(1,
        Timestamp.valueOf(LocalDate.now().atTime(LocalTime.NOON)))).thenReturn(false);
    assertThrows(BusinessException.class,
        () -> furnitureUCC.modifyWithdrawalDateFromCustomer(1, LocalDate.now()));
  }

  @DisplayName("test modifyDeliveryDate")
  @Test
  public void modifyDeliveryDateTest() {
    Mockito.when(daoFurniture.updateDeliveryDate(1,
        Timestamp.valueOf(LocalDate.now().atTime(LocalTime.NOON)))).thenReturn(true);
    assertTrue(furnitureUCC.modifyDeliveryDate(1, LocalDate.now()));
    Mockito.when(daoFurniture.updateDeliveryDate(1,
        Timestamp.valueOf(LocalDate.now().atTime(LocalTime.NOON)))).thenReturn(false);
    assertThrows(BusinessException.class,
        () -> furnitureUCC.modifyDeliveryDate(1, LocalDate.now()));
  }

  @DisplayName("test modifyBuyerEmail")
  @Test
  public void modifyBuyerEmailTest() {
    UserDTO buyer = userFactory.getUser();
    buyer.setId(1);
    buyer.setEmail("test@test.com");
    Mockito.when(daoUser.getUserByEmail("test@test.com")).thenReturn(buyer);
    Mockito.when(daoFurniture.updateBuyer(1, 1)).thenReturn(true);
    Mockito.when(daoFurniture.updateUnregisteredBuyerEmail(1, "test@test.com")).thenReturn(true);
    assertTrue(furnitureUCC.modifyBuyerEmail(1, "test@test.com"));

    Mockito.when(daoUser.getUserByEmail("test@test.com")).thenReturn(null);
    Mockito.when(daoFurniture.updateUnregisteredBuyerEmail(1, "test@test.com")).thenReturn(false);
    assertFalse(furnitureUCC.modifyBuyerEmail(1, "test@test.com"));
    Mockito.when(daoUser.getUserByEmail("test@test.com")).thenReturn(buyer);
    Mockito.when(daoFurniture.updateBuyer(1, 1)).thenReturn(false);
    assertFalse(furnitureUCC.modifyBuyerEmail(1, "test@test.com"));
  }

  @DisplayName("test getFurnitureById ")
  @Test
  public void getFurnitureByIdTest() {
    assertNull(furnitureUCC.getFurnitureById(1));
    FurnitureDTO furniture = furnitureFactory.getFurniture();
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furniture);
    assertEquals(furniture, furnitureUCC.getFurnitureById(1));
  }

  @DisplayName("test getPersonalFurnitureById")
  @Test
  public void getPersonalFurnitureByIdTest() {
    UserDTO buyer = userFactory.getUser();
    assertThrows(NullPointerException.class, () -> furnitureUCC.getPersonalFurnitureById(1, buyer));
    FurnitureDTO furniture = furnitureFactory.getFurniture();
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furniture);
    assertThrows(NullPointerException.class, () -> furnitureUCC.getPersonalFurnitureById(1, buyer));
    Mockito.when(daoUser.getUserById(1)).thenReturn(buyer);
    buyer.setId(1);
    furniture.setBuyer(buyer);
    assertEquals(furniture, furnitureUCC.getPersonalFurnitureById(1, buyer));
    furniture.setBuyer(null);
    VisitRequestDTO vr = vrFactory.getVisitRequest();
    furniture.setVisitRequest(vr);
    vr.setCustomer(buyer);
    assertEquals(furniture, furnitureUCC.getPersonalFurnitureById(1, buyer));
  }

  @DisplayName("test modifyFurniture")
  @Test
  public void modifyFurnitureTest() {
    FurnitureCondition condition = null;
    condition = null;
    

    FurnitureDTO furnitureToAdd = furnitureFactory.getFurniture();
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furnitureToAdd);

    assertTrue(furnitureUCC.modifyFurniture(0, null, -1, -1, -1, -1, null, null, null, null, null));

    Mockito.when(daoFurniture.updateSellingPrice(1, 200.000)).thenReturn(true);
    assertTrue(furnitureUCC.modifyFurniture(1, condition, 200.00, -1, -1, -1, null, null, null,
        null, null));

    UserDTO buyer = userFactory.getUser();
    buyer.setUserType(ValueLink.UserType.antiquaire);
    furnitureToAdd.setBuyer(buyer);
    Mockito.when(daoFurniture.updateSpecialSalePrice(1, 180.000)).thenReturn(true);
    assertTrue(furnitureUCC.modifyFurniture(1, condition, -1, 180.00, -1, -1, null, null, null,
        null, null));

    Mockito.when(daoFurniture.updatePurchasePrice(1, 100.00)).thenReturn(true);
    assertTrue(furnitureUCC.modifyFurniture(1, condition, -1, -1, 100.00, -1, null, null, null,
        null, null));

    TypeDTO type = typeFactory.getType();
    Mockito.when(daoFurniture.updateType(1, 1)).thenReturn(true);
    Mockito.when(daoType.selectTypeById(1)).thenReturn(type);
    assertTrue(
        furnitureUCC.modifyFurniture(1, condition, -1, -1, -1, 1, null, null, null, null, null));

    LocalDate date = LocalDate.now();
    Mockito.when(daoFurniture.updateWithdrawalDateFromCustomer(1,
        Timestamp.valueOf(date.atTime(LocalTime.NOON)))).thenReturn(true);
    assertTrue(
        furnitureUCC.modifyFurniture(1, condition, -1, -1, -1, -1, date, null, null, null, null));

    Mockito.when(daoFurniture.updateWithdrawalDateToCustomer(1,
        Timestamp.valueOf(date.atTime(LocalTime.NOON)))).thenReturn(true);
    assertTrue(
        furnitureUCC.modifyFurniture(1, condition, -1, -1, -1, -1, null, date, null, null, null));

    Mockito.when(daoFurniture.updateDeliveryDate(1, Timestamp.valueOf(date.atTime(LocalTime.NOON))))
        .thenReturn(true);
    assertTrue(
        furnitureUCC.modifyFurniture(1, condition, -1, -1, -1, -1, null, null, date, null, null));

    Mockito.when(daoFurniture.updateUnregisteredBuyerEmail(1, "test@test.be")).thenReturn(true);
    assertTrue(furnitureUCC.modifyFurniture(1, condition, -1, -1, -1, -1, null, null, null,
        "test@test.be", null));
    UserDTO user = userFactory.getUser();
    Mockito.when(daoUser.getUserByEmail("test@test.be")).thenReturn(user);
    user.setEmail("test@test.be");
    Mockito.when(daoUser.getUserByEmail("test@test.be")).thenReturn(null);
    Mockito.when(daoFurniture.updateBuyer(1, 1)).thenReturn(true);
    assertTrue(furnitureUCC.modifyFurniture(1, condition, -1, -1, -1, -1, null, null, null,
        "test@test.be", null));

    Mockito.when(daoFurniture.updateDescription(1, "test")).thenReturn(true);
    assertTrue(
        furnitureUCC.modifyFurniture(1, condition, -1, -1, -1, -1, null, null, null, null, "test"));

    condition = ValueLink.FurnitureCondition.en_magasin;
    assertTrue(furnitureUCC.modifyFurniture(1, condition, 200.00, 180.00, 100.00, 1, date, date,
        date, "test@test.be", "test"));

    LocalDate dateFalse = LocalDate.of(2000, 10, 15);
    assertThrows(BusinessException.class, () -> furnitureUCC.modifyFurniture(0, null, 120.00,
        100.00, 60.00, 2, dateFalse, dateFalse, dateFalse, "testFalse@testFalse.be", "testFalse"));
  }

  @DisplayName("test modifyFavouritePicture")
  @Test
  public void modifyFavouritePictureTest() {
    PictureDTO picture = pictureFactory.getPicture();
    FurnitureDTO furniture = furnitureFactory.getFurniture();
    picture.setFurniture(furniture);
    furniture.setId(1);
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furniture);
    Mockito.when(daoFurniture.updateFavouritePicture(1, 1)).thenReturn(true);
    Mockito.when(daoPicture.selectPictureById(1)).thenReturn(picture);
    assertTrue(furnitureUCC.modifyFavouritePicture(1));

    Mockito.when(daoFurniture.updateFavouritePicture(1, 1)).thenReturn(false);
    assertThrows(BusinessException.class, () -> furnitureUCC.modifyFavouritePicture(1));

    Mockito.when(daoFurniture.updateFavouritePicture(1, 1)).thenReturn(true);
    Mockito.when(daoPicture.selectPictureById(1)).thenReturn(null);
    assertThrows(NullPointerException.class, () -> furnitureUCC.modifyFavouritePicture(1));
    assertThrows(NullPointerException.class, () -> furnitureUCC.modifyFavouritePicture(0));
  }

  @DisplayName("test getSalesFurnitureAdmin")
  @Test
  public void getSalesFurnitureAdminTest() {
    List<FurnitureDTO> list = new ArrayList<FurnitureDTO>();
    assertNotNull(furnitureUCC.getSalesFurnitureAdmin());
    assertTrue(furnitureUCC.getSalesFurnitureAdmin().isEmpty());
    Mockito.when(daoFurniture.selectSalesFurniture()).thenReturn(list);
    assertEquals(list, furnitureUCC.getSalesFurnitureAdmin());
  }

  @DisplayName("test getFurnituresFiltered")
  @Test
  public void getFurnituresFilteredTest() {
    assertNotNull(furnitureUCC.getFurnituresFiltered("test", 200.00, "usernameTest"));
    assertTrue(furnitureUCC.getFurnituresFiltered("test", 200.00, "usernameTest").isEmpty());
    List<FurnitureDTO> list = new ArrayList<FurnitureDTO>();
    Mockito.when(daoFurniture.selectFurnituresFiltered("test", 200.00, "usernameTest"))
        .thenReturn(list);
    assertEquals(list, furnitureUCC.getFurnituresFiltered("test", 200.00, "usernameTest"));
  }

  @DisplayName("test getFurnitureBuyBy")
  @Test
  public void getFurnitureBuyByTest() {
    List<FurnitureDTO> list = new ArrayList<FurnitureDTO>();
    assertNotNull(furnitureUCC.getFurnitureBuyBy(1));
    assertTrue(furnitureUCC.getFurnitureBuyBy(1).isEmpty());
    Mockito.when(daoFurniture.getFurnitureBoughtByUserId(1)).thenReturn(list);
    assertEquals(list, furnitureUCC.getFurnitureBuyBy(1));
  }

  @DisplayName("test getFurnitureSellBy")
  @Test
  public void getFurnitureSellByTest() {
    List<FurnitureDTO> list = new ArrayList<FurnitureDTO>();
    assertNotNull(furnitureUCC.getFurnitureSellBy(1));
    assertTrue(furnitureUCC.getFurnitureSellBy(1).isEmpty());
    Mockito.when(daoFurniture.getFurnitureSoldByUserId(1)).thenReturn(list);
    assertEquals(list, furnitureUCC.getFurnitureSellBy(1));
  }

  @DisplayName("test selectFurnituresOfVisit")
  @Test
  public void selectFurnituresOfVisitTest() {
    List<FurnitureDTO> list = new ArrayList<FurnitureDTO>();
    assertNotNull(furnitureUCC.selectFurnituresOfVisit(1));
    assertTrue(furnitureUCC.selectFurnituresOfVisit(1).isEmpty());
    Mockito.when(daoFurniture.selectFurnituresOfVisit(1)).thenReturn(list);
    assertEquals(list, furnitureUCC.selectFurnituresOfVisit(1));
  }

}

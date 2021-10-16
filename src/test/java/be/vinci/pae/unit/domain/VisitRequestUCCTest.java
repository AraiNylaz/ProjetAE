package be.vinci.pae.unit.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.InputStream;
import java.sql.Timestamp;
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
import be.vinci.pae.domain.address.AddressDTO;
import be.vinci.pae.domain.address.AddressFactory;
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.furniture.FurnitureFactory;
import be.vinci.pae.domain.picture.PictureDTO;
import be.vinci.pae.domain.picture.PictureFactory;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.domain.user.UserFactory;
import be.vinci.pae.domain.visitrequest.VisitRequestDTO;
import be.vinci.pae.domain.visitrequest.VisitRequestFactory;
import be.vinci.pae.domain.visitrequest.VisitRequestUCCImpl;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.address.DAOAddress;
import be.vinci.pae.services.furniture.DAOFurniture;
import be.vinci.pae.services.picture.DAOPicture;
import be.vinci.pae.services.visitrequest.DAOVisitRequest;

public class VisitRequestUCCTest {

  VisitRequestFactory vrFactory;
  AddressFactory addressFactory;
  PictureFactory pictureFactory;
  FurnitureFactory furnitureFactory;
  UserFactory userFactory;

  @Mock
  DalServices dalServices;

  @Mock
  DAOVisitRequest daoVR;
  @Mock
  DAOFurniture daoFurniture;
  @Mock
  DAOAddress daoAddress;
  @Mock
  DAOPicture daoPicture;

  @InjectMocks
  VisitRequestUCCImpl vrUCC;

  @BeforeEach
  void initAll() {
    MockitoAnnotations.initMocks(this);
    ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinder());
    this.vrFactory = locator.getService(VisitRequestFactory.class);
    this.addressFactory = locator.getService(AddressFactory.class);
    this.pictureFactory = locator.getService(PictureFactory.class);
    this.furnitureFactory = locator.getService(FurnitureFactory.class);
    this.userFactory = locator.getService(UserFactory.class);

  }

  @DisplayName("test if VR is null")
  @Test
  public void vruccNotNullTest() {
    assertNotNull(this.vrUCC);
  }

  @DisplayName("test addVisitRequest")
  @Test
  public void addVisitRequestTest() {
    AddressDTO address = addressFactory.getAddress();
    VisitRequestDTO vr = vrFactory.getVisitRequest();
    vr.setAddress(address);
    List<FurnitureDTO> list = new ArrayList<FurnitureDTO>();
    vr.setFurnitureList(list);
    Mockito.when(daoAddress.addAddress(address)).thenReturn(1);
    Mockito.when(daoVR.addVisitRequest(vr)).thenReturn(1);
    FurnitureDTO furniture = furnitureFactory.getFurniture();
    Mockito.when(daoFurniture.insertFurniture(furniture)).thenReturn(1);
    PictureDTO picture = pictureFactory.getPicture();
    Mockito.when(daoPicture.addPicture(picture)).thenReturn(1);
    List<InputStream> inputStreamList = null;
    UserDTO user = userFactory.getUser();
    assertEquals(vr, vrUCC.addVisitRequest(vr, user, inputStreamList));
    Mockito.when(daoAddress.addAddress(address)).thenReturn(-1);
    assertNull(vrUCC.addVisitRequest(vr, user, inputStreamList));
    Mockito.when(daoAddress.addAddress(address)).thenReturn(1);
    Mockito.when(daoVR.addVisitRequest(vr)).thenReturn(-1);
    assertNull(vrUCC.addVisitRequest(vr, user, inputStreamList));
    Mockito.when(daoFurniture.insertFurniture(furniture)).thenReturn(-1);
    assertNull(vrUCC.addVisitRequest(vr, user, inputStreamList));
    Mockito.when(daoFurniture.insertFurniture(furniture)).thenReturn(1);
    Mockito.when(daoPicture.addPicture(picture)).thenReturn(-1);
    assertNull(vrUCC.addVisitRequest(vr, user, inputStreamList));

  }

  @DisplayName("test getAllVisitsOpenned")
  @Test
  public void getAllVisitsOpennedTest() {
    List<VisitRequestDTO> list = new ArrayList<VisitRequestDTO>();
    assertNotNull(vrUCC.getAllVisitsOpenned());
    assertTrue(vrUCC.getAllVisitsOpenned().isEmpty());
    Mockito.when(daoVR.getAllVisitsOpenned()).thenReturn(list);
    assertEquals(list, vrUCC.getAllVisitsOpenned());
  }

  @DisplayName("test getVisitRequestById")
  @Test
  public void getVisitRequestByIdTest() {
    VisitRequestDTO vr = vrFactory.getVisitRequest();
    Mockito.when(daoVR.selectVisitRequestById(1)).thenReturn(vr);
    assertEquals(vr, vrUCC.getVisitRequestById(1));
    Mockito.when(daoVR.selectVisitRequestById(1)).thenReturn(null);
    assertNull(vrUCC.getVisitRequestById(1));
  }

  @DisplayName("test modifyVisitRequest")
  @Test
  public void modifyVisitRequestTest() {
    assertEquals(null, vrUCC.modifyVisitRequest(1, "test", "2020-10-26T03:45"));
    Mockito.when(daoVR.cancelVisitRequest(1, "test")).thenReturn(true);
    Mockito.when(daoFurniture.refuseAllFurnitureByVisitId(1)).thenReturn(true);
    assertEquals("annulee", vrUCC.modifyVisitRequest(1, "test", "2018-06-12T12:00"));
    Mockito.when(daoVR.cancelVisitRequest(1, "test")).thenReturn(false);
    Mockito.when(daoFurniture.refuseAllFurnitureByVisitId(1)).thenReturn(false);
    assertEquals(null, vrUCC.modifyVisitRequest(1, "test", "2018-06-12T12:00"));
    Mockito
        .when(daoVR.chooseDateForVisit(1,
            Timestamp.valueOf(LocalDate.parse("2018-06-12").atTime(LocalTime.NOON))))
        .thenReturn(true);
    assertEquals("confirmee", vrUCC.modifyVisitRequest(1, "test", "2018-06-12T12:00"));
    Mockito
        .when(daoVR.chooseDateForVisit(1,
            Timestamp.valueOf(LocalDate.parse("2018-06-12").atTime(LocalTime.NOON))))
        .thenReturn(false);
    assertEquals(null, vrUCC.modifyVisitRequest(1, "test", "2018-06-12T12:00"));
    Mockito.when(daoVR.cancelVisitRequest(1, "test")).thenReturn(true);
    Mockito.when(daoFurniture.refuseAllFurnitureByVisitId(1)).thenReturn(true);
    Mockito
        .when(daoVR.chooseDateForVisit(1,
            Timestamp.valueOf(LocalDate.parse("2018-06-12").atTime(LocalTime.NOON))))
        .thenReturn(true);
    assertEquals(null, vrUCC.modifyVisitRequest(1, null, null));
    assertEquals(null, vrUCC.modifyVisitRequest(1, "test2", "2020-10-26T03:45"));
    assertEquals(null, vrUCC.modifyVisitRequest(2, "test", "2018-06-12T12:00"));
  }

  @DisplayName("test getVisitRequestByIdForUser")
  @Test
  public void getVisitRequestByIdForUserTest() {
    VisitRequestDTO vr = vrFactory.getVisitRequest();
    Mockito.when(daoVR.selectVisitRequestById(1)).thenReturn(vr);
    UserDTO user = userFactory.getUser();
    user.setId(1);
    vr.setCustomer(user);
    assertEquals(vr, vrUCC.getVisitRequestByIdForUser(1, 1));
    user.setId(2);
    assertNull(vrUCC.getVisitRequestByIdForUser(1, 1));
  }

  @DisplayName("test getVisitRequestsByUserId")
  @Test
  public void getVisitRequestsByUserIdTest() {
    List<VisitRequestDTO> list = new ArrayList<VisitRequestDTO>();
    assertNotNull(vrUCC.getVisitRequestsByUserId(1));
    assertTrue(vrUCC.getVisitRequestsByUserId(1).isEmpty());
    Mockito.when(daoVR.selectVisitRequestByUserId(1)).thenReturn(list);
    assertEquals(list, vrUCC.getVisitRequestsByUserId(1));
  }


}

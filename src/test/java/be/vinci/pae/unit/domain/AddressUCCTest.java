package be.vinci.pae.unit.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import be.vinci.pae.domain.address.AddressUCCImpl;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.address.DAOAddress;

public class AddressUCCTest {
  AddressFactory addressFactory;
  @Mock
  DalServices dalServices;
  @Mock
  DAOAddress daoAddress;
  @InjectMocks
  AddressUCCImpl addressUCC;

  @BeforeEach
  void initAll() {
    MockitoAnnotations.initMocks(this);
    ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinder());
    this.addressFactory = locator.getService(AddressFactory.class);
  }

  @DisplayName("test if option is null")
  @Test
  public void optionUCCNotNullTest() {
    assertNotNull(this.addressUCC);
  }

  @DisplayName("test getAllCommunes")
  @Test
  public void getAllCommunesTest() {
    List<String> list = new ArrayList<String>();
    assertNotNull(addressUCC.getAllCommunes());
    assertTrue(addressUCC.getAllCommunes().isEmpty());
    Mockito.when(daoAddress.getAllCommunes()).thenReturn(list);
    assertEquals(list, addressUCC.getAllCommunes());
  }

  @DisplayName("test AddressByUserId")
  @Test
  public void getAddressByUserIdTest() {
    AddressDTO address = addressFactory.getAddress();
    Mockito.when(daoAddress.getAddressByUserId(1)).thenReturn(address);
    assertEquals(address, addressUCC.getAddressByUserId(1));
    Mockito.when(daoAddress.getAddressByUserId(1)).thenReturn(null);
    assertNull(addressUCC.getAddressByUserId(1));
  }

}

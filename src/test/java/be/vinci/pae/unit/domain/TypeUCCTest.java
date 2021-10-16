package be.vinci.pae.unit.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import be.vinci.pae.domain.type.TypeDTO;
import be.vinci.pae.domain.type.TypeFactory;
import be.vinci.pae.domain.type.TypeUCCImpl;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.type.DAOType;
import be.vinci.pae.utils.BusinessException;

public class TypeUCCTest {

  TypeFactory typeFactory;

  @Mock
  DalServices dalServices;

  @Mock
  DAOType daoType;

  @InjectMocks
  TypeUCCImpl typeUCC;

  @BeforeEach
  void initAll() {
    MockitoAnnotations.initMocks(this);
    ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinder());
    this.typeFactory = locator.getService(TypeFactory.class);

  }

  @DisplayName("test if type is null")
  @Test
  public void typeUCCNotNullTest() {
    assertNotNull(this.typeUCC);
  }

  @DisplayName("test addType")
  @Test
  public void addTypeTest() {
    Mockito.when(daoType.addType("Test")).thenReturn(1);
    assertEquals(1, typeUCC.addFurnitureType("Test"));
    Mockito.when(daoType.addType("Test")).thenReturn(-1);
    assertThrows(BusinessException.class, () -> typeUCC.addFurnitureType("Test"));
  }

  @DisplayName("test deleteType")
  @Test
  public void deleteTypeTest() {
    Mockito.when(daoType.deleteFurnitureType(1)).thenReturn(true);
    assertTrue(typeUCC.deleteFurnitureType(1));
    Mockito.when(daoType.deleteFurnitureType(1)).thenReturn(false);
    assertThrows(BusinessException.class, () -> typeUCC.deleteFurnitureType(1));
  }

  @DisplayName("test getFurnitureTypes ")
  @Test
  public void getFurnitureTypesTest() {
    List<TypeDTO> list = new ArrayList<TypeDTO>();
    assertNotNull(typeUCC.getFurnitureTypes());
    assertTrue(typeUCC.getFurnitureTypes().isEmpty());
    TypeDTO furniture = typeFactory.getType();
    list.add(furniture);
    Mockito.when(daoType.selectFurnitureTypes()).thenReturn(list);
    assertEquals(list, typeUCC.getFurnitureTypes());
    assertTrue(typeUCC.getFurnitureTypes().contains(furniture));
    assertEquals(1, typeUCC.getFurnitureTypes().size());
  }

  @DisplayName("test getAllTypeNames")
  @Test
  public void getAllTypeNamesTest() {
    List<String> list = new ArrayList<String>();
    assertNotNull(typeUCC.getAllTypeNames());
    assertTrue(typeUCC.getAllTypeNames().isEmpty());
    Mockito.when(daoType.getAllTypesNames()).thenReturn(list);
    assertEquals(list, typeUCC.getAllTypeNames());
  }

}

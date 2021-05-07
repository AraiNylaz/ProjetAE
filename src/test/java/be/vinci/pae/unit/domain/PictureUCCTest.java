package be.vinci.pae.unit.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
import be.vinci.pae.domain.picture.PictureDTO;
import be.vinci.pae.domain.picture.PictureFactory;
import be.vinci.pae.domain.picture.PictureUCCImpl;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.furniture.DAOFurniture;
import be.vinci.pae.services.picture.DAOPicture;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.UploadInterface;

public class PictureUCCTest {

  PictureFactory pictureFactory;

  FurnitureFactory furnitureFactory;

  @Mock
  DalServices dalServices;

  @Mock
  DAOPicture daoPicture;
  @Mock
  DAOFurniture daoFurniture;
  @Mock
  UploadInterface upload;

  @InjectMocks
  PictureUCCImpl pictureUCC;

  @BeforeEach
  void initAll() {
    MockitoAnnotations.initMocks(this);
    ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinder());
    this.furnitureFactory = locator.getService(FurnitureFactory.class);
    this.pictureFactory = locator.getService(PictureFactory.class);

  }

  @DisplayName("test if picture is null")
  @Test
  public void pictureUCCNotNullTest() {
    assertNotNull(this.pictureUCC);
  }

  @DisplayName("test getCarouselPictures")
  @Test
  public void getCarouselPicturesTest() {
    List<PictureDTO> list = new ArrayList<PictureDTO>();
    assertNotNull(pictureUCC.getCarouselPictures());
    assertTrue(pictureUCC.getCarouselPictures().isEmpty());
    Mockito.when(daoPicture.getCarouselPictures()).thenReturn(list);
    assertEquals(list, pictureUCC.getCarouselPictures());
  }

  @DisplayName("test getPicturesByFurnitureId")
  @Test
  public void getPicturesByFurnitureIdTest() {
    List<PictureDTO> list = new ArrayList<PictureDTO>();
    assertNotNull(pictureUCC.getPicturesByFurnitureId(1));
    assertTrue(pictureUCC.getPicturesByFurnitureId(1).isEmpty());
    Mockito.when(daoPicture.selectPicturesByFurnitureId(1)).thenReturn(list);
    assertEquals(list, pictureUCC.getPicturesByFurnitureId(1));
  }

  @DisplayName("test getPublicPicturesByFurnitureId")
  @Test
  public void getPublicPicturesByFurnitureIdTest() {
    List<PictureDTO> list = new ArrayList<PictureDTO>();
    assertNotNull(pictureUCC.getPublicPicturesByFurnitureId(1));
    assertTrue(pictureUCC.getPublicPicturesByFurnitureId(1).isEmpty());
    Mockito.when(daoPicture.selectPublicPicturesByFurnitureId(1)).thenReturn(list);
    assertEquals(list, pictureUCC.getPublicPicturesByFurnitureId(1));
  }

  @DisplayName("test addPicture")
  @Test
  public void addPictureTest() throws FileNotFoundException {

    PictureDTO picture = pictureFactory.getPicture();
    assertThrows(BusinessException.class, () -> pictureUCC.addPicture(-1, picture, null, null));
    FurnitureDTO furniture = furnitureFactory.getFurniture();
    Mockito.when(daoFurniture.selectFurnitureById(1)).thenReturn(furniture);
    assertNull(pictureUCC.addPicture(1, picture, null, null));
    Mockito.when(daoPicture.addPicture(picture)).thenReturn(1);
    assertNull(pictureUCC.addPicture(1, picture, null, null));
    InputStream input = new FileInputStream("C:\\Users\\user\\Desktop\\deuil.jpg");
    String uploadedFileLocation = ".\\images\\1.jpg";
    Mockito.when(daoPicture.addPicture(picture)).thenReturn(1);
    Mockito.when(upload.saveToFile(input, uploadedFileLocation)).thenReturn(true);
    picture.setId(1);
    assertEquals(picture, pictureUCC.addPicture(1, picture, input, "jpg"));
  }

  @DisplayName("test modifyScrollingPicture")
  @Test
  public void modifyScrollingPictureTest() {
    Mockito.when(daoPicture.updateScrollingPicture(1)).thenReturn(false);
    assertThrows(BusinessException.class, () -> pictureUCC.modifyScrollingPicture(1));
    Mockito.when(daoPicture.updateScrollingPicture(1)).thenReturn(true);
    assertTrue(pictureUCC.modifyScrollingPicture(1));
  }

  @DisplayName("test deletePicture")
  @Test
  public void deletePictureTest() {
    PictureDTO picture = pictureFactory.getPicture();
    Mockito.when(daoPicture.selectPictureById(1)).thenReturn(picture);
    picture.setId(1);
    picture.setName("test.jpg");
    FurnitureDTO furniture = furnitureFactory.getFurniture();
    furniture.setId(1);
    picture.setFurniture(furniture);
    furniture.setFavouritePicture(1);
    assertFalse(pictureUCC.deletePicture(1));
    
    Mockito.when(daoPicture.deletePicture(1)).thenReturn(false);
    assertThrows(BusinessException.class, () -> pictureUCC.deletePicture(1));
    //assertFalse(pictureUCC.deletePicture(1));
    //Mockito.when(daoPicture.deletePicture(1)).thenReturn(true);
   // String uploadedFileLocation = ".\\images\\1.jpg";
   //Mockito.when(upload.deleteFile(uploadedFileLocation)).thenReturn(false);
    //assertFalse(pictureUCC.deletePicture(1));
    Mockito.when(daoPicture.deletePicture(1)).thenReturn(true);
    //Mockito.when(upload.deleteFile(uploadedFileLocation)).thenReturn(true);
    assertTrue(pictureUCC.deletePicture(1));
    
  }

  @DisplayName("test modifyVisibleForEveryone")
  @Test
  public void modifyVisibleForEveryoneTest() {
    Mockito.when(daoPicture.updateVisibleForEveryone(1)).thenReturn(false);
    assertThrows(BusinessException.class, () -> pictureUCC.modifyVisibleForEveryone(1));
    Mockito.when(daoPicture.updateVisibleForEveryone(1)).thenReturn(true);
    assertTrue(pictureUCC.modifyVisibleForEveryone(1));
  }

}

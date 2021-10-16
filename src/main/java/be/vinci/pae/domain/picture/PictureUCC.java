package be.vinci.pae.domain.picture;

import java.io.InputStream;
import java.util.List;

public interface PictureUCC {

  List<PictureDTO> getPicturesByFurnitureId(int furnitureId);

  PictureDTO addPicture(int furnitureId, PictureDTO newPicture, InputStream uploadedInputStream,
      String pictureType);

  boolean modifyScrollingPicture(int pictureId);

  boolean deletePicture(int pictureId);

  boolean modifyVisibleForEveryone(int pictureId);

  List<PictureDTO> getPublicPicturesByFurnitureId(int furnitureId);

  List<PictureDTO> getCarouselPictures();

}

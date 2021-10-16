package be.vinci.pae.services.picture;

import java.util.List;
import be.vinci.pae.domain.picture.PictureDTO;

public interface DAOPicture {

  int addPicture(PictureDTO picture);

  boolean updateScrollingPicture(int pictureId);

  PictureDTO selectPictureById(int id);

  boolean deletePicture(int pictureId);

  List<PictureDTO> selectPicturesByFurnitureId(int furnitureId);

  boolean updateVisibleForEveryone(int pictureId);

  List<PictureDTO> selectPublicPicturesByFurnitureId(int furnitureId);

  List<PictureDTO> getCarouselPictures();

  boolean updateScrollingPictureFalseByFurnitureId(int furnitureId);
}

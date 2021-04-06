package be.vinci.pae.services.picture;

import be.vinci.pae.domain.picture.PictureDTO;
import java.util.List;

public interface DAOPicture {

  int addPicture(PictureDTO picture);

  List<PictureDTO> selectPictureByFurnitureId(int idFurniture);

}
package be.vinci.pae.domain.picture;

import java.io.InputStream;
import java.util.List;
import org.apache.commons.text.StringEscapeUtils;
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.furniture.DAOFurniture;
import be.vinci.pae.services.picture.DAOPicture;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.Upload;
import jakarta.inject.Inject;

public class PictureUCCImpl implements PictureUCC {

  @Inject
  private DalServices dalServices;

  @Inject
  private DAOFurniture daoFurniture;

  @Inject
  private DAOPicture daoPicture;

  @Override
  public List<PictureDTO> getPicturesByFurnitureId(int furnitureId) {
    try {
      List<PictureDTO> listPicture = this.daoPicture.selectPicturesByFurnitureId(furnitureId);
      return listPicture;
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public PictureDTO addPicture(int furnitureId, PictureDTO newPicture,
      InputStream uploadedInputStream, String pictureType) {
    Picture picture;
    try {
      this.dalServices.startTransaction();
      picture = (Picture) newPicture;
      FurnitureDTO furniture = this.daoFurniture.selectFurnitureById(furnitureId);
      if (furniture == null) {
        throw new BusinessException("Le meuble n'existe pas");
      }
      picture.setAScrollingPicture(picture.isAScrollingPicture());
      picture.setFurniture(furniture);
      picture.setName(StringEscapeUtils.escapeHtml4(picture.getName()));
      picture.setVisibleForEveryone(picture.isVisibleForEveryone());
      int id = this.daoPicture.addPicture(newPicture);
      if (id == -1) {
        this.dalServices.rollbackTransaction();
        return null;
      } else {
        String uploadedFileLocation = ".\\images\\" + id + "." + pictureType;
        if (Upload.saveToFile(uploadedInputStream, uploadedFileLocation)) {
          this.dalServices.commitTransaction();
          picture.setId(id);
          return picture;
        } else {
          this.dalServices.rollbackTransaction();
        }
      }
      return null;
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public boolean modifyScrollingPicture(int pictureId) {
    try {
      this.dalServices.startTransaction();
      if (!this.daoPicture.updateScrollingPicture(pictureId)) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error modify scrolling picture");
      }
      this.dalServices.commitTransaction();
      return true;
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public boolean deletePicture(int pictureId) {
    FurnitureDTO furnitureDTO = this.daoFurniture.selectFurnitureByFavouritePicture(pictureId);
    PictureDTO pictureDTO = this.daoPicture.selectPictureById(pictureId);
    String pictureType = pictureDTO.getName().substring(pictureDTO.getName().lastIndexOf('.') + 1);
    String uploadedFileLocation = ".\\images\\" + pictureId + "." + pictureType;
    if (furnitureDTO == null) {
      try {
        this.dalServices.startTransaction();
        if (!this.daoPicture.deletePicture(pictureId) || !Upload.deleteFile(uploadedFileLocation)) {
          this.dalServices.rollbackTransaction();
          throw new BusinessException("Error delete picture");
        }
        this.dalServices.commitTransaction();
        return true;
      } finally {
        this.dalServices.closeConnection();
      }

      // TODO remove in server

    } else {
      return false;
    }

  }
}

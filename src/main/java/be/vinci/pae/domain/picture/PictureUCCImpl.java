package be.vinci.pae.domain.picture;

import java.io.InputStream;
import java.util.List;
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.furniture.DAOFurniture;
import be.vinci.pae.services.picture.DAOPicture;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.UploadInterface;
import jakarta.inject.Inject;

public class PictureUCCImpl implements PictureUCC {

  @Inject
  private DalServices dalServices;

  @Inject
  private DAOFurniture daoFurniture;

  @Inject
  private DAOPicture daoPicture;

  @Inject
  private UploadInterface upload;

  @Override
  public List<PictureDTO> getCarouselPictures() {
    try {
      return daoPicture.getCarouselPictures();
    } finally {
      this.dalServices.closeConnection();
    }
  }


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
  public List<PictureDTO> getPublicPicturesByFurnitureId(int furnitureId) {
    try {
      List<PictureDTO> listPicture = this.daoPicture.selectPublicPicturesByFurnitureId(furnitureId);
      return listPicture;
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public PictureDTO addPicture(int furnitureId, PictureDTO newPicture,
      InputStream uploadedInputStream, String pictureType) {
    try {
      this.dalServices.startTransaction();
      FurnitureDTO furniture = this.daoFurniture.selectFurnitureById(furnitureId);
      if (furniture == null) {
        throw new BusinessException("Le meuble n'existe pas");
      }
      newPicture.setFurniture(furniture);
      int id = this.daoPicture.addPicture(newPicture);
      if (id == -1) {
        this.dalServices.rollbackTransaction();
        return null;
      } else {
        String uploadedFileLocation = ".\\images\\" + id + "." + pictureType;
        if (upload.saveToFile(uploadedInputStream, uploadedFileLocation)) {
          this.dalServices.commitTransaction();
          newPicture.setId(id);
          return newPicture;
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
  public boolean deletePicture(int pictureId) {
    PictureDTO pictureDTO = this.daoPicture.selectPictureById(pictureId);
    // String pictureType = pictureDTO.getName().substring(pictureDTO.getName().lastIndexOf('.') + 1);
    // String uploadedFileLocation = ".\\images\\" + pictureId + "." + pictureType;
    if (pictureDTO.getFurniture().getFavouritePicture() != pictureId) {
      try {
        this.dalServices.startTransaction();
        // boolean b1 = this.daoPicture.deletePicture(pictureId);
        /* boolean b2 = */ // upload.deleteFile(uploadedFileLocation);
        // TODO deletefile on disk
        if (!this.daoPicture.deletePicture(pictureId) /* || !b2 */) {
          this.dalServices.rollbackTransaction();
          throw new BusinessException("Error delete picture");
        }

        this.dalServices.commitTransaction();
        return true;
      } finally {
        this.dalServices.closeConnection();
      }

    } else {
      return false;
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
  public boolean modifyVisibleForEveryone(int pictureId) {
    try {
      this.dalServices.startTransaction();
      if (!this.daoPicture.updateVisibleForEveryone(pictureId)) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error modify visible for everyone");
      }
      this.dalServices.commitTransaction();
      return true;
    } finally {
      this.dalServices.closeConnection();
    }
  }


}

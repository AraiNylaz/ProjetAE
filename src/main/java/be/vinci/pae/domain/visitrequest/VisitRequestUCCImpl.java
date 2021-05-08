package be.vinci.pae.domain.visitrequest;

import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.apache.commons.text.StringEscapeUtils;
import be.vinci.pae.domain.address.AddressDTO;
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.picture.PictureDTO;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.address.DAOAddress;
import be.vinci.pae.services.furniture.DAOFurniture;
import be.vinci.pae.services.picture.DAOPicture;
import be.vinci.pae.services.visitrequest.DAOVisitRequest;
import be.vinci.pae.utils.Upload;
import be.vinci.pae.utils.UploadInterface;
import be.vinci.pae.utils.ValueLink.FurnitureCondition;
import be.vinci.pae.utils.ValueLink.VisitRequestStatus;
import jakarta.inject.Inject;

public class VisitRequestUCCImpl implements VisitRequestUCC {

  @Inject
  private DalServices dalServices;

  @Inject
  private DAOFurniture daoFurniture;

  @Inject
  private DAOAddress daoAddress;

  @Inject
  private DAOVisitRequest daoVisitRequest;

  @Inject
  private DAOPicture daoPicture;
  
  @Inject
  private UploadInterface upload;

  @Override
  public VisitRequestDTO addVisitRequest(VisitRequestDTO newVisitRequest, UserDTO user,
      List<InputStream> inputStreamList) {
    VisitRequest visitRequest;
    try {
      this.dalServices.startTransaction();
      visitRequest = (VisitRequest) newVisitRequest;

      visitRequest.setCustomer(user);
      AddressDTO visitRequestAddress = visitRequest.getAddress();
      visitRequestAddress.setBuildingNumber(
          StringEscapeUtils.escapeHtml4(visitRequestAddress.getBuildingNumber()));
      visitRequestAddress
          .setCommune(StringEscapeUtils.escapeHtml4(visitRequestAddress.getCommune()));
      visitRequestAddress
          .setCountry(StringEscapeUtils.escapeHtml4(visitRequestAddress.getCountry()));
      visitRequestAddress
          .setPostcode(StringEscapeUtils.escapeHtml4(visitRequestAddress.getPostcode()));
      visitRequestAddress.setStreet(StringEscapeUtils.escapeHtml4(visitRequestAddress.getStreet()));
      if (visitRequestAddress.getUnitNumber() != null
          && !visitRequestAddress.getUnitNumber().isEmpty()) {
        visitRequestAddress
            .setUnitNumber(StringEscapeUtils.escapeHtml4(visitRequestAddress.getUnitNumber()));
      }
      AddressDTO userAddress = user.getAddress();
      if (!visitRequestAddress.equals(userAddress)) {
        System.out.println("nouvelle address");
        int addressId = this.daoAddress.addAddress(visitRequestAddress);
        if (addressId == -1) {
          this.dalServices.rollbackTransaction();
          return null;
        } else {
          visitRequestAddress.setId(addressId);
          visitRequest.setAddress(visitRequestAddress);
        }
      } else {
        visitRequest.setAddress(null);
      }
      visitRequest.setStatus(VisitRequestStatus.en_attente);
      visitRequest.setTimeSlot(StringEscapeUtils.escapeHtml4(visitRequest.getTimeSlot()));
      visitRequest.setRequestDate(visitRequest.getRequestDate());
      int visitRequestId = this.daoVisitRequest.addVisitRequest(newVisitRequest);
      if (visitRequestId == -1) {
        this.dalServices.rollbackTransaction();
        return null;
      }
      visitRequest.setId(visitRequestId);
      visitRequest.setFurnitureList(visitRequest.getFurnitureList());

      int count = 0;
      for (FurnitureDTO furniture : visitRequest.getFurnitureList()) {
        furniture.setVisitRequest(visitRequest);
        furniture.setCondition(FurnitureCondition.propose);
        int furnitureId = this.daoFurniture.insertFurniture(furniture);
        if (furnitureId == -1) {
          this.dalServices.rollbackTransaction();
          return null;
        }
        furniture.setId(furnitureId);
        for (PictureDTO picture : furniture.getPicturesList()) {
          picture.setFurniture(furniture);
          String pictureType = picture.getName().substring(picture.getName().lastIndexOf('.'));
          picture.setName(UUID.randomUUID() + pictureType);
          int pictureId = this.daoPicture.addPicture(picture);
          if (pictureId == -1) {
            this.dalServices.rollbackTransaction();
            return null;
          } else {
            String uploadedFileLocation = ".\\images\\" + picture.getName();
            if (!upload.saveToFile(inputStreamList.get(count), uploadedFileLocation)) {

              this.dalServices.rollbackTransaction();
              return null;
            } else {
              picture.setId(pictureId);
              count++;
            }
          }

        }
      }
      this.dalServices.commitTransaction();
      return visitRequest;
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public List<VisitRequestDTO> getAllVisitsOpenned() {
    try {
      return daoVisitRequest.getAllVisitsOpenned();
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public VisitRequestDTO getVisitRequestById(int id) {
    try {
      return daoVisitRequest.selectVisitRequestById(id);
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public VisitRequestDTO getVisitRequestByIdForUser(int id, int userId) {
    try {
      VisitRequestDTO visitRequest = daoVisitRequest.selectVisitRequestById(id);
      if (visitRequest.getCustomer().getId() != userId) {
        return null;
      }
      return visitRequest;
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public List<VisitRequestDTO> getVisitRequestsByUserId(int userId) {
    try {
      return daoVisitRequest.selectVisitRequestByUserId(userId);
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public String modifyVisitRequest(int id, String cancellationReason, String chosenDateTime) {
    try {
      dalServices.startTransaction();
      if (cancellationReason != null && daoVisitRequest.cancelVisitRequest(id, cancellationReason)
          && daoFurniture.refuseAllFurnitureByVisitId(id)) {
        dalServices.commitTransaction();
        return VisitRequestStatus.annulee.name();
      } else if (chosenDateTime != null && daoVisitRequest.chooseDateForVisit(id,
          Timestamp.valueOf(LocalDateTime.parse(chosenDateTime)))) {
        dalServices.commitTransaction();
        return VisitRequestStatus.confirmee.name();
      }
      dalServices.rollbackTransaction();
      return null;
    } finally {
      dalServices.closeConnection();
    }
  }
}

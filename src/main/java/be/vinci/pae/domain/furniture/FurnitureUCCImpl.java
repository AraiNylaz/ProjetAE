package be.vinci.pae.domain.furniture;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import be.vinci.pae.domain.option.OptionDTO;
import be.vinci.pae.domain.picture.PictureDTO;
import be.vinci.pae.domain.type.TypeDTO;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.furniture.DAOFurniture;
import be.vinci.pae.services.option.DAOOption;
import be.vinci.pae.services.picture.DAOPicture;
import be.vinci.pae.services.type.DAOType;
import be.vinci.pae.services.user.DAOUser;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.ValueLink.FurnitureCondition;
import be.vinci.pae.utils.ValueLink.UserType;
import jakarta.inject.Inject;

public class FurnitureUCCImpl implements FurnitureUCC {

  @Inject
  private DAOUser daoUser;

  @Inject
  private DAOFurniture daoFurniture;

  @Inject
  private DAOType daoType;

  @Inject
  private DAOOption daoOption;

  @Inject
  private DAOPicture daoPicture;

  @Inject
  private FurnitureFactory furnitureFactory;

  @Inject
  private DalServices dalServices;

  @Override
  public List<FurnitureDTO> getAllFurniture() {
    try {
      List<FurnitureDTO> listFurniture = this.daoFurniture.selectAllFurniture();
      listFurniture = checkFurnitures(listFurniture);
      return listFurniture;
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public List<FurnitureDTO> getFurnitureUsers() {
    try {
      List<FurnitureDTO> listFurniture = this.daoFurniture.selectFurnitureUsers();
      listFurniture = checkFurnitures(listFurniture);
      return listFurniture;
    } finally {
      this.dalServices.closeConnection();
    }
  }


  @Override
  public FurnitureDTO addFurniture(FurnitureDTO newFurniture) {
    // TODO A tester
    try {
      this.dalServices.startTransaction();
      int id = this.daoFurniture.insertFurniture(newFurniture);
      if (id == -1) {
        this.dalServices.rollbackTransaction();
      } else {
        this.dalServices.commitTransaction();
      }
      newFurniture.setId(id);
      return newFurniture;
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public boolean modifyCondition(int id, FurnitureCondition condition) {
    try {
      this.dalServices.startTransaction();

      FurnitureDTO furnitureDTO = daoFurniture.selectFurnitureById(id);
      boolean noError = true;

      switch (condition) {
        case propose:
          if (furnitureDTO.getCondition() == FurnitureCondition.ne_convient_pas
              || furnitureDTO.getCondition() == FurnitureCondition.achete) {
            noError = daoFurniture.updateCondition(id, condition.ordinal());
          }
          break;
        case ne_convient_pas:
          if (furnitureDTO.getCondition() == FurnitureCondition.propose) {
            noError = daoFurniture.updateCondition(id, condition.ordinal());
          }
          break;
        case achete:
          if (furnitureDTO.getCondition() == FurnitureCondition.propose
              || furnitureDTO.getCondition() == FurnitureCondition.emporte_par_patron
              || furnitureDTO.getCondition() == FurnitureCondition.vendu) {
            noError = daoFurniture.updateCondition(id, condition.ordinal());
          }
          break;
        case emporte_par_patron:
          if (furnitureDTO.getCondition() == FurnitureCondition.achete
              || furnitureDTO.getCondition() == FurnitureCondition.en_magasin
              || furnitureDTO.getCondition() == FurnitureCondition.en_restauration
              || furnitureDTO.getCondition() == FurnitureCondition.vendu) {
            noError = daoFurniture.updateCondition(id, condition.ordinal());
          }
          break;
        case en_restauration:
          if (furnitureDTO.getCondition() == FurnitureCondition.emporte_par_patron
              || furnitureDTO.getCondition() == FurnitureCondition.en_magasin
              || furnitureDTO.getCondition() == FurnitureCondition.vendu) {
            noError = daoFurniture.updateCondition(id, condition.ordinal());
          }
          break;
        case en_magasin:
          if (furnitureDTO.getCondition() == FurnitureCondition.en_vente
              || furnitureDTO.getCondition() == FurnitureCondition.emporte_par_patron
              || furnitureDTO.getCondition() == FurnitureCondition.en_restauration
              || furnitureDTO.getCondition() == FurnitureCondition.vendu) {
            noError = daoFurniture.updateDepositDate(id, Instant.now())
                && daoFurniture.updateCondition(id, condition.ordinal());
          }
          break;
        case en_vente:
          if (furnitureDTO.getCondition() == FurnitureCondition.reserve
              || furnitureDTO.getCondition() == FurnitureCondition.en_option
              || furnitureDTO.getCondition() == FurnitureCondition.en_magasin
              || furnitureDTO.getCondition() == FurnitureCondition.retire_de_vente
              || furnitureDTO.getCondition() == FurnitureCondition.vendu) {

            noError = daoFurniture.updateSellingDate(id, Instant.now())
                && daoFurniture.updateCondition(id, condition.ordinal());
          }
          break;
        case retire_de_vente:
          if (furnitureDTO.getCondition() == FurnitureCondition.en_vente
              || furnitureDTO.getCondition() == FurnitureCondition.vendu) {
            noError = daoPicture.updateScrollingPictureFalseByFurnitureId(furnitureDTO.getId())
                && daoFurniture.updateCondition(id, condition.ordinal());
          }
          break;
        case en_option:
          if (furnitureDTO.getCondition() == FurnitureCondition.en_vente
              || furnitureDTO.getCondition() == FurnitureCondition.vendu) {
            noError = daoFurniture.updateCondition(id, condition.ordinal());
          }
          break;
        case vendu:
          if (furnitureDTO.getCondition() == FurnitureCondition.en_option
              || furnitureDTO.getCondition() == FurnitureCondition.en_vente
              || furnitureDTO.getCondition() == FurnitureCondition.livre
              || furnitureDTO.getCondition() == FurnitureCondition.reserve
              || furnitureDTO.getCondition() == FurnitureCondition.emporte_par_client
              || ((furnitureDTO.getCondition() == FurnitureCondition.achete
                  || furnitureDTO.getCondition() == FurnitureCondition.emporte_par_patron
                  || furnitureDTO.getCondition() == FurnitureCondition.en_magasin
                  || furnitureDTO.getCondition() == FurnitureCondition.en_restauration
                  || furnitureDTO.getCondition() == FurnitureCondition.retire_de_vente)
                  && furnitureDTO.getBuyer() != null
                  && furnitureDTO.getBuyer().getUserType() == UserType.antiquaire)) {
            OptionDTO optionDTO = this.daoOption.selectOptionByFurnitureId(id);
            if (optionDTO != null) {
              noError = daoOption.finishOption(optionDTO.getId())
                  && daoFurniture.updateCondition(id, condition.ordinal());
            } else {
              noError = daoFurniture.updateCondition(id, condition.ordinal());
            }
          }else {
            noError = false;
          }
          break;
        case reserve:
          if (furnitureDTO.getCondition() == FurnitureCondition.vendu
              || furnitureDTO.getCondition() == FurnitureCondition.en_vente
              || furnitureDTO.getCondition() == FurnitureCondition.emporte_par_client) {
            noError = daoFurniture.updateCondition(id, condition.ordinal());
          }
          break;
        case emporte_par_client:
          if (furnitureDTO.getCondition() == FurnitureCondition.vendu
              || furnitureDTO.getCondition() == FurnitureCondition.reserve) {
            noError = daoFurniture.updateCondition(id, condition.ordinal());
          }
          break;
        case livre:
          if (furnitureDTO.getCondition() == FurnitureCondition.vendu
              || furnitureDTO.getCondition() == FurnitureCondition.emporte_par_client) {
            noError = daoFurniture.updateCondition(id, condition.ordinal());
          }
          break;
        default:
          break;
      }
      if (!noError) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException(
            "Une erreur est survenue lors de la modification de l'état du meuble.");
      }
      this.dalServices.commitTransaction();
      return true;
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public boolean modifyType(int furnitureId, int typeId) {
    try {
      TypeDTO type = this.daoType.selectTypeById(typeId);
      if (type == null) {
        throw new BusinessException("Type doesn't exist");
      }

      this.dalServices.startTransaction();

      if (!this.daoFurniture.updateType(furnitureId, typeId)) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error modify Type");
      }
      this.dalServices.commitTransaction();
      return true;
    } finally {
      this.dalServices.closeConnection();
    }
  }


  @Override
  public boolean modifyPurchasePrice(int id, double price) {
    try {
      this.dalServices.startTransaction();

      if (!this.daoFurniture.updatePurchasePrice(id, price)) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error modify purchase price");
      }
      this.dalServices.commitTransaction();
      return true;
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public boolean modifySellingPrice(int id, double price) {
    try {
      this.dalServices.startTransaction();

      if (!this.daoFurniture.updateSellingPrice(id, price)) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error modify selling price");
      }
      this.dalServices.commitTransaction();
      return true;
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public boolean modifySpecialSalePrice(int id, double price) {
    try {
      this.dalServices.startTransaction();

      if (!this.daoFurniture.updateSpecialSalePrice(id, price)) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error modify special sale price");
      }
      this.dalServices.commitTransaction();
      return true;
    } finally {
      this.dalServices.closeConnection();
    }
  }


  @Override
  public boolean modifyDescription(int id, String description) {
    try {
      this.dalServices.startTransaction();

      if (!this.daoFurniture.updateDescription(id, description)) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error modify description");
      }
      this.dalServices.commitTransaction();
      return true;
    } finally {
      this.dalServices.closeConnection();
    }
  }


  @Override
  public boolean modifyWithdrawalDateToCustomer(int id, LocalDate time) {
    try {
      this.dalServices.startTransaction();

      if (!this.daoFurniture.updateWithdrawalDateToCustomer(id,
          Timestamp.valueOf(time.atTime(LocalTime.NOON)))) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error modify WithdrawalDateToCustomer");
      }
      this.dalServices.commitTransaction();
      return true;
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public boolean modifyWithdrawalDateFromCustomer(int id, LocalDate time) {
    try {
      this.dalServices.startTransaction();

      if (!this.daoFurniture.updateWithdrawalDateFromCustomer(id,
          Timestamp.valueOf(time.atTime(LocalTime.NOON)))) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error modify WithdrawalDateFromCustomer");
      }
      this.dalServices.commitTransaction();
      return true;
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public boolean modifyDeliveryDate(int id, LocalDate time) {
    try {
      this.dalServices.startTransaction();

      if (!this.daoFurniture.updateDeliveryDate(id,
          Timestamp.valueOf(time.atTime(LocalTime.NOON)))) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error modify delivery date");
      }
      this.dalServices.commitTransaction();
      return true;
    } finally {
      this.dalServices.closeConnection();
    }
  }


  @Override
  public boolean modifyBuyerEmail(int id, String email) {
    try {
      UserDTO userDTO = daoUser.getUserByEmail(email);
      if (userDTO != null) {
        return daoFurniture.updateBuyer(id, userDTO.getId());
      } else {
        return daoFurniture.updateUnregisteredBuyerEmail(id, email);
      }
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public FurnitureDTO getFurnitureById(int id) {
    try {
      FurnitureDTO furniture = this.daoFurniture.selectFurnitureById(id);
      furniture = checkFurniture(furniture);
      return furniture;
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public FurnitureDTO getPersonalFurnitureById(int id, UserDTO user) {
    try {
      FurnitureDTO furniture = this.daoFurniture.selectFurnitureById(id);
      furniture = checkFurniture(furniture);
      // Si tu l'as acheté
      if (furniture.getBuyer() != null && furniture.getBuyer().getId() == user.getId()) {
        return furniture;
      }
      // Si tu l'as vendu
      if (furniture.getVisitRequest().getCustomer().getId() == user.getId()) {
        return furniture;
      }
      return furnitureFactory.getFurniture();
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public boolean modifyFurniture(int furnitureId, FurnitureCondition condition, double sellingPrice,
      double specialSalePrice, double purchasePrice, int type, LocalDate withdrawalDateFromCustomer,
      LocalDate withdrawalDateToCustomer, LocalDate deliveryDate, String buyerEmail,
      String description) {

    FurnitureDTO furnitureDTO = daoFurniture.selectFurnitureById(furnitureId);
    // Ne pas pouvoir modifier des états sans modifier certains champs.
    if (condition != null) {
      if (condition == FurnitureCondition.achete
          && ((furnitureDTO.getPurchasePrice() == 0 && purchasePrice == -1)
              || (withdrawalDateFromCustomer == null
                  && furnitureDTO.getWithdrawalDateFromCustomer() == null))) {
        throw new BusinessException("Veuillez remplir les champs.");
      }
      if (condition == FurnitureCondition.vendu
          && ((withdrawalDateToCustomer != null && deliveryDate != null)
              || ((withdrawalDateToCustomer == null && deliveryDate == null)
                  || (furnitureDTO.getWithdrawalDateToCustomer() == null
                      && furnitureDTO.getDeliveryDate() == null))
                  && (buyerEmail == null
                      && (furnitureDTO.getBuyer() == null && furnitureDTO.getBuyer() == null)))) {
        throw new BusinessException("Veuillez remplir les champs.");
      }
      if (condition == FurnitureCondition.en_vente
          && (sellingPrice == -1 && furnitureDTO.getSellingPrice() == 0)) {
        throw new BusinessException("Veuillez remplir les champs.");
      }
    }

    try {
      dalServices.startMultipleTransactions();
      boolean noError = true;
      if (sellingPrice != -1) {
        noError = noError && modifySellingPrice(furnitureId, sellingPrice);
      }
      if (type != -1) {
        noError = noError && modifyType(furnitureId, type);
      }
      if (purchasePrice != -1) {
        noError = noError && modifyPurchasePrice(furnitureId, purchasePrice);
      }
      if (description != null) {
        noError = noError && modifyDescription(furnitureId, description);
      }
      if (withdrawalDateFromCustomer != null) {
        noError =
            noError && modifyWithdrawalDateFromCustomer(furnitureId, withdrawalDateFromCustomer);
      }
      if (withdrawalDateToCustomer != null) {
        noError = noError && modifyWithdrawalDateToCustomer(furnitureId, withdrawalDateToCustomer);
      }
      if (deliveryDate != null) {
        noError = noError && modifyDeliveryDate(furnitureId, deliveryDate);
      }
      if (buyerEmail != null) {
        noError = noError && modifyBuyerEmail(furnitureId, buyerEmail);
      }
      if (specialSalePrice != -1 && furnitureDTO.getBuyer().getUserType() == UserType.antiquaire) {
        noError = noError && modifySpecialSalePrice(furnitureId, specialSalePrice);
      }
      if (condition != null) {
        noError = noError && modifyCondition(furnitureId, condition);
      }
      if (noError) {
        dalServices.commitMultipleTransactions();
        return true;
      }
      dalServices.rollbackMultipleTransaction();
      return false;
    } finally {
      dalServices.closeConnection();
    }
  }


  @Override
  public boolean modifyFavouritePicture(int pictureId) {
    PictureDTO pictureDTO = this.daoPicture.selectPictureById(pictureId);
    try {
      this.dalServices.startTransaction();
      if (!this.daoFurniture.updateFavouritePicture(pictureDTO.getFurniture().getId(), pictureId)) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error modify picture");
      }
      this.dalServices.commitTransaction();
      return true;
    } finally {
      this.dalServices.closeConnection();
    }

  }

  @Override
  public List<FurnitureDTO> getSalesFurnitureAdmin() {
    try {
      return this.daoFurniture.selectSalesFurniture();
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public List<FurnitureDTO> getFurnituresFiltered(String type, double price, String username) {
    try {
      return this.daoFurniture.selectFurnituresFiltered(type, price, username);
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public List<FurnitureDTO> getFurnitureBuyBy(int id) {
    try {
      return daoFurniture.getFurnitureBoughtByUserId(id);
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public List<FurnitureDTO> getFurnitureSellBy(int id) {
    try {
      return daoFurniture.getFurnitureSoldByUserId(id);
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public List<FurnitureDTO> selectFurnituresOfVisit(int id) {
    try {
      return daoFurniture.selectFurnituresOfVisit(id);
    } finally {
      this.dalServices.closeConnection();
    }
  }

  private List<FurnitureDTO> checkFurnitures(List<FurnitureDTO> listFurniture) {
    for (int i = 0; i < listFurniture.size(); i++) {
      listFurniture.set(i, checkFurniture(listFurniture.get(i)));
    }
    return listFurniture;
  }

  private FurnitureDTO checkFurniture(FurnitureDTO furnitureDTO) {
    if (furnitureDTO != null && furnitureDTO.getWithdrawalDateToCustomer() != null) {
      System.out.println("date emport " + furnitureDTO.getWithdrawalDateToCustomer());
      Date now = new Date();
      System.out.println("durée en jour " + TimeUnit.DAYS.convert(
          now.getTime() - furnitureDTO.getWithdrawalDateToCustomer().getTime(),
          TimeUnit.MILLISECONDS));
      if (furnitureDTO.getCondition() == FurnitureCondition.vendu
          && TimeUnit.DAYS.convert(
              furnitureDTO.getWithdrawalDateToCustomer().getTime() - now.getTime(),
              TimeUnit.MILLISECONDS) < 0
          && modifyCondition(furnitureDTO.getId(), FurnitureCondition.reserve)) {
        System.out.println("passage en reserve");
        furnitureDTO.setCondition(FurnitureCondition.reserve);
      }
      if (furnitureDTO.getCondition() == FurnitureCondition.reserve && TimeUnit.DAYS.convert(
          now.getTime() - furnitureDTO.getWithdrawalDateToCustomer().getTime(),
          TimeUnit.MILLISECONDS) > 366 && returnToSelling(furnitureDTO.getId())) {
        System.out.println("passage en vente");
        furnitureDTO.setCondition(FurnitureCondition.en_vente);
      }
    }
    return furnitureDTO;
  }

  private boolean returnToSelling(int id) {
    try {
      dalServices.startTransaction();
      if (daoFurniture.returnToSelling(id)) {
        dalServices.commitTransaction();
        return true;
      }
      dalServices.rollbackTransaction();
      return false;
    } finally {
      this.dalServices.closeConnection();
    }
  }

}

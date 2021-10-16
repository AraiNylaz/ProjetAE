package be.vinci.pae.domain.furniture;

import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.utils.ValueLink.FurnitureCondition;
import java.time.LocalDate;
import java.util.List;

public interface FurnitureUCC {

  List<FurnitureDTO> getAllFurniture();

  List<FurnitureDTO> getFurnitureUsers();

  FurnitureDTO addFurniture(FurnitureDTO furniture);

  boolean modifyCondition(int id, FurnitureCondition condition);

  boolean modifyType(int furnitureId, int typeId);

  boolean modifyPurchasePrice(int id, double price);

  boolean modifyDescription(int id, String description);

  boolean modifyBuyerEmail(int id, String email);

  FurnitureDTO getFurnitureById(int id);

  List<FurnitureDTO> getSalesFurnitureAdmin();

  boolean modifyDeliveryDate(int id, LocalDate time);

  boolean modifyWithdrawalDateFromCustomer(int id, LocalDate time);

  boolean modifyWithdrawalDateToCustomer(int id, LocalDate time);

  boolean modifySellingPrice(int id, double price);

  boolean modifySpecialSalePrice(int id, double price);

  boolean modifyFavouritePicture(int pictureId);

  List<FurnitureDTO> getFurnituresFiltered(String type, double price, String username);

  List<FurnitureDTO> getFurnitureBuyBy(int id);

  List<FurnitureDTO> getFurnitureSellBy(int id);

  List<FurnitureDTO> selectFurnituresOfVisit(int id);

  FurnitureDTO getPersonalFurnitureById(int id, UserDTO user);

  boolean modifyFurniture(int furnitureId, FurnitureCondition condition, double sellingPrice,
      double specialSalePrice, double purchasePrice, int type, LocalDate withdrawalDateFromCustomer,
      LocalDate withdrawalDateToCustomer, LocalDate deliveryDate,
      String buyerEmail, String description);
}

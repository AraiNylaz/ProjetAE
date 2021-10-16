package be.vinci.pae.services.furniture;

import be.vinci.pae.domain.furniture.FurnitureDTO;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public interface DAOFurniture {

  List<FurnitureDTO> selectAllFurniture();

  List<FurnitureDTO> selectFurnitureUsers();

  FurnitureDTO selectFurnitureById(int id);

  int insertFurniture(FurnitureDTO newFurniture);

  boolean updateSellingDate(int id, Instant now);

  boolean updateCondition(int id, int condition);

  boolean updateDepositDate(int id, Instant now);

  boolean updateSellingPrice(int id, Double price);

  boolean updateDescription(int id, String description);

  List<FurnitureDTO> selectSalesFurniture();

  boolean updateType(int furnitureId, int typeId);

  boolean updatePurchasePrice(int id, Double price);

  boolean updateWithdrawalDateToCustomer(int id, Timestamp now);

  boolean updateWithdrawalDateFromCustomer(int id, Timestamp now);

  boolean updateDeliveryDate(int id, Timestamp now);

  boolean updateSpecialSalePrice(int id, Double price);

  boolean updateUnregisteredBuyerEmail(int id, String buyer);

  boolean updateBuyer(int id, int buyerId);

  boolean updateFavouritePicture(int id, int pictureId);

  List<FurnitureDTO> selectFurnituresFiltered(String type, double price, String username);

  List<FurnitureDTO> getFurnitureBoughtByUserId(int id);

  List<FurnitureDTO> getFurnitureSoldByUserId(int id);

  boolean returnToSelling(int id);

  boolean refuseAllFurnitureByVisitId(int id);

  List<FurnitureDTO> selectFurnituresOfVisit(int id);
}

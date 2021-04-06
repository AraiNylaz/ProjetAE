package be.vinci.pae.services.furniture;

import java.time.Instant;
import java.util.List;
import be.vinci.pae.domain.furniture.FurnitureDTO;

public interface DAOFurniture {

  List<FurnitureDTO> selectAllFurniture();

  List<FurnitureDTO> selectFurnitureByType(String type);

  List<FurnitureDTO> selectFurnitureByPrice(double price);

  List<FurnitureDTO> selectFurnitureByUser(String lastNameCustomer);

  FurnitureDTO selectFurnitureById(int id);

  int insertFurniture(FurnitureDTO newFurniture);

  boolean updateSellingDate(int id, Instant now);

  boolean updateCondition(int id, int condition);

  boolean updateDepositDate(int id, Instant now);

  boolean updateSellingPrice(int id, Double price);

}
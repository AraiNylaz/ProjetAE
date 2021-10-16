package be.vinci.pae.services.furniture;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.furniture.FurnitureFactory;
import be.vinci.pae.domain.picture.PictureDTO;
import be.vinci.pae.services.DalBackendServices;
import be.vinci.pae.services.type.DAOType;
import be.vinci.pae.services.user.DAOUser;
import be.vinci.pae.services.visitrequest.DAOVisitRequest;
import be.vinci.pae.utils.FatalException;
import be.vinci.pae.utils.ValueLink.FurnitureCondition;
import jakarta.inject.Inject;

public class DAOFurnitureImpl implements DAOFurniture {

  private String querySelectAllFurniture;
  private String querySelectFurnitureUser;
  private String querySelectFurnitureById;
  private String queryInsertFurniture;
  private String queryUpdateSellingDate;
  private String queryUpdateCondition;
  private String queryUpdateDepositDate;
  private String queryUpdateSellingPrice;
  private String queryUpdateSpecialSalePrice;
  private String querySelectSalesFurniture;
  private String querySelectFurnituresFiltered;
  private String querySelectFurnitureBoughtByUserId;
  private String querySelectFurnitureSoldByUserId;
  private String queryUpdateType;
  private String queryUpdatePurchasePrice;
  private String queryUpdateDescription;
  private String queryUpdateWithdrawalDateToCustomer;
  private String queryUpdateWithdrawalDateFromCustomer;
  private String queryUpdateDeliveryDate;
  private String queryUpdateUnregisteredBuyerEmail;
  private String queryUpdateBuyer;
  private String queryUpdateFavouritePicture;
  private String queryDeleteBuyer;
  private String queryUpdateRefuseAllFurnitureByVisitId;
  private String querySelectFurnituresOfVisit;

  @Inject
  private FurnitureFactory furnitureFactory;

  @Inject
  private DalBackendServices dalServices;

  @Inject
  private DAOType daoType;

  @Inject
  private DAOVisitRequest daoVisitRequest;

  @Inject
  private DAOUser daoUser;


  /**
   * Contructor of DAOFurnitureImpl. Contain queries.
   */
  public DAOFurnitureImpl() {
    querySelectAllFurniture = "SELECT f.furniture_id, f.description, f.type,"
        + " f.visit_request, f.purchase_price, f.withdrawal_date_from_customer, f.selling_price,"
        + " f.special_sale_price, f.deposit_date, f.selling_date, f.delivery_date,"
        + " f.withdrawal_date_to_customer, f.buyer,f.condition, f.unregistered_buyer_email,"
        + " f.favourite_picture FROM project.furniture f";
    querySelectFurnitureUser = "SELECT f.furniture_id, f.description, f.type,"
        + " f.visit_request, f.purchase_price, f.withdrawal_date_from_customer, f.selling_price,"
        + " f.special_sale_price, f.deposit_date, f.selling_date, f.delivery_date,"
        + " f.withdrawal_date_to_customer, f.buyer,f.condition, f.unregistered_buyer_email,"
        + " f.favourite_picture FROM project.furniture f WHERE f.condition = ? OR f.condition = ?";
    querySelectFurnitureById = "SELECT f.furniture_id, f.description, f.type, f.visit_request,"
        + " f.purchase_price, f.withdrawal_date_from_customer, f.selling_price,"
        + " f.special_sale_price, f.deposit_date, f.selling_date, f.delivery_date,"
        + " f.withdrawal_date_to_customer, f.buyer, f.condition, f.unregistered_buyer_email,"
        + " f.favourite_picture FROM project.furniture f WHERE f.furniture_id = ?";
    queryInsertFurniture = "INSERT INTO project.furniture (furniture_id, description, type,"
        + " visit_request, purchase_price, withdrawal_date_from_customer, selling_price,"
        + " special_sale_price, deposit_date, selling_date, delivery_date,"
        + " withdrawal_date_to_customer, buyer, condition, unregistered_buyer_email,"
        + " favourite_picture) VALUES (DEFAULT,?,?,?,"
        + "NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,?,NULL,NULL)";
    queryUpdateSellingDate = "UPDATE project.furniture SET selling_date = ? WHERE furniture_id = ?";
    queryUpdateCondition = "UPDATE project.furniture SET condition = ? WHERE furniture_id = ?";
    queryUpdateDepositDate = "UPDATE project.furniture SET deposit_date = ? WHERE furniture_id = ?";
    queryUpdateSellingPrice =
        "UPDATE project.furniture SET selling_price = ? WHERE furniture_id = ?";
    queryUpdateSpecialSalePrice =
        "UPDATE project.furniture SET special_sale_price = ? WHERE furniture_id = ?";
    querySelectSalesFurniture = "SELECT f.furniture_id, f.description, f.type,"
        + " f.visit_request, f.purchase_price, f.withdrawal_date_from_customer, f.selling_price,"
        + " f.special_sale_price, f.deposit_date, f.selling_date, f.delivery_date,"
        + " f.withdrawal_date_to_customer, f.buyer,f.condition, f.unregistered_buyer_email,"
        + " f.favourite_picture FROM project.furniture f " + " WHERE f.condition = "
        + FurnitureCondition.vendu.ordinal() + " OR f.condition = "
        + FurnitureCondition.propose.ordinal();
    querySelectFurnituresFiltered = "SELECT DISTINCT f.furniture_id, f.description, f.type,"
        + " f.visit_request, f.purchase_price, f.withdrawal_date_from_customer, f.selling_price,"
        + " f.special_sale_price, f.deposit_date, f.selling_date, f.delivery_date,"
        + " f.withdrawal_date_to_customer, f.buyer,f.condition, f.unregistered_buyer_email,"
        + " f.favourite_picture FROM project.furniture f, project.furniture_types t, "
        + "project.users u, project.visit_requests v WHERE lower(t.name) LIKE lower(?) AND "
        + "(f.purchase_price < ? OR f.selling_price < ?) AND lower(u.username) LIKE lower(?) "
        + "AND f.type = t.type_id AND (f.buyer = u.user_id OR "
        + "(v.customer = u.user_id AND v.visit_request_id = f.visit_request))";
    querySelectFurnitureBoughtByUserId = "SELECT f.furniture_id, f.description, f.type,"
        + " f.visit_request, f.purchase_price, f.withdrawal_date_from_customer, f.selling_price,"
        + " f.special_sale_price, f.deposit_date, f.selling_date, f.delivery_date,"
        + " f.withdrawal_date_to_customer, f.buyer,f.condition, f.unregistered_buyer_email,"
        + " f.favourite_picture FROM project.furniture f WHERE f.buyer = ?";
    querySelectFurnitureSoldByUserId = "SELECT f.furniture_id, f.description, f.type,"
        + " f.visit_request, f.purchase_price, f.withdrawal_date_from_customer, f.selling_price,"
        + " f.special_sale_price, f.deposit_date, f.selling_date, f.delivery_date,"
        + " f.withdrawal_date_to_customer, f.buyer,f.condition, f.unregistered_buyer_email,"
        + " f.favourite_picture FROM project.furniture f, project.visit_requests v"
        + " WHERE f.visit_request = v.visit_request_id AND v.customer = ?";
    queryUpdateType = "UPDATE project.furniture SET type = ? WHERE furniture_id = ?";
    queryUpdatePurchasePrice =
        "UPDATE project.furniture SET purchase_price = ? WHERE furniture_id = ?";
    queryUpdateDescription = "UPDATE project.furniture SET description = ? WHERE furniture_id = ?";
    queryUpdateWithdrawalDateToCustomer =
        "UPDATE project.furniture SET withdrawal_date_to_customer = ? WHERE furniture_id = ?";
    queryUpdateWithdrawalDateFromCustomer =
        "UPDATE project.furniture SET withdrawal_date_from_customer = ? WHERE furniture_id = ?";
    queryUpdateDeliveryDate =
        "UPDATE project.furniture SET delivery_date = ? WHERE furniture_id = ?";
    queryUpdateUnregisteredBuyerEmail =
        "UPDATE project.furniture SET unregistered_buyer_email = ? WHERE furniture_id = ?";
    queryUpdateBuyer = "UPDATE project.furniture SET buyer = ? WHERE furniture_id = ?";
    queryUpdateFavouritePicture =
        "UPDATE project.furniture SET favourite_picture = ? WHERE furniture_id = ?";
    queryDeleteBuyer = "UPDATE project.furniture SET buyer = null, unregistered_buyer_email = null "
        + "WHERE furniture_id = ?";
    queryUpdateRefuseAllFurnitureByVisitId =
        "UPDATE project.furniture SET condition = ? " + "WHERE visit_request = ?";
    querySelectFurnituresOfVisit = "SELECT f.furniture_id, f.description, f.type,"
        + " f.visit_request, f.purchase_price, f.withdrawal_date_from_customer, f.selling_price,"
        + " f.special_sale_price, f.deposit_date, f.selling_date, f.delivery_date,"
        + " f.withdrawal_date_to_customer, f.buyer,f.condition, f.unregistered_buyer_email,"
        + " f.favourite_picture FROM project.furniture f WHERE f.visit_request = ?";
  }

  @Override
  public List<FurnitureDTO> selectAllFurniture() {
    try {
      PreparedStatement selectAllFurniture =
          dalServices.getPreparedStatement(querySelectAllFurniture);
      try (ResultSet rs = selectAllFurniture.executeQuery()) {
        List<FurnitureDTO> listFurniture = new ArrayList<>();
        FurnitureDTO furniture;
        do {
          furniture = createFurniture(rs);
          listFurniture.add(furniture);
        } while (furniture != null);
        listFurniture.remove(listFurniture.size() - 1);
        return listFurniture;
      }
    } catch (Exception e) {
      throw new FatalException("Data error : selectAllFurnitures", e);
    }
  }

  @Override
  public List<FurnitureDTO> selectFurnitureUsers() {
    List<FurnitureDTO> list = new ArrayList<FurnitureDTO>();
    try {
      PreparedStatement selectFurnitureUsers =
          dalServices.getPreparedStatement(querySelectFurnitureUser);
      selectFurnitureUsers.setInt(1, FurnitureCondition.en_vente.ordinal());
      selectFurnitureUsers.setInt(2, FurnitureCondition.en_option.ordinal());
      try (ResultSet rs = selectFurnitureUsers.executeQuery()) {
        FurnitureDTO furniture;
        do {
          furniture = createFurniture(rs);
          list.add(furniture);
        } while (furniture != null);
        list.remove(list.size() - 1);
      }
      return list;
    } catch (Exception e) {
      throw new FatalException("Data error : selectAllFurnituresUser", e);
    }
  }

  @Override
  public FurnitureDTO selectFurnitureById(int id) {
    try {
      PreparedStatement selectFurnitureById =
          dalServices.getPreparedStatement(querySelectFurnitureById);
      selectFurnitureById.setInt(1, id);
      try (ResultSet rs = selectFurnitureById.executeQuery()) {
        return createFurniture(rs);
      }
    } catch (Exception e) {
      throw new FatalException("Data error : selectFurnitureById", e);
    }
  }

  @Override
  public int insertFurniture(FurnitureDTO newFurniture) {
    int furnitureId = -1;
    try {
      PreparedStatement insertFurniture =
          this.dalServices.getPreparedStatementAdd(queryInsertFurniture);
      insertFurniture.setString(1, newFurniture.getDescription());
      insertFurniture.setInt(2, newFurniture.getType().getId());
      insertFurniture.setInt(3, newFurniture.getVisitRequest().getId());
      insertFurniture.setInt(4, newFurniture.getCondition().ordinal());
      insertFurniture.execute();
      try (ResultSet rs = insertFurniture.getGeneratedKeys()) {
        if (rs.next()) {
          furnitureId = rs.getInt(1);
        }
        return furnitureId;
      }
    } catch (SQLException e) {
      throw new FatalException("Data error : insertFurniture", e);
    }
  }

  @Override
  public boolean updateSellingDate(int id, Instant now) {
    try {
      PreparedStatement updateSellingDate =
          this.dalServices.getPreparedStatement(queryUpdateSellingDate);
      updateSellingDate.setTimestamp(1, Timestamp.from(now));
      updateSellingDate.setInt(2, id);
      return updateSellingDate.executeUpdate() == 1;
    } catch (SQLException e) {
      throw new FatalException("Data error : updateSellingDate", e);
    }
  }

  @Override
  public boolean updateSellingPrice(int id, Double price) {
    try {
      PreparedStatement updateSellingPrice =
          this.dalServices.getPreparedStatement(queryUpdateSellingPrice);
      updateSellingPrice.setDouble(1, price);
      updateSellingPrice.setInt(2, id);
      return updateSellingPrice.executeUpdate() == 1;
    } catch (SQLException e) {
      throw new FatalException("Data error : updateSellingPrice", e);
    }
  }

  @Override
  public boolean updateSpecialSalePrice(int id, Double price) {
    try {
      PreparedStatement updateSpecialSalePrice =
          this.dalServices.getPreparedStatement(queryUpdateSpecialSalePrice);
      updateSpecialSalePrice.setDouble(1, price);
      updateSpecialSalePrice.setInt(2, id);
      return updateSpecialSalePrice.executeUpdate() == 1;
    } catch (SQLException e) {
      throw new FatalException("Data error : updateSpecialSalePrice", e);
    }
  }

  @Override
  public boolean updateCondition(int id, int condition) {
    try {
      PreparedStatement updateCondition =
          this.dalServices.getPreparedStatement(queryUpdateCondition);
      updateCondition.setInt(1, condition);
      updateCondition.setInt(2, id);
      return updateCondition.executeUpdate() == 1;
    } catch (Exception e) {
      throw new FatalException("Data error : updateCondition", e);
    }
  }

  @Override
  public boolean updateDepositDate(int id, Instant now) {
    try {
      PreparedStatement updateDepositDate =
          this.dalServices.getPreparedStatement(queryUpdateDepositDate);
      updateDepositDate.setTimestamp(1, Timestamp.from(now));
      updateDepositDate.setInt(2, id);
      return updateDepositDate.executeUpdate() == 1;
    } catch (Exception e) {
      throw new FatalException("Data error : updateDepositDate", e);
    }
  }

  @Override
  public boolean updateType(int furnitureId, int typeId) {
    try {
      PreparedStatement updateType = this.dalServices.getPreparedStatement(queryUpdateType);
      updateType.setInt(1, typeId);
      updateType.setInt(2, furnitureId);
      return updateType.executeUpdate() == 1;
    } catch (Exception e) {
      throw new FatalException("Data error : updateType", e);
    }
  }

  @Override
  public boolean updatePurchasePrice(int id, Double price) {
    try {
      PreparedStatement updatePurchasePrice =
          this.dalServices.getPreparedStatement(queryUpdatePurchasePrice);
      updatePurchasePrice.setDouble(1, price);
      updatePurchasePrice.setInt(2, id);
      return updatePurchasePrice.executeUpdate() == 1;
    } catch (SQLException e) {
      throw new FatalException("Data error : updatePurchasePrice", e);
    }
  }

  @Override
  public boolean updateDescription(int id, String description) {
    try {
      PreparedStatement updateDescription =
          this.dalServices.getPreparedStatement(queryUpdateDescription);
      updateDescription.setString(1, description);
      updateDescription.setInt(2, id);
      return updateDescription.executeUpdate() == 1;
    } catch (SQLException e) {
      throw new FatalException("Data error : updateDescription", e);
    }
  }

  @Override
  public boolean updateWithdrawalDateToCustomer(int id, Timestamp now) {
    try {
      PreparedStatement updateWithdrawalDateToCustomer =
          this.dalServices.getPreparedStatement(queryUpdateWithdrawalDateToCustomer);
      updateWithdrawalDateToCustomer.setTimestamp(1, now);
      updateWithdrawalDateToCustomer.setInt(2, id);
      return updateWithdrawalDateToCustomer.executeUpdate() == 1;
    } catch (Exception e) {
      throw new FatalException("Data error : updateWithdrawalDateToCustomer", e);
    }
  }

  @Override
  public boolean updateWithdrawalDateFromCustomer(int id, Timestamp now) {
    try {
      PreparedStatement updateWithdrawalDateFromCustomer =
          this.dalServices.getPreparedStatement(queryUpdateWithdrawalDateFromCustomer);
      updateWithdrawalDateFromCustomer.setTimestamp(1, now);
      updateWithdrawalDateFromCustomer.setInt(2, id);
      return updateWithdrawalDateFromCustomer.executeUpdate() == 1;
    } catch (Exception e) {
      throw new FatalException("Data error : updateWithdrawalDateFromCustomer", e);
    }
  }

  @Override
  public boolean updateDeliveryDate(int id, Timestamp now) {
    try {
      PreparedStatement updateDeliveryDate =
          this.dalServices.getPreparedStatement(queryUpdateDeliveryDate);
      updateDeliveryDate.setTimestamp(1, now);
      updateDeliveryDate.setInt(2, id);
      return updateDeliveryDate.executeUpdate() == 1;
    } catch (Exception e) {
      throw new FatalException("Data error : updateDeliveryDate", e);
    }
  }

  @Override
  public boolean updateUnregisteredBuyerEmail(int id, String buyerEmail) {
    try {
      PreparedStatement updateUnregisteredBuyerEmail =
          this.dalServices.getPreparedStatement(queryUpdateUnregisteredBuyerEmail);
      updateUnregisteredBuyerEmail.setString(1, buyerEmail);
      updateUnregisteredBuyerEmail.setInt(2, id);
      return updateUnregisteredBuyerEmail.executeUpdate() == 1;
    } catch (Exception e) {
      throw new FatalException("Data error : updateUnregisteredBuyerEmail", e);
    }
  }

  @Override
  public boolean updateBuyer(int id, int buyerId) {
    try {
      PreparedStatement updateUnregisteredBuyer =
          this.dalServices.getPreparedStatement(queryUpdateBuyer);
      updateUnregisteredBuyer.setInt(1, buyerId);
      updateUnregisteredBuyer.setInt(2, id);
      return updateUnregisteredBuyer.executeUpdate() == 1;
    } catch (Exception e) {
      throw new FatalException("Data error : updateBuyer", e);
    }
  }


  @Override
  public List<FurnitureDTO> selectSalesFurniture() {
    try {
      PreparedStatement selectSalesFurniture =
          dalServices.getPreparedStatement(querySelectSalesFurniture);
      try (ResultSet rs = selectSalesFurniture.executeQuery()) {
        List<FurnitureDTO> listFurniture = new ArrayList<>();
        FurnitureDTO furniture;
        do {
          furniture = createFurniture(rs);
          listFurniture.add(furniture);
        } while (furniture != null);
        listFurniture.remove(listFurniture.size() - 1);
        return listFurniture;
      }
    } catch (Exception e) {
      throw new FatalException("Data error : selectSalesFurnitures", e);
    }
  }

  @Override
  public List<FurnitureDTO> selectFurnituresFiltered(String type, double price, String username) {
    try {
      PreparedStatement selectFurnituresFiltered =
          dalServices.getPreparedStatement(querySelectFurnituresFiltered);
      selectFurnituresFiltered.setString(1, type + "%");
      selectFurnituresFiltered.setDouble(2, price);
      selectFurnituresFiltered.setDouble(3, price);
      selectFurnituresFiltered.setString(4, username + "%");
      try (ResultSet rs = selectFurnituresFiltered.executeQuery()) {
        List<FurnitureDTO> listFurniture = new ArrayList<>();
        FurnitureDTO furniture;
        do {
          furniture = createFurniture(rs);
          listFurniture.add(furniture);
        } while (furniture != null);
        listFurniture.remove(listFurniture.size() - 1);
        return listFurniture;
      }
    } catch (Exception e) {
      throw new FatalException("Data error : selectFurnituresFiltered", e);
    }
  }

  @Override
  public List<FurnitureDTO> getFurnitureBoughtByUserId(int id) {
    try {
      PreparedStatement selectFurnitureBoughtByUserId =
          dalServices.getPreparedStatement(querySelectFurnitureBoughtByUserId);
      selectFurnitureBoughtByUserId.setInt(1, id);
      try (ResultSet rs = selectFurnitureBoughtByUserId.executeQuery()) {
        List<FurnitureDTO> listFurniture = new ArrayList<>();
        FurnitureDTO furniture;
        do {
          furniture = createFurniture(rs);
          listFurniture.add(furniture);
        } while (furniture != null);
        listFurniture.remove(listFurniture.size() - 1);
        return listFurniture;
      }
    } catch (Exception e) {
      throw new FatalException("Data error : getFurnitureBuyBy", e);
    }
  }

  @Override
  public List<FurnitureDTO> getFurnitureSoldByUserId(int id) {
    try {
      PreparedStatement selectFurnitureSoldByUserId =
          dalServices.getPreparedStatement(querySelectFurnitureSoldByUserId);
      selectFurnitureSoldByUserId.setInt(1, id);
      try (ResultSet rs = selectFurnitureSoldByUserId.executeQuery()) {
        List<FurnitureDTO> listFurniture = new ArrayList<>();
        FurnitureDTO furniture;
        do {
          furniture = createFurniture(rs);
          listFurniture.add(furniture);
        } while (furniture != null);
        listFurniture.remove(listFurniture.size() - 1);
        return listFurniture;
      }
    } catch (Exception e) {
      throw new FatalException("Data error : getFurnitureSellBy", e);
    }
  }

  @Override
  public boolean returnToSelling(int id) {
    try {
      PreparedStatement deleteBuyer = this.dalServices.getPreparedStatement(queryDeleteBuyer);
      deleteBuyer.setInt(1, id);
      return updateCondition(id, FurnitureCondition.en_vente.ordinal())
          && updateWithdrawalDateToCustomer(id, null) && deleteBuyer.executeUpdate() == 1;
    } catch (Exception e) {
      throw new FatalException("Data error : returnToSelling", e);
    }
  }

  @Override
  public boolean refuseAllFurnitureByVisitId(int id) {
    try {
      PreparedStatement updateRefuseAllFurnitureByVisitId =
          this.dalServices.getPreparedStatement(queryUpdateRefuseAllFurnitureByVisitId);
      updateRefuseAllFurnitureByVisitId.setInt(1, FurnitureCondition.ne_convient_pas.ordinal());
      updateRefuseAllFurnitureByVisitId.setInt(2, id);
      return updateRefuseAllFurnitureByVisitId.executeUpdate() <= 1;
    } catch (Exception e) {
      throw new FatalException("Data error : refuseAllFurnitureByVisitId", e);
    }
  }

  @Override
  public List<FurnitureDTO> selectFurnituresOfVisit(int id) {
    try {
      PreparedStatement selectFurnituresOfVisit =
          dalServices.getPreparedStatement(querySelectFurnituresOfVisit);
      selectFurnituresOfVisit.setInt(1, id);
      try (ResultSet rs = selectFurnituresOfVisit.executeQuery()) {
        List<FurnitureDTO> listFurniture = new ArrayList<>();
        FurnitureDTO furniture;
        do {
          furniture = createFurniture(rs);
          listFurniture.add(furniture);
        } while (furniture != null);
        listFurniture.remove(listFurniture.size() - 1);
        return listFurniture;
      }
    } catch (Exception e) {
      throw new FatalException("Data error : selectAllFurnitures", e);
    }
  }

  @Override
  public boolean updateFavouritePicture(int id, int pictureId) {
    try {
      PreparedStatement updateFavouritePicture =
          this.dalServices.getPreparedStatement(queryUpdateFavouritePicture);
      updateFavouritePicture.setInt(1, pictureId);
      updateFavouritePicture.setInt(2, id);
      return updateFavouritePicture.executeUpdate() == 1;
    } catch (Exception e) {
      throw new FatalException("Data error : updateFavouritePicture", e);
    }
  }

  private FurnitureDTO createFurniture(ResultSet rs) throws SQLException {

    FurnitureDTO furniture = null;
    if (rs.next()) {
      furniture = this.furnitureFactory.getFurniture();
      furniture.setId(rs.getInt("furniture_id"));
      furniture.setDescription(rs.getString("description"));
      furniture.setType(this.daoType.selectTypeById(rs.getInt("type")));
      furniture
          .setVisitRequest(this.daoVisitRequest.selectVisitRequestById(rs.getInt("visit_request")));
      furniture.setPurchasePrice(rs.getDouble("purchase_price"));
      furniture.setWithdrawalDateFromCustomer(rs.getDate("withdrawal_date_from_customer"));
      furniture.setSellingPrice(rs.getDouble("selling_price"));
      furniture.setSpecialSalePrice(rs.getDouble("special_sale_price"));
      furniture.setDepositDate(rs.getDate("deposit_date"));
      furniture.setSellingDate(rs.getDate("selling_date"));
      furniture.setDeliveryDate(rs.getDate("delivery_date"));
      furniture.setWithdrawalDateToCustomer(rs.getDate("withdrawal_date_to_customer"));
      furniture.setBuyer(this.daoUser.getUserById(rs.getInt("buyer")));
      furniture.setCondition(FurnitureCondition.values()[rs.getInt("condition")]);
      furniture.setUnregisteredBuyerEmail(rs.getString("unregistered_buyer_email"));
      furniture.setPicturesList(new ArrayList<PictureDTO>());
      furniture.setFavouritePicture(rs.getInt("favourite_picture"));
    }
    return furniture;
  }
}

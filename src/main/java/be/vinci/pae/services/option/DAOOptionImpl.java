package be.vinci.pae.services.option;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import be.vinci.pae.domain.option.OptionDTO;
import be.vinci.pae.domain.option.OptionFactory;
import be.vinci.pae.services.DalBackendServices;
import be.vinci.pae.services.furniture.DAOFurniture;
import be.vinci.pae.services.user.DAOUser;
import be.vinci.pae.utils.FatalException;
import be.vinci.pae.utils.ValueLink.OptionStatus;
import jakarta.inject.Inject;

public class DAOOptionImpl implements DAOOption {

  private String querySelectOptionsOfFurniture;
  private String querySelectOptionByFurnitureId;
  private String querySelectOptionsOfBuyerFromFurniture;
  private String queryAddOption;
  private String queryChangeStatusOption;
  private String queryGetLastOptionOfFurniture;

  @Inject
  private OptionFactory optionFactory;
  @Inject
  private DalBackendServices dalServices;
  @Inject
  private DAOFurniture daoFurniture;
  @Inject
  private DAOUser daoUser;

  /**
   * constructor of DAOOptionImpl. contains queries.
   */
  public DAOOptionImpl() {
    querySelectOptionsOfFurniture = "SELECT option_id, buyer, furniture, duration, date, "
        + "status FROM project.options WHERE furniture = ?";
    querySelectOptionsOfBuyerFromFurniture = "SELECT option_id, buyer, furniture, duration, date, "
        + "status FROM project.options WHERE buyer = ? AND furniture = ?";
    queryAddOption = "INSERT INTO project.options (option_id, buyer, furniture, duration, date, "
        + "status) VALUES (DEFAULT, ?, ?, ?, ?, ?)";
    queryChangeStatusOption = "UPDATE project.options SET status = ? WHERE option_id = ?";
    queryGetLastOptionOfFurniture = "SELECT option_id, buyer, furniture, duration, date, status "
        + "FROM project.options WHERE furniture = ? AND date = "
        + "(SELECT MAX(date) FROM project.options WHERE furniture = ?)";
    querySelectOptionByFurnitureId =
        "SELECT o.option_id, o.buyer, o.furniture, o.duration, o.date, o.status "
            + "FROM project.options o WHERE o.furniture = ?";
  }

  @Override
  public int addOption(OptionDTO option) {
    try {
      PreparedStatement addOption = dalServices.getPreparedStatementAdd(queryAddOption);
      addOption.setInt(1, option.getBuyer().getId());
      addOption.setInt(2, option.getFurniture().getId());
      addOption.setInt(3, option.getDuration());
      addOption.setTimestamp(4, Timestamp.from(option.getDate().toInstant()));
      addOption.setInt(5, option.getStatus().ordinal());
      addOption.executeUpdate();
      try (ResultSet rs = addOption.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
        return -1;
      }
    } catch (Exception e) {
      throw new FatalException("Database error : addOption");
    }
  }

  @Override
  public List<OptionDTO> selectOptionsOfFurniture(int idFurniture) {
    try {
      PreparedStatement selectOptionsOfFurniture =
          dalServices.getPreparedStatement(querySelectOptionsOfFurniture);
      selectOptionsOfFurniture.setInt(1, idFurniture);
      try (ResultSet rs = selectOptionsOfFurniture.executeQuery()) {
        List<OptionDTO> listOptions = new ArrayList<OptionDTO>();
        OptionDTO option;
        do {
          option = createOption(rs);
          listOptions.add(option);
        } while (option != null);
        listOptions.remove(listOptions.size() - 1);
        return listOptions;
      }
    } catch (Exception e) {
      throw new FatalException("Database error : selectOptionsOfFurniture");
    }
  }

  @Override
  public OptionDTO selectOptionByFurnitureId(int idFurniture) {
    try {
      PreparedStatement selectOptionByFurnitureId =
          dalServices.getPreparedStatement(querySelectOptionByFurnitureId);
      selectOptionByFurnitureId.setInt(1, idFurniture);
      try (ResultSet rs = selectOptionByFurnitureId.executeQuery()) {
        return createOption(rs);
      }
    } catch (Exception e) {
      throw new FatalException("Data error : selectOptionByFurnitureId");
    }
  }

  @Override
  public List<OptionDTO> selectOptionsOfBuyerFromFurniture(int idBuyer, int idFurniture) {
    try {
      PreparedStatement selectOptionsOfBuyerFromFurniture =
          dalServices.getPreparedStatement(querySelectOptionsOfBuyerFromFurniture);
      selectOptionsOfBuyerFromFurniture.setInt(1, idBuyer);
      selectOptionsOfBuyerFromFurniture.setInt(2, idFurniture);
      try (ResultSet rs = selectOptionsOfBuyerFromFurniture.executeQuery()) {
        List<OptionDTO> list = new ArrayList<>();
        do {
          list.add(createOption(rs));
        } while (list.get(list.size() - 1) != null);
        list.remove(list.size() - 1);
        return list;
      }
    } catch (Exception e) {
      throw new FatalException("Database error : selectOptionsOfBuyerFromFurniture");
    }
  }

  @Override
  public boolean finishOption(int id) {
    try {
      PreparedStatement finishOption = dalServices.getPreparedStatement(queryChangeStatusOption);
      finishOption.setInt(1, OptionStatus.finie.ordinal());
      finishOption.setInt(2, id);
      return finishOption.executeUpdate() == 1;
    } catch (SQLException e) {
      throw new FatalException("Database error : finishOption");
    }
  }

  @Override
  public boolean cancelOption(OptionDTO optionToCancel) {
    try {
      PreparedStatement cancelOption = dalServices.getPreparedStatement(queryChangeStatusOption);
      cancelOption.setInt(1, OptionStatus.annulee.ordinal());
      cancelOption.setInt(2, optionToCancel.getId());
      return cancelOption.executeUpdate() == 1;
    } catch (SQLException e) {
      throw new FatalException("Database error : cancelOption");
    }
  }

  @Override
  public OptionDTO getLastOptionOfFurniture(int idFurniture) {
    try {
      PreparedStatement getLastOptionOfFurniture =
          dalServices.getPreparedStatement(queryGetLastOptionOfFurniture);
      getLastOptionOfFurniture.setInt(1, idFurniture);
      getLastOptionOfFurniture.setInt(2, idFurniture);
      try (ResultSet rs = getLastOptionOfFurniture.executeQuery()) {
        return createOption(rs);
      }
    } catch (SQLException e) {
      throw new FatalException("Database error : getLastOptionOfFurniture");
    }
  }

  private OptionDTO createOption(ResultSet rs) throws SQLException {
    OptionDTO option = null;
    if (rs.next()) {
      option = optionFactory.getOption();
      option.setId(rs.getInt("option_id"));
      option.setDate(rs.getDate("date"));
      option.setDuration(rs.getInt("duration"));
      option.setStatus(OptionStatus.values()[rs.getInt("status")]);
      option.setFurniture(daoFurniture.selectFurnitureById(rs.getInt("furniture")));
      option.setBuyer(daoUser.getUserById(rs.getInt("buyer")));
    }
    return option;
  }

}

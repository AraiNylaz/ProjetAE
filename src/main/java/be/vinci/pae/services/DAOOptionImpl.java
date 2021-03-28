package be.vinci.pae.services;

import be.vinci.pae.domain.OptionDTO;
import be.vinci.pae.domain.OptionFactory;
import be.vinci.pae.utils.FatalException;
import be.vinci.pae.utils.ValueLiaison;
import jakarta.inject.Inject;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOOptionImpl implements DAOOption {

  private PreparedStatement selectAllOptions;
  private PreparedStatement selectUserId;
  private PreparedStatement selectFurnitureId;
  private String querySelectAllOptions;
  private String queryAddOption;
  private String querySelectUserId;
  private String querySelectFurnitureId;

  @Inject
  private OptionFactory optionFactory;
  @Inject
  private DalBackendServices dalBackendServices;

  /**
   * constructor of DAOOptionImpl. contains queries.
   */
  public DAOOptionImpl() {
    querySelectAllOptions = "SELECT o.option_id, o.buyer, o.furniture, o.duration, o.date"
        + "o.status FROM project.options o;";
    queryAddOption = "INSERT INTO project.options (option_id, buyer, furniture, duration, date"
        + "status VALUES (DEFAULT, ?, ?, ?, ?, ?);";
    querySelectUserId = "SELECT u.user_id, u.username, u.password, u.last_name, u.first_name,"
        + "a.street, a.building_number, a.unit_number, a.postcode, a.commune, a.country, u.email,"
        + "u.registration_date, u.valid_registration, u.user_type FROM project.addresses a,"
        + "project.users u WHERE u.user_id = ?";
    // TODO faire un select complet...
    querySelectFurnitureId = "SELECT f.* FROM project.furnitures f WHERE f.furniture_id = ?";
  }

  @Override
  public List<OptionDTO> selectAllOptions() {
    List<OptionDTO> listOptions = new ArrayList<OptionDTO>();
    try {
      selectAllOptions = dalBackendServices.getPreparedStatement(querySelectAllOptions);
      ResultSet rs = selectAllOptions.executeQuery();
      while (rs.next()) {
        OptionDTO option = optionFactory.getOption();
        listOptions.add(option);
      }
      return listOptions;
    } catch (Exception e) {
      e.printStackTrace();
      throw new FatalException("Database error : selectAllOptions");
    }
  }

  @Override
  public int addOption(OptionDTO option) {
    // TODO
    int optionId = -1;
    try {
      PreparedStatement addOption = this.dalBackendServices.getPreparedStatement(queryAddOption);
      if (selectUserId == null && selectFurnitureId == null) {
        selectFurnitureId = this.dalBackendServices.getPreparedStatement(querySelectFurnitureId);
        selectUserId = this.dalBackendServices.getPreparedStatement(querySelectUserId);
      }
      selectFurnitureId.setInt(1, option.getFurniture().getId());
      selectUserId.setInt(1, option.getBuyer().getId());
      ResultSet rsFurniture = selectFurnitureId.executeQuery();
      ResultSet rsUser = selectUserId.executeQuery();
      if (rsFurniture != null && rsUser != null) {
        addOption.setInt(2, option.getBuyer().getId());
        addOption.setInt(3, option.getFurniture().getId());
        addOption.setInt(4, option.getDuration());
        // TODO setDate to LocalDate --> is that correct ?
        addOption.setDate(5, (Date) option.getDate());
        addOption.setInt(6, ValueLiaison.stringToIntOption(option.getStatus()));
      } else {
        throw new FatalException("there is no furnitureId or userId as mentionned");
      }
      try (ResultSet rs = addOption.getGeneratedKeys()) {
        if (rs.next()) {
          optionId = rs.getInt(1);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new FatalException("Database error : addOption");
    }
    return optionId;
  }

  @Override
  public OptionDTO selectOptionByID(int id) {
    // TODO
    OptionDTO option = null;
    try {
      selectAllOptions = dalBackendServices.getPreparedStatement(querySelectAllOptions);
      ResultSet rs = selectAllOptions.executeQuery();
      if (rs == null) {
        option = optionFactory.getOption();
      }
      return option;
    } catch (Exception e) {
      e.printStackTrace();
      throw new FatalException("Database error : selectAllOptions");
    }
  }

}
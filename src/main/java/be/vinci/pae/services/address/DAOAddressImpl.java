package be.vinci.pae.services.address;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import be.vinci.pae.domain.address.AddressDTO;
import be.vinci.pae.domain.address.AddressFactory;
import be.vinci.pae.services.DalBackendServices;
import be.vinci.pae.utils.FatalException;
import jakarta.inject.Inject;

public class DAOAddressImpl implements DAOAddress {

  private String querySelectAllCommunes;
  private String querySelectAddressIdWithUnitNumber;
  private String querySelectAddressIdWithoutUnitNumber;
  private String queryAddAddressWithUnitNumber;
  private String queryAddAddressWithoutUnitNumber;
  private String querySelectAddressById;
  private String querySelectAddressByUserId;

  @Inject
  DalBackendServices dalBackendServices;

  @Inject
  AddressFactory addressFactory;

  /**
   * constructor of DAOAddressImpl. contains queries.
   */
  public DAOAddressImpl() {
    querySelectAllCommunes = "SELECT DISTINCT a.commune FROM project.addresses a";
    querySelectAddressIdWithUnitNumber =
        "SELECT a.address_id FROM project.addresses a WHERE a.street = ? AND "
            + "a.building_number = ? AND a.postcode = ? AND "
            + "a.commune = ? AND a.country = ? AND a.unit_number = ?";
    querySelectAddressIdWithoutUnitNumber =
        "SELECT a.address_id FROM project.addresses a WHERE a.street = ? AND "
            + "a.building_number = ? AND a.postcode = ? AND a.commune = ? AND a.country = ?";
    queryAddAddressWithUnitNumber = "INSERT INTO project.addresses (address_id, street, "
        + "building_number, postcode, commune, country, unit_number) "
        + "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";
    queryAddAddressWithoutUnitNumber = "INSERT INTO project.addresses (address_id, street, "
        + "building_number, unit_number, postcode, commune, country) VALUES"
        + " (DEFAULT, ?, ?, NULL, ?, ?, ?)";
    querySelectAddressById = "SELECT a.address_id, a.street, a.building_number, a.unit_number, "
        + "a.postcode, a.commune, a.country FROM project.addresses a WHERE a.address_id = ?";
    querySelectAddressByUserId =
        "SELECT a.address_id, a.street, a.building_number, a.unit_number, a.postcode,"
            + " a.commune, a.country FROM project.addresses a, project.users u"
            + " WHERE a.address_id = u.address AND u.user_id = ?";
  }

  @Override
  public List<String> getAllCommunes() {
    try {
      PreparedStatement selectAllCommunes =
          this.dalBackendServices.getPreparedStatement(querySelectAllCommunes);
      List<String> allCommunes = new ArrayList<>();
      try (ResultSet rs = selectAllCommunes.executeQuery()) {
        while (rs.next()) {
          allCommunes.add(rs.getString(1));
        }
      }
      return allCommunes;
    } catch (SQLException e) {
      throw new FatalException("Database error : getAllCommunes", e);
    }
  }

  @Override
  public int selectAddressID(AddressDTO addressDTO) {
    try {
      PreparedStatement selectAddressId;
      if (addressDTO.getUnitNumber() != null) {
        selectAddressId =
            this.dalBackendServices.getPreparedStatement(querySelectAddressIdWithUnitNumber);
        selectAddressId.setString(6, addressDTO.getUnitNumber());
      } else {
        selectAddressId =
            this.dalBackendServices.getPreparedStatement(querySelectAddressIdWithoutUnitNumber);
      }
      selectAddressId.setString(1, addressDTO.getStreet());
      selectAddressId.setString(2, addressDTO.getBuildingNumber());
      selectAddressId.setString(3, addressDTO.getPostcode());
      selectAddressId.setString(4, addressDTO.getCommune());
      selectAddressId.setString(5, addressDTO.getCountry());
      try (ResultSet rs = selectAddressId.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("address_id");
        }
        return -1;
      }
    } catch (SQLException e) {
      throw new FatalException("Database error : addUser get Address", e);
    }
  }

  @Override
  public int addAddress(AddressDTO addressDTO) {
    try {
      PreparedStatement addAddress;
      if (addressDTO.getUnitNumber() != null && !addressDTO.getUnitNumber().isEmpty()) {
        addAddress = this.dalBackendServices.getPreparedStatementAdd(queryAddAddressWithUnitNumber);
        addAddress.setString(6, addressDTO.getUnitNumber());
      } else {
        addAddress =
            this.dalBackendServices.getPreparedStatementAdd(queryAddAddressWithoutUnitNumber);
      }
      addAddress.setString(1, addressDTO.getStreet());
      addAddress.setString(2, addressDTO.getBuildingNumber());
      addAddress.setString(3, addressDTO.getPostcode());
      addAddress.setString(4, addressDTO.getCommune());
      addAddress.setString(5, addressDTO.getCountry());

      addAddress.executeUpdate();

      try (ResultSet rs = addAddress.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
        return -1;
      }
    } catch (SQLException e) {
      throw new FatalException("Database error : addUser Add Address", e);
    }
  }

  @Override
  public AddressDTO getAddressById(int id) {
    try {
      PreparedStatement selectAddressById =
          this.dalBackendServices.getPreparedStatement(querySelectAddressById);
      selectAddressById.setInt(1, id);
      try (ResultSet rs = selectAddressById.executeQuery()) {
        return createAddress(rs);
      }
    } catch (SQLException e) {
      throw new FatalException("Database error : getAllCommunes", e);
    }
  }

  @Override
  public AddressDTO getAddressByUserId(int userId) {
    try {
      PreparedStatement selectAddressByUserId =
          this.dalBackendServices.getPreparedStatement(querySelectAddressByUserId);
      selectAddressByUserId.setInt(1, userId);
      try (ResultSet rs = selectAddressByUserId.executeQuery()) {
        return createAddress(rs);
      }
    } catch (SQLException e) {
      throw new FatalException("Database error : getAddressByUserId", e);
    }
  }

  private AddressDTO createAddress(ResultSet rs) throws SQLException {
    AddressDTO addressDTO = null;
    if (rs.next()) {
      addressDTO = addressFactory.getAddress();
      addressDTO.setId(rs.getInt("address_id"));
      addressDTO.setStreet(rs.getString("street"));
      addressDTO.setBuildingNumber(rs.getString("building_number"));
      addressDTO.setUnitNumber(rs.getString("unit_number"));
      addressDTO.setPostcode(rs.getString("postcode"));
      addressDTO.setCommune(rs.getString("commune"));
      addressDTO.setCountry(rs.getString("country"));
    }
    return addressDTO;
  }
}

package be.vinci.pae.domain.address;

import java.util.List;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.address.DAOAddress;
import jakarta.inject.Inject;

public class AddressUCCImpl implements AddressUCC {

  @Inject
  DalServices dalServices;

  @Inject
  DAOAddress daoAddress;

  @Override
  public List<String> getAllCommunes() {
    try {
      return daoAddress.getAllCommunes();
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public AddressDTO getAddressByUserId(int userId) {
    try {
      return daoAddress.getAddressByUserId(userId);
    } finally {
      dalServices.closeConnection();
    }
  }
}

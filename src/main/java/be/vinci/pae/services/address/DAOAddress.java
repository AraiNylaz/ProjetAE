package be.vinci.pae.services.address;

import java.util.List;
import be.vinci.pae.domain.address.AddressDTO;

public interface DAOAddress {

  List<String> getAllCommunes();

  int selectAddressID(AddressDTO addressDTO);

  int addAddress(AddressDTO addressDTO);

  AddressDTO getAddressById(int id);

  AddressDTO getAddressByUserId(int userId);
}

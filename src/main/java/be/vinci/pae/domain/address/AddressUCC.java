package be.vinci.pae.domain.address;

import java.util.List;

public interface AddressUCC {

  List<String> getAllCommunes();

  AddressDTO getAddressByUserId(int userId);
}

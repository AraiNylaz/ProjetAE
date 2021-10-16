package be.vinci.pae.domain.address;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = AddressImpl.class)
public interface AddressDTO {

  String getStreet();

  void setStreet(String street);

  String getBuildingNumber();

  void setBuildingNumber(String buildingNumber);

  String getUnitNumber();

  void setUnitNumber(String unitNumber);

  String getPostcode();

  void setPostcode(String postcode);

  String getCommune();

  void setCommune(String commune);

  String getCountry();

  void setCountry(String country);

  int getId();

  void setId(int id);

}

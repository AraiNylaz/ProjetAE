package be.vinci.pae.domain.address;

public class AddressFactoryImpl implements AddressFactory {

  @Override
  public AddressDTO getAddress() {
    return new AddressImpl();
  }

}

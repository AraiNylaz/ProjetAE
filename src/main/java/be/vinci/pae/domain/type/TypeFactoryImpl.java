package be.vinci.pae.domain.type;

public class TypeFactoryImpl implements TypeFactory {

  @Override
  public TypeDTO getType() {
    return new TypeImpl();
  }
}

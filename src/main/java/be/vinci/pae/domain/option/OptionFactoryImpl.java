package be.vinci.pae.domain.option;

public class OptionFactoryImpl implements OptionFactory {

  @Override
  public OptionDTO getOption() {
    return new OptionImpl();
  }

}
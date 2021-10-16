package be.vinci.pae.domain.picture;

public class PictureFactoryImpl implements PictureFactory {

  @Override
  public PictureDTO getPicture() {
    return new PictureImpl();
  }

}


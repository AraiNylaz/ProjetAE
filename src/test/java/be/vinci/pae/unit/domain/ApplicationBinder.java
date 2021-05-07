package be.vinci.pae.unit.domain;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import be.vinci.pae.domain.address.AddressFactory;
import be.vinci.pae.domain.address.AddressFactoryImpl;
import be.vinci.pae.domain.furniture.FurnitureFactory;
import be.vinci.pae.domain.furniture.FurnitureFactoryImpl;
import be.vinci.pae.domain.option.OptionFactory;
import be.vinci.pae.domain.option.OptionFactoryImpl;
import be.vinci.pae.domain.picture.PictureFactory;
import be.vinci.pae.domain.picture.PictureFactoryImpl;
import be.vinci.pae.domain.type.TypeFactory;
import be.vinci.pae.domain.type.TypeFactoryImpl;
import be.vinci.pae.domain.user.UserFactory;
import be.vinci.pae.domain.user.UserFactoryImpl;
import be.vinci.pae.domain.visitrequest.VisitRequestFactory;
import be.vinci.pae.domain.visitrequest.VisitRequestFactoryImpl;
import be.vinci.pae.utils.Upload;
import be.vinci.pae.utils.UploadInterface;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationBinder extends AbstractBinder {

  @Override
  protected void configure() {
    bind(UserFactoryImpl.class).to(UserFactory.class).in(Singleton.class);
    bind(OptionFactoryImpl.class).to(OptionFactory.class).in(Singleton.class);
    bind(FurnitureFactoryImpl.class).to(FurnitureFactory.class).in(Singleton.class);
    bind(TypeFactoryImpl.class).to(TypeFactory.class).in(Singleton.class);
    bind(VisitRequestFactoryImpl.class).to(VisitRequestFactory.class).in(Singleton.class);
    bind(PictureFactoryImpl.class).to(PictureFactory.class).in(Singleton.class);
    bind(AddressFactoryImpl.class).to(AddressFactory.class).in(Singleton.class);
    bind(Upload.class).to(UploadInterface.class).in(Singleton.class);
  }
}

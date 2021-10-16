package be.vinci.pae.utils;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import be.vinci.pae.domain.address.AddressFactory;
import be.vinci.pae.domain.address.AddressFactoryImpl;
import be.vinci.pae.domain.address.AddressUCC;
import be.vinci.pae.domain.address.AddressUCCImpl;
import be.vinci.pae.domain.furniture.FurnitureFactory;
import be.vinci.pae.domain.furniture.FurnitureFactoryImpl;
import be.vinci.pae.domain.furniture.FurnitureUCC;
import be.vinci.pae.domain.furniture.FurnitureUCCImpl;
import be.vinci.pae.domain.option.OptionFactory;
import be.vinci.pae.domain.option.OptionFactoryImpl;
import be.vinci.pae.domain.option.OptionUCC;
import be.vinci.pae.domain.option.OptionUCCImpl;
import be.vinci.pae.domain.picture.PictureFactory;
import be.vinci.pae.domain.picture.PictureFactoryImpl;
import be.vinci.pae.domain.picture.PictureUCC;
import be.vinci.pae.domain.picture.PictureUCCImpl;
import be.vinci.pae.domain.type.TypeFactory;
import be.vinci.pae.domain.type.TypeFactoryImpl;
import be.vinci.pae.domain.type.TypeUCC;
import be.vinci.pae.domain.type.TypeUCCImpl;
import be.vinci.pae.domain.user.UserFactory;
import be.vinci.pae.domain.user.UserFactoryImpl;
import be.vinci.pae.domain.user.UserUCC;
import be.vinci.pae.domain.user.UserUCCImpl;
import be.vinci.pae.domain.visitrequest.VisitRequestFactory;
import be.vinci.pae.domain.visitrequest.VisitRequestFactoryImpl;
import be.vinci.pae.domain.visitrequest.VisitRequestUCC;
import be.vinci.pae.domain.visitrequest.VisitRequestUCCImpl;
import be.vinci.pae.services.DalBackendServices;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.DalServicesImpl;
import be.vinci.pae.services.address.DAOAddress;
import be.vinci.pae.services.address.DAOAddressImpl;
import be.vinci.pae.services.furniture.DAOFurniture;
import be.vinci.pae.services.furniture.DAOFurnitureImpl;
import be.vinci.pae.services.option.DAOOption;
import be.vinci.pae.services.option.DAOOptionImpl;
import be.vinci.pae.services.picture.DAOPicture;
import be.vinci.pae.services.picture.DAOPictureImpl;
import be.vinci.pae.services.type.DAOType;
import be.vinci.pae.services.type.DAOTypeImpl;
import be.vinci.pae.services.user.DAOUser;
import be.vinci.pae.services.user.DAOUserImpl;
import be.vinci.pae.services.visitrequest.DAOVisitRequest;
import be.vinci.pae.services.visitrequest.DAOVisitRequestImpl;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationBinder extends AbstractBinder {

  @Override
  protected void configure() {
    // ADDRESS
    bind(AddressFactoryImpl.class).to(AddressFactory.class).in(Singleton.class);
    bind(AddressUCCImpl.class).to(AddressUCC.class).in(Singleton.class);
    bind(DAOAddressImpl.class).to(DAOAddress.class).in(Singleton.class);

    // FURNITURE
    bind(FurnitureFactoryImpl.class).to(FurnitureFactory.class).in(Singleton.class);
    bind(FurnitureUCCImpl.class).to(FurnitureUCC.class).in(Singleton.class);
    bind(DAOFurnitureImpl.class).to(DAOFurniture.class).in(Singleton.class);

    // OPTION
    bind(OptionFactoryImpl.class).to(OptionFactory.class).in(Singleton.class);
    bind(OptionUCCImpl.class).to(OptionUCC.class).in(Singleton.class);
    bind(DAOOptionImpl.class).to(DAOOption.class).in(Singleton.class);

    // PICTURE
    bind(PictureFactoryImpl.class).to(PictureFactory.class).in(Singleton.class);
    bind(PictureUCCImpl.class).to(PictureUCC.class).in(Singleton.class);
    bind(DAOPictureImpl.class).to(DAOPicture.class).in(Singleton.class);

    // TYPE
    bind(TypeFactoryImpl.class).to(TypeFactory.class).in(Singleton.class);
    bind(TypeUCCImpl.class).to(TypeUCC.class).in(Singleton.class);
    bind(DAOTypeImpl.class).to(DAOType.class).in(Singleton.class);

    // USER
    bind(UserFactoryImpl.class).to(UserFactory.class).in(Singleton.class);
    bind(UserUCCImpl.class).to(UserUCC.class).in(Singleton.class);
    bind(DAOUserImpl.class).to(DAOUser.class).in(Singleton.class);

    // VISIT REQUEST
    bind(VisitRequestFactoryImpl.class).to(VisitRequestFactory.class).in(Singleton.class);
    bind(VisitRequestUCCImpl.class).to(VisitRequestUCC.class).in(Singleton.class);
    bind(DAOVisitRequestImpl.class).to(DAOVisitRequest.class).in(Singleton.class);

    // DALSERVICES
    bind(DalServicesImpl.class).to(DalServices.class).to(DalBackendServices.class)
        .in(Singleton.class);
    
    //UPLOAD
    bind(Upload.class).to(UploadInterface.class).in(Singleton.class);
  }
}

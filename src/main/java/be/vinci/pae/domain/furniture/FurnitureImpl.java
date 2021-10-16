package be.vinci.pae.domain.furniture;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import be.vinci.pae.domain.picture.PictureDTO;
import be.vinci.pae.domain.type.TypeDTO;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.domain.visitrequest.VisitRequestDTO;
import be.vinci.pae.utils.ValueLink.FurnitureCondition;
import views.Views;

@JsonInclude(JsonInclude.Include.NON_NULL)
class FurnitureImpl implements Furniture {

  @JsonView(Views.Public.class)
  private int id;
  @JsonView(Views.Public.class)
  private String description;
  @JsonView(Views.Public.class)
  private TypeDTO type;
  @JsonView(Views.Public.class)
  private VisitRequestDTO visitRequest;
  @JsonView(Views.Public.class)
  private double sellingPrice;
  @JsonView(Views.Public.class)
  private Date sellingDate;
  @JsonView(Views.Public.class)
  private Date deliveryDate;
  @JsonView(Views.Public.class)
  private Date withdrawalDateToCustomer;
  @JsonView(Views.Public.class)
  private UserDTO buyer;
  @JsonView(Views.Public.class)
  private FurnitureCondition condition;
  @JsonView(Views.Public.class)
  private List<PictureDTO> picturesList;

  @JsonView(Views.Public.class)
  private double purchasePrice;

  @JsonView(Views.Admin.class)
  private Date withdrawalDateFromCustomer;
  @JsonView(Views.Admin.class)
  private double specialSalePrice;
  @JsonView(Views.Admin.class)
  private Date depositDate;
  @JsonView(Views.Admin.class)
  private String unregisteredBuyerEmail;
  @JsonView(Views.Admin.class)
  private int favouritePicture;

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public TypeDTO getType() {
    return type;
  }

  @Override
  public void setType(TypeDTO type) {
    this.type = type;
  }

  @Override
  public VisitRequestDTO getVisitRequest() {
    return visitRequest;
  }

  @Override
  public void setVisitRequest(VisitRequestDTO visitRequest) {
    this.visitRequest = visitRequest;
  }

  @Override
  public List<PictureDTO> getPicturesList() {
    return picturesList;
  }

  @Override
  public void setPicturesList(List<PictureDTO> picturesList) {
    this.picturesList = picturesList;
  }

  @Override
  public double getPurchasePrice() {
    return purchasePrice;
  }

  @Override
  public void setPurchasePrice(double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  @Override
  public Date getWithdrawalDateFromCustomer() {
    return withdrawalDateFromCustomer;
  }

  @Override
  public void setWithdrawalDateFromCustomer(Date withdrawalDateFromCustomer) {
    this.withdrawalDateFromCustomer = withdrawalDateFromCustomer;
  }

  @Override
  public double getSellingPrice() {
    return sellingPrice;
  }

  @Override
  public void setSellingPrice(double sellingPrice) {
    this.sellingPrice = sellingPrice;
  }

  @Override
  public double getSpecialSalePrice() {
    return specialSalePrice;
  }

  @Override
  public void setSpecialSalePrice(double specialSalePrice) {
    this.specialSalePrice = specialSalePrice;
  }

  @Override
  public Date getDepositDate() {
    return depositDate;
  }

  @Override
  public void setDepositDate(Date depositDate) {
    this.depositDate = depositDate;
  }

  @Override
  public Date getSellingDate() {
    return sellingDate;
  }

  @Override
  public void setSellingDate(Date sellingDate) {
    this.sellingDate = sellingDate;
  }

  @Override
  public Date getDeliveryDate() {
    return deliveryDate;
  }

  @Override
  public void setDeliveryDate(Date deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  @Override
  public Date getWithdrawalDateToCustomer() {
    return withdrawalDateToCustomer;
  }

  @Override
  public void setWithdrawalDateToCustomer(Date withdrawalDateToCustomer) {
    this.withdrawalDateToCustomer = withdrawalDateToCustomer;
  }

  @Override
  public UserDTO getBuyer() {
    return buyer;
  }

  @Override
  public void setBuyer(UserDTO buyer) {
    this.buyer = buyer;
  }

  @Override
  public FurnitureCondition getCondition() {
    return condition;
  }

  @Override
  public void setCondition(FurnitureCondition condition) {
    this.condition = condition;
  }

  @Override
  public String getUnregisteredBuyerEmail() {
    return unregisteredBuyerEmail;
  }

  @Override
  public void setUnregisteredBuyerEmail(String unregisteredBuyerEmail) {
    this.unregisteredBuyerEmail = unregisteredBuyerEmail;
  }

  @Override
  public int getFavouritePicture() {
    return favouritePicture;
  }

  @Override
  public void setFavouritePicture(int favouritePicture) {
    this.favouritePicture = favouritePicture;
  }


}

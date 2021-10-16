package be.vinci.pae.domain.visitrequest;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import be.vinci.pae.domain.address.AddressDTO;
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.utils.ValueLink.VisitRequestStatus;
import views.Views;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisitRequestImpl implements VisitRequest {

  @JsonView(Views.Public.class)
  private int id;
  @JsonView(Views.Public.class)
  private Date requestDate;
  @JsonView(Views.Public.class)
  private String timeSlot;
  @JsonView(Views.Public.class)
  private AddressDTO address;
  @JsonView(Views.Public.class)
  private VisitRequestStatus status;
  @JsonView(Views.Public.class)
  private List<FurnitureDTO> furnitureList;
  @JsonView(Views.Public.class)
  private Date chosenDateTime;
  @JsonView(Views.Public.class)
  private String cancellationReason;
  @JsonView(Views.Public.class)
  private UserDTO customer;

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public Date getRequestDate() {
    return requestDate;
  }

  @Override
  public void setRequestDate(Date requestDate) {
    this.requestDate = requestDate;
  }

  @Override
  public String getTimeSlot() {
    return timeSlot;
  }

  @Override
  public void setTimeSlot(String timeSlot) {
    this.timeSlot = timeSlot;
  }

  @Override
  public AddressDTO getAddress() {
    return address;
  }

  @Override
  public void setAddress(AddressDTO address) {
    this.address = address;
  }

  @Override
  public VisitRequestStatus getStatus() {
    return status;
  }

  @Override
  public void setStatus(VisitRequestStatus status) {
    this.status = status;
  }

  @Override
  public List<FurnitureDTO> getFurnitureList() {
    return furnitureList;
  }

  @Override
  public void setFurnitureList(List<FurnitureDTO> furnitureList) {
    this.furnitureList = furnitureList;
  }

  @Override
  public Date getChosenDateTime() {
    return chosenDateTime;
  }

  @Override
  public void setChosenDateTime(Date chosenDateTime) {
    this.chosenDateTime = chosenDateTime;
  }

  @Override
  public String getCancellationReason() {
    return cancellationReason;
  }

  @Override
  public void setCancellationReason(String cancellationReason) {
    this.cancellationReason = cancellationReason;
  }

  @Override
  public UserDTO getCustomer() {
    return customer;
  }

  @Override
  public void setCustomer(UserDTO customer) {
    this.customer = customer;
  }


}

package be.vinci.pae.domain.visitrequest;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import be.vinci.pae.domain.address.AddressDTO;
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.utils.ValueLink.VisitRequestStatus;

@JsonDeserialize(as = VisitRequestImpl.class)
public interface VisitRequestDTO {

  int getId();

  void setId(int id);

  Date getRequestDate();

  void setRequestDate(Date requestDate);

  String getTimeSlot();

  void setTimeSlot(String timeSlot);

  AddressDTO getAddress();

  void setAddress(AddressDTO address);

  VisitRequestStatus getStatus();

  void setStatus(VisitRequestStatus status);

  Date getChosenDateTime();

  void setChosenDateTime(Date chosenDateTime);

  String getCancellationReason();

  void setCancellationReason(String cancellationReason);

  UserDTO getCustomer();

  void setCustomer(UserDTO customer);

  List<FurnitureDTO> getFurnitureList();

  void setFurnitureList(List<FurnitureDTO> furnitureList);
}

package be.vinci.pae.domain.option;

import java.util.Date;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.utils.ValueLink.OptionStatus;

@JsonDeserialize(as = OptionImpl.class)
public interface OptionDTO {

  int getId();

  void setId(int id);

  UserDTO getBuyer();

  void setBuyer(UserDTO buyer);

  FurnitureDTO getFurniture();

  void setFurniture(FurnitureDTO furniture);

  int getDuration();

  void setDuration(int duration);

  Date getDate();

  void setDate(Date date);

  OptionStatus getStatus();

  void setStatus(OptionStatus status);

}

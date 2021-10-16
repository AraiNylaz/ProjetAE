package be.vinci.pae.domain.option;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.utils.ValueLink.OptionStatus;
import views.Views;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OptionImpl implements Option {

  @JsonView(Views.Public.class)
  private int id;
  @JsonView(Views.Public.class)
  private UserDTO buyer;
  @JsonView(Views.Public.class)
  private FurnitureDTO furniture;
  @JsonView(Views.Public.class)
  private int duration;
  @JsonView(Views.Public.class)
  private Date date;
  @JsonView(Views.Public.class)
  private OptionStatus status;

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
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
  public FurnitureDTO getFurniture() {
    return furniture;
  }

  @Override
  public void setFurniture(FurnitureDTO furniture) {
    this.furniture = furniture;
  }

  @Override
  public int getDuration() {
    return duration;
  }

  @Override
  public void setDuration(int duration) {
    this.duration = duration;
  }

  @Override
  public Date getDate() {
    return date;
  }

  @Override
  public void setDate(Date date) {
    this.date = date;
  }

  @Override
  public OptionStatus getStatus() {
    return status;
  }

  @Override
  public void setStatus(OptionStatus status) {
    this.status = status;
  }
}

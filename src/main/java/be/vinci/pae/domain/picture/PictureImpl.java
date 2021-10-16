package be.vinci.pae.domain.picture;

import be.vinci.pae.domain.furniture.FurnitureDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import views.Views;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PictureImpl implements Picture {

  @JsonView(Views.Public.class)
  private int id;
  @JsonView(Views.Public.class)
  private String name;
  @JsonView(Views.Public.class)
  private boolean visibleForEveryone;
  @JsonView(Views.Public.class)
  private FurnitureDTO furniture;
  @JsonView(Views.Public.class)
  private boolean scrollingPicture;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isVisibleForEveryone() {
    return visibleForEveryone;
  }

  public void setVisibleForEveryone(boolean isVisibleForEveryone) {
    this.visibleForEveryone = isVisibleForEveryone;
  }

  public FurnitureDTO getFurniture() {
    return furniture;
  }

  public void setFurniture(FurnitureDTO furniture) {
    this.furniture = furniture;
  }

  public boolean isAScrollingPicture() {
    return scrollingPicture;
  }

  public void setAScrollingPicture(boolean scrollingPicture) {
    this.scrollingPicture = scrollingPicture;
  }


}

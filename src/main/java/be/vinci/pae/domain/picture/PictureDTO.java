package be.vinci.pae.domain.picture;

import be.vinci.pae.domain.furniture.FurnitureDTO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = PictureImpl.class)
public interface PictureDTO {

  int getId();

  void setId(int id);

  String getName();

  void setName(String name);

  boolean isVisibleForEveryone();

  void setVisibleForEveryone(boolean isVisibleForEveryone);

  FurnitureDTO getFurniture();

  void setFurniture(FurnitureDTO furniture);

  boolean isAScrollingPicture();

  void setAScrollingPicture(boolean isAScrollingPicture);

}

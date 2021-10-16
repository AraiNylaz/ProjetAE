package be.vinci.pae.domain.option;

import be.vinci.pae.domain.user.UserDTO;

public interface OptionUCC {

  boolean cancelOption(int id, UserDTO user);

  boolean cancelOptionByAdmin(int idFurniture);

  OptionDTO getLastOptionOfFurniture(int idFurniture);

  boolean addOption(int idFurniture, int duration, UserDTO user);
}
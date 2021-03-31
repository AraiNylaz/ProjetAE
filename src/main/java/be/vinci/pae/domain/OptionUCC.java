package be.vinci.pae.domain;

import java.util.List;

public interface OptionUCC {

  List<OptionDTO> getAllOptions();

  List<OptionDTO> getAllOptionsByFurniture(int idFurniture);

  List<OptionDTO> getAllOptionsByBuyer(int idBuyer);

  OptionDTO addOption(OptionDTO option);

  void cancelOption(int id, UserDTO user);

  void cancelOptionByAdmin(int idFurniture);

  OptionDTO getLastOptionOfFurniture(int idFurniture);
}
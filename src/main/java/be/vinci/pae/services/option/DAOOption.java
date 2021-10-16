package be.vinci.pae.services.option;

import be.vinci.pae.domain.option.OptionDTO;
import java.util.List;

public interface DAOOption {

  int addOption(OptionDTO picture);

  List<OptionDTO> selectOptionsOfFurniture(int idFurniture);

  List<OptionDTO> selectOptionsOfBuyerFromFurniture(int idBuyer, int idFurniture);

  boolean finishOption(int id);

  boolean cancelOption(OptionDTO optionToCancel);

  OptionDTO getLastOptionOfFurniture(int idFurniture);

  OptionDTO selectOptionByFurnitureId(int idFurniture);
}

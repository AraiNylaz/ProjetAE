package be.vinci.pae.domain.option;

import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.furniture.DAOFurniture;
import be.vinci.pae.services.option.DAOOption;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.ValueLink.FurnitureCondition;
import be.vinci.pae.utils.ValueLink.OptionStatus;
import be.vinci.pae.utils.ValueLink.UserType;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OptionUCCImpl implements OptionUCC {

  // private static final int MAX_DURATION_OPTION = Config.getIntProperty("MaxDurationOption");
  // private static final int MIN_DURATION_OPTION = Config.getIntProperty("MinDurationOption");

  @Inject
  private DAOOption daoOption;

  @Inject
  private DAOFurniture daoFurniture;

  @Inject
  private DalServices dalServices;

  @Inject
  private OptionFactory optionFactory;

  @Override
  public boolean addOption(int idFurniture, int duration, UserDTO user) {
    try {
      dalServices.startTransaction();
      if (duration > Config.getIntProperty("MaxDurationOption")
          || duration < Config.getIntProperty("MinDurationOption")) {
        throw new BusinessException("Durée de l'option incorrecte");
      }
      FurnitureDTO furniture = daoFurniture.selectFurnitureById(idFurniture);
      if (furniture == null) {
        throw new BusinessException("Le meuble n'existe pas");
      }
      if (!furniture.getCondition().equals(FurnitureCondition.en_vente)
          && !furniture.getCondition().equals(FurnitureCondition.en_option)) {
        throw new BusinessException("Le meuble n'est pas en vente");
      }
      if (!user.isValidRegistration() || !user.getUserType().equals(UserType.client)) {
        throw new BusinessException(
            "Votre compte n'est pas encore validé ou vous n'êtes pas client");
      }

      List<OptionDTO> listPreviousOptionBuyer =
          daoOption.selectOptionsOfBuyerFromFurniture(user.getId(), furniture.getId());

      if (listPreviousOptionBuyer != null) {
        int totalDuration = duration;
        for (OptionDTO option : listPreviousOptionBuyer) {
          totalDuration += option.getDuration();
          if (totalDuration > Config.getIntProperty("MaxDurationOption")) {
            throw new BusinessException(
                "La duree cumulee de vos options depasse la duree maximale");
          }
        }
      }

      OptionDTO newOption = optionFactory.getOption();
      newOption.setDuration(duration);
      newOption.setFurniture(furniture);
      newOption.setBuyer(user);
      newOption.setDate(Date.from(Instant.now()));
      newOption.setStatus(OptionStatus.en_cours);

      List<OptionDTO> listPreviousOption = daoOption.selectOptionsOfFurniture(furniture.getId());
      for (OptionDTO option : listPreviousOption) {
        if (option.getStatus() == OptionStatus.en_cours
            && TimeUnit.DAYS.convert(newOption.getDate().getTime() - option.getDate().getTime(),
                TimeUnit.MILLISECONDS) <= option.getDuration()) {
          throw new BusinessException("Il y a deja une option en cours pour ce meuble");
        }
      }

      int idOption = daoOption.addOption(newOption);
      boolean updateFurniture =
          daoFurniture.updateCondition(idFurniture, FurnitureCondition.en_option.ordinal());

      if (idOption == -1 && !updateFurniture) {
        dalServices.rollbackTransaction();
        return false;
      } else {
        dalServices.commitTransaction();
        return true;
      }
    } finally {
      dalServices.closeConnection();
    }


  }

  @Override
  public boolean cancelOption(int idFurniture, UserDTO user) {
    try {
      dalServices.startTransaction();
      List<OptionDTO> list = daoOption.selectOptionsOfFurniture(idFurniture);
      if (list.size() == 0) {
        throw new BusinessException("Il n'y a pas d'option sur ce meuble");
      }
      OptionDTO optionToCancel = null;
      for (OptionDTO option : list) {
        if (option.getStatus().equals(OptionStatus.en_cours)) {
          optionToCancel = option;
          break;
        }
      }
      if (optionToCancel == null) {
        throw new BusinessException("Il n'y a pas d'option en cours sur ce meuble");
      }
      if (optionToCancel.getBuyer().getId() != user.getId()) {
        throw new BusinessException("Vous ne pouvez pas annuler l'option d'un autre utilisateur");
      }
      boolean canceled = daoOption.cancelOption(optionToCancel);
      boolean updateFurniture =
          daoFurniture.updateCondition(idFurniture, FurnitureCondition.en_vente.ordinal());
      if (canceled && updateFurniture) {
        dalServices.commitTransaction();
        return true;
      } else {
        dalServices.rollbackTransaction();
        return false;
      }
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public boolean cancelOptionByAdmin(int idFurniture) {
    try {
      dalServices.startTransaction();
      List<OptionDTO> list = daoOption.selectOptionsOfFurniture(idFurniture);
      if (list.isEmpty()) {
        throw new BusinessException("Il n'y a pas d'option sur ce meuble");
      }
      OptionDTO optionToCancel = null;
      for (OptionDTO option : list) {
        if (option.getStatus().equals(OptionStatus.en_cours)) {
          optionToCancel = option;
          break;
        }
      }
      if (optionToCancel == null) {
        throw new BusinessException("Il n'y a pas d'option en cours sur ce meuble");
      }
      boolean canceled = daoOption.cancelOption(optionToCancel);
      boolean updateFurniture =
          daoFurniture.updateCondition(idFurniture, FurnitureCondition.en_vente.ordinal());
      if (canceled && updateFurniture) {
        dalServices.commitTransaction();
        return true;
      } else {
        dalServices.rollbackTransaction();
        return false;
      }
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public OptionDTO getLastOptionOfFurniture(int idFurniture) {
    try {
      if (daoFurniture.selectFurnitureById(idFurniture) == null) {
        throw new BusinessException("Ce meuble n'existe pas");
      }
      OptionDTO option = daoOption.getLastOptionOfFurniture(idFurniture);
      option = verifyOptionStatus(option);
      return option == null ? optionFactory.getOption() : option;
    } finally {
      dalServices.closeConnection();
    }
  }


  // TODO voir si ce n'est pas automatisable
  private OptionDTO verifyOptionStatus(OptionDTO option) {
    if (option != null && option.getStatus().equals(
        OptionStatus.en_cours)
        && TimeUnit.DAYS.convert(new Date().getTime() - option.getDate().getTime(),
            TimeUnit.MILLISECONDS) > option.getDuration()) {
      try {
        dalServices.startTransaction();
        if (daoOption.finishOption(option.getId()) && daoFurniture.updateCondition(
            option.getFurniture().getId(), FurnitureCondition.en_vente.ordinal())) {
          dalServices.commitTransaction();
          option.setStatus(OptionStatus.finie);
        } else {
          dalServices.rollbackTransaction();
        }
      } finally {
        dalServices.closeConnection();
      }
    }
    return option;
  }


}

package be.vinci.pae.domain.type;

import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.type.DAOType;
import be.vinci.pae.utils.BusinessException;
import jakarta.inject.Inject;
import java.util.List;

public class TypeUCCImpl implements TypeUCC {

  @Inject
  private DAOType daoType;

  @Inject
  private DalServices dalServices;

  @Override
  public List<TypeDTO> getFurnitureTypes() {
    try {
      return this.daoType.selectFurnitureTypes();
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public boolean deleteFurnitureType(int type) {
    try {
      this.dalServices.startTransaction();
      if (!this.daoType.deleteFurnitureType(type)) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error delete furniture Type");
      } else {
        this.dalServices.commitTransaction();
        return true;
      }
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public int addFurnitureType(String type) {
    try {
      this.dalServices.startTransaction();
      int id = this.daoType.addType(type);
      if (id == -1) {
        this.dalServices.rollbackTransaction();
        throw new BusinessException("Error add furniture Type");
      } else {
        this.dalServices.commitTransaction();
        return id;
      }
    } finally {
      this.dalServices.closeConnection();
    }
  }

  @Override
  public List<String> getAllTypeNames() {
    try {
      return daoType.getAllTypesNames();
    } finally {
      dalServices.closeConnection();
    }
  }
}
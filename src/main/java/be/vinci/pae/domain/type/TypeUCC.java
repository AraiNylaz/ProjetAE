package be.vinci.pae.domain.type;

import java.util.List;

public interface TypeUCC {

  List<TypeDTO> getFurnitureTypes();

  boolean deleteFurnitureType(int id);

  int addFurnitureType(String type);

  List<String> getAllTypeNames();
}

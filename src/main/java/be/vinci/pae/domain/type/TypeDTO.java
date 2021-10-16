package be.vinci.pae.domain.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = TypeImpl.class)
public interface TypeDTO {

  int getId();

  void setId(int id);

  String getName();

  void setName(String name);

}

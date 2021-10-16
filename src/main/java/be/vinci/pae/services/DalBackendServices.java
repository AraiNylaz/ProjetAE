package be.vinci.pae.services;

import java.sql.PreparedStatement;

public interface DalBackendServices {

  PreparedStatement getPreparedStatement(String query);

  PreparedStatement getPreparedStatementAdd(String query);

}

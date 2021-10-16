package be.vinci.pae.services;

import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.FatalException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.dbcp2.BasicDataSource;

public class DalServicesImpl implements DalServices, DalBackendServices {

  private static final String DB_STRING_CONNECTION = Config.getProperty("DatabaseStringConnection");
  private static final String DB_USERNAME = Config.getProperty("DatabaseUsername");
  private static final String DB_PASSWORD = Config.getProperty("DatabasePassword");
  private static final int DB_MINCONNECTION = Config.getIntProperty("MinIdle");
  private static final int DB_MAXCONNECTION = Config.getIntProperty("MaxIdle");
  private BasicDataSource bds;
  private ThreadLocal<Connection> tl;
  private boolean multipleTransaction;

  /**
   * Create pool of Connection.
   */
  public DalServicesImpl() {
    bds = new BasicDataSource();
    bds.setUrl(DB_STRING_CONNECTION);
    bds.setUsername(DB_USERNAME);
    bds.setPassword(DB_PASSWORD);
    // @TODO à chercher utilité
    bds.setMinIdle(DB_MINCONNECTION);
    bds.setMaxIdle(DB_MAXCONNECTION);
    tl = new ThreadLocal<Connection>();
  }

  @Override
  public PreparedStatement getPreparedStatement(String query) {
    Connection conn = tl.get();
    if (conn == null) {
      conn = getConnection();
    }
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = conn.prepareStatement(query);
    } catch (SQLException e) {
      throw new FatalException("Data error : getPreparedStatement", e);
    }
    return preparedStatement;
  }

  @Override
  public PreparedStatement getPreparedStatementAdd(String query) {
    Connection conn = tl.get();
    if (conn == null) {
      conn = getConnection();
    }
    try {
      return conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    } catch (SQLException e) {
      throw new FatalException("Data error : getPreparedStatementAdd", e);
    }
  }

  private Connection getConnection() {
    Connection conn = null;
    try {
      conn = bds.getConnection();
    } catch (SQLException e) {
      throw new FatalException("Data error : getConnection", e);
    }
    tl.set(conn);
    return conn;
  }

  @Override
  public void startTransaction() {
    if (!multipleTransaction) {
      Connection conn = tl.get();
      if (conn == null) {
        conn = getConnection();
      }
      try {
        conn.setAutoCommit(false);
      } catch (SQLException e) {
        throw new FatalException("Data error : startTransaction", e);
      }
    }
  }

  @Override
  public void commitTransaction() {
    if (!multipleTransaction) {
      Connection conn = tl.get();
      try {
        conn.commit();
      } catch (SQLException e) {
        throw new FatalException("Data error : commitTransaction", e);
      }
    }
  }

  @Override
  public void rollbackTransaction() {
    if (!multipleTransaction) {
      Connection conn = tl.get();
      try {
        conn.rollback();
      } catch (SQLException e) {
        throw new FatalException("Data error : rollbackTransaction", e);
      }
    }
  }

  @Override
  public void closeConnection() {
    if (!multipleTransaction) {
      Connection conn = tl.get();
      tl.remove();
      if (conn == null) {
        return;
      }
      try {
        conn.close();
      } catch (SQLException e) {
        throw new FatalException("Data error : closeConnection", e);
      }
    }
  }

  @Override
  public void rollbackMultipleTransaction() {
    multipleTransaction = false;
    rollbackTransaction();
  }

  @Override
  public void commitMultipleTransactions() {
    multipleTransaction = false;
    commitTransaction();
  }

  @Override
  public void startMultipleTransactions() {
    startTransaction();
    multipleTransaction = true;
  }


}

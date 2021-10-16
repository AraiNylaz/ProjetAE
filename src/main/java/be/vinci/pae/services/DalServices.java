package be.vinci.pae.services;

public interface DalServices {

  void startTransaction();

  void commitTransaction();

  void rollbackTransaction();

  void closeConnection();

  void rollbackMultipleTransaction();

  void commitMultipleTransactions();

  void startMultipleTransactions();
}

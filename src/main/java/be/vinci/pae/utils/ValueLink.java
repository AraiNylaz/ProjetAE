package be.vinci.pae.utils;

public interface ValueLink {

  public enum UserType {
    client, antiquaire, admin
  };

  public enum FurnitureCondition {
    propose, ne_convient_pas, achete, emporte_par_patron, en_restauration, en_magasin, en_vente, retire_de_vente, en_option, vendu, reserve, livre, emporte_par_client
  };

  public enum OptionStatus {
    annulee, en_cours, finie
  };

  public enum VisitRequestStatus {
    annulee, en_attente, confirmee
  };
}

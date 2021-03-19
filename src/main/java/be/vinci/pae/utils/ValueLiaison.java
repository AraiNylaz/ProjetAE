package be.vinci.pae.utils;

public class ValueLiaison {

  public static final String ADMIN_STRING = "admin";
  public static final String CLIENT_STRING = "client";
  public static final String ANTIQUAIRE_STRING = "antiquaire";
  public static final int ADMIN_INT = 2;
  public static final int CLIENT_INT = 0;
  public static final int ANTIQUAIRE_INT = 1;

  public static int StringToIntUserType(String type) {
    switch (type.toLowerCase()) {
      case "client":
        return 0;
      case "antiquaire":
        return 1;
      case "administrateur":
        return 2;
      default:
        throw new BusinessException("Le type n'existe pas");
    }
  }

  public static String IntToStringUserType(int type) {
    switch (type) {
      case 0:
        return "client";
      case 1:
        return "antiquaire";
      case 2:
        return "administrateur";
      default:
        throw new BusinessException("Le type n'existe pas");
    }
  }

  public static boolean isValidUserType(String type) {
    switch (type.toLowerCase()) {
      case "client":
      case "antiquaire":
      case "administrateur":
        return true;
      default:
        return false;
    }
  }

  public static boolean isValidUserType(int type) {
    switch (type) {
      case 0:
      case 1:
      case 2:
        return true;
      default:
        return false;
    }
  }
}
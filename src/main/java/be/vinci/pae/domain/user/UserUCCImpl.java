package be.vinci.pae.domain.user;

import be.vinci.pae.services.DalServices;
import be.vinci.pae.services.address.DAOAddress;
import be.vinci.pae.services.user.DAOUser;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.ValueLink.UserType;
import jakarta.inject.Inject;
import java.util.List;
import org.apache.commons.text.StringEscapeUtils;


public class UserUCCImpl implements UserUCC {

  @Inject
  private DAOUser daoUser;

  @Inject
  private DAOAddress daoAddress;

  @Inject
  private DalServices dalServices;

  @Override
  public UserDTO login(String username, String password) {
    try {
      User user = (User) this.daoUser.getUserByUsername(username);
      if (user == null || !user.checkPassword(password)) {
        throw new BusinessException("Pseudo ou mot de passe incorrect");
      }
      return user;
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public UserDTO register(UserDTO newUser) {
    // si champs unique alors recherche dans DB si pas deja utilise
    User user;
    try {
      this.dalServices.startTransaction();
      user = (User) this.daoUser.getUserByUsername(newUser.getUsername());
      if (user != null) {
        throw new BusinessException("Ce pseudo est deja utilise");
      }
      user = (User) this.daoUser.getUserByEmail(newUser.getEmail());
      if (user != null) {
        throw new BusinessException("Cet email est deja utilise");
      }
      user = (User) newUser;
      user.setPassword(user.hashPassword(user.getPassword()));

      // escape dangerous chars to protect against XSS attacks
      user.setEmail(StringEscapeUtils.escapeHtml4(user.getEmail()));
      user.setFirstName(StringEscapeUtils.escapeHtml4(user.getFirstName()));
      user.setLastName(StringEscapeUtils.escapeHtml4(user.getLastName()));
      user.setPassword(StringEscapeUtils.escapeHtml4(user.getPassword()));
      user.setUsername(StringEscapeUtils.escapeHtml4(user.getUsername()));
      user.getAddress()
          .setBuildingNumber(StringEscapeUtils.escapeHtml4(user.getAddress().getBuildingNumber()));
      user.getAddress().setCommune(StringEscapeUtils.escapeHtml4(user.getAddress().getCommune()));
      user.getAddress().setCountry(StringEscapeUtils.escapeHtml4(user.getAddress().getCountry()));
      user.getAddress().setPostcode(StringEscapeUtils.escapeHtml4(user.getAddress().getPostcode()));
      user.getAddress().setStreet(StringEscapeUtils.escapeHtml4(user.getAddress().getStreet()));
      if (user.getAddress().getUnitNumber() != null && !user.getAddress().getUnitNumber()
          .isEmpty()) {
        user.getAddress()
            .setUnitNumber(StringEscapeUtils.escapeHtml4(user.getAddress().getUnitNumber()));
      }

      int addressID = daoAddress.selectAddressID(user.getAddress());
      if (addressID == -1) {
        addressID = daoAddress.addAddress(user.getAddress());
      }
      user.getAddress().setId(addressID);

      int id = daoUser.addUser(user);
      if (id == -1) {
        this.dalServices.rollbackTransaction();
      } else {
        this.dalServices.commitTransaction();
      }
      user.setId(id);
      return user;
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public boolean validateUser(int id, UserType type) {
    try {
      this.dalServices.startTransaction();
      User user = (User) daoUser.getUserById(id);
      if (user == null) {
        throw new BusinessException("L'utilisateur n'existe pas");
      }
      if (user.isValidRegistration()) {
        throw new BusinessException("L'utilisateur est déjà validé");
      }
      if (daoUser.validateUser(id, type.ordinal())) {
        this.dalServices.commitTransaction();
        dalServices.closeConnection();
        return true;
      } else {
        this.dalServices.rollbackTransaction();
        return false;
      }
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public List<UserDTO> getUnvalidatedUsers() {
    try {
      return daoUser.getUnvalidatedUsers();
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public UserDTO getUserById(int id) {
    try {
      return daoUser.getUserById(id);
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public List<String> getAllLastnames() {
    try {
      return daoUser.getAllLastnames();
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public List<UserDTO> getUsersFiltered(String username, String postcode, String commune) {
    try {
      return daoUser.getUsersFiltered(username, postcode, commune);
    } finally {
      dalServices.closeConnection();
    }
  }

  @Override
  public UserDTO getUserByEmail(String email) {
    try {
      return daoUser.getUserByEmail(email);
    } finally {
      dalServices.closeConnection();
    }
  }
}

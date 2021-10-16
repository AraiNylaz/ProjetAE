package be.vinci.pae.api;

import be.vinci.pae.api.utils.Json;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.domain.user.UserUCC;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.FatalException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Singleton
@Path("/auths")
public class Authentication {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final ObjectMapper jsonMapper = new ObjectMapper();
  private static final int DURATION_TOKEN = Config.getIntProperty("DurationToken");

  @Inject
  private UserUCC userUCC;

  /**
   * Checking the credentials of a user, create a token, get the user object associate.
   *
   * @param json DOM event user informations
   * @return a response from the responseBuilder
   */
  @POST
  @Path("login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response login(JsonNode json) {
    // Get and check credentials
    if (!json.hasNonNull("username") || !json.hasNonNull("password")
        || json.get("username").asText().isEmpty() || json.get("password").asText().isEmpty()) {
      return Response.status(Status.UNAUTHORIZED).entity("Veuillez remplir les champs")
          .type(MediaType.TEXT_PLAIN).build();
    }

    String username = json.get("username").asText();
    String password = json.get("password").asText();

    // Try to login
    UserDTO user = userUCC.login(username, password);

    // Create token
    String token = createToken(user);

    // Build response
    // load the user data from a public JSON view to filter out the private info not
    // to be returned by the API (such as password)
    UserDTO publicUser = Json.filterPublicJsonView(user, UserDTO.class);
    ObjectNode node = jsonMapper.createObjectNode().put("token", token).putPOJO("user", publicUser);
    return Response.ok(node, MediaType.APPLICATION_JSON).build();

  }

  /**
   * Checking the credentials of a user, create a token, create a user object.
   *
   * @param user userDTO informations
   * @return a response from the responseBuilder
   */
  @POST
  @Path("register")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response register(UserDTO user) {
    // Get and check credentials
    if (user == null || user.getUsername() == null || user.getUsername().isEmpty()
        || user.getLastName() == null || user.getLastName().isEmpty() || user.getFirstName() == null
        || user.getFirstName().isEmpty() || user.getEmail() == null || user.getEmail().isEmpty()
        || user.getPassword() == null || user.getPassword().isEmpty() || user.getAddress() == null
        || user.getAddress().getStreet() == null || user.getAddress().getStreet().isEmpty()
        || user.getAddress().getBuildingNumber() == null
        || user.getAddress().getBuildingNumber().isEmpty()
        || user.getAddress().getPostcode() == null || user.getAddress().getPostcode().isEmpty()
        || user.getAddress().getCommune() == null || user.getAddress().getCommune().isEmpty()
        || user.getAddress().getCountry() == null || user.getAddress().getCountry().isEmpty()) {
      return Response.status(Status.UNAUTHORIZED).entity("Veuillez remplir les champs")
          .type(MediaType.TEXT_PLAIN).build();
    }
    user.setRegistrationDate(Date.from(Instant.now()));
    user.setValidRegistration(false);

    // Try to register
    user = userUCC.register(user);

    // Create token
    String token = createToken(user);

    // load the user data from a public JSON view to filter out the private info not
    // to be returned by the API (such as password)
    UserDTO publicUser = Json.filterPublicJsonView(user, UserDTO.class);
    ObjectNode node = jsonMapper.createObjectNode().put("token", token).putPOJO("user", publicUser);
    // Build response
    return Response.ok(node, MediaType.APPLICATION_JSON).build();
  }

  /**
   * create a json web token with the secret in the properties file has an expire in 5 days.
   *
   * @param user description
   * @return description
   * @throws WebApplicationException description
   */
  private String createToken(UserDTO user) {
    String token;
    try {
      token = JWT.create().withIssuer("auth0").withClaim("user", user.getId())
          .withClaim("username", user.getUsername()).withExpiresAt(Date.from(LocalDate.now()
              .plusMonths(DURATION_TOKEN).atStartOfDay(ZoneId.systemDefault()).toInstant()))
          .sign(this.jwtAlgorithm);
    } catch (Exception e) {
      throw new FatalException("Unable to create token", e);
    }
    return token;
  }

}

package be.vinci.pae.api;

import be.vinci.pae.api.filters.AuthorizeAdmin;
import be.vinci.pae.api.utils.Json;
import be.vinci.pae.domain.address.AddressUCC;
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.furniture.FurnitureUCC;
import be.vinci.pae.domain.option.OptionUCC;
import be.vinci.pae.domain.picture.PictureDTO;
import be.vinci.pae.domain.picture.PictureFactory;
import be.vinci.pae.domain.picture.PictureUCC;
import be.vinci.pae.domain.type.TypeUCC;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.domain.user.UserUCC;
import be.vinci.pae.domain.visitrequest.VisitRequestDTO;
import be.vinci.pae.domain.visitrequest.VisitRequestUCC;
import be.vinci.pae.utils.ValueLink;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.util.List;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Singleton
@Path("/admin")
public class Administration {

  private final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private UserUCC userUCC;

  @Inject
  private OptionUCC optionUCC;

  @Inject
  private FurnitureUCC furnitureUCC;

  @Inject
  private TypeUCC typeUCC;

  @Inject
  private AddressUCC addressUCC;

  @Inject
  private PictureFactory pictureFactory;

  @Inject
  private PictureUCC pictureUcc;

  @Inject
  private VisitRequestUCC visitRequestUCC;

  /**
   * Validate an user.
   *
   * @param json json of the user
   * @return http response
   */
  @PUT
  @Path("validate")
  @Consumes(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public Response validateUser(JsonNode json) {
    if (!json.hasNonNull("id") || !json.hasNonNull("type") || json.get("id").asText().isEmpty()
        || json.get("type").asText().isEmpty()) {
      return Response.status(Status.UNAUTHORIZED).entity("Veuillez remplir les champs")
          .type(MediaType.TEXT_PLAIN).build();
    }
    return userUCC.validateUser(json.get("id").asInt(),
        ValueLink.UserType.valueOf(json.get("type").asText())) ? Response.ok().build()
            : Response.serverError().build();
  }

  /**
   * List all furniture.
   *
   * @return List of FurnitureDTO
   */
  @GET
  @Path("allFurniture")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<FurnitureDTO> listAllFurniture() {
    return Json.filterAdminJsonViewAsList(furnitureUCC.getAllFurniture(), FurnitureDTO.class);
  }

  /**
   * List all unvalidated users.
   *
   * @return List of userDTO
   */
  @GET
  @Path("validate")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<UserDTO> listUnvalidatedUser() {
    return Json.filterAdminJsonViewAsList(userUCC.getUnvalidatedUsers(), UserDTO.class);
  }

  @PUT
  @Path("/{id}/cancelOption")
  @AuthorizeAdmin
  public Response cancelOption(@PathParam("id") int id) {
    return optionUCC.cancelOptionByAdmin(id) ? Response.ok().build()
        : Response.serverError().build();
  }


  /**
   * Delete furniture type.
   *
   * @return http response
   */
  @DELETE
  @Path("/type/{id}")
  @AuthorizeAdmin
  public Response deleteFurnitureType(@PathParam("id") int id) {
    if (id == 0) {
      throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
          .entity("Lacks of mandatory id info").type("text/plain").build());
    }
    return typeUCC.deleteFurnitureType(id) ? Response.ok().build() : Response.serverError().build();
  }


  /**
   * Add a furniture type.
   *
   * @return http response
   */
  @POST
  @Path("/type")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public Response addFurnitureType(JsonNode json) {
    if (!json.hasNonNull("type") || json.get("type").asText().isEmpty()) {
      return Response.status(Status.UNAUTHORIZED).entity("Veuillez remplir les champs")
          .type(MediaType.TEXT_PLAIN).build();
    }
    int type = typeUCC.addFurnitureType(json.get("type").asText());
    return type != -1 ? Response.ok(type).build() : Response.serverError().build();
  }

  /**
   * Get all usernames.
   *
   * @return List of String
   */
  @GET
  @Path("/alllastnames")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<String> allUsers() {
    return Json.filterAdminJsonViewAsList(userUCC.getAllLastnames(), String.class);
  }

  /**
   * Get all type names.
   *
   * @return List of String
   */
  @GET
  @Path("/alltypesnames")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<String> allTypes() {
    return Json.filterAdminJsonViewAsList(typeUCC.getAllTypeNames(), String.class);
  }

  /**
   * Get all communes.
   *
   * @return List of String
   */
  @GET
  @Path("/allcommunes")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<String> allCommunes() {
    return Json.filterAdminJsonViewAsList(addressUCC.getAllCommunes(), String.class);
  }

  /**
   * Get all users filtered by username, postcode or commune.
   *
   * @return List of UserDTO
   */
  @GET
  @Path("/users")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<UserDTO> usersFiltered(@DefaultValue("") @QueryParam("username") String username,
      @DefaultValue("") @QueryParam("postcode") String postcode,
      @DefaultValue("") @QueryParam("commune") String commune) {
    return Json.filterAdminJsonViewAsList(userUCC.getUsersFiltered(username, postcode, commune),
        UserDTO.class);
  }

  /**
   * Get all funitures filtered by type, price or username.
   *
   * @return List of FurnitureDTO
   */
  @GET
  @Path("/furnitures")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<FurnitureDTO> furnituresFiltered(@DefaultValue("") @QueryParam("type") String type,
      @DefaultValue("" + Double.MAX_VALUE) @QueryParam("price") double price,
      @DefaultValue("") @QueryParam("username") String username) {
    return Json.filterAdminJsonViewAsList(furnitureUCC.getFurnituresFiltered(type, price, username),
        FurnitureDTO.class);
  }

  /**
   * Get an user.
   *
   * @return UserDTO
   */
  @GET
  @Path("/user/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public UserDTO getUser(@PathParam("id") int id) {
    return Json.filterAdminJsonView(userUCC.getUserById(id), UserDTO.class);
  }

  /**
   * <<<<<<< HEAD Get the furniture buy by an user.
   *
   * @return List of FurnitureDTO
   */
  @GET
  @Path("/furniturebuyby/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<FurnitureDTO> getFurnitureBuyBy(@PathParam("id") int id) {
    return Json.filterPublicJsonViewAsList(furnitureUCC.getFurnitureBuyBy(id), FurnitureDTO.class);
  }

  /**
   * Get the furniture sell by an user.
   *
   * @return List of FurnitureDTO
   */
  @GET
  @Path("/furnituresellby/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<FurnitureDTO> getFurnitureSellBy(@PathParam("id") int id) {
    return Json.filterPublicJsonViewAsList(furnitureUCC.getFurnitureSellBy(id), FurnitureDTO.class);
  }

  /**
   * Get a furniture.
   *
   * @return FurnitureDTO
   */
  @GET
  @Path("/{idFurniture}")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public FurnitureDTO getFurniture(@PathParam("idFurniture") int idFurniture) {
    return Json.filterAdminJsonView(furnitureUCC.getFurnitureById(idFurniture), FurnitureDTO.class);
  }

  /**
   * Receive file from the frontend.
   *
   * @param enabled FormData
   * @param uploadedInputStream FormDataFile
   * @param fileDetail Details of the FormDataFile
   * @return Status code
   */
  @POST
  @Path("picture")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @AuthorizeAdmin
  public Response uploadFile(@DefaultValue("true") @FormDataParam("enabled") boolean enabled,
      @FormDataParam("furnitureID") int furnitureId,
      @FormDataParam("file") InputStream uploadedInputStream,
      @FormDataParam("file") FormDataContentDisposition fileDetail) {
    // @TODO regarder avec fileDetail.getType() si c'est pas possible d'avoir le MIME TYPE
    // (Plus officiel)
    String pictureType = fileDetail.getFileName()
        .substring(fileDetail.getFileName().lastIndexOf('.') + 1).toLowerCase();
    if (!pictureType.equals("jpg") && !pictureType.equals("jpeg") && !pictureType.equals("png")) {
      return Response.status(Status.UNAUTHORIZED)
          .entity("Le type de la photo doit être jpg, jpeg ou png").type(MediaType.TEXT_PLAIN)
          .build();
    }
    PictureDTO picture = pictureFactory.getPicture();
    picture.setAScrollingPicture(false);
    picture.setName(fileDetail.getFileName());
    picture.setVisibleForEveryone(false);
    picture = this.pictureUcc.addPicture(furnitureId, picture, uploadedInputStream, pictureType);
    return picture != null ? Response.ok().build() : Response.serverError().build();
  }

  /**
   * Get all visit request.
   *
   * @return List of VisitRequestDTO
   */
  @GET
  @Path("/allvisitsopenned")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<VisitRequestDTO> allVisitsOpenned() {
    return Json.filterAdminJsonViewAsList(visitRequestUCC.getAllVisitsOpenned(),
        VisitRequestDTO.class);
  }

  /**
   * Get a visit request.
   *
   * @return VisitRequestDTO
   */
  @GET
  @Path("/visit/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public VisitRequestDTO getVisitRequest(@PathParam("id") int id) {
    return Json.filterAdminJsonView(visitRequestUCC.getVisitRequestById(id), VisitRequestDTO.class);
  }

  /**
   * Modify a visit request.
   *
   * @return VisitRequestDTO
   */
  @PUT
  @Path("/visit/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public Response modifyVisitRequest(@PathParam("id") int id, JsonNode json) {
    String cancellationReason =
        json.hasNonNull("cancellationReason") && !json.get("cancellationReason").asText().isEmpty()
            ? json.get("cancellationReason").asText()
            : null;
    String chosenDateTime =
        json.hasNonNull("chosenDateTime") && !json.get("chosenDateTime").asText().isEmpty()
            ? json.get("chosenDateTime").asText()
            : null;
    if (cancellationReason == null && chosenDateTime == null) {
      return Response.status(Status.UNAUTHORIZED).entity("Veuillez remplir les champs").build();
    }

    String status = visitRequestUCC.modifyVisitRequest(id, cancellationReason, chosenDateTime);
    ObjectNode node = jsonMapper.createObjectNode().putPOJO("status", status);

    return node != null ? Response.ok(node, MediaType.APPLICATION_JSON).build()
        : Response.serverError().build();
  }


  /**
   * Get all furniture of visit request.
   *
   * @return List of FurnitureDTO
   */
  @GET
  @Path("/furnituresvisit/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<FurnitureDTO> selectFurnituresOfVisit(@PathParam("id") int id) {
    return Json.filterAdminJsonViewAsList(furnitureUCC.selectFurnituresOfVisit(id),
        FurnitureDTO.class);
  }

}

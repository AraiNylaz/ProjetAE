package be.vinci.pae.api;

import java.time.LocalDate;
import java.util.List;
import org.glassfish.jersey.server.ContainerRequest;
import com.fasterxml.jackson.databind.JsonNode;
import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.api.filters.AuthorizeAdmin;
import be.vinci.pae.api.utils.Json;
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.furniture.FurnitureUCC;
import be.vinci.pae.domain.option.OptionDTO;
import be.vinci.pae.domain.option.OptionUCC;
import be.vinci.pae.domain.picture.PictureDTO;
import be.vinci.pae.domain.picture.PictureUCC;
import be.vinci.pae.domain.type.TypeDTO;
import be.vinci.pae.domain.type.TypeUCC;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.utils.ValueLink.FurnitureCondition;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Singleton
@Path("/furniture")
public class Furniture {

  @Inject
  private FurnitureUCC furnitureUCC;

  @Inject
  private OptionUCC optionUCC;

  @Inject
  private TypeUCC typeUCC;

  @Inject
  private PictureUCC pictureUCC;


  /**
   * List all furniture.
   *
   * @return List of FurnitureDTO
   */
  @GET
  @Path("allFurniture")
  @Produces(MediaType.APPLICATION_JSON)
  public List<FurnitureDTO> listAllFurniture() {
    return Json.filterPublicJsonViewAsList(furnitureUCC.getFurnitureUsers(), FurnitureDTO.class);
  }

  /**
   * List all furniture types.
   *
   * @return List of TypeDTO
   */
  @GET
  @Path("allFurnitureTypes")
  @Produces(MediaType.APPLICATION_JSON)
  public List<TypeDTO> listAllFurnitureTypes() {
    return Json.filterPublicJsonViewAsList(typeUCC.getFurnitureTypes(), TypeDTO.class);
  }

  /**
   * Get furniture with id.
   *
   * @return FurnitureDTO
   */
  @GET
  @Path("/{idFurniture}")
  @Produces(MediaType.APPLICATION_JSON)
  public FurnitureDTO getFurniture(@PathParam("idFurniture") int idFurniture) {
    return Json.filterPublicJsonView(furnitureUCC.getFurnitureById(idFurniture),
        FurnitureDTO.class);
  }

  /**
   * Get the last option on the furniture with id.
   *
   * @return OptionDTO
   */
  @GET
  @Path("/{idFurniture}/option")
  @Produces(MediaType.APPLICATION_JSON)
  public OptionDTO getOption(@PathParam("idFurniture") int idFurniture) {
    return Json.filterPublicJsonView(optionUCC.getLastOptionOfFurniture(idFurniture),
        OptionDTO.class);
  }


  /**
   * Get personal furniture with id.
   *
   * @return FurnitureDTO
   */
  @GET
  @Path("/personal/{idFurniture}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public FurnitureDTO getPersonalFurniture(@PathParam("idFurniture") int idFurniture,
      @Context ContainerRequest request) {
    UserDTO currentUser = (UserDTO) request.getProperty("user");
    return Json.filterPublicJsonView(
        furnitureUCC.getPersonalFurnitureById(idFurniture, currentUser), FurnitureDTO.class);
  }


  /**
   * Cancel the option on the furniture with id.
   *
   * @return Response
   */
  @PUT
  @Path("/{idFurniture}/option")
  @Authorize
  public Response cancelOption(@PathParam("idFurniture") int idFurniture,
      @Context ContainerRequest request) {
    UserDTO currentUser = (UserDTO) request.getProperty("user");
    return optionUCC.cancelOption(idFurniture, currentUser) ? Response.ok().build()
        : Response.serverError().build();
  }

  /**
   * Add an option on the furniture with id.
   *
   * @return Response
   */
  @POST
  @Path("/{idFurniture}/option")
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public Response addOption(@PathParam("idFurniture") int idFurniture,
      @Context ContainerRequest request, JsonNode json) {
    if (!json.has("duration")) {
      return Response.status(Status.UNAUTHORIZED).entity("Veuillez remplir les champs")
          .type(MediaType.TEXT_PLAIN).build();
    }
    return optionUCC.addOption(idFurniture, json.get("duration").asInt(),
        (UserDTO) request.getProperty("user")) ? Response.ok().build()
            : Response.serverError().build();
  }

  /**
   * Get the furniture buy by an user.
   *
   * @return List of FurnitureDTO
   */
  @GET
  @Path("/furniturebuyby/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public List<FurnitureDTO> getFurnitureBuyBy(@PathParam("id") int id) {
    return Json.filterPublicJsonViewAsList(furnitureUCC.getFurnitureBuyBy(id), FurnitureDTO.class);
  }

  /**
   * List sales furniture.
   *
   * @return List of FurnitureDTO
   */

  @GET
  @Path("/sales/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<FurnitureDTO> listSalesFurniture() {
    return Json.filterPublicJsonViewAsList(furnitureUCC.getSalesFurnitureAdmin(),
        FurnitureDTO.class);
  }

  /**
   * List all pictures by furniture ID.
   *
   * @return List of PictureDTO
   */
  @GET
  @Path("{idFurniture}/all-pictures-furniture")
  @Produces(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public List<PictureDTO> getAllPicturesByFurnitureId(@PathParam("idFurniture") int idFurniture) {
    return Json.filterPublicJsonViewAsList(this.pictureUCC.getPicturesByFurnitureId(idFurniture),
        PictureDTO.class);
  }

  /**
   * List public pictures by furniture ID.
   *
   * @return List of PictureDTO
   */
  @GET
  @Path("{idFurniture}/public-pictures-furniture")
  @Produces(MediaType.APPLICATION_JSON)
  public List<PictureDTO> getPublicPicturesByFurnitureId(
      @PathParam("idFurniture") int idFurniture) {
    return Json.filterPublicJsonViewAsList(this.pictureUCC.getPicturesByFurnitureId(idFurniture),
        PictureDTO.class);
  }


  /**
   * Get the furniture sell by an user.
   *
   * @return List of FurnitureDTO
   */
  @GET
  @Path("/furnituresellby/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public List<FurnitureDTO> getFurnitureSellBy(@PathParam("id") int id) {
    return Json.filterPublicJsonViewAsList(furnitureUCC.getFurnitureSellBy(id), FurnitureDTO.class);
  }

  /**
   * Add an favourite picture on the furniture with id.
   *
   * @return Response
   */
  @PUT
  @Path("/{idPicture}/favourite-picture")
  @AuthorizeAdmin
  public Response modifyFavouritePicture(@PathParam("idPicture") int idPicture) {
    return furnitureUCC.modifyFavouritePicture(idPicture) ? Response.ok().build()
        : Response.serverError().build();
  }

  /**
   * update scrolling picture or not on the picture with id.
   *
   * @return Response
   */
  @PUT
  @Path("/{idPicture}/scrolling-picture")
  @AuthorizeAdmin
  public Response modifyScrollingPicture(@PathParam("idPicture") int idPicture) {
    return pictureUCC.modifyScrollingPicture(idPicture) ? Response.ok().build()
        : Response.status(Status.UNAUTHORIZED).entity("Erreur ajouter photo défilante")
            .type(MediaType.TEXT_PLAIN).build();
  }



  /**
   * update visible for everyone or not on the picture with id.
   *
   * @return Response
   */
  @PUT
  @Path("/{idPicture}/visible")
  @AuthorizeAdmin
  public Response modifyVisibleForEveryone(@PathParam("idPicture") int idPicture) {
    if (!pictureUCC.modifyVisibleForEveryone(idPicture)) {
      return Response.status(Status.UNAUTHORIZED).entity("Erreur rendre public ou privé.")
          .type(MediaType.TEXT_PLAIN).build();
    }
    return Response.ok().build();
  }



  /**
   * delete picture with id.
   *
   * @return Response
   */
  @DELETE
  @Path("/{idPicture}/picture")
  @AuthorizeAdmin
  public Response deletePicture(@PathParam("idPicture") int idPicture) {
    return pictureUCC.deletePicture(idPicture) ? Response.ok().build()
        : Response.status(Status.UNAUTHORIZED).entity("Erreur retirer l'image")
            .type(MediaType.TEXT_PLAIN).build();
  }

  /**
   * List carousel pictures.
   *
   * @return List of PictureDTO
   */
  @GET
  @Path("carousel-pictures")
  @Produces(MediaType.APPLICATION_JSON)
  public List<PictureDTO> getCarouselPictures() {
    return Json.filterPublicJsonViewAsList(pictureUCC.getCarouselPictures(), PictureDTO.class);
  }

  /**
   * modify furniture information.
   *
   * @param json json of the user
   * @return http response
   */
  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @AuthorizeAdmin
  public Response modifyFurniture(@PathParam("id") int id, JsonNode json) {

    FurnitureCondition condition = null;
    if (json.hasNonNull("condition") && !json.get("condition").asText().isEmpty()) {
      condition = FurnitureCondition.valueOf(json.get("condition").asText());
    }
    double sellingPrice = -1;
    if (json.hasNonNull("sellingPrice")) {
      sellingPrice = json.get("sellingPrice").asDouble();
    }
    double specialSalePrice = -1;
    if (json.hasNonNull("specialSalePrice")) {
      specialSalePrice = json.get("specialSalePrice").asDouble();
    }
    int type = -1;
    if (json.hasNonNull("type")) {
      type = json.get("type").asInt();
    }
    double purchasePrice = -1;
    if (json.hasNonNull("purchasePrice")) {
      purchasePrice = json.get("purchasePrice").asDouble();
    }
    String description = null;
    if (json.hasNonNull("description") && !json.get("description").asText().isEmpty()) {
      description = json.get("description").asText();
    }
    LocalDate withdrawalDateFromCustomer = null;
    if (json.hasNonNull("withdrawalDateFromCustomer")
        && !json.get("withdrawalDateFromCustomer").asText().isEmpty()) {
      withdrawalDateFromCustomer = LocalDate.parse(json.get("withdrawalDateFromCustomer").asText());
    }
    LocalDate withdrawalDateToCustomer = null;
    if (json.hasNonNull("withdrawalDateToCustomer")
        && !json.get("withdrawalDateToCustomer").asText().isEmpty()) {
      withdrawalDateToCustomer = LocalDate.parse(json.get("withdrawalDateToCustomer").asText());
    }
    LocalDate deliveryDate = null;
    if (json.hasNonNull("deliveryDate") && !json.get("deliveryDate").asText().isEmpty()) {
      deliveryDate = LocalDate.parse(json.get("deliveryDate").asText());
    }
    String buyerEmail = null;
    if (json.hasNonNull("buyerEmail") && !json.get("buyerEmail").asText().isEmpty()) {
      buyerEmail = json.get("buyerEmail").asText();
    }

    return furnitureUCC.modifyFurniture(id, condition, sellingPrice, specialSalePrice,
        purchasePrice, type, withdrawalDateFromCustomer, withdrawalDateToCustomer, deliveryDate,
        buyerEmail, description) ? Response.ok().build() : Response.serverError().build();
  }
}

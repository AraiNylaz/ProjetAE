package be.vinci.pae.api;

import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.api.filters.AuthorizeAdmin;
import be.vinci.pae.api.utils.Json;
import be.vinci.pae.domain.address.AddressDTO;
import be.vinci.pae.domain.address.AddressUCC;
import be.vinci.pae.domain.furniture.FurnitureDTO;
import be.vinci.pae.domain.picture.PictureDTO;
import be.vinci.pae.domain.picture.PictureFactory;
import be.vinci.pae.domain.user.UserDTO;
import be.vinci.pae.domain.user.UserUCC;
import be.vinci.pae.domain.visitrequest.VisitRequestDTO;
import be.vinci.pae.domain.visitrequest.VisitRequestFactory;
import be.vinci.pae.domain.visitrequest.VisitRequestUCC;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/visit")
public class VisitRequest {

  @Inject
  VisitRequestFactory visitRequestFactory;

  @Inject
  PictureFactory pictureFactory;

  @Inject
  VisitRequestUCC visitRequestUcc;

  @Inject
  AddressUCC addressUcc;

  @Inject
  UserUCC userUCC;

  /**
   * Get Address from userId.
   *
   * @return AddressDTO
   */
  @GET
  @Path("/address/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  public AddressDTO getAddress(@PathParam("userId") int userId) {
    AddressDTO addressDTO = this.addressUcc.getAddressByUserId(userId);
    if (addressDTO == null) {
      throw new WebApplicationException("Ressource with id = " + userId + " could not be found",
          null, Status.NOT_FOUND);
    }
    return Json.filterPublicJsonView(addressDTO, AddressDTO.class);
  }

  /**
   * Add a visitRequest for another user.
   *
   * @return Http response
   */
  @POST
  @Path("/addforother") // Your Path or URL to call this service
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @AuthorizeAdmin
  public Response uploadFileForOther(FormDataMultiPart formDataMultiPart) {
    String email = formDataMultiPart.getField("email").getValueAs(String.class);
    if (email.equals("")) {
      return Response.status(Status.UNAUTHORIZED).entity("Veuillez mettre l'email du client")
          .type(MediaType.TEXT_PLAIN).build();
    }
    UserDTO user = userUCC.getUserByEmail(email);
    if (user == null) {
      return Response.status(Status.UNAUTHORIZED).entity("Cet utilisateur n'existe pas")
          .type(MediaType.TEXT_PLAIN).build();
    }
    return verifyFormData(formDataMultiPart, true, user);
  }

  /**
   * Add a visitRequest.
   *
   * @return Http response
   */
  @POST
  @Path("/add") // Your Path or URL to call this service
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Authorize
  public Response addVisitRequest(FormDataMultiPart formDataMultiPart,
      @Context ContainerRequest request) {
    UserDTO currentUser = (UserDTO) request.getProperty("user");
    return verifyFormData(formDataMultiPart, false, currentUser);
  }

  private Response verifyFormData(FormDataMultiPart formDataMultiPart, boolean admin,
      UserDTO currentUser) {
    formDataMultiPart.getField("json").setMediaType(MediaType.APPLICATION_JSON_TYPE);
    VisitRequestDTO visitRequest =
        formDataMultiPart.getField("json").getValueAs(VisitRequestDTO.class);
    if (visitRequest == null) {
      return Response.status(Status.UNAUTHORIZED).entity("La demande de visite est vide")
          .type(MediaType.TEXT_PLAIN).build();
    }
    boolean homeVisit = false;
    if (admin) {
      formDataMultiPart.getField("home_visit").setMediaType(MediaType.APPLICATION_JSON_TYPE);
      homeVisit = formDataMultiPart.getField("home_visit").getValueAs(Boolean.class);
    }
    if (homeVisit) {
      visitRequest.setAddress(currentUser.getAddress());
    } else {
      AddressDTO address = visitRequest.getAddress();
      if (address == null || address.getStreet() == null || address.getStreet().isEmpty()
          || address.getBuildingNumber() == null || address.getBuildingNumber().isEmpty()
          || address.getPostcode() == null || address.getPostcode().isEmpty()
          || address.getCommune() == null || address.getCommune().isEmpty()
          || address.getCountry() == null || address.getCountry().isEmpty()) {
        return Response.status(Status.UNAUTHORIZED)
            .entity("Un ou plusieurs champs sont vides dans l'adresse").type(MediaType.TEXT_PLAIN)
            .build();
      }
    }
    if (visitRequest.getFurnitureList() == null || visitRequest.getFurnitureList().isEmpty()
        || visitRequest.getTimeSlot() == null || visitRequest.getTimeSlot().isEmpty()) {
      return Response.status(Status.UNAUTHORIZED)
          .entity("Il n'y a pas de meuble ou la plage horaire est vide").type(MediaType.TEXT_PLAIN)
          .build();
    }
    for (FurnitureDTO furniture : visitRequest.getFurnitureList()) {
      if (furniture.getDescription() == null || furniture.getDescription().isEmpty()
          || furniture.getType() == null) {
        return Response.status(Status.UNAUTHORIZED)
            .entity("Les informations encodées pour un meuble sont vides")
            .type(MediaType.TEXT_PLAIN).build();
      }
    }
    List<InputStream> inputStreamList = new ArrayList<>();
    int count = 0;
    List<FormDataBodyPart> parts = formDataMultiPart.getFields("file" + count);
    while (parts != null) {
      FurnitureDTO furniture = visitRequest.getFurnitureList().get(count);
      List<PictureDTO> picturesList = new ArrayList<PictureDTO>();

      for (FormDataBodyPart part : parts) {
        PictureDTO picture = pictureFactory.getPicture();
        picture.setName(part.getFormDataContentDisposition().getFileName());
        String pictureType =
            picture.getName().toLowerCase().substring(picture.getName().lastIndexOf('.') + 1);
        if (!pictureType.equals("jpg") && !pictureType.equals("jpeg")
            && !pictureType.equals("png")) {
          return Response.status(Status.UNAUTHORIZED)
              .entity("Le type de la photo doit être jpg, jpeg ou png").type(MediaType.TEXT_PLAIN)
              .build();
        }
        picture.setAScrollingPicture(false);
        picture.setVisibleForEveryone(false);
        picturesList.add(picture);
        InputStream inputStream = part.getEntityAs(InputStream.class);
        inputStreamList.add(inputStream);
      }
      furniture.setPicturesList(picturesList);
      count++;
      parts = formDataMultiPart.getFields("file" + count);
    }
    for (FurnitureDTO furniture : visitRequest.getFurnitureList()) {
      if (furniture.getPicturesList() == null || furniture.getPicturesList().isEmpty()) {
        return Response.status(Status.UNAUTHORIZED)
            .entity("Il manque une photo pour un ou plusieurs meubles").type(MediaType.TEXT_PLAIN)
            .build();
      }
    }

    visitRequest.setRequestDate(Date.from(Instant.now()));
    visitRequest = this.visitRequestUcc.addVisitRequest(visitRequest, currentUser, inputStreamList);
    if (visitRequest == null) {
      return Response.serverError().build();
    } else {
      return Response.ok().build();
    }
  }

  /**
   * List furniture with id.
   *
   * @return FurnitureDTO
   */
  @GET
  @Path("/user/{userId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public List<VisitRequestDTO> get(@PathParam("userId") int userId) {
    List<VisitRequestDTO> visitRequestDTOList =
        this.visitRequestUcc.getVisitRequestsByUserId(userId);
    if (visitRequestDTOList == null) {
      throw new WebApplicationException("Ressource with id = " + userId + " could not be found",
          null, Status.NOT_FOUND);
    }
    return Json.filterPublicJsonViewAsList(visitRequestDTOList, VisitRequestDTO.class);
  }

  /**
   * Get a visit request.
   *
   * @return VisitRequestDTO
   */
  @GET
  @Path("/visit/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public VisitRequestDTO getVisitRequest(@PathParam("id") int id,
      @Context ContainerRequest request) {
    UserDTO currentUser = (UserDTO) request.getProperty("user");
    return Json.filterPublicJsonView(
        visitRequestUcc.getVisitRequestByIdForUser(id, currentUser.getId()), VisitRequestDTO.class);
  }

}

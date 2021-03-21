package be.vinci.pae.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Singleton
@Path("/visit")
public class VisitRequest {


  /**
   * Receive file from the frontend.
   * 
   * @param enabled FormData
   * @param uploadedInputStream FormDataFile
   * @param fileDetail Details of the FormDataFile
   * @return Status code
   */
  @POST
  @Path("add") // Your Path or URL to call this service
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response uploadFile(@DefaultValue("true") @FormDataParam("enabled") boolean enabled,
      @FormDataParam("file") InputStream uploadedInputStream,
      @FormDataParam("file") FormDataContentDisposition fileDetail,
      // @FormDataParam("data") InputStream uploadBody,
      @FormDataParam("data") FormDataBodyPart jsonPart) {
    // System.out.println("Uploaded Input Stream : " + uploadedInputStream);
    // System.out.println("File detail : " + fileDetail);


    // Read text

    // Map<String, String> mapa = jsonPart.getParameters();
    jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
    System.out.println("DATA : " + jsonPart.getFormDataContentDisposition());



    // Save picture
    File file = new File(".\\images\\");
    file.mkdir();
    // Your local disk path where you want to store the file
    String uploadedFileLocation = ".\\images\\" + fileDetail.getFileName();
    System.out.println(uploadedFileLocation);
    // save it
    File objFile = new File(uploadedFileLocation);
    if (objFile.exists()) {
      objFile.delete();

    }

    saveToFile(uploadedInputStream, uploadedFileLocation);

    String output = "File uploaded via Jersey based RESTFul Webservice to: " + uploadedFileLocation;

    return Response.status(200).entity(output).build();

  }

  private void saveToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

    try {
      OutputStream out = null;
      int read = 0;
      byte[] bytes = new byte[1024];

      out = new FileOutputStream(new File(uploadedFileLocation));
      while ((read = uploadedInputStream.read(bytes)) != -1) {
        out.write(bytes, 0, read);
      }
      out.flush();
      out.close();
    } catch (IOException e) {

      e.printStackTrace();
    }

  }
}

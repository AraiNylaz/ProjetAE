package be.vinci.pae.utils;

import org.apache.log4j.Logger;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class FatalException extends WebApplicationException {

  private final Logger log = Logger.getLogger(FatalException.class);

  private static final long serialVersionUID = -5926196101906096391L;

  public FatalException(Throwable cause) {
    super(cause, Response.status(Status.INTERNAL_SERVER_ERROR).build());
    log.error(cause.getMessage(), cause);
  }

  public FatalException(String message, Throwable cause) {
    super(cause,
        Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).type("text/plain").build());
    log.error(message, cause);
  }

  public FatalException(String message) {
    super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).type("text/plain").build());
    log.error(message, this);
  }

}

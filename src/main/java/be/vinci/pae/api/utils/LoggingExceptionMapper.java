package be.vinci.pae.api.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.log4j.Logger;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.Config;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LoggingExceptionMapper implements ExceptionMapper<Throwable> {

  private static final Logger log = Logger.getLogger(ExceptionMapper.class);

  @Override
  public Response toResponse(Throwable exception) {
    if (Config.getBoolProperty("SendStackTraceToClient")) {
      return Response.status(getStatusCode(exception)).entity(getEntity(exception)).build();
    }
    return Response.status(getStatusCode(exception)).entity(exception.getMessage()).build();
  }

  /**
   * Get appropriate HTTP status code for an exception.
   *
   * @param exception throwable object
   * @return Status code from internal server error as integer
   */
  private int getStatusCode(Throwable exception) {
    if (exception instanceof BusinessException) {
      log.warn(exception.getMessage(), exception);
      return Response.Status.UNAUTHORIZED.getStatusCode();
    }
    if (exception instanceof WebApplicationException) {
      log.warn(exception.getMessage(), exception);
      return ((WebApplicationException) exception).getResponse().getStatus();
    }
    log.fatal(exception.getMessage(), exception);
    return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
  }

  /**
   * Get response body for an exception.
   *
   * @param exception throwable object
   * @return Object error message
   */
  private Object getEntity(Throwable exception) {
    StringWriter errorMsg = new StringWriter();
    exception.printStackTrace(new PrintWriter(errorMsg));
    return errorMsg.toString();
  }

}

package be.vinci.pae.utils;

import java.io.InputStream;

public interface UploadInterface {

  public boolean saveToFile(InputStream uploadedInputStream, String uploadedFileLocation);

  public boolean deleteFile(String fileLocation);

}

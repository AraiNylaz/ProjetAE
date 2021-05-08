package be.vinci.pae.utils;

import java.io.InputStream;

public interface UploadInterface {

  boolean saveToFile(InputStream uploadedInputStream, String uploadedFileLocation);

  boolean deleteFile(String fileLocation);

}

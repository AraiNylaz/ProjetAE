package be.vinci.pae.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

  private static Properties props;

  /**
   * Load the properties file and load the content.
   *
   * @param file file to load
   */
  public static void load(String file) {
    props = new Properties();
    try (InputStream input = new FileInputStream(file)) {
      props.load(input);
    } catch (IOException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get the property by his key
   * 
   * @param key
   * @return property
   */
  public static String getProperty(String key) {
    String property = props.getProperty(key);
    if (property == null) {
      throw new FatalException("Missing property");
    }
    return property;
  }

  public static Integer getIntProperty(String key) {
    return Integer.parseInt(props.getProperty(key));
  }

  public static boolean getBoolProperty(String key) {
    return Boolean.parseBoolean(props.getProperty(key));
  }

}

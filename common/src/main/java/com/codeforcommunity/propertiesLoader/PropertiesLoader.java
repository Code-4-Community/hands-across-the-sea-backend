package com.codeforcommunity.propertiesLoader;

import com.codeforcommunity.logger.SLogger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
  private static final String basePath = "properties/";
  private static final String fileName = "server.properties";

  private static final Properties serverProperties = getServerProperties();

  /**
   * Loads the server properties file on server start.
   *
   * @return the properties file.
   */
  private static Properties getServerProperties() {
    String path = basePath + fileName;

    try (InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(path)) {
      Properties prop = new Properties();
      prop.load(input);
      return prop;
    } catch (IOException | NullPointerException e) {
      String errorMsg = String.format("Failed to load file: `%s`", path);
      SLogger.logApplicationError(e);
      throw new RuntimeException(errorMsg);
    }
  }

  /**
   * Gets the value of the property with the given name from the server properties file.
   *
   * @param propertyName the property to load.
   * @return the property's value.
   * @throws IllegalArgumentException if the given property is not found.
   */
  public static String loadProperty(String propertyName) {
    String value = serverProperties.getProperty(propertyName);

    if (value == null) {
      String errorMsg =
          String.format("No property found `%s` in property file: `%s`", propertyName, fileName);
      throw new IllegalArgumentException(errorMsg);
    }

    return value;
  }
}

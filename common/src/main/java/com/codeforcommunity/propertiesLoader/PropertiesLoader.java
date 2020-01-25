package com.codeforcommunity.propertiesLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

  private static Properties getProperties(String file) {

    try (InputStream input = PropertiesLoader.class.getClassLoader().
            getResourceAsStream(file)) {
      Properties prop = new Properties();
      prop.load(input);
      return prop;
    } catch (IOException ex) {
      throw new IllegalArgumentException("Cannot find file: " + file, ex);
    }
  }

  public static Properties getEmailerProperties() {
    return getProperties("emailer.properties");
  }
  public static Properties getDbProperties() {
    return getProperties("db.properties");
  }
}

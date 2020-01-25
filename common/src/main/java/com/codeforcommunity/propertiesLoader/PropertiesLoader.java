package com.codeforcommunity.propertiesLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

  public static Properties getProperties(Class cl) throws Exception {

    String propertiesFile = cl.getSimpleName().toLowerCase().concat(".properties");

    try (InputStream input = cl.getClassLoader().getResourceAsStream(propertiesFile)) {
      Properties prop = new Properties();
      prop.load(input);
      return prop;
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    throw new Exception();

  }

}

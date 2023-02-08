package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
  private static ConfigUtil instance;

  private final Properties properties;

  private ConfigUtil() {
    this.properties = readProperties();
  }

  public static ConfigUtil getInstance() {
    if (instance == null) {
      instance = new ConfigUtil();
    }

    return instance;
  }

  public Properties getProperties() {
    return properties;
  }

  /**
   * Read application.properties file on resources folder
   *
   * @return Properties
   */
  private Properties readProperties() {
    Properties properties = new Properties();

    try {
      try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
        try {
          properties.load(is);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return properties;
  }
}

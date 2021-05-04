package com.codeforcommunity.dto;

import com.codeforcommunity.logger.SLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/** */
public class CsvSerializer {
  protected static final CsvMapper mapper = new CsvMapper();
  private static SLogger logger = new SLogger(CsvSerializer.class);

  /**
   * Gets the schema for a given object
   *
   * @param o This is the object to get a schema for
   * @return This returns the schema for the inputted object
   */
  private static CsvSchema getSchema(Object o) {
    return mapper.schemaFor(o.getClass());
  }

  /**
   * Gets a list of the fields of a given object in csv format
   *
   * @param o This is the object we want to get the header for
   * @return This returns the header for the object, which is all the fields of the object
   */
  public static String getObjectHeader(Object o) {
    StringBuilder builder = new StringBuilder();
    boolean first = true;
    for (CsvSchema.Column col : getSchema(o)) {
      if (!first) {
        builder.append(",");
      }
      builder.append(col.getName());
      first = false;
    }
    builder.append('\n');
    return builder.toString();
  }

  /**
   * Gets the values of all the fields of a given object in csv format
   *
   * @param o This is the object we want to generate the csv for
   * @return This returns all the field values of the given object in csv form
   */
  public static String toCsv(Object o) {
    try {
      return mapper.writer(getSchema(o)).writeValueAsString(o);
    } catch (JsonProcessingException e) {
      logger.error("Something went wrong with toCSV", e);
      throw new IllegalStateException("Something went wrong with toCSV", e);
    }
  }
}

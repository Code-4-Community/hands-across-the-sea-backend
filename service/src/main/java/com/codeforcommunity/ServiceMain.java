package com.codeforcommunity;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.api.INotesProcessor;
import com.codeforcommunity.processor.AuthDataBaseImpl;
import com.codeforcommunity.processor.AuthProcessorImpl;
import com.codeforcommunity.processor.NotesProcessorImpl;
import com.codeforcommunity.rest.ApiRouter;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServiceMain {
  private DSLContext db;
  private final Properties dbProperties = new Properties();

  public static void main(String[] args) {
    try {
      ServiceMain serviceMain = new ServiceMain();
      serviceMain.initialize();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Start the server, get everything going.
   */
  public void initialize() throws Exception {
    loadProperties();
    connectDb();
    initializeServer();
  }

  /**
   * Load properties from a db.properties file into a Properties field.
   */
  private void loadProperties() {
    InputStream propertiesStream = this.getClass().getClassLoader().getResourceAsStream("db.properties");
    try {
      dbProperties.load(propertiesStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Connect to the database and create a DSLContext so jOOQ can interact with it.
   */
  private void connectDb() {
    //This block ensures that the MySQL driver is loaded in the classpath
    try {
      Class.forName(dbProperties.getProperty("database.driver"));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    //TODO: These arguments should be read out of a properties file
    DSLContext db = DSL.using(dbProperties.getProperty("database.url"),
        dbProperties.getProperty("database.username"), dbProperties.getProperty("database.password"));
    this.db = db;
  }

  /**
   * Initialize the server and get all the supporting classes going.
   */
  private void initializeServer() throws Exception {
    INotesProcessor notesProcessor = new NotesProcessorImpl(this.db);
    IAuthProcessor authProcessor = new AuthProcessorImpl(new AuthDataBaseImpl(this.db));
    ApiRouter router = new ApiRouter(notesProcessor, authProcessor);
    startApiServer(router);
  }

  /**
   * Start up the actual API server that will listen for requests.
   */
  private void startApiServer(ApiRouter router) {
    ApiMain apiMain = new ApiMain(router);
    apiMain.startApi();
  }
}

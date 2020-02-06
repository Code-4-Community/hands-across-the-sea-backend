package com.codeforcommunity;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.api.INotesProcessor;
import com.codeforcommunity.auth.JWTAuthorizer;
import com.codeforcommunity.auth.JWTCreator;
import com.codeforcommunity.auth.JWTHandler;
import com.codeforcommunity.processor.AuthProcessorImpl;
import com.codeforcommunity.processor.NotesProcessorImpl;
import com.codeforcommunity.propertiesLoader.PropertiesLoader;
import com.codeforcommunity.rest.ApiRouter;
import com.codeforcommunity.rest.subrouter.FailureHandler;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServiceMain {
  private DSLContext db;
  private final Properties dbProperties = PropertiesLoader.getDbProperties();

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
  public void initialize() {
    connectDb();
    initializeServer();
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

    DSLContext db = DSL.using(dbProperties.getProperty("database.url"),
        dbProperties.getProperty("database.username"), dbProperties.getProperty("database.password"));
    this.db = db;
  }

  /**
   * Initialize the server and get all the supporting classes going.
   */
  private void initializeServer() {
    JWTHandler jwtHandler = new JWTHandler("this is secret, don't tell anyone"); //TODO: Dynamically load this
    JWTAuthorizer jwtAuthorizer = new JWTAuthorizer(jwtHandler);
    JWTCreator jwtCreator = new JWTCreator(jwtHandler);

    INotesProcessor notesProcessor = new NotesProcessorImpl(this.db);
    IAuthProcessor authProcessor = new AuthProcessorImpl(this.db, jwtCreator);
    ApiRouter router = new ApiRouter(notesProcessor, authProcessor, jwtAuthorizer);
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

package com.codeforcommunity;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.api.INotesProcessor;
import com.codeforcommunity.auth.JWTAuthorizer;
import com.codeforcommunity.auth.JWTCreator;
import com.codeforcommunity.auth.JWTHandler;
import com.codeforcommunity.logger.SLogger;
import com.codeforcommunity.processor.AuthProcessorImpl;
import com.codeforcommunity.processor.NotesProcessorImpl;
import com.codeforcommunity.propertiesLoader.PropertiesLoader;
import com.codeforcommunity.rest.ApiRouter;
import io.vertx.core.Vertx;
import java.util.Properties;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

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

  /** Start the server, get everything going. */
  public void initialize() {
    setUpSystemProperties();
    connectDb();
    initializeServer();
  }

  /** Adds any necessary system properties. */
  private void setUpSystemProperties() {
    Properties systemProperties = System.getProperties();
    systemProperties.setProperty(
        "vertx.logger-delegate-factory-class-name",
        "io.vertx.core.logging.SLF4JLogDelegateFactory");
    System.setProperties(systemProperties);
  }

  /** Connect to the database and create a DSLContext so jOOQ can interact with it. */
  private void connectDb() {
    // This block ensures that the MySQL driver is loaded in the classpath
    try {
      Class.forName(dbProperties.getProperty("database.driver"));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    DSLContext db =
        DSL.using(
            dbProperties.getProperty("database.url"),
            dbProperties.getProperty("database.username"),
            dbProperties.getProperty("database.password"));
    this.db = db;
  }

  /** Initialize the server and get all the supporting classes going. */
  private void initializeServer() {
    Properties jwtProperties = PropertiesLoader.getJwtProperties();
    String jwtSecretKey = PropertiesLoader.loadProperty(jwtProperties, "secret_key");

    JWTHandler jwtHandler = new JWTHandler(jwtSecretKey);
    JWTAuthorizer jwtAuthorizer = new JWTAuthorizer(jwtHandler);
    JWTCreator jwtCreator = new JWTCreator(jwtHandler);

    Vertx vertx = Vertx.vertx();
    String productName = "C4C Backend Scaffold";
    SLogger.initializeLogger(vertx, productName);

    // Log uncaught exceptions to Slack
    vertx.exceptionHandler(SLogger::logApplicationError);

    INotesProcessor notesProcessor = new NotesProcessorImpl(this.db);
    IAuthProcessor authProcessor = new AuthProcessorImpl(this.db, jwtCreator);
    ApiRouter router = new ApiRouter(notesProcessor, authProcessor, jwtAuthorizer);
    startApiServer(router, vertx);
  }

  /** Start up the actual API server that will listen for requests. */
  private void startApiServer(ApiRouter router, Vertx vertx) {
    ApiMain apiMain = new ApiMain(router);
    apiMain.startApi(vertx);
  }
}

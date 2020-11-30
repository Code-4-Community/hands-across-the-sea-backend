package com.codeforcommunity;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.api.IProtectedUserProcessor;
import com.codeforcommunity.auth.JWTAuthorizer;
import com.codeforcommunity.auth.JWTCreator;
import com.codeforcommunity.auth.JWTHandler;
import com.codeforcommunity.logger.SLogger;
import com.codeforcommunity.processor.AuthProcessorImpl;
import com.codeforcommunity.processor.ProtectedUserProcessorImpl;
import com.codeforcommunity.propertiesLoader.PropertiesLoader;
import com.codeforcommunity.requester.Emailer;
import com.codeforcommunity.rest.ApiRouter;
import io.vertx.core.Vertx;
import java.util.Properties;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

public class ServiceMain {
  private DSLContext db;

  public static void main(String[] args) {
    try {
      ServiceMain serviceMain = new ServiceMain();
      serviceMain.initialize();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Start the server, get everything going. */
  public void initialize() throws ClassNotFoundException {
    updateSystemProperties();
    createDatabaseConnection();
    initializeServer();
  }

  /** Adds any necessary system properties. */
  private void updateSystemProperties() {
    String propertyKey = "vertx.logger-delegate-factory-class-name";
    String propertyValue = "io.vertx.core.logging.SLF4JLogDelegateFactory";

    Properties systemProperties = System.getProperties();
    systemProperties.setProperty(propertyKey, propertyValue);
    System.setProperties(systemProperties);
  }

  /** Connect to the database and create a DSLContext so jOOQ can interact with it. */
  private void createDatabaseConnection() throws ClassNotFoundException {
    // Load configuration from db.properties file
    String databaseDriver = PropertiesLoader.loadProperty("database_driver");
    String databaseUrl = PropertiesLoader.loadProperty("database_url");
    String databaseUsername = PropertiesLoader.loadProperty("database_username");
    String databasePassword = PropertiesLoader.loadProperty("database_password");

    // This throws an exception of the database driver is not on the classpath
    Class.forName(databaseDriver);

    // Create a DSLContext from the above configuration
    this.db = DSL.using(databaseUrl, databaseUsername, databasePassword);
  }

  /** Initialize the server and get all the supporting classes going. */
  private void initializeServer() {
    // Load the JWT secret key from the properties file
    String jwtSecretKey = PropertiesLoader.loadProperty("jwt_secret_key");

    JWTHandler jwtHandler = new JWTHandler(jwtSecretKey);
    JWTAuthorizer jwtAuthorizer = new JWTAuthorizer(jwtHandler);
    JWTCreator jwtCreator = new JWTCreator(jwtHandler);

    // Create the Vertx instance
    Vertx vertx = Vertx.vertx();

    // Configure the Slack logger and log uncaught exceptions
    String productName = PropertiesLoader.loadProperty("slack_product_name");
    SLogger.initializeLogger(vertx, productName);
    vertx.exceptionHandler(SLogger::logApplicationError);

    Emailer emailer = new Emailer();

    // Create the processor implementation instances
    IAuthProcessor authProc = new AuthProcessorImpl(this.db, emailer, jwtCreator);
    IProtectedUserProcessor protectedUserProc = new ProtectedUserProcessorImpl(this.db, emailer);

    // Create the API router and start the HTTP server
    ApiRouter router = new ApiRouter(authProc, protectedUserProc, jwtAuthorizer);
    startApiServer(router, vertx);
  }

  /** Start up the actual API server that will listen for requests. */
  private void startApiServer(ApiRouter router, Vertx vertx) {
    ApiMain apiMain = new ApiMain(router);
    apiMain.startApi(vertx);
  }
}

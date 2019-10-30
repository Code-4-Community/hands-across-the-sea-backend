# C4C Backend Scaffold

This project is the Java backend api scaffold for Code4Community. It is meant to demonstrate API routing and database connection techniques that should be utilized in all C4C Java back ends.

### Running the Backend

1. Update the properties file in `/persist/src/main/resources/db.properties` to contain your database connection information
2. Run `mvn clean install` from the root directory
3. Either run the `ServiceMain.java` file directly or run the command `mvn exec:java` from the /service module.
4. Query the API from `localhost:8081`


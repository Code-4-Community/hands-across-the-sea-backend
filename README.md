# C4C Backend Scaffold

This project is the Java backend api scaffold for Code4Community. It is meant to demonstrate API routing and database connection techniques that should be utilized in all C4C Java back ends.

#### Start a local postgres database
1. Download postgres with pgAdmin4. https://www.postgresql.org/download/
2. Start pgAdmin4, the database should start on localhost, port 5432 by default.
3. You must create a login/ group role that you can connect to the database with.
On the left side bar on pgAdmin open Login/Group Roles, right click and select create.
Set the name to a username of your choosing.
Under the Definition tab, set a password for the user.
Under the Privileges tab, select yes for every option.
4. Create a Database named 'checkin' with your created user as the owner.
Right click Databases and select create.
Set the name to 'checkin' and then select your created user.

#### Update Secret Files
1. Update the properties file in `/common/src/main/resources/db.properties` to contain your database connection information.
You should only have to change the username and password. 

#### Compile the code base
1. Run `mvn clean install` from the root directory

#### Run the API
1. The `ServiceMain.java` class has the main method for running the code, this can be run directly in IntelliJ.
Alternatively: `mvn install` creates a jar file at:
`service/target/service-1.0-SNAPSHOT-jar-with-dependencies.jar`.
This can be run from the command line with the command `java -jar service-1.0-SNA....`

#### Hitting the API
By default the API is accessible at `http://localhost:8081`. All routes
have the `/api/v1` prefix to them.

The HTTP request to get all notes for example would be:
`GET http://localhost:8081/api/v1/notes`


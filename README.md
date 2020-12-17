# C4C Backend Scaffold

[![Build Status](https://travis-ci.org/Code-4-Community/backend-scaffold.svg?branch=master)](https://travis-ci.org/Code-4-Community/backend-scaffold)
[![Coverage Status](https://coveralls.io/repos/github/Code-4-Community/backend-scaffold/badge.svg?branch=master)](https://coveralls.io/github/Code-4-Community/backend-scaffold?branch=master)

This project is the Java backend api scaffold for Code4Community. It is meant to demonstrate API routing and database connection techniques that should be utilized in all C4C Java back ends.

#### Complete Setup Guide
 - See https://docs.c4cneu.com/getting-started/setup-local-dev/ for a complete setup guide for:
   - Installing, creating, and running a local PostgreSQL database (`backend-scaffold`)
   - Configuring IntelliJ
   - Installing Maven
   - Installing Java 8
   - Configuring project properties files
   - Compiling and running the API

#### Compile the Code Base
 - If `mvn clean install` fails because of `spotless:check`, 
 then run `mvn spotless:apply` to apply code formatting corrections before
 re-running `mvn clean install`. 

#### Run the API
 - The `ServiceMain.java` class has the main method for running the code, this can be run directly in IntelliJ.
 - Alternatively: `mvn install` creates a jar file at:
 `service/target/service-1.0-SNAPSHOT-jar-with-dependencies.jar`.
   - This can be run from the command line with the command `java -jar service-1.0-SNA....`.

#### Hitting the API
 - By default the API is accessible at `http://localhost:8081`.
 - All routes have the `/api/v1` prefix to them.
 - **Example**: the HTTP request to get all notes would be be: `GET http://localhost:8081/api/v1/notes`.

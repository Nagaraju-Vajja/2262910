Prerequisites
=============

* Java 11 
* Maven 3.x

Running the Application
=======================

Embedded Undertow
-----------------

**Step 1**: Compile the project with `dev` profile to package executable WAR that can be run "in-place":

    mvn clean install -P dev

**Step 2**: Use maven spring-boot command to run the application with dev profile:

    cd service
    mvn spring-boot:run -P dev


Using JBOSS or any other Java Servlet Container
-----------------------------------------------

Use Maven to get the WAR file:

    mvn clean install

The WAR file will be located in `service/target`

Using IntelliJ IDEA
-------------------
**Step 1**: Open **Maven Projects** tool window and make `dev` profile active. Make sure that IDEA pulled all required
dependencies by clicking **Reimport All Maven Projects** button.

**Step 2**: Run the main method of the `Application` class.

------------------



Please note, that by default main application log will be written in `/www/logs/referenceapp/referenceapp_app.log`. If the `/www/logs/referenceapp`
directory doesn't exist, you will see an exception while deploying. Despite the exception, application
will start. Use
`logging.path` property to define your own location.

Running Integration Tests
=========================

Integration tests can be run by switching `skipITs` flag to false:

    mvn clean install -DskipITs=false

By default, the tests are run against `localhost:8080`.

Please note, that the command will just run integration tests, it won't start the application. Thus, you need to either
deploy the application locally or use `dev` profile, which will start the application in embedded mode:

    mvn clean install -P dev -DskipITs=false 

 
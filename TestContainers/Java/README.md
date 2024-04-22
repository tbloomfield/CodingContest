A Test Container for compiling, executing, and running tests on arbitrary Java code.

- Captures CPU and memory before and after execution
- Compiles arbitrary text into a valid Java class.
- Virtually executes classes via reflection, invoking specified test methods and capturing results.

# Web Demo
To start the Virtual Queue service, execute `mvn spring-boot:run`

After Startup, Swagger is available at: `http://localhost:8082/swagger-ui/index.html`

# Deployment
This code is meant to be Dockerized with a small total memory footprint and either:
 - deployed to a managed cluster and monitored via a container manager.
 - deployed programatically, executed, and spun down.

# Dependencies
Running / starting this project requires:

- Java 21 + (Virtual Threads are utilized in this project)
- Maven

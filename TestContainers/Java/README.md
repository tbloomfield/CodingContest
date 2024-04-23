A Test Container for compiling, executing, and running tests on arbitrary Java code.

- Captures CPU and memory before and after execution
- Compiles arbitrary text into a valid Java class.
- Virtually executes classes via reflection, invoking specified test methods and capturing results.

# Future features
- Loading test cases and configuration from a remote repository (S3)
- Support for more dynamic test cases versus input / output cases.

# Web Demo
To start the Virtual Queue service, execute `mvn spring-boot:run`

After Startup, Swagger is available at: `http://localhost:8082/swagger-ui/index.html`

Sample curl and response:
```
curl -X 'POST' \
  'http://localhost:8082/codeRunner/execute' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "testCases": [
    {
      "testCaseId": "123"     
    }
  ],
  "codeToExecute": "public class HelloWorld {public HelloWorld() {} public String outputHelloWorld() {return \"hello world\"; }}",
  "className": "HelloWorld",
  "methodNameToTest": "outputHelloWorld"
}'
```

Response:
```
[
  {
    "testCaseId": "123",
    "result": "hello world"
  }
]
```

# Deployment
This code is meant to be Dockerized with a small total memory footprint and either:
 - deployed to a managed cluster and monitored via a container manager.
 - deployed programatically, executed, and spun down.

## Building
From the base directory of this project:
```
mvn install
docker build --build-arg JAR_FILE=target/*.jar -t tbloomfield/java-testcontainer .
```

# Dependencies
Running / starting this project requires:

- Java 21 + (Virtual Threads are utilized in this project)
- Maven

A Test Container for compiling, executing, and running tests on arbitrary Java code.

- Captures CPU and memory before and after execution
- Compiles arbitrary text into a valid Java class.
- Includes two ways of running tests:
  - Pass test cases via REST endpoint to run in-memory.
  - Write test cases in code.  Allows for more detailed testing of target code.

# Future features
- Loading test cases and configuration from a remote repository (S3)

# Web Demo
To start the Virtual Queue service, execute `mvn spring-boot:run`

After Startup, Swagger is available at: `http://localhost:8082/swagger-ui/index.html`

Sample curl and response:
```
curl -X 'POST' \
  'http://localhost:8082/codeRunner/execute' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{"testCases":[{"testCaseId":"123","arguments":["User123"]}],"codeToExecute":"public class PrintName {\r\n    public String printMyName(String name) {\r\n    \treturn String.format(\"Your name is %s\", name);\r\n    }\r\n}","className":"PrintName","methodNameToTest":"printMyName","argTypes":["java.lang.String"]}'
```

Response:
```
{
  "performanceInfo": {
    "cpuUsage": 0,
    "elapsedCPUInNs": 0,
    "elapsedTime": 3,
    "usedMemoryInBytes": -8192
  },
  "testResults": [
    {
      "testCaseId": "123",
      "result": "Your name is User123"
    }
  ]
}
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

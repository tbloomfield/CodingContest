package org.tbloomfield.codingcontest.executor.containers;

import java.io.File;
import java.time.Duration;

import org.springframework.web.client.RestClient;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class TestContainerExecutor {
	
	private final int HTTP_PORT = 8080;
	private DockerComposeContainer environment;

	private void loadContainer() {
		environment =
		    new DockerComposeContainer(new File("src/main/resources/compose-test.yml"))
		           .withExposedService("web", HTTP_PORT, 
		           Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
                   .withLocalCompose(true);
	}
	
	private void requestContainerExecution() {		
		String hostPort = String.format("http://%s:%d", environment.getServiceHost("web", HTTP_PORT), environment.getServicePort("web", HTTP_PORT));
		RestClient containerClient = RestClient.builder()
				.baseUrl(hostPort)
				.build();
		
		
	}
}

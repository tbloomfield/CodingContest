package org.tbloomfield.codingcontest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Runnable entry point for our virtual queue service.
 */
@SpringBootApplication
public class VirtualQueueService  {

	public static void main(String[] args) {
		SpringApplication.run(VirtualQueueService.class, args);
	}
}


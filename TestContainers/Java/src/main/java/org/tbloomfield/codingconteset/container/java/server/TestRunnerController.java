package org.tbloomfield.codingconteset.container.java.server;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.tbloomfield.codingconteset.container.java.executor.TestCase;

@RestController
public class TestRunnerController {
	
	@PostMapping("/execute")
	public boolean executeTest(@RequestBody List<TestCase<?>> testCases, String testCode) { 
		return true;
	}
}

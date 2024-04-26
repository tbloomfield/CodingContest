package org.tbloomfield.codingcontest.executor.server;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tbloomfield.codingconteset.container.java.executor.ExecutionContext;
import org.tbloomfield.codingconteset.container.java.executor.FileHelper;
import org.tbloomfield.codingconteset.container.java.executor.JavaExecutor;
import org.tbloomfield.codingconteset.container.java.executor.TestCase;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/solution")
public class SolutionController {
    
  @GetMapping("/listQuestions")
  public List<Question> list() { 
      
  }
	
	@PostMapping("/submit")
	public List<TestResult> submitCode(@RequestBody CodeEntry entry) throws ClassNotFoundException {
		List<TestResult> testResults = new ArrayList<>();
		
		try {
		} catch (IOException | URISyntaxException e) {
			log.error(e.getMessage(), e);
		}
		return testResults;
	}
}

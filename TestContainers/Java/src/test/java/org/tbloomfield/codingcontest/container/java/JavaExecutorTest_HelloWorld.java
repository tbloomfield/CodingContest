package org.tbloomfield.codingcontest.container.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import org.tbloomfield.codingcontest.container.bo.TestCase;
import org.tbloomfield.codingcontest.container.bo.TestResult;
import org.tbloomfield.codingcontest.container.java.executor.ExecutionContext;
import org.tbloomfield.codingcontest.container.java.executor.JavaExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * Tests compilation of arbitrary test files, "HelloWorld.java" and execution of that generated file using
 * reflection.
 * 
 */
@Slf4j
public class JavaExecutorTest_HelloWorld {
	private JavaExecutor executor;
	
	@BeforeEach
	public void setup() {
		executor = new JavaExecutor();
	}
	
	@Test
	public void testSubmissionCreation() throws IOException, URISyntaxException {		
        File file = ResourceUtils.getFile("classpath:submissions/HelloWorld.java");                
		executor.compile(file.toURI());
	}
	
	@Test 
	public void testSubmissionExecution() throws FileNotFoundException {
		File file = ResourceUtils.getFile("classpath:submissions/HelloWorld.java");
        List<TestCase> testCases = List.of(new TestCase("123", null, "Hello, World!"));
        
        ExecutionContext context = ExecutionContext.builder()
        		.entryMethodName("outputHelloWorld")
        		.file(file)
        		.ttlInSeconds(100)
        		.testCases(Optional.of(testCases))
        		.build();
        		
		List<TestResult> results = executor.executeCode(context);
		assertEquals(1, results.size());
		assertEquals("Hello, World!", results.getFirst().getResult());
	}
}

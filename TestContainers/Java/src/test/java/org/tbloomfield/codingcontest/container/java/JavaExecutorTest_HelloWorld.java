package org.tbloomfield.codingcontest.container.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import org.tbloomfield.codingconteset.container.java.executor.ExecutionContext;
import org.tbloomfield.codingconteset.container.java.executor.JavaExecutor;
import org.tbloomfield.codingconteset.container.java.executor.TestCase;
import org.tbloomfield.codingconteset.container.java.server.TestCaseDto;
import org.tbloomfield.codingconteset.container.java.server.TestResult;

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
	
	/*@AfterAll
	public static void teardown() {
		//cleanup file created
		File tempDir = FileUtils.getTempDirectory();
		File compiledHelloWorld = new File(String.format("%s/%s", tempDir.getPath(), "HelloWorld.class" ));
		try {
			FileUtils.delete(compiledHelloWorld);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}*/
	
	@Test
	public void testSubmissionCreation() throws IOException, URISyntaxException {		
        File file = ResourceUtils.getFile("classpath:submissions/HelloWorld.java");                
		executor.compile(file.toURI());
	}
	
	@Test 
	public void testSubmissionExecution() throws FileNotFoundException {
		File file = ResourceUtils.getFile("classpath:submissions/HelloWorld.java");
        List<TestCase> testCases = List.of(new TestCase("123", null));
        
        ExecutionContext context = ExecutionContext.builder()
        		.className("HelloWorld")
        		.entryMethodName("outputHelloWorld")
        		.filePath(file.getParentFile())
        		.ttlInSeconds(100)
        		.testCases(testCases)
        		.build();
        		
		List<TestResult> results = executor.executeCode(context);
		assertEquals(1, results.size());
		assertEquals("Hello, World!", results.getFirst().getResult());
	}
}

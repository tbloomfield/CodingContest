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
import org.tbloomfield.codingcontest.container.java.executor.CompileResult;
import org.tbloomfield.codingcontest.container.java.executor.ExecutionContext;
import org.tbloomfield.codingcontest.container.java.executor.JavaExecutor;
import org.tbloomfield.codingcontest.container.java.executor.LocalFileHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * Tests compilation of arbitrary test files, "Fibonacci.java" and execution of that generated file using
 * reflection and file-store strategies.
 * 
 */
@Slf4j
public class JavaExecutorTest_Fibonacci {
	private JavaExecutor executor;
	
	@BeforeEach
	public void setup() {
		executor = new JavaExecutor();
	}
	
	@Test
	public void testSubmissionCreation() throws IOException, URISyntaxException {		
    File file = ResourceUtils.getFile("classpath:submissions/Fibonacci.java");		
		executor.compile(file.toURI());
	}
	
	@Test 
	public void testSubmissionExecution() throws FileNotFoundException {
    List<TestCase> testCases = List.of(new TestCase("123", List.of(5), 8));
    File file = ResourceUtils.getFile("classpath:submissions/Fibonacci.java");
    
    ExecutionContext context = ExecutionContext.builder()    		
    		.entryMethodName("findFib")
    		.file(file)
    		.ttlInSeconds(100)
        .methodParameters(Optional.of(List.of(int.class)))
    		.testCases(Optional.of(testCases))
    		.build();
        		
		List<TestResult> results = executor.executeCode(context);
		assertEquals(1, results.size());
		assertEquals(8, results.getFirst().getResult());
	}
	
	@Test
	public void testSubmissionExecution_File() throws IOException, URISyntaxException {
    String contents = TestHelper.findAndReturnSubmissionContents("Fibonacci", LocalFileHelper.JAVA_EXTENSION);
    File writtenFile = LocalFileHelper.writeRandomTempFileWithContents(contents, "Fibonacci");
    List<File> testFiles = LocalFileHelper.copySupportingTestFiles(writtenFile, LocalFileHelper.JAVA_EXTENSION);
    
    //execute testcase
    for(File f : testFiles) {
      //compile test file and their dependencies.
      CompileResult result = executor.compile(f.toURI());
      log.info(result.getCompilationOutput());
      assertEquals(0, result.getStatusCode());
        
      ExecutionContext context = ExecutionContext.builder()        
              .entryMethodName("executeTest")
              .file(f)
              .ttlInSeconds(100)
              .build();
      
      List<TestResult> testResult = executor.executeCode(context);
      log.info(testResult.get(0).toString());
      
    }
	}
}

package org.tbloomfield.codingcontest.container.java.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tbloomfield.codingcontest.container.java.executor.CompileResult;
import org.tbloomfield.codingcontest.container.java.executor.ExecutionContext;
import org.tbloomfield.codingcontest.container.java.executor.JavaExecutor;
import org.tbloomfield.codingcontest.container.java.executor.LocalFileHelper;
import org.tbloomfield.codingcontest.container.java.executor.TestCase;
import org.tbloomfield.codingcontest.container.java.server.metrics.JVMMetricDelta;
import org.tbloomfield.codingcontest.container.java.server.metrics.JVMMetrics;
import org.tbloomfield.codingcontest.container.java.server.metrics.JVMMetricsMonitor;
import org.tbloomfield.codingcontest.container.java.service.dto.CodeEntryDto;
import org.tbloomfield.codingcontest.container.java.service.dto.ExecutionResultDto;
import org.tbloomfield.codingcontest.container.java.service.dto.TestCaseDto;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/codeRunner")
public class ContainerController {
	
	@Autowired private JavaExecutor executor;
	
	@PostMapping("/execute")
	public ExecutionResultDto executeTest(@RequestBody CodeEntryDto entry) throws ClassNotFoundException {		
		List<TestResult> testResults = new ArrayList<>();
    String possibleErrors = null;
		JVMMetricDelta delta = null;
		CompileResult result = null;

		try {
			File tempFile = LocalFileHelper.writeRandomTempFileWithContents(entry.getCodeToExecute(), entry.getClassName());
			result = executor.compile(tempFile.toURI());
			
			//handle compilation errors
			if(result.getStatusCode() == CompileResult.OK_STATUS) {
			    
	      //if no test parameters are specified, copy test files 
	      if(entry.getTestCases() != null && !entry.getTestCases().isEmpty()) { 
	          LocalFileHelper.copySupportingTestFiles(tempFile, LocalFileHelper.JAVA_EXTENSION);
	      }
			    
				//cast test cases from object to expected types
				var paramTypes = getMethodParamTypes(entry.getArgTypes());
				var testCases = caseTestTypes(paramTypes, entry.getTestCases());				
				
				 ExecutionContext context = ExecutionContext.builder()
	        		.entryMethodName(entry.getMethodNameToTest())	        		
	        		.file(tempFile)
	        		.ttlInSeconds(100)
	        		.methodParameters(Optional.of(getMethodParamTypes(entry.getArgTypes())))
	        		.testCases(Optional.of(testCases))
	        		.build();
		 
				 JVMMetrics start = JVMMetricsMonitor.capture();
				 testResults = executor.executeCode(context);
				 delta = JVMMetricsMonitor.endCapture(start);
			}
		} catch (RuntimeException | IOException | URISyntaxException e) {
		  possibleErrors = e.getMessage();	
		}
		
		if(result != null && result.getStatusCode() != CompileResult.OK_STATUS) { 
			possibleErrors = scrubError(entry.getClassName(), result.getCompilationOutput());
		}
		
		return ExecutionResultDto.builder()
				.performanceInfo(delta)
				.testResults(testResults)
				.errors( possibleErrors )
				.build();
	}
	
	/**
	 * Removes directory information from the error for security purposes:
	 * 
	 * @param originalError
	 * @return
	 */
	private String scrubError(String className, String originalError) {
		return originalError.substring(
				originalError.indexOf(className + ".java:"));
	}
	
	/**
	 * Converts from method parameter string types to class types; required for proper reflection of 
	 * code submitted.
	 * 
	 * @param entryTestCases
	 * @return A list of class method parameter types, in order.
	 * @throws ClassNotFoundException if String type was not a valid class type.
	 */
	private List<Class> getMethodParamTypes(String[] entryTestCases) throws ClassNotFoundException {
		if(entryTestCases == null) { 
			return List.of();
		}
		List<Class> paramTypes = new ArrayList<>(entryTestCases.length);
		for(String paramType : entryTestCases) {
			paramTypes.add(Class.forName(paramType));
		}
		return paramTypes;
	}
	
	/**
	 * Cast method parameter arguments from String type (as passed in the DTO) to their native types.
	 * @return
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<TestCase> caseTestTypes(@NonNull List<Class> paramTypes, List<TestCaseDto> entryTestCases) throws ClassNotFoundException {
		List<TestCase> filteredResults = new ArrayList<>();	
		
		for(TestCaseDto testCaseDto : entryTestCases) {
			List<Object> castArguments = new ArrayList<>();
			TestCase testCase = new TestCase();
			testCase.setTestCaseId(testCaseDto.getTestCaseId());
			testCase.setExpectedResult(testCaseDto.getExpectedResult());
		
			//convert parameter types.
			for(int i = 0 ; testCaseDto.getArguments() != null && i < testCaseDto.getArguments().length; i++) {
				Class paramType = paramTypes.get(i);
				String argumentVal = testCaseDto.getArguments()[i];
				if(paramType == int.class) { 
					castArguments.add(Integer.valueOf(argumentVal));
				} else if(paramType == String.class) {
					castArguments.add(argumentVal);
				} else if(paramType == Void.class) {
					castArguments.add(null);
				}
			}
			testCase.setArguments(castArguments);
			filteredResults.add(testCase);
		}
		
		return filteredResults;
	}
}

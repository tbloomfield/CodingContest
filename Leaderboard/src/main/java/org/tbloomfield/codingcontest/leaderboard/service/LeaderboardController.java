package org.tbloomfield.codingcontest.leaderboard.service;

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
import org.tbloomfield.codingconteset.container.java.executor.CompileResult;
import org.tbloomfield.codingconteset.container.java.executor.ExecutionContext;
import org.tbloomfield.codingconteset.container.java.executor.FileHelper;
import org.tbloomfield.codingconteset.container.java.executor.JavaExecutor;
import org.tbloomfield.codingconteset.container.java.executor.TestCase;
import org.tbloomfield.codingconteset.container.java.server.metrics.JVMMetricDelta;
import org.tbloomfield.codingconteset.container.java.server.metrics.JVMMetrics;
import org.tbloomfield.codingconteset.container.java.server.metrics.JVMMetricsMonitor;
import org.tbloomfield.codingconteset.container.java.service.dto.CodeEntryDto;
import org.tbloomfield.codingconteset.container.java.service.dto.ExecutionResultDto;
import org.tbloomfield.codingconteset.container.java.service.dto.TestCaseDto;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/codeRunner")
public class LeaderboardController {
	
	@Autowired private JavaExecutor executor;
	
	@PostMapping("/execute")
	public ExecutionResultDto executeTest(Score score) throws ClassNotFoundException {		
		List<TestResult> testResults = new ArrayList<>();
		JVMMetricDelta delta = null;
		CompileResult result = null;
		
		try {
			File tempFile = FileHelper.writeRandomTempFileWithContents(entry.getCodeToExecute(), entry.getClassName());
			result = executor.compile(tempFile.toURI());
			
			//handle compilation errors
			if(result.getStatusCode() == CompileResult.OK_STATUS) {			
				//cast test cases from object to expected types
				var paramTypes = getMethodParamTypes(entry.getArgTypes());
				var testCases = caseTestTypes(paramTypes, entry.getTestCases());			
				
				 ExecutionContext context = ExecutionContext.builder()
	        		.className(entry.getClassName())
	        		.entryMethodName(entry.getMethodNameToTest())
	        		.methodParameters(getMethodParamTypes(entry.getArgTypes()))
	        		.filePath(tempFile.getParentFile())
	        		.ttlInSeconds(100)
	        		.testCases(testCases)
	        		.build();
		 
				 JVMMetrics start = JVMMetricsMonitor.capture();
				 testResults = executor.executeCode(context);
				 delta = JVMMetricsMonitor.endCapture(start);
			}
		} catch (IOException | URISyntaxException e) {
			log.error(e.getMessage(), e);
		}
		
		String possibleErrors = null;
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

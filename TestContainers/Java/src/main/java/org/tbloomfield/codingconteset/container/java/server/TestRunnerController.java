package org.tbloomfield.codingconteset.container.java.server;

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
@RequestMapping("/codeRunner")
public class TestRunnerController {
	
	@Autowired private JavaExecutor executor;
	
	@PostMapping("/execute")
	public List<TestResult> executeTest(@RequestBody CodeEntry entry) throws ClassNotFoundException {
		List<TestResult> testResults = new ArrayList<>();
		
		try {
			File tempFile = FileHelper.writeRandomTempFileWithContents(entry.getCodeToExecute(), entry.getClassName());
			executor.compile(tempFile.toURI());
			
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
	 
			 testResults = executor.executeCode(context);
		} catch (IOException | URISyntaxException e) {
			log.error(e.getMessage(), e);
		}
		return testResults;
	}
	
	/**
	 * Converts from method parameter string types to actual object classes.
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

package org.tbloomfield.codingcontest.container.java.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.tbloomfield.codingcontest.container.java.bo.CodeEntry;
import org.tbloomfield.codingcontest.container.java.bo.ExecutionResult;
import org.tbloomfield.codingcontest.container.java.executor.TestCase;
import org.tbloomfield.codingcontest.container.java.service.dto.CodeEntryDto;
import org.tbloomfield.codingcontest.container.java.service.dto.ExecutionResultDto;
import org.tbloomfield.codingcontest.container.java.service.dto.TestCaseDto;

import lombok.NonNull;

public class DtoHelper {
    /**
     * Removes directory information from the error for security purposes:
     * 
     * @param originalError
     * @return
     */
    public static String scrubError(String className, String originalError) {
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
    public static List<Class> toClassTypes(String[] entryTestCases) throws ClassNotFoundException {
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
    @SuppressWarnings({ "rawtypes" })
    public static List<TestCase> toTestCase(@NonNull List<Class> paramTypes, List<TestCaseDto> entryTestCases) throws ClassNotFoundException {
      List<TestCase> filteredResults = new ArrayList<>(); 
      if(entryTestCases == null) { 
          return filteredResults;
      }
      
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
    
    /**
     * Converts to internal DTO for code entries.
     * 
     * @return
     * @throws ClassNotFoundException 
     */
    public static CodeEntry toCodeEntry(@NonNull CodeEntryDto codeEntryDto) throws ClassNotFoundException{
      var paramTypes = toClassTypes(codeEntryDto.getArgTypes());
      var testCases = toTestCase(paramTypes, codeEntryDto.getTestCases());        

      return CodeEntry.builder() 
              .argTypes(!paramTypes.isEmpty() ? Optional.of(paramTypes) : Optional.empty())
              .className(codeEntryDto.getClassName())
              .testCases(!testCases.isEmpty() ? Optional.of(testCases) : Optional.empty())
              .codeToExecute(codeEntryDto.getCodeToExecute())
              .methodNameToTest(codeEntryDto.getMethodNameToTest())
              .build();
    }
    
    /**
     * Converts from internal ExecutionResult to external DTO
     *  
     */
    public static ExecutionResultDto fromExecutionResult(@NonNull ExecutionResult executionResult) {
      return ExecutionResultDto.builder()
              .performanceInfo(executionResult.getPerformanceInfo())
              .testResults(executionResult.getTestResults())
              .errors(executionResult.getErrors())
              .build();
    }
}

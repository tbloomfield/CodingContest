package org.tbloomfield.codingcontest.container.java.service.dto;

import org.tbloomfield.codingcontest.container.java.bo.ExecutionResult;

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

package org.tbloomfield.codingcontest.container.java.bo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tbloomfield.codingcontest.container.java.executor.CompileResult;
import org.tbloomfield.codingcontest.container.java.executor.ExecutionContext;
import org.tbloomfield.codingcontest.container.java.executor.JavaExecutor;
import org.tbloomfield.codingcontest.container.java.executor.LocalFileHelper;
import org.tbloomfield.codingcontest.container.java.server.metrics.JVMMetricDelta;
import org.tbloomfield.codingcontest.container.java.server.metrics.JVMMetrics;
import org.tbloomfield.codingcontest.container.java.server.metrics.JVMMetricsMonitor;
import org.tbloomfield.codingcontest.container.java.service.DtoHelper;
import org.tbloomfield.codingcontest.container.java.service.TestResult;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExecutorBoImpl implements ExecutorBo {
    
    @Autowired private JavaExecutor executor;
    private final int MAX_RUNTIME_TTL_SECONDS = 5; 

    @Override
    public ExecutionResult executeArgumentBasedTest(CodeEntry entry) {      
      CompileResult result = null;
      ExecutionResult executionResult = new ExecutionResult();

      File tempFile = writeContentsToTempDirectory(entry.getCodeToExecute(), entry.getClassName());
      result = compileEntry(tempFile);
      
      //handle compilation errors
      if(result.getStatusCode() == CompileResult.OK_STATUS) {
        ExecutionContext context = ExecutionContext.builder()
              .entryMethodName(entry.getMethodNameToTest())             
              .file(tempFile)
              .ttlInSeconds(MAX_RUNTIME_TTL_SECONDS)
              .methodParameters(entry.getArgTypes())
              .testCases(entry.getTestCases())
              .build();
        executionResult = executeTest(context);
      } else {          
        executionResult.setErrors(DtoHelper.scrubError(entry.getClassName(), result.getCompilationOutput()));
      }
      return executionResult;
    }

    @Override
    public ExecutionResult executeFileBasedTest(CodeEntry entry) {        
      CompileResult result = null;
      ExecutionResult executionResult = new ExecutionResult();
        
      File tempFile = writeContentsToTempDirectory(entry.getCodeToExecute(), entry.getClassName());
      result = compileEntry(tempFile);
      if(result.getStatusCode() == CompileResult.OK_STATUS) {
          copySupportingTestFiles(tempFile);            
          ExecutionContext context = ExecutionContext.builder()
                .entryMethodName(entry.getMethodNameToTest())
                .methodParameters(entry.getArgTypes())
                .file(tempFile)
                .ttlInSeconds(MAX_RUNTIME_TTL_SECONDS)
                .build();
          executionResult = executeTest(context);
      } else {
        executionResult.setErrors(DtoHelper.scrubError(entry.getClassName(), result.getCompilationOutput()));
      }
      return executionResult;
    }
    
    
    private File writeContentsToTempDirectory(String content, String filename) {
      File tempFile; 
      try {
        tempFile = LocalFileHelper.writeRandomTempFileWithContents(content, filename);
      } catch (IOException e) {
        log.error(e.getMessage(), e);
        throw new RuntimeException(e);
      }
      return tempFile;
    }
    
    private void copySupportingTestFiles(File testFileLocation) { 
      try {
        LocalFileHelper.copySupportingTestFiles(testFileLocation, LocalFileHelper.JAVA_EXTENSION);
      } catch (IOException | URISyntaxException e) {
        log.error(e.getMessage(), e);
        throw new RuntimeException(e);
      }
    }
    
    private CompileResult compileEntry(File tempFile) {
      CompileResult result;
      try {
          result = executor.compile(tempFile.toURI());
      } catch (IOException | URISyntaxException e) {
          throw new RuntimeException(e);
      }
      return result;
    }
      
      private ExecutionResult executeTest(ExecutionContext context) {
        List<TestResult> testResults = new ArrayList<>();
        JVMMetricDelta delta = null;

        JVMMetrics start = JVMMetricsMonitor.capture();
        testResults = executor.executeCode(context);
        delta = JVMMetricsMonitor.endCapture(start);
        
        return ExecutionResult.builder()
                .performanceInfo(delta)
                .testResults(testResults)
                .build();
      }
}
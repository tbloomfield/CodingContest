package org.tbloomfield.codingcontest.container.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tbloomfield.codingcontest.container.dto.CodeEntryDto;
import org.tbloomfield.codingcontest.container.dto.CodeEntryMethodDto;
import org.tbloomfield.codingcontest.container.dto.CodeEntryWithTestDto;
import org.tbloomfield.codingcontest.container.dto.CodeEntryWithTestFileDto;
import org.tbloomfield.codingcontest.container.dto.TestCaseDto;
import org.tbloomfield.codingcontest.container.java.executor.LocalFileHelper;

public class TestCaseEntryBuilder {
    
    private List<String> testCode;
    private List<TestCaseDto> testCases;
    private CodeEntryDto codeEntry;
    
    private TestCaseEntryBuilder() {}
    
    public static TestCaseEntryBuilder builder() { 
        return new TestCaseEntryBuilder();
    }
    
    public CodeEntryWithTestFileDto testedByFile() { 
        return new CodeEntryWithTestFileDto(codeEntry, testCode);
    }
    
    public CodeEntryWithTestDto testedByCases() {
        return new CodeEntryWithTestDto(codeEntry, testCases);
    }
    
    public TestCaseEntryBuilder withTestCase(String id, String[] parameters, Object expectedResults) {      
      if(testCases == null) { 
          testCases = new ArrayList<>();
      }
      //build sample tests to run
      TestCaseDto dto = TestCaseDto.builder()
          .testCaseId(id)
          .arguments(parameters)
          .expectedResult(expectedResults)
          .build();
      testCases.add(dto);
      return this;
    }
    
    public TestCaseEntryBuilder withTestCaseFromFile(String testFileName) throws IOException {
      if(testCode == null) { 
          testCode = new ArrayList<>();
      }
      String code = TestHelper.findAndReturnTestContent(testFileName, LocalFileHelper.JAVA_EXTENSION);
      testCode.add(code);
      return this;
    }
    
    public TestCaseEntryBuilder withCode(String code, String className) { 
      if(codeEntry == null) { 
          codeEntry = new CodeEntryDto();
      }
      codeEntry.setClassName(className);
      codeEntry.setCodeToExecute(code);        
      return this;
    }
    
    public TestCaseEntryBuilder withCodeFromFile(String fileName, String className) throws IOException { 
      if(codeEntry == null) { 
          codeEntry = new CodeEntryDto();
      }
      String code = TestHelper.findAndReturnSubmissionContents(fileName, LocalFileHelper.JAVA_EXTENSION);
      codeEntry.setClassName(className);
      codeEntry.setCodeToExecute(code);        
      return this;
    }
    
    public TestCaseEntryBuilder withExecutionMethod(String methodName, List<String> argumentTypes) { 
      if(codeEntry == null) { 
          codeEntry = new CodeEntryDto();
      }
      
      CodeEntryMethodDto method = CodeEntryMethodDto.builder()
              .methodArgTypes(argumentTypes)
              .methodNameToTest(methodName)
              .build();
      codeEntry.setMethod(method);
      return this;
    }
}

package org.tbloomfield.codingcontest.container.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.tbloomfield.codingcontest.container.bo.CodeEntry;
import org.tbloomfield.codingcontest.container.bo.TestCase;

import lombok.NonNull;

public class DtoHelper {
    
    /**
     * Converts from method parameter string types to class types; required for proper reflection of 
     * code submitted.
     * 
     * @param entryTestCases
     * @return A list of class method parameter types, in order.
     * @throws ClassNotFoundException if String type was not a valid class type.
     */
    public static List<Class> toClassTypes(List<String> entryTestCases) throws ClassNotFoundException {
      if(entryTestCases == null) { 
        return List.of();
      }
      List<Class> paramTypes = new ArrayList<>(entryTestCases.size());
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
    public static CodeEntry toCodeEntry(@NonNull CodeEntryWithTestDto codeEntryDto) throws ClassNotFoundException{
      CodeEntryMethodDto method = codeEntryDto.getCodeEntry().getMethod();
      var paramTypes = toClassTypes(method.getMethodArgTypes());
      var testCases = toTestCase(paramTypes, codeEntryDto.getTestCases());        

      return CodeEntry.builder()
              .className(codeEntryDto.getCodeEntry().getClassName())
              .codeToExecute(codeEntryDto.getCodeEntry().getCodeToExecute())
              .testCases(!testCases.isEmpty() ? Optional.of(testCases) : Optional.empty())
              .methodNameToTest(method.getMethodNameToTest())
              .argTypes(!paramTypes.isEmpty() ? Optional.of(paramTypes) : Optional.empty()) //support for zero-arg methods              
              .build();
    }
    
    /**
     * Converts to internal DTO for code entries.
     * 
     * @return
     * @throws ClassNotFoundException 
     */
    public static CodeEntry toCodeEntry(@NonNull CodeEntryWithTestFileDto codeEntryDto) throws ClassNotFoundException{
      return CodeEntry.builder()
              .className(codeEntryDto.getCodeEntry().getClassName())
              .codeToExecute(codeEntryDto.getCodeEntry().getCodeToExecute())
              .build();
    }
}

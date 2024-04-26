package org.tbloomfield.codingcontest.container.bo;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeEntry {
  private String codeToExecute;
  private String className;
  private String methodNameToTest;
  
  private Optional<List<TestCase>> testCases;
  private Optional<List<Class>> argTypes;
}
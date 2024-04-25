package org.tbloomfield.codingcontest.container.java.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CodeEntryDto {
	private String codeToExecute;
	private String className;
	private String methodNameToTest;
	
	 private List<TestCaseDto> testCases;
   private String[] argTypes;
}
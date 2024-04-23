package org.tbloomfield.codingconteset.container.java.server;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CodeEntry {
	private List<TestCaseDto> testCases;
	private String codeToExecute;
	private String className;
	private String methodNameToTest;
	private String[] argTypes;
}
package org.tbloomfield.codingconteset.container.java.server;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestResult {
	private String testCaseId;
	private Object result;
}
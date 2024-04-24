package org.tbloomfield.codingcontest.container.java.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestResult {
	private String testCaseId;
	private boolean passing;
	private Object expected;
	private Object result;
}
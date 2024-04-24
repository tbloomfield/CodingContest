package org.tbloomfield.codingcontest.container.java.executor;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCase {
	private String testCaseId;
	private List<?> arguments;
	private Object expectedResult;
}

package org.tbloomfield.codingconteset.container.java.executor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holder for test data to run against a class. 
 * @param <T>
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TestCase<T> {
	private String testCaseId;
	private T arguments;
}
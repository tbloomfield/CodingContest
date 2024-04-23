package org.tbloomfield.codingconteset.container.java.server;

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
public class TestCaseDto {
	private String testCaseId;
	private String[] arguments;
}
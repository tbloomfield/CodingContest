package org.tbloomfield.codingconteset.container.java.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holder for test data to run against a class. 
 * @param <T>
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TestCaseDto {
	private String testCaseId;
	private String[] arguments;
}
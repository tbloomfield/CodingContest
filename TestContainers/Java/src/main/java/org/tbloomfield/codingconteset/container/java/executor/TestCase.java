package org.tbloomfield.codingconteset.container.java.executor;

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
}

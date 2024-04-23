package org.tbloomfield.codingconteset.container.java.executor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompileResult {
	public final static int OK_STATUS = 0;
	private int statusCode;
	private String compilationOutput;
}

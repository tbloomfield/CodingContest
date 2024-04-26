package org.tbloomfield.codingcontest.container.dto;

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
	private CodeEntryMethodDto method;
}
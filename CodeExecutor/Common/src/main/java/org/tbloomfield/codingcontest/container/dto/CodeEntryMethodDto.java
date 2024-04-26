package org.tbloomfield.codingcontest.container.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Information about the method to invoke tests on in supplied code.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeEntryMethodDto {
    private String methodNameToTest;
    private List<String> methodArgTypes;
}

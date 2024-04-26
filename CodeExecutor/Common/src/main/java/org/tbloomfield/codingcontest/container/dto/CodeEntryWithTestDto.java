package org.tbloomfield.codingcontest.container.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeEntryWithTestDto {
    private CodeEntryDto codeEntry;    
    private List<TestCaseDto> testCases;
}
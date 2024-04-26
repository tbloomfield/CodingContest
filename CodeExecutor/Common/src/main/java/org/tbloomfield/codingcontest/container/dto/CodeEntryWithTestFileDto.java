package org.tbloomfield.codingcontest.container.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeEntryWithTestFileDto {
  private CodeEntryDto codeEntry;
  private List<String> testFiles;
}

package org.tbloomfield.codingcontest.container.java;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

public class TestHelper {    
  public static String findAndReturnSubmissionContents(String filePrefix, String extension) throws IOException {
    File file = ResourceUtils.getFile(String.format("classpath:submissions/%s.%s",filePrefix, extension));
    String fileContents = FileUtils.readFileToString(file, Charset.defaultCharset());
    return fileContents;
  }
  
  public static String findAndReturnTestContent(String filePrefix, String extension) throws IOException {
    File file = ResourceUtils.getFile(String.format("classpath:testcases/%s_Tests.%s",filePrefix, extension));
    String fileContents = FileUtils.readFileToString(file, Charset.defaultCharset());
    return fileContents;
  }
}

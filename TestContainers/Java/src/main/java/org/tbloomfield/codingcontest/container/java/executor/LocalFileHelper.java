package org.tbloomfield.codingcontest.container.java.executor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;
import org.tbloomfield.codingcontest.container.java.executor.test.FileTestCase;
import org.tbloomfield.codingcontest.container.java.service.TestResult;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class LocalFileHelper {
    
  public static final String JAVA_EXTENSION = "java";
      
	public static File writeRandomTempFileWithContents(String contents, String classname) throws IOException {		
		//create a random nanoId backed directory to enforce uniqueness between runs		
		String randomDir = NanoIdUtils.randomNanoId();
		File tempDir = FileUtils.getTempDirectory();
		File tempFile = new File(String.format("%s/%s/%s.java", tempDir.getPath(), randomDir, classname));
		FileUtils.writeByteArrayToFile(tempFile, contents.getBytes());		
		return tempFile;
	}
	
	/**
	 * Copies solution and test case files alongside a file submitted for execution.
	 * @param supportingFile
	 * @return
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public static List<File> copySupportingTestFiles(File submittedFile, String extension) throws IOException, URISyntaxException {
	  String fileName = getNameWithoutExtension(submittedFile);
	  File submittedDirectory = submittedFile.getParentFile();	  
    File testFile = ResourceUtils.getFile(String.format("classpath:testcases/%s_Tests.%s", fileName, extension));
    FileUtils.copyFileToDirectory(testFile, submittedDirectory);
    
    //supporting files:
    File testCaseFile = new File(FileTestCase.class.getResource("FileTestCase.class").toURI());
    File testResultFile = new File(TestResult.class.getResource("TestResult.class").toURI());
    
    //moving these files requires preservation of package structure.
    File testCaseFilePath = new File(submittedDirectory.getPath() + "/" + getPackagePath(testCaseFile));
    File testResultFilePath = new File(submittedDirectory.getPath() + "/" + getPackagePath(testResultFile));    
    FileUtils.copyFileToDirectory(testCaseFile, testCaseFilePath);
    FileUtils.copyFileToDirectory(testResultFile, testResultFilePath);
    
    //return the test file location to execute
    File movedFile = FileUtils.getFile(submittedDirectory, testFile.getName());    
    return List.of(movedFile);
	}
	
	public static String getNameWithoutExtension(File file) {
    int dotIndex = file.getName().lastIndexOf('.');
    return (dotIndex == -1) ? file.getName() : file.getName().substring(0, dotIndex);
	}
	
	public static String getPackagePath(File file) { 
	    int startIndex = file.getPath().indexOf("org");
	    int endIndex = file.getPath().lastIndexOf("\\");
	    String trimmedDirectory = file.getPath().substring(startIndex, endIndex);
	    return trimmedDirectory;
	}
	
}

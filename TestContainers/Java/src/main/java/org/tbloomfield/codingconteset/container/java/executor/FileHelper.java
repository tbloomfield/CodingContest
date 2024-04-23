package org.tbloomfield.codingconteset.container.java.executor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public class FileHelper {
	
	public static File writeRandomTempFileWithContents(String contents, String classname) throws IOException {		
		//create a random nanoId backed directory to enforce uniqueness between runs		
		String randomDir = NanoIdUtils.randomNanoId();
		File tempDir = FileUtils.getTempDirectory();
		File tempFile = new File(String.format("%s/%s/%s.java", tempDir.getPath(), randomDir, classname));
		FileUtils.writeByteArrayToFile(tempFile, contents.getBytes());		
		return tempFile;
	}
	
	public static String findAndReturnFileContents(String filename) throws IOException {
        File file = ResourceUtils.getFile("classpath:submissions/" + filename);
        String fileContents = FileUtils.readFileToString(file, Charset.defaultCharset());
        return fileContents;
	}
}

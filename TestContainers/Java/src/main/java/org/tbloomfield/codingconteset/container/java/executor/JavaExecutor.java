package org.tbloomfield.codingconteset.container.java.executor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ReflectionUtils;
import org.tbloomfield.codingconteset.container.java.server.TestResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaExecutor {
	/**
	 * Compiles the specified resource, placing the results into the OS temporary
	 * directory.
	 * 
	 * @throws URISyntaxException if file to compile can't be loaded
	 * @throws IOException        if compile process doesn't succeed.
	 */
	public void compile(URI resource) throws IOException, URISyntaxException {
		// write text to the specified filename
		File tempDir = FileUtils.getTempDirectory();
		File tempResource = new File(resource);
		String location = String.format("%s", tempResource.getPath());

		ProcessBuilder pb = new ProcessBuilder("javac ", "-d", tempDir.getPath(), location);
		pb.redirectErrorStream(true);
		pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		Process process = pb.start();
		String result = new String(process.getInputStream().readAllBytes());
		log.info(result);
	}

	/**
	 * Invokes test method in a class, passing input arguments  
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TestResult> executeCode(ExecutionContext context) {
		List<TestResult> testResults = null;

		try {
			URL url = context.getFilePath().toURI().toURL();
			URL[] urls = new URL[] { url };
			ClassLoader cl = new URLClassLoader(urls);
			Class testClass = cl.loadClass(context.getClassName());

			Constructor constructor = ReflectionUtils.accessibleConstructor(testClass);
			if (constructor == null) {
				log.error("no constructor found");
			}

			Method entryMethod;
			
			
			if(context.getMethodParameters().isEmpty()) { 
				entryMethod = ReflectionUtils.findMethod(testClass, context.getEntryMethodName());
			} else { 
				Class[] argTypes = context.getMethodParameters().toArray(new Class[context.getMethodParameters().size()]);
				entryMethod = ReflectionUtils.findMethod(testClass, context.getEntryMethodName(), argTypes);
			}
			if (entryMethod == null) {
				log.error("no method found");
			}

			Object testObject = constructor.newInstance();

			// execute method in a thread with a fixed duration of time
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<List<TestResult>> future = executor.submit(() -> {
				List<TestResult> executionResults = new ArrayList<>(context.getTestCases().size());
				for (TestCase<?> t : context.getTestCases()) {
					Object result;
					if(t.getArguments() instanceof Void || t.getArguments() ==null) { 
						result = ReflectionUtils.invokeMethod(entryMethod, testObject);
					} else { 
						result = ReflectionUtils.invokeMethod(entryMethod, testObject, t.getArguments());
					}
					executionResults.add(new TestResult(t.getTestCaseId(), result));
				}

				return executionResults;
			});

			try {
				testResults = future.get(context.getTtlInSeconds(), TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				future.cancel(true);
			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				executor.shutdownNow();
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | MalformedURLException | ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}

		return testResults;
	}

}
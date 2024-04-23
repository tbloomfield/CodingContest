package org.tbloomfield.codingconteset.container.java.executor;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.tbloomfield.codingconteset.container.java.service.TestResult;

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
	public CompileResult compile(URI resource) throws IOException, URISyntaxException {
		// write text to the specified filename
		File javaFile = new File(resource);

		ProcessBuilder pb = new ProcessBuilder("javac ", "-d", javaFile.getParent(), javaFile.getPath());
		pb.redirectErrorStream(true);
		pb.inheritIO();
		pb.redirectError(Redirect.PIPE);
		pb.redirectInput(Redirect.PIPE);
		pb.redirectOutput(Redirect.PIPE);
		
		Process process = pb.start();
		try {
			//block until process returns, or throw exception to prevent endless block.
			//TODO - make this configurable
			process.waitFor(3, TimeUnit.SECONDS);
			process.onExit().get();
		} catch (InterruptedException | ExecutionException e) {
			log.error(e.getMessage(), e);
		}
		
		int procVal = process.exitValue();
		String result = new String(process.getInputStream().readAllBytes());
		return CompileResult.builder()
				.statusCode(procVal)
				.compilationOutput(result)
				.build();
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
			
			if(CollectionUtils.isEmpty(context.getMethodParameters())) { 
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
				for (TestCase t : context.getTestCases()) {
					Object result;					
					if(CollectionUtils.isEmpty(t.getArguments())) { 
						result = ReflectionUtils.invokeMethod(entryMethod, testObject);
					} else {
						Object[] args = t.getArguments().toArray();  
						result = ReflectionUtils.invokeMethod(entryMethod, testObject, args);
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
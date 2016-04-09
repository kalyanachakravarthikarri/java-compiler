package com.neophoenix.compiler.impl;

import java.io.File;
import java.util.Collections;
import java.util.logging.Logger;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

import com.neophoenix.compiler.Compiler;
import com.neophoenix.compiler.CompilerOutput;

/**
 * Class to compile a maven project.
 * test phase is executed
 * @author Kalyan
 *
 */
public class MavenProjectCompiler implements Compiler {
	
	private static final String TEST_PHASE = "test";
	final static Logger logger = Logger.getLogger(MavenProjectCompiler.class.getName());

	@Override
	public CompilerOutput compile(String... files) {
		logger.entering(MavenProjectCompiler.class.getName(), "compile", files);
		CompilerOutput response = new CompilerOutput();
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(new File(files[0]));
		request.setGoals(Collections.singletonList(TEST_PHASE));

		Invoker invoker = new DefaultInvoker();
		InvocationResult result = null;
		try {
			result = invoker.execute(request);
		} catch (MavenInvocationException e) {
			logger.severe(e.getMessage());
		}
		if (result.getExitCode() != 0) {
			response.setError(true);
			response.setDescription(result.getExecutionException().getMessage());
		}
		logger.exiting(MavenProjectCompiler.class.getName(), "compile", files);
		return response;
	}

}

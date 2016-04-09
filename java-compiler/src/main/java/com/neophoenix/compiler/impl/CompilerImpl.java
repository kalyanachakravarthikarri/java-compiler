package com.neophoenix.compiler.impl;

import java.util.logging.Logger;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.neophoenix.compiler.Compiler;
import com.neophoenix.compiler.CompilerOutput;

public abstract class CompilerImpl implements Compiler {
	
	final static Logger logger = Logger.getLogger(CompilerImpl.class.getName());

	@Override
	public CompilerOutput compile(String... filesToBeCompiled) {
		logger.entering(CompilerImpl.class.getName(), "compile", filesToBeCompiled);
		final CompilerOutput output = new CompilerOutput();
		/*
		 * the compiler will send its messages to this listener
		 */
		DiagnosticListener listener = new DiagnosticListener() {

			StringBuffer sBuf = new StringBuffer();
			
			public void report(Diagnostic diagnostic) {
				sBuf.append(diagnostic.getSource());
				sBuf.append(" line-number: " + diagnostic.getLineNumber());
				sBuf.append(" error: " + diagnostic.getMessage(null));
				sBuf.append("\n");
				output.setError(true);
				output.setDescription(sBuf.toString());
				logger.info("Got an error during compilation "+ sBuf.toString());
			}
		};

		// getting the compiler object
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager manager = compiler.getStandardFileManager(null,
				null, null);
		Iterable<? extends JavaFileObject> files = manager
				.getJavaFileObjects(filesToBeCompiled);
		JavaCompiler.CompilationTask task = compiler.getTask(null, manager,
				listener, null, null, files);

		// the compilation occures here
		task.call();
		logger.exiting(CompilerImpl.class.getName(), "compile", filesToBeCompiled);
		return output;
	}

}

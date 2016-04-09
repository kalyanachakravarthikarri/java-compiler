package com.neophoenix.compiler.impl;

import java.util.logging.Logger;

import com.neophoenix.compiler.CompilerOutput;

/**
 * Class to compile given list of java files
 * @author Kalyan
 *
 */
public class JavaFileCompiler extends CompilerImpl{

	final static Logger logger = Logger.getLogger(JavaFileCompiler.class.getName());
	
	@Override
	public CompilerOutput compile(String... filesToBeCompiled) {
		logger.entering(CompilerImpl.class.getName(), "compile", filesToBeCompiled);
		return super.compile(filesToBeCompiled);
	}

}

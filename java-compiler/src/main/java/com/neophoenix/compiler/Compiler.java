package com.neophoenix.compiler;


/**
 * Compiler interface to abstract out the compilation process
 * from the type of file
 * Sample subtypes are 
 * 	{@link com.neophoenix.compiler.impl.CompilerImpl}
 *  {@link com.neophoenix.compiler.impl.JavaFileCompiler}
 *  {@link com.neophoenix.compiler.impl.MavenProjectCompiler}
 * @author Kalyan
 *
 */
public interface Compiler {
	
	/**
	 * Compile the given files and return instance of {@link CompilerOutput}
	 * @param filesToBeCompiled - list of files to compile
	 * @return - instance of CompilerOutput
	 */
	public CompilerOutput compile(String... filesToBeCompiled);

}

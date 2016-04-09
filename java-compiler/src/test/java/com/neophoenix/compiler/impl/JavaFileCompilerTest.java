package com.neophoenix.compiler.impl;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.neophoenix.compiler.CompilerOutput;

public class JavaFileCompilerTest {

	/**
	 * Success case of java file compiler
	 */
	@Test
	public void testCompileSuccess() {
		File javaFileToCompile = new File("src/test/resources/HelloWorld.java");
		JavaFileCompiler javaFileCompiler = new JavaFileCompiler();
		String[] filesToCompile = {javaFileToCompile.getAbsolutePath()};
		CompilerOutput output = javaFileCompiler.compile(filesToCompile);
		assertFalse(output.isError());
		File generatedClassFile = new File("src/test/resources/HelloWorld.class");
		generatedClassFile.delete();
	}
	
	/**
	 * Test multiple java files
	 */
	@Test
	public void testCompileWithMultipleJavaFiles() {
		File javaFileToCompile = new File("src/test/resources/HelloWorld.java");
		JavaFileCompiler javaFileCompiler = new JavaFileCompiler();
		String[] filesToCompile = {javaFileToCompile.getAbsolutePath(), javaFileToCompile.getAbsolutePath()};
		CompilerOutput output = javaFileCompiler.compile(filesToCompile);
		assertFalse(output.isError());
		File generatedClassFile = new File("src/test/resources/HelloWorld.class");
		generatedClassFile.delete();
	}
	
	/**
	 * Failure case of java file compiler
	 */
	@Test
	public void testCompileFailure() {
		File javaFileToCompile = new File("src/test/resources/BadJavaFile.java");
		JavaFileCompiler javaFileCompiler = new JavaFileCompiler();
		String[] filesToCompile = {javaFileToCompile.getAbsolutePath()};
		CompilerOutput output = javaFileCompiler.compile(filesToCompile);
		assertTrue(output.isError());
		File generatedClassFile = new File("src/test/resources/BadJavaFile.class");
		generatedClassFile.delete();
	}
}

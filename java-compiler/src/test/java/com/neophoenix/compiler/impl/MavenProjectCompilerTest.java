package com.neophoenix.compiler.impl;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Test;

import com.neophoenix.compiler.CompilerOutput;

public class MavenProjectCompilerTest {

	/**
	 * Test maven project
	 */
	public void testCompileSuccess() {
		String pomXml = "src/test/resources/SpringExample/pom.xml";
		MavenProjectCompiler mavenProjectCompiler = new MavenProjectCompiler();
		String[] filesToCompile = {pomXml};
		CompilerOutput output = mavenProjectCompiler.compile(filesToCompile);
		assertFalse(output.isError());
		File generatedTargetFile = new File("src/test/resources/SpringExample/target");
		generatedTargetFile.delete();
	}
}

package com.neophoenix.compiler;

/**
 * POJO to hold compiler response
 * 
 * @author Kalyan
 * 
 */
public class CompilerOutput {

	// Flag to determine if there has been any error in compilation
	private boolean error = false;
	// Error description
	private String description = "Congratulations, your file has been compiled successfully!!!";

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

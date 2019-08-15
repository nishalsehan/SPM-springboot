package com.af.project.model;

public class Method {
	String name;
	int startLine;
	int endLine;
	
	
	public Method() {
		super();
	}

	public Method(String name, int startLine, int endLine) {
		super();
		this.name = name;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getStartLine() {
		return startLine;
	}


	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}


	public int getEndLine() {
		return endLine;
	}


	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}


	
	

}

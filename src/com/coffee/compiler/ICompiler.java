package com.coffee.compiler;

import javax.json.JsonObject;

public interface ICompiler {
	public JsonObject getOneSolution() throws Exception;
	public JsonObject getNSolutions(int n) throws Exception;
	public  void setUpCompilation(CompilationParameters params) throws Exception ;


}

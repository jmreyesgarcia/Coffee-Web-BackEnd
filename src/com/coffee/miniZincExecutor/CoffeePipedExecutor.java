package com.coffee.miniZincExecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.siemens.ct.jmz.ModelBuilder;
import at.siemens.ct.jmz.executor.Executor;
import at.siemens.ct.jmz.executor.FlatZincSolver;
import at.siemens.ct.jmz.executor.FlatZincSolverExecutable;
import at.siemens.ct.jmz.executor.MznToFznExecutable;
import at.siemens.ct.jmz.executor.PipedMiniZincExecutor;
import at.siemens.ct.jmz.writer.IModelWriter;
import at.siemens.ct.jmz.writer.ModelWriter;

public class CoffeePipedExecutor extends Executor{
	String FZN_EXT=".fzn";
	public static final String MZN_EXT = ".mzn";

	private FlatZincSolver solver = FlatZincSolver.GECODE;

	public List<String> params;
	File mznFile;
	File fznFile;
	ModelWriter emptyModel;

	public CoffeePipedExecutor() {
		// I created an empty model to call the constructor 
		super("CoffePipedExectutor");
	}
	
	public CoffeePipedExecutor(String compilationPath, String mznName)/*, List<String> params)*/ {
		this();
		//this.params= params;
		mznFile= new File(compilationPath + mznName +  MZN_EXT);
		fznFile= new File (compilationPath + mznName + FZN_EXT);
		ModelBuilder emptyBuilder= new ModelBuilder();
		emptyBuilder.add(new ArrayList<>());
		emptyModel= new ModelWriter(emptyBuilder);
	}
	public void startProcess(String... additionalOptions) throws Exception{
		
		startProcess(emptyModel, additionalOptions);
	}
	  @Override
	  public void startProcess(IModelWriter modelWriter, Long timeoutMs, String... additionalOptions) throws IOException {
	    //super.startProcess(modelWriter, timeoutMs, additionalOptions);
	
	    startProcess(new MznToFznExecutable(mznFile, fznFile, solver), modelWriter, timeoutMs,
	        additionalOptions);
	  }
	  
	  @Override
	  public long waitForSolution() throws InterruptedException, IOException {
	    // wait for compiler:
		 
		  
	    long elapsedTime = super.waitForSolution();
	    

	    printLastSolverErrors();

	    if (getLastExitCode() == EXIT_CODE_SUCCESS) {
	      // execute and wait for solver:
	      startProcess(new FlatZincSolverExecutable(fznFile, solver), emptyModel, remainingTime());
	      elapsedTime += super.waitForSolution();
	    }

	    return elapsedTime;
	  }
	

	
	

}

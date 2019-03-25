package com.coffee.miniZincExecutor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.coffee.compiler.Solver;

import at.siemens.ct.jmz.ModelBuilder;
import at.siemens.ct.jmz.executor.Executor;
import at.siemens.ct.jmz.writer.IModelWriter;
import at.siemens.ct.jmz.writer.ModelWriter;


public class CoffeeMiniZincExecutor extends  Executor{
	public Solver solver;
	public String mznPath; 
	public String modelName;

	ModelWriter emptyModel;
	public List<String> params;
	
	public CoffeeMiniZincExecutor() {
		// I created an empty model to call the constructor 
		super("CoffeExectutor");
	}

//	 TODO this is the constructor that I'm using
//	public CoffeeMiniZincExecutor( List<String> params) {
//		this();
//		this.params= params;
//	}

	public CoffeeMiniZincExecutor(String compilationPath, String mznName, Solver solver) {
		this();
		this.mznPath = compilationPath;
		this.modelName = mznName;
		
		//this.dznPath = dznPath;
		this.solver= solver;
		
		ModelBuilder emptyBuilder= new ModelBuilder();
		emptyBuilder.add(new ArrayList<>());
		emptyModel= new ModelWriter(emptyBuilder);
	}

	public CoffeeMiniZincExecutor(String identifier) {
		super(identifier);

	}
	public void startProcess(String... additionalOptions) throws Exception{
		startProcess(emptyModel, additionalOptions);
	}

	

	
	@Override
	 public void startProcess(IModelWriter modelWriter, Long timeoutMs, String... additionalOptions) throws IOException {
		//File mznFile = new File(this.mznPath);
		//MiniZincExecutable minizincExecutable= new MiniZincExecutable(mznFile);
		CoffeeExecutable coffee= new CoffeeExecutable(solver, mznPath, modelName);
		startProcess(coffee,
				modelWriter,  
				timeoutMs,
			    additionalOptions);
	}

	

}

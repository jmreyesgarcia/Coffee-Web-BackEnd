package com.coffee.miniZincExecutor;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.coffee.compiler.Solver;

import at.siemens.ct.jmz.executor.Executable;

public class CoffeeExecutable implements Executable {
	private static final String MINIZINC_CMD = "minizinc" ;
	private static final String MZN_EXT=".mzn";
	
	Solver solver;
	String compilationPath;
	String mznName;
	File mznFile;
	

	
	public CoffeeExecutable(Solver solver, String compilationPath, String mznName ) {
		this.solver= solver;
		this.compilationPath = compilationPath;
		this.mznName=mznName;

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return MINIZINC_CMD;
	}

	@Override
	public List<String> getOptions(Long arg0, Collection<Path> arg1) {
		mznFile= new File(compilationPath + mznName +  MZN_EXT);
	    
		List<String> options = new ArrayList<>();
	    options.addAll(solver.getOptions());
	    options.add(mznFile.getAbsolutePath());

	    return options;
	}

}

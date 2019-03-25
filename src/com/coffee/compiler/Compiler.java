package com.coffee.compiler;


import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.coffee.coffeeParser.CoffeeMiniZincParser;
import com.coffee.miniZincExecutor.CoffeeMiniZincExecutor;
import com.coffee.miniZincExecutor.CoffeePipedExecutor;

import at.siemens.ct.jmz.executor.Executor;
import at.siemens.ct.jmz.executor.IExecutor;
import at.siemens.ct.jmz.parser.MiniZincOutputParser;




public class Compiler implements ICompiler{
	
	//===================================================================================================
	// Constants
		
	/**
	 *  path of the json file with the info of the solvers installed 
	 */
	
	
	private static final String SOLVER_PARAM = "--solver" ; // + file + --solver + solvercmd + params
	
	private static final String SPACE = " " ; // + file + --solver + solvercmd + params
	
	//===================================================================================================
	// Attributes
	/** 
	 * info about the queries and the reasoning required
	 */
	private JsonObject operationsInfo;
	
	

	/**
	 * Json object with the information about the instaled solvers
	 */
	private JsonObject solversInfo;
	
	/**
	 * Json object with the information from the frontEnd (configurations)
	 */
	private JsonObject frontEndParameters;

	/**
	 *  Solver used to perform the compilation
	 */
	private Solver solver;
	
	private String modelFileName;
	
	CompilationParameters compilationParameters;
	
	/**
	 * Executor 
	 */
	private CoffeeMiniZincExecutor executor;
	



	public void setUpCompilation(CompilationParameters params) throws Exception {

		//
		compilationParameters= params;
		//loading the json with the configuration of the solvers
		solversInfo= compilationParameters. getSolversJson();
		//Loading the json with the operations information
		operationsInfo= compilationParameters. getOperationsJson();
		// Loading the Json with the frontEnd parameters
		frontEndParameters= compilationParameters.getFrontEndJson();
		// obtaining the mzn name
		modelFileName= compilationParameters.getModelName() ;
		

		
		// determining which solver should be used
		solver=getSolver();
		
		//return params.getSource();
	}
	/**
	 * Method tha decides wich solver should be compiled and return the compilation command
	 * @return
	 */
	public Solver getSolver() throws Exception {
		Solver solver;
		
		//String problemType= operationsInfo.getString("problemType"); 
		// type of problem from the parameters object
		switch(compilationParameters.getProblemType()){
		case BOOL:
			solver= selectSolver(SolverType.SATSolver);
			break;
		default:
			solver= selectSolver(SolverType.CSPSolver);		
//		case "ATT":
//			break;
//		case "INST":
//			break;
		}
		
		return solver;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	private Solver selectSolver(SolverType type) throws Exception {
		Solver solver=null;
		String selected= frontEndParameters.getString("solverSelected");
		
		// Obtaining the list of solvers
		JsonArray solverList= solversInfo.getJsonArray(type.toString());
		if (solverList.isEmpty()) {
			
			// if there are no solvers with the type requested, then use a CSPSolver 
			solverList= solversInfo.getJsonArray(SolverType.CSPSolver.toString());
			
			// if it is again empty, then there are now solvers
			if (solverList.isEmpty()) {
				throw new Exception("Error when loading the solver, the solver configurations file is empty");
			}
		}
		
		// if no solver is selected
		if (selected.equals("")) {
			JsonObject sol=  solverList.getJsonObject(0);
			solver= new Solver(type, sol);

		}else {
			// FIXME it is important to take into account the right solver for the type of problem
			boolean found=false;
			for(int i=0; i< solverList.size() && !found; i++) {
				JsonObject sol=  solverList.getJsonObject(i);
				if(sol.getString("solverId").equals(selected)) {
					found=true;
					solver= new Solver(type,sol);
				}
			}
			if (found == false) {
				throw new Exception("There is not a solver registered with the identifer " + selected);
			}
		}
		return solver;
	}
	
	public JsonObject getOneSolution() throws Exception{
		///create the executor
	    executor= new CoffeeMiniZincExecutor(compilationParameters.getMznFilesPath(), modelFileName, solver);
	    
	    // use the time parameters
	    executor.startProcess("--output-time");
	    
	    //execute
	    long elapsedTime= executor.waitForSolution();
	    
	    //obtain the output
	    //System.out.println(executor.getLastSolverOutput());
	    return processOutput(executor, elapsedTime);
	    //return null;
	    

	}
	
	public JsonObject getNSolutions(int n) throws Exception{
		///create the executor
	    executor= new CoffeeMiniZincExecutor(compilationParameters.getMznFilesPath(), modelFileName, solver);
	    
	 // use the time parameters and number of solutions required
	    executor.startProcess("--output-time", "-n "+n);
	    
	    //execute
	    long elapsedTime= executor.waitForSolution();
	    
	    //System.out.println(executor.getLastSolverOutput());

	    //obtain the output	
	    return processOutput(executor, elapsedTime);
	}
	
	private JsonObject processOutput(IExecutor executor, long elapsedTime) {
	    JsonObjectBuilder builder= Json.createObjectBuilder();
	    CoffeeMiniZincParser parser = new CoffeeMiniZincParser(executor);
	    // setting the overall answer: COMPLETE, UNBOUNDED, UNSATISFIABLE, UNKNOWN, SATISFIABLE
	    // put the overall of the solving
	    String outputMsg = parser.getSolverMessage() ==null? "SATISFIABLE": parser.getSolverMessage().toString() ;
	    builder.add("exitCode", parser.getExitCode() );
		builder.add("state", outputMsg);
		builder.add("overAllTime", elapsedTime);
	    //put the solutions
	    builder.add("solutions", parser.getSolutions());
	    builder.add("numberOfSolutions", parser.getNumSolutions());
	    return builder.build();
	}
	

	
	public JsonObject getOperationsInfo() {
		return operationsInfo;
	}

	public JsonObject getSolversInfo() {
		return solversInfo;
	}

	public JsonObject getProblemsInfo() {
		return frontEndParameters;
	}



}

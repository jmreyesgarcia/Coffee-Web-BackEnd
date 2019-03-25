package com.coffee.coffeeParser;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.coffee.common.utils.JsonMng;

import at.siemens.ct.jmz.executor.IExecutor;
import at.siemens.ct.jmz.parser.MiniZincOutputParser;

public class CoffeeMiniZincParser extends MiniZincOutputParser{
	private static final String SEPARATOR="----------\n";
	//private JsonObjectBuilder builder ;
	private IExecutor executor;
	private int numSolutions;

	public CoffeeMiniZincParser(IExecutor executor) {
		super(executor);
		this.executor= executor;
	}
	

	
	public int getExitCode() {
		return executor.getLastExitCode();
	}
	

	public JsonArray getSolutions() {
		
		JsonArrayBuilder array = Json.createArrayBuilder();
		String solutionsFromExecutor= executor.getLastSolverOutput();
		if (solutionsFromExecutor.startsWith("% solving")) {
			solutionsFromExecutor = solutionsFromExecutor.substring(solutionsFromExecutor.indexOf(System.getProperty("line.separator"))+1);
			solutionsFromExecutor = solutionsFromExecutor.substring(solutionsFromExecutor.indexOf(System.getProperty("line.separator"))+1);
		}
		
		//System.out.println(solutionsFromExecutor);

				
		String[] solutions= solutionsFromExecutor.split(SEPARATOR);
		
		
		//obtaining the number of solutions
		numSolutions= solutions.length;
		
		for (int s = 0; s < solutions.length; s++) {
			array.add(processSolution(solutions[s]));
			//System.out.println("solucion " + s + "---");
			//System.out.println(solutions[s] + "--fin");
		}
		return array.build();
	}
	
	public JsonObject processSolution(String solution) {
		
		String[] parts= solution.split("\n% time elapsed: ");
		String ln = System.getProperty("line.separator");		
		//solution
		String solutionString= parts[0].replaceAll(";"+ln, ","+ln+"\"").replaceAll(" = ", "\" : ");		
		solutionString= "{\""+ solutionString.replace(";", "") + "}";
		
		//time
		System.out.println(solutionString);
		JsonObject object= JsonMng.getfromString(solutionString);
		JsonObjectBuilder builder= Json.createObjectBuilder(object);
		//builder.add(solutionString);
		builder.add("solverTime", parts[1].split(" ")[0]);
		//builder.add(object);
		
		return builder.build();
	}
	
	public int getNumSolutions() {
		return numSolutions;
	}
	
	
}

package com.coffee.compiler;

import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.junit.jupiter.api.Test;

import com.coffee.common.utils.JsonMng;

class CompilerTest {
	//private String SOLVERS_CONFIGURATION_PATH= "/Users/Angela/Coffee/compiler_path";
	//public static final String COMPILATION_PATH = "/Users/Angela/Coffee/compiler_path/";
	public static final String INPUT_FILES_PATH = "testfiles/InputFiles/";
	public static final String MZN_FILES_PATH = "testfiles/MZNFiles/";
	public static final String OUTPUT_FILES_PATH = "testfiles/OutputFiles/";


	public static final String MODEL_NAME = "MobilePhone";
	public static final String SOLVERS_CONFIGURATION_FILE = "CoffeeSolvers";
	public static final String FRONT_END_FILE = "frontEndData";

	public static final String JSON_EXT = ".json";
	public static final String MZN_EXT = ".mzn";
	CompilationParameters params;
	
	void createParams() throws FileNotFoundException {
		 params= new CompilationParameters(
				INPUT_FILES_PATH, 
				MZN_FILES_PATH, 
				OUTPUT_FILES_PATH,
				MODEL_NAME,
				SOLVERS_CONFIGURATION_FILE,
				FRONT_END_FILE,
				SourceOfCompilation.FILE
				);
	}


	@Test
	void testLoadSolverConfiguration() {
		//loading the json with the configuration of the solvers
		try {
			System.out.println(INPUT_FILES_PATH);
			JsonObject solversInfo= JsonMng.getfromFile(INPUT_FILES_PATH+ SOLVERS_CONFIGURATION_FILE + JSON_EXT);

			SolverType type= SolverType.CSPSolver;
			JsonArray solverList= solversInfo.getJsonArray(type.toString());


			//JsonArray solverList= solversInfo.getJsonArray("CSPSolver");

			for(int i=0; i< solverList.size(); i++) {
				JsonObject sol=  solverList.getJsonObject(0);
				assertEquals("Gecode", sol.getString("solverId"));
				assertEquals("Gecode", sol.getString("cmd"));				
			}

			solverList= solversInfo.getJsonArray("SATSolver");
			for(int i=0; i< solverList.size(); i++) {
				JsonObject sol=  solverList.getJsonObject(0);
				assertEquals("picat", sol.getString("solverId"));
				assertEquals("picat_sat_cmd_line", sol.getString("cmd"));				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("File not found");
		}
	}

	@Test
	void createSolverObject() {
		JsonObject solversInfo;
		try {
			solversInfo = JsonMng.getfromFile(INPUT_FILES_PATH+ SOLVERS_CONFIGURATION_FILE + JSON_EXT);


			JsonArray solverList= solversInfo.getJsonArray("CSPSolver");

			JsonObject sol=  solverList.getJsonObject(0);
			Solver solver= new Solver(SolverType.CSPSolver, sol);


			assertEquals(solver.getName(), "Gecode");
			assertEquals(solver.getCommand(), "Gecode");
			assertEquals(solver.getMaxSolutions(), 10);
			assertEquals(solver.getMaxTime(), 10000);
			assertEquals(solver.getAllInfo(), "-v");
			assertEquals(solver.getAllSolutions(), "-a");
			assertEquals(solver.getBoundSolutions(), "-n");
			assertEquals(solver.getTimeLimit(), "--time-limit");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("File not found");
		}
	}

		@Test
		void setUpCompilationTestUsingFiles() throws Exception {
			Compiler compiler= new Compiler();
			params= new CompilationParameters(
					INPUT_FILES_PATH, 
					MZN_FILES_PATH, 
					OUTPUT_FILES_PATH,
					"Test0_bool",
					SOLVERS_CONFIGURATION_FILE,
					FRONT_END_FILE,
					SourceOfCompilation.FILE
					);
			
			//testing the loading using files, the parameters represent paths

			
			try {
				compiler.setUpCompilation(params);
				assertNotNull(compiler.getSolversInfo());
				assertNotNull(compiler.getOperationsInfo());
				assertEquals(compiler.getOperationsInfo().getString("problemType"), "BOOL");
	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("file not found");
			}
		}

	@Test
	void getSelectedSolverTest()  {
		//initializing the compiler
		Compiler compiler= new Compiler();
		
		try {
			params= new CompilationParameters(
					INPUT_FILES_PATH, 
					MZN_FILES_PATH, 
					OUTPUT_FILES_PATH,
					"Test0_bool",
					SOLVERS_CONFIGURATION_FILE,
					FRONT_END_FILE,
					SourceOfCompilation.FILE
					);
			//setting up the compiler 
			compiler.setUpCompilation(params);
			//obtaining the solver
			Solver solver = compiler.getSolver();
			assertEquals("picat", solver.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
	
	@Test
	void getSolverTest()  {
		//initializing the compiler
		Compiler compiler= new Compiler();
		try {
			params= new CompilationParameters(
					INPUT_FILES_PATH, 
					MZN_FILES_PATH, 
					OUTPUT_FILES_PATH,
					"Test0_bool",
					SOLVERS_CONFIGURATION_FILE,
					FRONT_END_FILE,
					SourceOfCompilation.FILE
					);
			//setting up the compiler 
			compiler.setUpCompilation(params);
			//obtaining the solver
			Solver solver = compiler.getSolver();
			assertEquals("picat", solver.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
	
	@Test
	void testCompile() throws FileNotFoundException {
		CompilationParameters params= new CompilationParameters(
				INPUT_FILES_PATH, 
				MZN_FILES_PATH, 
				OUTPUT_FILES_PATH,
				MODEL_NAME,
				SOLVERS_CONFIGURATION_FILE,
				FRONT_END_FILE,
				SourceOfCompilation.FILE
				);
		//initializing the compiler
		Compiler compiler= new Compiler();
		try {
			//setting up the compiler 
			compiler.setUpCompilation(params);
			//compiling
			System.out.println("one solution");
			System.out.println(compiler.getOneSolution().toString());
			
			System.out.println("-----");
			
			System.out.println("5 solutions");
			System.out.println(compiler.getNSolutions(5).toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	void testCompileBool() throws FileNotFoundException {
		CompilationParameters params= new CompilationParameters(
				INPUT_FILES_PATH, 
				MZN_FILES_PATH, 
				OUTPUT_FILES_PATH,
				"Test0_bool",
				SOLVERS_CONFIGURATION_FILE,
				FRONT_END_FILE,
				SourceOfCompilation.FILE
				);
		//initializing the compiler
		Compiler compiler= new Compiler();
		try {
			//setting up the compiler 
			compiler.setUpCompilation(params);
			//compiling
			System.out.println("one solution, picat_test");
			//compiler.getOneSolution();
			System.out.println(compiler.getOneSolution().toString());
			
			System.out.println("-----");
			
			System.out.println("5 solutions, picat_test\"");
			System.out.println(compiler.getNSolutions(5).toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

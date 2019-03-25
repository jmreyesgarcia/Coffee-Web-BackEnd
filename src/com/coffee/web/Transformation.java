package com.coffee.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.coffee.common.commandExecutor.CmdExecutor;
import com.coffee.compiler.CompilationParameters;
import com.coffee.compiler.Compiler;
import com.coffee.compiler.SourceOfCompilation;
import com.coffee.modelParsers.basicHLVLPackage.IHlvlParser;
import com.coffee.modelParsers.splot2HLVL.Splot2HlvlParser;
import com.coffee.modelParsers.utils.ParsingParameters;
import com.coffee.modelParsers.varXmlToHLVLParser.VariamosXMLToHlvlParser;

/**
 * @author Juan Reyes <jmreyes@icesi.edu.co>
 */
public class Transformation {
	public final static String VARXML = "VARXML";
	public final static String SPLOT = "SPLOT";
	
	public final static String URL = "URL";
	public final static String TEXT = "TEXT";
	
	public final static String JSON = "JSON";
	public final static String HTML = "HTML";
	
	private final static String BASE_DIR = "coffee";
	private final static String VARXML_DIR = "varxml";
	private final static String HLVL_DIR = "hlvl";
	private final static String SPLOT_DIR = "splot";
	
	private final static String DEFAULT_NAME = "modelX";
	private final static String COMPILE_MODEL_NAME = DEFAULT_NAME+"_generated";
	
	private final static String HLVL_PARSER_BASE_DIR = "src-gen";
	private final static String SOLVER_INPUT_DIR = "src-gen";
	private final static String MZN_INPUT_DIR = "src-gen";
	private final static String HLVL_PARSER_BASIC_BOOL = DEFAULT_NAME+"_generated.mzn";
	private final static String HLVL_PARSER_BASIC_OPS = DEFAULT_NAME+"_generated_Operations.json";
	
	private final static String SOLUTION_DIR = "solution";
	private static final String SOLVERS_CONFIGURATION_FILE = "CoffeeSolvers";
	private static final String FRONT_END_FILE = "frontEndData";
	
	public static String transform(
			String modelType, 
			String resourceType, 
			String resourceContent, 
			String responseType, 
			String libDirPath, 
			String dataDirPath,
			String solverParameters,
			String solverType,
			String problemType,
			int numberOfSolutions) throws IOException, InterruptedException, Exception {
		
		String initialModelDir = transformToHLVL(modelType, resourceType, resourceContent, responseType, dataDirPath);
		parseHLVL(libDirPath, dataDirPath);
		writeFrontEndData(dataDirPath,solverParameters,solverType,problemType);
		JsonObject jsonCompileResults = coffeeCompile(dataDirPath, numberOfSolutions);
		
		System.out.println("All parsers and the compiler passed!");
		
		JsonObject jsonResult = buildJsonResult(
				modelType, 
				resourceType, 
				resourceContent, 
				responseType, 
				initialModelDir, 
				dataDirPath, 
				jsonCompileResults);
		String responseText = formatResult(responseType, jsonResult);
		return responseText;
	}
	
	private static void writeFrontEndData(String dataDirPath, String solverParameters, String solverType,
			String problemType) throws IOException {
		String frontEndData = "{\n";
		frontEndData += "\"solverSelected\" : \""+solverType+"\",\n";
		frontEndData += "\"problemType\" : \""+problemType+"\",\n";
		frontEndData += "\"configuration\" : {\n"+solverParameters+"\n}\n";
		frontEndData += "}";
		saveInputTempFile(dataDirPath+"/"+BASE_DIR+"/"+SOLVER_INPUT_DIR, FRONT_END_FILE+".json", frontEndData);
	}

	public static JsonObject coffeeCompile(String dataDirPath, int numberOfSolutions) throws Exception {
		CompilationParameters params= new CompilationParameters(
				dataDirPath+"/"+BASE_DIR+"/"+SOLVER_INPUT_DIR+"/", //INPUT_FILES_PATH 
				dataDirPath+"/"+BASE_DIR+"/"+MZN_INPUT_DIR+"/", //MZN_FILES_PATH 
				dataDirPath+"/"+BASE_DIR+"/"+SOLUTION_DIR+"/", //OUTPUT_FILES_PATH
				COMPILE_MODEL_NAME,
				SOLVERS_CONFIGURATION_FILE,
				FRONT_END_FILE,
				SourceOfCompilation.FILE
				);
		//initializing the compiler
		Compiler compiler= new Compiler();
		//setting up the compiler 
		compiler.setUpCompilation(params);
		//compiling
		System.out.println("one solution");
		JsonObject solution = compiler.getNSolutions(numberOfSolutions);
		
		return solution;
	}
	
	private static void parseHLVL(String libDirPath, String dataDirPath) throws InterruptedException, IOException {
		CmdExecutor executor = new CmdExecutor(dataDirPath+"/"+BASE_DIR);
		List<String> params = new ArrayList<String>();
		
		/*params.add("java"); 
		params.add("-jar");
		params.add(libDirPath+"/HLVLParser.jar");
		params.add(HLVL_DIR+"/"+DEFAULT_NAME+".hlvl");*/
		String command = "java -jar "+libDirPath+"/HLVLParser.jar "+HLVL_DIR+"/"+DEFAULT_NAME+".hlvl";
		params.add(command);
		
		executor.setCommandInConsole(params);
		executor.runCmd();
	}
	
	private static String transformToHLVL(String modelType, String resourceType, String resourceContent, String responseType, String dataDirPath) throws IOException {
		String modelContent = getModelContent(resourceType, resourceContent);

		String currentDir = dataDirPath+"/"+getCurrentDir(modelType);

		verifyDirectory(dataDirPath+"/"+BASE_DIR);

		saveInputTempFile(currentDir, DEFAULT_NAME+".xml", modelContent);

		verifyDirectory(dataDirPath+"/"+BASE_DIR+"/"+HLVL_DIR);
		
		convertToHLVL(modelType, dataDirPath, currentDir, modelContent);
		
		return currentDir;
	}
	
	private static String formatResult(String responseType, JsonObject jsonResult) {
		String formatedString = "";
		switch(responseType) {
			case JSON:
				formatedString = jsonResult.toString();
			break;
			case HTML:
				formatedString = buildHTMLPage(jsonResult);
			break;
		}
		return formatedString;
	}

	private static String buildHTMLPage(JsonObject jsonResult) {
		String htmlPage = "<html>\n  <head>\n    <title>Test Results</title>\n  </head>\n  <body>\n";
		
		htmlPage += "    <h1>Test Results</h1>\n";
		htmlPage += "    <h2>Input Params</h2>\n";
		htmlPage += "    <p>modelType="+jsonResult.getString("modelType")+"</p>\n";
		htmlPage += "    <p>resourceType="+jsonResult.getString("resourceType")+"</p>\n";
		htmlPage += "    <p>responseType="+jsonResult.getString("responseType")+"</p>\n";
		htmlPage += "    <p>resourceContent:</p>\n";
		htmlPage += "    <p><pre>"+jsonResult.getString("resourceContent").replaceAll("<", "&lt;")+"</pre></p>\n";
		htmlPage += "    <h2>Output</h2>\n";
		htmlPage += "    <h3>sourceModel:</h3>\n";
		htmlPage += "    <p><pre>"+jsonResult.getString("sourceModel").replaceAll("<", "&lt;")+"</pre></p>\n";
		htmlPage += "    <h3>HLVL:</h3>\n";
		htmlPage += "    <p><pre>"+jsonResult.getString("hlvl").replaceAll("<", "&lt;")+"</pre></p>\n";
		htmlPage += "    <h3>Basic Bool:</h3>\n";
		htmlPage += "    <p><pre>"+jsonResult.getString("basicBool").replaceAll("<", "&lt;")+"</pre></p>\n";
		htmlPage += "    <h3>Basic Operations:</h3>\n";
		htmlPage += "    <p><pre>"+jsonResult.getString("basicOps").replaceAll("<", "&lt;")+"</pre></p>\n";

		JsonObject compilationResults = jsonResult.getJsonObject("compilationResults");
				
		htmlPage += "    <h3>Compilation Results:</h3>\n";
		htmlPage += "    <p>state:<pre>"+compilationResults.getString("state")+"</pre></p>\n";
		htmlPage += "    <p>overAllTime:<pre>"+compilationResults.getInt("overAllTime")+"</pre></p>\n";
		htmlPage += "    <p>numberOfSolutions:<pre>"+compilationResults.getInt("numberOfSolutions")+"</pre></p>\n";
		
		JsonArray solutions = compilationResults.getJsonArray("solutions");
		
		htmlPage += "    <h4>Solutions:</h4>\n";
		int i=1;
		for (JsonValue jsonValue : solutions) {
			JsonObject sol = jsonValue.asJsonObject();
			htmlPage += "    <p>Solution "+i+":</p>\n";
			Set<String> keys = sol.keySet();
			for (String k:keys) {
				htmlPage += "    "+k+": "+sol.get(k)+"<br/>";
			}
			i++;
		}

		htmlPage += "  </body>\n</html>";
		return htmlPage;
	}

	private static JsonObject buildJsonResult(String modelType, String resourceType, String resourceContent, String responseType,
		String currentDir, String dataDirPath, JsonObject jsonCompilationResults) throws IOException {
		//System.out.println("sourceModel: "+currentDir+"/"+DEFAULT_NAME+".xml");
		JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
				  .add("modelType", modelType)
				  .add("resourceType", resourceType)
				  .add("resourceContent", resourceContent)
				  .add("responseType", responseType)
				  .add("sourceModel", localPathToString(currentDir+"/"+DEFAULT_NAME+".xml"))
				  .add("hlvl", localPathToString(dataDirPath+"/"+BASE_DIR+"/"+HLVL_DIR+"/"+DEFAULT_NAME+".hlvl"))
				  .add("basicBool", localPathToString(dataDirPath+"/"+BASE_DIR+"/"+HLVL_PARSER_BASE_DIR+"/"+HLVL_PARSER_BASIC_BOOL))
				  .add("basicOps", localPathToString(dataDirPath+"/"+BASE_DIR+"/"+HLVL_PARSER_BASE_DIR+"/"+HLVL_PARSER_BASIC_OPS))
				  .add("compilationResults", jsonCompilationResults);
		
		JsonObject jsonObject = objectBuilder.build();
		
        //System.out.println(jsonObject.toString());
		return jsonObject;
	}
	private static String getCurrentDir(String modelType) {
		String currentDir = "";
		switch(modelType) {
			case VARXML:
				currentDir = BASE_DIR+"/"+VARXML_DIR;
				break;
			case SPLOT:
				currentDir = BASE_DIR+"/"+SPLOT_DIR;
				break;
		}
		return currentDir;
	}
	
	private static String getModelContent(String resourceType, String resourceContent) throws MalformedURLException, IOException {
		String modelContent = "";
		switch(resourceType) {
			case URL:
				modelContent = urlToString(new URL(resourceContent));
				break;
			case TEXT:
				modelContent = resourceContent;
				break;
		}
		return modelContent;
	}
	
	public static void convertToHLVL(String modelType, String dataDirPath, String currentDir, String modelContent) throws IOException {
		ParsingParameters params= new ParsingParameters();
		params.setOutputPath(new File(dataDirPath+"/"+BASE_DIR+"/"+HLVL_DIR).getAbsolutePath());
		params.setTargetName(DEFAULT_NAME);
		
		IHlvlParser parser = null;//ParserFactory.createParser(modelType,params);
		
		switch(modelType) {
			case VARXML:
				params.setInputPath(new File(currentDir).getAbsolutePath());
				parser = new VariamosXMLToHlvlParser(params);
				((VariamosXMLToHlvlParser)parser).loadArrayLists();
			break;
			case SPLOT:
				params.setInputPath(new File(currentDir).getAbsolutePath()+"/"+DEFAULT_NAME+".xml");
				parser = new Splot2HlvlParser(params);
			break;
		}
		
		try {
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static File verifyDirectory(String dir) {
		File fileDir = new File(dir);
		//System.out.println("fileDir: "+fileDir.getAbsolutePath());
		if(!fileDir.exists()) fileDir.mkdir();
		return fileDir;
	}

	private static void saveInputTempFile(String currentDir, String fileName, String modelContent) throws IOException {
		File currentFileDir = verifyDirectory(currentDir);
		
		File currentModelFile = new File(currentFileDir.getAbsolutePath()+"/"+fileName);
		//System.out.println(currentModelFile.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(new FileWriter(currentModelFile));
		bw.write(modelContent);
		bw.close();		
	}
	
	private static String urlToString(URL url) throws IOException {		
		Scanner s = new Scanner(url.openStream());
		String urlContent = "";
		while(s.hasNext()) {
			urlContent += s.nextLine()+"\n";
		}
		s.close();
		return urlContent;
	}
	
	private static String localPathToString(String filePath) throws IOException {		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String fileContent = "";
		String line;
		while((line = br.readLine())!=null) {
			fileContent += line+"\n";
		}
		br.close();
		return fileContent;
	}
}

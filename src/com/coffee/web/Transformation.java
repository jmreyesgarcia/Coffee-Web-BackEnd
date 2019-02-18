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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.coffee.common.commandExecutor.CmdExecutor;
import com.coffee.modelParsers.basicHLVLPackage.IHlvlParser;
import com.coffee.modelParsers.splot2HLVL.Splot2HlvlParser;
import com.coffee.modelParsers.utils.ParsingParameters;
import com.coffee.modelParsers.xmlToHLVLParser.VariamosXMLToHlvlParser;

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
	
	private final static String BASE_DIR = "temp";
	private final static String VARXML_DIR = BASE_DIR+"/model"; 
	private final static String HLVL_DIR = BASE_DIR+"/hlvl";
	private final static String SPLOT_DIR = BASE_DIR+"/splot";
	
	private final static String HLVL_PARSER_BASE_DIR = "src-gen";
	private final static String HLVL_PARSER_BASIC_BOOL = "BasicFeatureModel_bool.mzn";
	private final static String HLVL_PARSER_BASIC_OPS = "BasicFeatureModel_Operations.json";
	
	
	private final static String DEFAULT_NAME = "model";

	
	public static String transform(String modelType, String resourceType, String resourceContent, String responseType) throws IOException, InterruptedException {
		
		String initialModelDir = transformToHLVL(modelType, resourceType, resourceContent, responseType);
		//parseHLVL();
		
		System.out.println("All parsers passed!");
		
		JsonObject jsonResult = buildJsonResult(modelType, resourceType, resourceContent,responseType, initialModelDir);
		String responseText = formatResult(responseType, jsonResult);
		return responseText;
	}
	
	private static void parseHLVL() throws InterruptedException, IOException {
		CmdExecutor executor = new CmdExecutor("./");
		List<String> params = new ArrayList<String>();
		
		//params.add("java -jar HLVLParser.jar "+HLVL_DIR+"/"+DEFAULT_NAME+".hlvl");
		/*params.add("java");
		params.add("-jar");
		params.add("HLVLParser.jar");
		params.add(HLVL_DIR+"/"+DEFAULT_NAME+".hlvl");
		*/
		params.add("ls");
		executor.setCommandInConsole(params);
		executor.runCmd();
	}
	
	private static String transformToHLVL(String modelType, String resourceType, String resourceContent, String responseType) throws IOException {
		String modelContent = getModelContent(resourceType, resourceContent);		

		String currentDir = getCurrentDir(modelType);

		verifyDirectory(BASE_DIR);

		saveInputTempFile(currentDir, modelContent);

		convertToHLVL(modelType, currentDir, modelContent);
		
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
		/*
		htmlPage += "    <h3>Basic Bool:</h3>\n";
		htmlPage += "    <p><pre>"+jsonResult.getString("basicBool").replaceAll("<", "&lt;")+"</pre></p>\n";
		htmlPage += "    <h3>Basic Operations:</h3>\n";
		htmlPage += "    <p><pre>"+jsonResult.getString("basicOps").replaceAll("<", "&lt;")+"</pre></p>\n";
		*/
		htmlPage += "  </body>\n</html>";
		return htmlPage;
	}

	private static JsonObject buildJsonResult(String modelType, String resourceType, String resourceContent, String responseType,
		String currentDir) throws IOException {
		//System.out.println("sourceModel: "+currentDir+"/"+DEFAULT_NAME+".xml");
		JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
				  .add("modelType", modelType)
				  .add("resourceType", resourceType)
				  .add("resourceContent", resourceContent)
				  .add("responseType", responseType)
				  .add("sourceModel", localPathToString(currentDir+"/"+DEFAULT_NAME+".xml"))
				  .add("hlvl", localPathToString(HLVL_DIR+"/"+DEFAULT_NAME+".hlvl"))/*
				  .add("basicBool", localPathToString(HLVL_PARSER_BASE_DIR+"/"+HLVL_PARSER_BASIC_BOOL))
				  .add("basicOps", localPathToString(HLVL_PARSER_BASE_DIR+"/"+HLVL_PARSER_BASIC_OPS))*/;
		
		JsonObject jsonObject = objectBuilder.build();
        //System.out.println(jsonObject.toString());
		return jsonObject;
	}
	private static String getCurrentDir(String modelType) {
		String currentDir = "";
		switch(modelType) {
			case VARXML:
				currentDir = VARXML_DIR;
				break;
			case SPLOT:
				currentDir = SPLOT_DIR;
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
	
	public static void convertToHLVL(String modelType, String currentDir, String modelContent) throws IOException {
		ParsingParameters params= new ParsingParameters();
		params.setOutputPath(new File(HLVL_DIR).getAbsolutePath());
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

	private static void saveInputTempFile(String currentDir, String modelContent) throws IOException {
		File currentFileDir = verifyDirectory(currentDir);
		
		File currentModelFile = new File(currentFileDir.getAbsolutePath()+"/"+DEFAULT_NAME+".xml");
		//System.out.println(currentModelFile.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(new FileWriter(currentModelFile));
		bw.write(modelContent);
		bw.close();
		
		File hlvlDir = new File(HLVL_DIR);
		if(!hlvlDir.exists()) hlvlDir.mkdir();
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

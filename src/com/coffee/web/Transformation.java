package com.coffee.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

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
	
	private final static String BASE_DIR = "temp";
	private final static String VARXML_DIR = BASE_DIR+"/model"; 
	private final static String HLVL_DIR = BASE_DIR+"/hlvl";
	private final static String SPLOT_DIR = BASE_DIR+"/splot";
	
	private final static String DEFAULT_NAME = "model";
	
	public static void transformToHLVL(String modelType, String resourceType, String resourceContent) throws IOException {
		String modelContent = "";
		
		switch(resourceType) {
			case URL:
				modelContent = urlToString(new URL(resourceContent));
			break;
			case TEXT:
				modelContent = resourceContent;
			break;
		}
		
		String currentDir = "";
		switch(modelType) {
			case VARXML:
				currentDir = VARXML_DIR;
			break;
			case SPLOT:
				currentDir = SPLOT_DIR;
			break;
		}
		
		verifyDirectory(BASE_DIR);
		saveInputTempFile(currentDir, modelContent);
		
		convertToHLVL(modelType, currentDir, modelContent);
	}
	
	public static void convertToHLVL(String modelType, String currentDir, String modelContent) throws IOException {
		ParsingParameters params= new ParsingParameters();
		params.setInputPath(new File(currentDir).getAbsolutePath()+"/"+DEFAULT_NAME+".xml");
		params.setOutputPath(new File(HLVL_DIR).getAbsolutePath());
		params.setTargetName(DEFAULT_NAME);
		
		IHlvlParser parser = null;//ParserFactory.createParser(modelType,params);
		
		switch(modelType) {
			case VARXML:
				parser = new VariamosXMLToHlvlParser(params);
				((VariamosXMLToHlvlParser)parser).loadArrayLists();
			break;
			case SPLOT:
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
	
}

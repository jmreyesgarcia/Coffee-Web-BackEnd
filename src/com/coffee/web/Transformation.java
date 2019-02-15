package com.coffee.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import com.coffee.modelParsers.utils.ParsingParameters;
import com.coffee.modelParsers.xmlToHLVLParser.VariamosXMLToHlvlParser;

public class Transformation {
	private final static String MXGRAPH_DIR = "temp/model"; 
	private final static String HLVL_DIR = "temp/hlvl"; 
	private final static String SPLOT_DIR = "temp/splot"; 
	private final static String DEFAULT_NAME = "model";
	
	private final static String MXGRAPH = "MXGRAPH";
	private final static String SPLOT = "SPLOT";
	
	private final static String URL = "URL";
	private final static String TEXT = "TEXT";
	
	
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
			case MXGRAPH:
				currentDir = MXGRAPH_DIR;
			break;
			case SPLOT:
				currentDir = SPLOT_DIR;
			break;
		}
		
		saveInputTempFile(currentDir, modelContent);
		
		switch(modelType) {
			case MXGRAPH:
				transformMXGraphToHLVL(currentDir, modelContent);
			break;
			case SPLOT:
				transformSPLOTToHLVL(currentDir, modelContent);
			break;
		}
	}
	
	public static void transformSPLOTToHLVL(String currentDir, String modelContent) throws IOException {
		
	}
	
	public static void transformMXGraphToHLVL(String currentDir, String modelContent) throws IOException {		
		ParsingParameters params= new ParsingParameters();		
		params.setInputPath(new File(currentDir).getAbsolutePath());
		params.setOutputPath(new File(HLVL_DIR).getAbsolutePath());
		params.setTargetName(DEFAULT_NAME);
		
		VariamosXMLToHlvlParser parser = new VariamosXMLToHlvlParser(params);
		parser.loadArrayLists();
		try {
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void saveInputTempFile(String currentDir, String modelContent) throws IOException {
		File currentFileDir = new File(currentDir);
		if(!currentFileDir.exists()) currentFileDir.mkdir();		
		File currentModelFile = new File(currentFileDir+"/"+DEFAULT_NAME+".xml");
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
			urlContent += s.nextLine();
		}
		s.close();
		return urlContent;
	}
	
}

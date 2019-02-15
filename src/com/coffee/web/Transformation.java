package com.coffee.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.coffee.modelParsers.utils.ParsingParameters;
import com.coffee.modelParsers.xmlToHLVLParser.VariamosXMLToHlvlParser;

public class Transformation {
	private final static String MXGRAPH_DIR = "temp/model"; 
	private final static String HLVL_DIR = "temp/hlvl"; 
	private final static String DEFAULT_NAME = "model"; 
	private final static String MXGRAPH = "MXGRAPH";
	
	public static void transformToHLVL(String modelType, String modelContent) throws IOException {
		switch(modelType) {
			case MXGRAPH:
				transformMXGraphToHLVL(modelContent);
			break;
		}
	}
	
	public static void transformMXGraphToHLVL(String modelContent) throws IOException {
		File mxGraphDir = new File(MXGRAPH_DIR);
		if(!mxGraphDir.exists()) mxGraphDir.mkdir();
		File hlvlDir = new File(HLVL_DIR);
		if(!hlvlDir.exists()) hlvlDir.mkdir();
		
		File mxGraphModelFile = new File(MXGRAPH_DIR+"/"+DEFAULT_NAME+".xml");
		BufferedWriter bw = new BufferedWriter(new FileWriter(mxGraphModelFile));
		bw.write(modelContent);
		bw.close();
		
		ParsingParameters params= new ParsingParameters();
		
		params.setInputPath(mxGraphDir.getAbsolutePath());
		params.setOutputPath(hlvlDir.getAbsolutePath());
		params.setTargetName(DEFAULT_NAME);
		
		VariamosXMLToHlvlParser parser = new VariamosXMLToHlvlParser(params);
		parser.loadArrayLists();
		try {
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

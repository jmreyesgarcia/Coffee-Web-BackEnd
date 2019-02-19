package com.coffee.web;

import java.io.IOException;

public class TransformationCustomTest {
	public static String testTransformToHLVLFromSPLOT() throws IOException, InterruptedException {
		String modelType = Transformation.SPLOT;
		String resourceType = Transformation.URL;
		String responseType = Transformation.HTML;
		String resourceContent = "http://52.32.1.180:8080/SPLOT/models/temp_models/model_20190117_1122449247.xml";
		String libDirPath = "C:/Users/Juan/workspace/variamosbackend/WebContent/WEB-INF/lib";
		String dataDirPath = ".";
		
		String textResponse = Transformation.transform(modelType, resourceType, resourceContent, responseType, libDirPath, dataDirPath);
		return textResponse;
	}
	
	public static String testTransformToHLVLFromVARXML() throws IOException, InterruptedException {
		String modelType = Transformation.VARXML;
		String resourceType = Transformation.URL;
		String responseType = Transformation.HTML;
		String resourceContent = "http://localhost:8888/variamosbackend/VarXMLTest.xml";
		String libDirPath = "C:/Users/Juan/workspace/variamosbackend/WebContent/WEB-INF/lib";
		String dataDirPath = ".";
		
		String textResponse = Transformation.transform(modelType, resourceType, resourceContent, responseType, libDirPath, dataDirPath);
		return textResponse;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String textResponse1 = testTransformToHLVLFromSPLOT();
		String textResponse2 = testTransformToHLVLFromVARXML();
		System.out.println(textResponse1);
		System.out.println(textResponse2);
	}
}

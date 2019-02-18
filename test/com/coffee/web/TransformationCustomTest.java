package com.coffee.web;

import java.io.IOException;

public class TransformationCustomTest {
	public static String testTransformToHLVLFromSPLOT() throws IOException, InterruptedException {
		String modelType = Transformation.SPLOT;
		String resourceType = Transformation.URL;
		String responseType = Transformation.HTML;
		String resourceContent = "http://52.32.1.180:8080/SPLOT/models/temp_models/model_20190117_1122449247.xml";
		
		String textResponse = Transformation.transform(modelType, resourceType, resourceContent, responseType);
		return textResponse;
	}
	
	public static String testTransformToHLVLFromVARXML() throws IOException, InterruptedException {
		String modelType = Transformation.VARXML;
		String resourceType = Transformation.URL;
		String responseType = Transformation.HTML;
		String resourceContent = "http://localhost:8888/variamosbackend/VarXMLTest.xml";
		
		String textResponse = Transformation.transform(modelType, resourceType, resourceContent, responseType);
		return textResponse;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		//String textResponse = testTransformToHLVLFromSPLOT();
		String textResponse = testTransformToHLVLFromVARXML();
		System.out.println(textResponse);
	}
}

package com.coffee.web;

import java.io.IOException;

public class TransformationCustomTest {
	public static String testTransformToHLVL_SPLOT() throws IOException {
		String modelType = Transformation.SPLOT;
		String resourceType = Transformation.URL;
		String responseType = Transformation.HTML;
		String resourceContent = "http://52.32.1.180:8080/SPLOT/models/temp_models/model_20190117_1122449247.xml";
		
		String textResponse = Transformation.transformToHLVL(modelType, resourceType, resourceContent, responseType);
		return textResponse;
	}
	
	public static void main(String[] args) throws IOException {
		String textResponse = testTransformToHLVL_SPLOT();
		System.out.println(textResponse);
	}
}

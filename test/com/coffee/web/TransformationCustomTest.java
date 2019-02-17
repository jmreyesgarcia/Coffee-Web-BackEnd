package com.coffee.web;

import java.io.IOException;

public class TransformationCustomTest {
	public static void testTransformToHLVL_SPLOT() throws IOException {
		String modelType = Transformation.SPLOT;
		String resourceType = Transformation.URL;
		String resourceContent = "http://52.32.1.180:8080/SPLOT/models/temp_models/model_20190117_1122449247.xml";
		
		Transformation.transformToHLVL(modelType, resourceType, resourceContent);		
	}
	
	public static void main(String[] args) throws IOException {
		testTransformToHLVL_SPLOT();
	}
}

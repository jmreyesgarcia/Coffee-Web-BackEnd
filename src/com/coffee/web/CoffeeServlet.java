package com.coffee.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class VariamosWeb
 */
@WebServlet("/coffee")
public class CoffeeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default constructor. 
	 */
	public CoffeeServlet() {
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	private void printParameters(Map<String, String[]> data) {
		for (Map.Entry<String, String[]> param : data.entrySet()) {			
			System.out.println(param.getKey());
			for (String value: param.getValue()) {
				System.out.println(value+" ");
			}
		}		
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printParameters(request.getParameterMap());		
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		PrintWriter out = response.getWriter();
		out.print("Successfull Operation!");
		
		String modelType = request.getParameter("modelType");		
		String modelContent = request.getParameter("modelContent");
		
		Transformation.transformToHLVL(modelType, modelContent);
		
		/*
		try {
			URL url = new URL("https://www.google.com/url?q=http://52.32.1.180:8080/SPLOT/models/temp_models/model_20190117_1122449247.xml&sa=D&source=hangouts&ust=1550245400717000&usg=AFQjCNGlRzLeWWpzYUog-IVRPRU3u6bKjQ");
			Scanner s = new Scanner(url.openStream());
			
			while(s.hasNext()) {
				System.out.println(s.hasNextLine());
			}
			
			// read from your scanner
		}
		catch(IOException ex) {
			// there was some connection problem, or the file did not exist on the server,
			// or your URL was not in the right format.
			// think about what to do now, and put it here.
			ex.printStackTrace(); // for now, simply output it.
		}
		*/
	}

}

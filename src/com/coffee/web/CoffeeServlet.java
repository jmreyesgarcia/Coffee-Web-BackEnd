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
 * @author Juan Reyes
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
	
	/** 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		printParameters(request.getParameterMap());		
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		PrintWriter out;
		try {
			out = response.getWriter();
			String modelType = request.getParameter("modelType");
			String resourceType = request.getParameter("resourceType");
			String resourceContent = request.getParameter("resourceContent");
			String responseType = request.getParameter("responseType");
			
			String stringResponse = "HTTP PARAMS RECEIVED";
			try {
				stringResponse = Transformation.transform(modelType, resourceType, resourceContent, responseType);
			} catch (InterruptedException e) {
				stringResponse = e.getMessage();
				e.printStackTrace();
			} catch (IOException e) {
				stringResponse = e.getMessage();
				e.printStackTrace();
			}
			out.print(stringResponse);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printParameters(Map<String, String[]> data) {
		for (Map.Entry<String, String[]> param : data.entrySet()) {			
			System.out.println(param.getKey());
			for (String value: param.getValue()) {
				System.out.println(value+" ");
			}
		}		
	}	
}

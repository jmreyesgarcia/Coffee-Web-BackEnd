package com.coffee.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
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
			String solverParameters = request.getParameter("solverParameters");
			String solverType = request.getParameter("solverType");
			String problemType = request.getParameter("problemType");
			String numberOfSolutions = request.getParameter("numberOfSolutions");
			
			String stringResponse = "<br/>HTTP PARAMS RECEIVED";
			String realRootPath = request.getServletContext().getRealPath("/");
			String libDir = realRootPath+"/WEB-INF/lib";
			String dataDir = realRootPath+"/WEB-INF";
			
			try {
				int nos = Integer.parseInt(numberOfSolutions);
				stringResponse = Transformation.transform(
						modelType, 
						resourceType, 
						resourceContent, 
						responseType, 
						libDir, 
						dataDir,
						solverParameters,
						solverType,
						problemType,
						nos);
			} catch (InterruptedException e) {
				stringResponse = e.getMessage();
				e.printStackTrace();
			} catch (IOException e) {
				stringResponse = e.getMessage();
				e.printStackTrace();
			} catch (NumberFormatException e) {
				stringResponse = e.getMessage();
				e.printStackTrace();				
			} catch (Exception e) {
				final StringBuilder stringResponseJsonParsing = new StringBuilder(e.getMessage());
				Arrays.stream(e.getStackTrace()).forEach(excp -> stringResponseJsonParsing.append("<br>"+excp));
				e.printStackTrace();
				stringResponse = stringResponseJsonParsing.toString();
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

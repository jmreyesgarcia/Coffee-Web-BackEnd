package com.coffee.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.coffee.modelParsers.utils.ParsingParameters;
import com.coffee.modelParsers.xmlToHLVLParser.VariamosXMLToHlvlParser;

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
		// TODO Auto-generated constructor stub
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
	
	private void transformXMLToHLVL(String xmlModel) throws IOException {
		File directory = new File("temp");
		if(!directory.exists()) directory.mkdir();
		
		File file = new File("temp/model.xml");
		String modelFileName = file.getAbsolutePath();
		System.out.println("pathDir:"+directory.getAbsolutePath());
		System.out.println("pathFile:"+modelFileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(xmlModel);
		bw.close();
		
		ParsingParameters params= new ParsingParameters();
		
		params.setInputPath(directory.getAbsolutePath());
		params.setOutputPath(directory.getAbsolutePath());
		params.setTargetName("model");
		
		VariamosXMLToHlvlParser parser = new VariamosXMLToHlvlParser(params);
		parser.loadArrayLists();
		try {
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		out.print("Successfull Operation!");
		printParameters(request.getParameterMap());

		String xmlModel = request.getParameter("xmlModel");
		transformXMLToHLVL(xmlModel);
		
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

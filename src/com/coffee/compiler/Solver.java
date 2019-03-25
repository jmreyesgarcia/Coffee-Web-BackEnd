package com.coffee.compiler;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;

public class Solver {
	private String solverID;
	private String command;
	private List<String> parameters =new ArrayList<String>();
;
	private SolverType type;
	private int maxSolutions;
	private int maxTime;
	private String allInfo;
	private String allSolutions;
	private String boundSolutions;
	private String timeLimit;
	
	
	

	
	public Solver(String solverID, String command, SolverType type) {
		super();
		this.solverID = solverID;
		this.command = command;
		this.type = type;
	}
	
	public List<String> getOptions(){
		parameters.clear();
		parameters.add("--solver");
		parameters.add(command);
		return parameters;
	}
	public Solver(SolverType type) {
		this.type = type;
	}
	
	public Solver (SolverType type, JsonObject json) {
		this.type = type;
		this.solverID = json.getString("solverId");
		this.command = json.getString("cmd");
		maxSolutions = json.getInt("maxSolutions");
		maxTime = json.getInt("maxTime");
		allInfo = json.getString("allInfo");
		allSolutions = json.getString("allSolutions");
		boundSolutions = json.getString("boundSolutions");
		timeLimit = json.getString("timeLimit");
		
	}
	public String getName() {
		return solverID;
	}
	public void setName(String name) {
		this.solverID = name;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public List<String> getParameters() {
		return parameters;
	}
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}
	public int getMaxSolutions() {
		return maxSolutions;
	}
	public void setMaxSolutions(int maxSolutions) {
		this.maxSolutions = maxSolutions;
		parameters.add(maxSolutions+"");
	}
	public int getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}
	public String getAllInfo() {
		return allInfo;
	}
	public void setAllInfo(String allInfo) {
		this.allInfo = allInfo;
	}
	public String getAllSolutions() {
		return allSolutions;
	}
	public void setAllSolutions(String allSolutions) {
		this.allSolutions = allSolutions;
	}
	public String getBoundSolutions() {
		return boundSolutions;
	}
	public void setBoundSolutions(String boundSolutions) {
		this.boundSolutions = boundSolutions;
	}
	public String getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}
	
	public SolverType getType() {
		return type;
	}
	public void setType(SolverType type) {
		this.type = type;
	}

}

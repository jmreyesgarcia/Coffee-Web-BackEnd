package com.coffee.common.commandExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * For executing calls in the console
 * 
 * @author Angela Villota
 * @version coffee V1 January 2019
 */

public class CmdExecutor {
	private static final String EXECUTION_PATH = "COFFEE_EXECUTION_PATH";
	private boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	private boolean isEnvrVariable;
	public String directory;
	private ProcessBuilder processBuilder;

	public CmdExecutor() {

//		System.out.println(System.getenv(EXECUTION_PATH));		
		processBuilder = new ProcessBuilder();
		processBuilder.directory(new File(System.getenv(EXECUTION_PATH)));
		isEnvrVariable = true;
	}

	public CmdExecutor(String executionPath) {

		processBuilder = new ProcessBuilder();
		processBuilder.directory(new File(executionPath));

	}

	public void setCommandInConsole(List<String> params) {
		if(isWindows) {
			params.add(0, "cmd.exe");
			params.add(1, "/c");
		}else {
			params.add(0, "sh");			
			params.add(1, "-c");			
		}
		System.out.println(params);
		processBuilder.command(params);
	}
	
	public void addCmd(List<String> params) {
		//if (isEnvrVariable) {
			processBuilder.command(params);

		/*} else {
			if (isWindows) {
				processBuilder.command("cmd.exe", "/c", "dir");
			} else {
				processBuilder.command("sh", "-c", "ls");
			}

		}*/
	}
	
	public int runCmd() throws InterruptedException, IOException {
		Process process = processBuilder.start();
		
		//StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
		//Executors.newSingleThreadExecutor().submit(streamGobbler);
		int exitCode = process.waitFor();
        BufferedReader stdInput = new BufferedReader(new 
                InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new 
                InputStreamReader(process.getErrorStream()));		
        
        String line;
        String inputStr = "";
        String errStr = "";
        while ((line = stdInput.readLine()) != null) {
        	inputStr += line + "\n";
        }
        while ((line = stdError.readLine()) != null) {
        	errStr += line + "\n";
        }
        
        System.out.println(inputStr);
        System.out.println(errStr);
        
		System.out.println("exitCode="+exitCode);
		System.out.println("p.isAlive()="+process.isAlive());
		//assert exitCode == 0;
		return exitCode;
	}

}

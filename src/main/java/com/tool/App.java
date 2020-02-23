package com.tool;

import java.util.List;

/**
 * This is the main application class.
 * 
 * @author r3c1us0
 */

public class App {
	
	public final static String settingsFileName = "settings.xml";
	public static String workspace = "";
	public static ApplicationFactory appFactory;
	
	public static void main(String[] args) {
		System.out.println("FlexibleBackupTool running...");
		boolean doRestore = false;
		
		appFactory = ApplicationFactory.getInstance();
		
    	if(args.length > 0) {
    		for(int i = 0; i < args.length; i++) {
    			if(args[i].equalsIgnoreCase("restore"))
    				doRestore = true;
    			if(args[i].startsWith("workspace=")) {
    				String[] workSpaceValues = args[i].split("="); 
    				workspace = workSpaceValues[1];
    			}
    		}
    	}
    	else {
    		appFactory.displayConsoleManual();
    	}
    	
    	if(workspace.isEmpty()) {
    		workspace = appFactory.getRootPath();
    	}
    	
    	appFactory.setWorkspace(workspace);
    	
    	// initialize application system files
    	appFactory.initSettingsFileHandler();
    	appFactory.initLog4jPropertyHandler();
    	appFactory.initLogFilenameHandler();
    	appFactory.writeLogFilePathToLog4jPropertyFile();
    	appFactory.initApplicationLogger();

    	List<String> warningList = appFactory.getWarningList();
    	if(warningList.size() > 0) {
    		for(String warningMessage : warningList) {
    			System.out.println(warningMessage);
    		}
    		
    		System.err.println("Application cannot be run properly.");
    		System.exit(0);
    	}
    	
    	StringBuffer logMsg = new StringBuffer();
    	if(!doRestore)
    		logMsg.append( String.format("Application start for backup from system environment workspace=%s", workspace));
    	else 
    		logMsg.append( String.format("Application start for restore from system environment workspace=%s", workspace));
    	
    	appFactory.getApplicationLogger().writeInfo(logMsg.toString());
    	
    	if(!doRestore)
    		appFactory.startBackupProcesses();
    	else 
    		appFactory.startRestoreProcess();
    	
    	long currentTime = System.currentTimeMillis();
    	
    	long timing = currentTime - appFactory.getStartApplicationTime();
    	StringBuffer stringBuffer = new StringBuffer();
    	stringBuffer.append("End application running time");
    	
    	String[] splitedValues = appFactory.convertMillisToHumanReadable(timing).split(":");
    	boolean allValuesNull = true;
    	for(int i = 0; i < splitedValues.length; i++) {
    		if(!splitedValues[i].equals("00"))
    			allValuesNull = false;
    	}
    	if(!allValuesNull) {
    		stringBuffer.append(" - " + appFactory.convertMillisToHumanReadable(timing));
    	}
    	else {
    		stringBuffer.append(" - " + timing + " milliseconds.");
    	}
    	
    	appFactory.getApplicationLogger().writeInfo(stringBuffer.toString());
	}

}

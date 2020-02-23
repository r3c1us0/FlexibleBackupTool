package com.tool.log4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Uses to communicate with LOG4J class methods. 
 * 
 *@author r3c1us0
 */
public class ApplicationLogger {

	private static volatile ApplicationLogger instance = null;
	private static final Object mutex = new Object();
	
	private String log4jPropertiesFilePath = "";
	
	// a path specified for example to log.txt uses for properties key - 'appender.LogFileAppender.file' 
	private String log4jLogPath = "";
	
	// log properties file handler
	private File log4jPropertiesFileHandler;
	

	private Logger logger;
	
	private ApplicationLogger() {
		if(logger == null) {
			logger = Logger.getLogger("FlexibleBackupTool");
		}
	}
	
	public File getLog4jPropertiesFileHandler() {
		return log4jPropertiesFileHandler;
	}

	public void setLog4jPropertiesFileHandler(File log4jPropertiesFileHandler) {
		this.log4jPropertiesFileHandler = log4jPropertiesFileHandler;
	}

	public String getLog4jPropertiesFilePath() {
		return log4jPropertiesFilePath;
	}

	public void setLog4jPropertiesFilePath(String log4jPropertiesFilePath) {
		this.log4jPropertiesFilePath = log4jPropertiesFilePath;
	}

	public String getLog4jLogPath() {
		return log4jLogPath;
	}

	public void setLog4jLogPath(String log4jLogPath) {
		this.log4jLogPath = log4jLogPath;
	}
	
	public String getPropertyKeyValue(String propsKey) throws Exception {
		String result = "";

		if(this.log4jPropertiesFileHandler.exists() && this.log4jPropertiesFileHandler.canWrite()) {
			Properties properties = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(this.log4jPropertiesFileHandler);
				properties.load(fis);
				
				result = properties.getProperty(propsKey);
			}
			catch (Exception e) {
				throw new Exception(ApplicationLogger.class.getName() + " getPropertyKeyValue " + e.getMessage());
			}
			finally {
				if(fis != null) 
					fis.close();
			}
		}		
		
		return result;
	}
	
	public void setPropertyValue(String key, String value) throws Exception {
		
		if(this.log4jPropertiesFileHandler.exists() && this.log4jPropertiesFileHandler.canWrite()) {
			Properties properties = new Properties();
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try {
				fis = new FileInputStream(this.log4jPropertiesFileHandler);
				properties.load(fis);
				properties.setProperty(key, value);
				fos = new FileOutputStream(this.log4jPropertiesFileHandler);
				properties.store(fos, "");
			} catch (Exception e) {
				throw new Exception(ApplicationLogger.class.getName() + " setPropertyValue: " + e.getMessage());
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
					if (fos != null) {
						fos.close();
					}
				} catch (Exception e) {
					throw new Exception(ApplicationLogger.class.getName() + " setPropertyValue: " + e.getMessage());
				}
			}
		}
	}
	
	public static ApplicationLogger getInstance() {
		ApplicationLogger singleton = instance;
		
		if(singleton != null)
			return singleton;
		synchronized (mutex) {
			if(instance == null) {
				instance = new ApplicationLogger();
			}
			return instance;
		}
	}
	
//	public void initLogger(String name) {
//		this.logger = Logger.getLogger(name);
//	}
//	
//	public void initLogger(Class<?> clazz) {
//		this.logger = Logger.getLogger(clazz);
//	}

	public void writeInfo(String message) {
		if(this.logger != null) {
			this.logger.info(message);
		}
	}

	public void writeWarning(String message) {
		if(this.logger != null) {
			this.logger.warn(message);
		}		
	}

	public void writeDedug(String message) {
		if(this.logger != null) {
			this.logger.debug(message);
		}		
	} 

	public void writeError(String message) {
		if(this.logger != null) {
			this.logger.error(message);
		}		
	} 
	
	public void writeFatal(String message) {
		if(this.logger != null) {
			this.logger.fatal(message);
		}		
	} 

	public String newlineDevider() {
		return System.lineSeparator();
	}
	
	public void shutdown() {
		LogManager.shutdown();
	}
	
}

package com.tool.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 * Uses to store and get the configurable parameters
 * in Properties java file.  
 * 
 *@author r3c1us0
 */
public class PropertyEditor {
	private File fileHandler;
	
	public PropertyEditor(File file) {
		this.fileHandler = file;
	}
	
	public Properties getProperties() throws IOException {
		Properties properties = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(this.fileHandler);
			properties.load(fis);
		}
		finally {
			if(fis != null) 
				fis.close();
		}
		return properties;
	}
	
	public Set<Object> getPropertyKeySet() throws IOException {
		Properties properties = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(this.fileHandler);
			properties.load(fis);
			return properties.keySet();
		}
		finally {
			if (fis != null)
				fis.close();
		}
	}
	
	public String getProperty(String key) throws IOException {
        Properties properties = new Properties();
        FileInputStream fis = null;
        String result = "";
		try {
			fis = new FileInputStream(this.fileHandler);
			properties.load(fis);
			result = properties.getProperty(key);
		}
		finally {
			if (fis != null)
				fis.close();
		}
        
        return result;
	}
	
	public void writeProperty(String key, String value) throws IOException {
		Properties properties = new Properties();
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			fis = new FileInputStream(this.fileHandler);
			properties.load(fis);
			properties.put(key, value);

			fos = new FileOutputStream(this.fileHandler);
			properties.store(fos, "");
		}
		finally {
			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}			
		}
	}
	
	public boolean removeProperty(String key, String value) throws IOException {
		Properties properties = new Properties();
		FileInputStream fis = null;
		FileOutputStream fos = null;
		boolean result = false;
		
		try {
			fis = new FileInputStream(this.fileHandler);
			properties.load(fis);
			result = properties.remove(key, value);

			fos = new FileOutputStream(this.fileHandler);
			properties.store(fos, "");
		}
		finally {
			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}			
		}
		return result;
	}	
}

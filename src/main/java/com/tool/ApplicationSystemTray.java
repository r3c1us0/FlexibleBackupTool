package com.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/***
 * Uses to collect useful methods that can be uses in separate classes. 
 * 
 * @author r3c1us0
 */

public class ApplicationSystemTray {
	public final static String dirSeparator = "/";
	private static List<String> filesList = new ArrayList<String>();
	protected final static int BUFFER_SIZE = 1024;
	
	public boolean isWindowsOS() {
		boolean result = false;
		String windowsOS = "windows";
		String osName = System.getProperty("os.name");
		
		if( osName.matches("(?i:^" + windowsOS + ".*)")) {
			result = true;
		}
		return result;
	}

	public String normalizePath(String dirPath) {
		String result = "";
		result = dirPath.trim().replaceAll("\\\\", "/");
		result = replaceBackToForwardSlashes(result);
		return (result.endsWith("/")) ? result.substring(0, result.length() -1) : result;
	}
	
	public String replaceBackToForwardSlashes(String dirPath) {
		return dirPath.replaceAll("\\\\", "/");
	}
	
	public String getRootPath() {
		String rootPath = "";
		int upToFolderPath = 0;
		
		try {
			rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		}
		catch(NullPointerException e) {
			try {
				rootPath = new File(".").getCanonicalPath();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		rootPath = replaceBackToForwardSlashes( rootPath );
		if(isWindowsOS()) {
			if(rootPath.startsWith(dirSeparator)) 
				rootPath = rootPath.substring(1);
		}
		
		String[] splited = rootPath.split(dirSeparator);  
		StringBuilder stringBuilder = new StringBuilder();
		
		int counter = 0;
		for(String dir : splited) {
			if(isWindowsOS()) {
				if(counter == splited.length - upToFolderPath) 
					break;
				if(dir.length() > 0) {
					stringBuilder.append(dir);
					stringBuilder.append(dirSeparator);
				}
			}
			counter++;
		}

		if(isWindowsOS()) {
			rootPath = stringBuilder.toString();
		}
		return rootPath.substring(0, rootPath.lastIndexOf(dirSeparator));
	}

	public String getTemporaryPath() {
		String result = System.getProperty("java.io.tmpdir");
		result = normalizePath(result);
		if(result.lastIndexOf("/") == result.length() -1) {
			result = result.substring(0, result.length()-1);
		}
		return result;
	}
	
	public List<String> getDirectoryAndSubdirectoryFilesList(String sourceDir, List<String> fileExtensions) {
		filesList = new ArrayList<String>();
		File file = new File(sourceDir);
		if( (file.exists() && file.canRead()) )
			dirTree(file, fileExtensions, sourceDir);
		
		return filesList;
	}
	
	public List<String> getDirectoryAndSubdirectoryFilesList(String sourceDir) {
		filesList = new ArrayList<String>();
		File file = new File(sourceDir);
		if( (file.exists() && file.canRead()) )
			dirTree(file, sourceDir);
		
		return filesList;
	}
	
	private void dirTree(File dir, List<String> fileExtensions, String sourceDir) {
		File[] subdirs = dir.listFiles();
		
		for(int i = 0; i < subdirs.length; i++) {
			File subdir = subdirs[i];
			if(subdir.isDirectory() && (subdir.exists() && subdir.canRead()) ) {
				dirTree(subdir, fileExtensions, sourceDir);
			}
			else if(subdir.exists()) {
				String ext = subdir.getName().substring(subdir.getName().lastIndexOf('.') +1, subdir.getName().length());
				if(fileExtensions.contains(ext)) {
					String filePath = replaceBackToForwardSlashes( subdir.getAbsolutePath() );
					filesList.add( replaceBackToForwardSlashes(filePath) );
				}
			}
			
		}
	}

	private void dirTree(File dir, String sourceDir) {
		File[] subdirs = dir.listFiles();
		
		for(File subdir : subdirs) {
			if(subdir.isDirectory() && (subdir.exists() && subdir.canRead()) ) {
				dirTree(subdir, sourceDir);
			}
			else if(subdir.exists()) {
				filesList.add( replaceBackToForwardSlashes(subdir.getAbsolutePath()) );
			}
		}
	}	
	
	protected void deleteDirectoryRecursively(String tempFolderPath) {
		File tmpFolderHandler = new File(tempFolderPath);
		if(tmpFolderHandler.exists()) {
			deleteDirectory(tmpFolderHandler);
		}
	}
	
	private void deleteDirectory(File file) {
		for (File subFile : file.listFiles()) {
			if (subFile.isDirectory()) {
				deleteDirectory(subFile);
			} else if(subFile.exists()) {
				subFile.delete();
			}
		}
		file.delete();
	}
	
	protected double calculatePercentage(double obtained, double total) {
        return obtained * 100 / total;
    }	
	
	public String convertMillisToHumanReadable(long timing) {
		String result = "";
		
		result = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timing),
				TimeUnit.MILLISECONDS.toMinutes(timing) % TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(timing) % TimeUnit.MINUTES.toSeconds(1));
		
		return result;
	}
	
}

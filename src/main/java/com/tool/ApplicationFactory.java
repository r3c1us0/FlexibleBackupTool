package com.tool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.PropertyConfigurator;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import com.tool.json.RestoreJSONParser;
import com.tool.log4j.ApplicationLogger;
import com.tool.model.EntryFileModel;
import com.tool.model.RestoreEntryModel;
import com.tool.model.RestoreEntryService;
import com.tool.property.PropertyEditor;
import com.tool.settings.ApplicationSettings;

/**
 * This is Lazy initialization method of Singleton class.
 * Basic work of the application like content initialization,
 * console outputs, and other logical operations  are performed
 * inside this class. 
 * Engineered for the work in Main method of application class.
 * 
 * @author r3c1us0
 */
public class ApplicationFactory extends ApplicationSystemTray {
	private static ApplicationFactory instance = null;
	
	public final static String settingsFileName = "settings.xml";
	public String workspace = "";
	
	// application timing
	private long startApplicationTime = 0;
	private String timeStamp = "";
	
	// application necessary file handler list
	private File settingsFileHandler; // readable mode
	private File log4jPropertyHandler;
	private File logFilenameHandler;
	private File jsonFileHandler;
	private File checksumFileHandler;
	
	// files counters
	private AtomicLong positiveFilesCounter = new AtomicLong(0);
	private AtomicLong negativeFilesCounter = new AtomicLong(0);
	private AtomicLong absoluteFilesCounter = new AtomicLong(0);
	private long totalFilesQuantity = 0;
	
	// log predefined errors
	private static List<String> warningList = new CopyOnWriteArrayList<String>();
	// log end list info 
	private static List<String> informationList = new CopyOnWriteArrayList<String>();
	
	private ApplicationLogger applicationLogger;
	
	private ApplicationFactory() {
		initStartApplicationTime();
		initTimeStamp();
	}
	
	public static ApplicationFactory getInstance() {
		//Double check locking pattern
		if(instance == null) { //Check for the first time
			
			synchronized (ApplicationFactory.class) { //Check for the second time
				
				if(instance == null)
					instance = new ApplicationFactory();
			}
		}
		return instance;
	}
	
	public List<String> getWarningList() {
		return warningList;
	}
	
	public long getStartApplicationTime() {
		return startApplicationTime;
	}

	private void setStartApplicationTime(long startApplicationTime) {
		this.startApplicationTime = startApplicationTime;
	}

	public void initStartApplicationTime() {
		setStartApplicationTime(System.currentTimeMillis());
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}

	private void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	private void initTimeStamp() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmms");
		Date date = new Date();
		setTimeStamp(format.format(date));
	}
	
	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public AtomicLong getPositiveFilesCounter() {
		return positiveFilesCounter;
	}

	public void setPositiveFilesCounter(AtomicLong positiveFilesCounter) {
		this.positiveFilesCounter = positiveFilesCounter;
	}

	public long getAndIncrementPositiveFilesCounter() {
		return this.positiveFilesCounter.getAndIncrement();
	}
	
	public AtomicLong getNegativeFilesCounter() {
		return negativeFilesCounter;
	}

	public void setNegativeFilesCounter(AtomicLong negativeFilesCounter) {
		this.negativeFilesCounter = negativeFilesCounter;
	}

	public long getAndIncrementNegativeFilesCounter() {
		return this.negativeFilesCounter.getAndIncrement();
	}
	
	public long getTotalFilesQuantity() {
		return totalFilesQuantity;
	}

	public void setTotalFilesQuantity(long totalFilesQuantity) {
		this.totalFilesQuantity = totalFilesQuantity;
	}
	
	public AtomicLong getAbsoluteFilesCounter() {
		return absoluteFilesCounter;
	}

	public void setAbsoluteFilesCounter(AtomicLong absoluteFilesCounter) {
		this.absoluteFilesCounter = absoluteFilesCounter;
	}

	public long getAndIncrementAbsoluteFilesCounter() {
		return this.absoluteFilesCounter.getAndIncrement();
	}
	
	/**
	 * 	
	 * SettingsFileHandler functions
	 */
	public File getSettingsFileHandler() {
		return settingsFileHandler;
	}
	
	public void setSettingsFileHandler(File settingsFileHandler) {
		this.settingsFileHandler = settingsFileHandler;
	}
	
	public void initSettingsFileHandler() {
		String settingsFilePath = normalizePath(workspace).concat(dirSeparator).concat(settingsFileName);
		File settingFileHandler = new File(settingsFilePath);
		if(settingFileHandler.exists() && settingFileHandler.canRead()) {
			setSettingsFileHandler(settingFileHandler);
		}
		else {
			if(!settingFileHandler.exists()) {
				warningList.add(String.format("Settings file does not exists by given path '%s'", settingsFilePath));
			}
			else if(!settingFileHandler.canRead()) {
				warningList.add(String.format("Settings file exists with read permission denied '%s'", settingsFilePath));
			}
		}
	}

	/**
	 * 	
	 * Log4jPropertyHandler functions
	 */
	public File getLog4jPropertyHandler() {
		return log4jPropertyHandler;
	}

	public void setLog4jPropertyHandler(File log4jPropertyHandler) {
		this.log4jPropertyHandler = log4jPropertyHandler;
	}

	public void initLog4jPropertyHandler() {
		String filePath = normalizePath(workspace).concat(dirSeparator).concat("log4j.properties");
		File fileHandler = new File(filePath);
		if(fileHandler.exists() && fileHandler.canRead() && fileHandler.canWrite()) {
			setLog4jPropertyHandler(fileHandler);
		}
		else {
			if(!fileHandler.exists()) {
				warningList.add(String.format("Log4jProperty file does not exists by given path '%s'", filePath));
			}
			else if(!fileHandler.canRead()) {
				warningList.add(String.format("Log4jProperty file exists with read permission denied '%s'", filePath));
			}
			else if(!fileHandler.canWrite()) {
				warningList.add(String.format("Log4jProperty file exists with write permission denied '%s'", filePath));
			}
		}
	}
	
	/**
	 * 
	 * LogFilenameHandler
	 */
	public File getLogFilenameHandler() {
		return logFilenameHandler;
	}

	public void setLogFilenameHandler(File logFilenameHandler) {
		this.logFilenameHandler = logFilenameHandler;
	}

	public void initLogFilenameHandler() {
		String logFilePath = "";
		String logDirectoryPath = getWorkspace().concat(dirSeparator).concat("log");
		File logDirectoryHandler = new File(logDirectoryPath);
		
		if(!logDirectoryHandler.exists()) {
			if(logDirectoryHandler.mkdirs()) {
				logFilePath = logDirectoryPath.concat(dirSeparator).concat("flexiblerestback.log");
				File logFileHandler = new File(logFilePath);
				if(!logFileHandler.exists()) {
					try {
						if(logFileHandler.createNewFile()) {
							setLogFilenameHandler(logFileHandler);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						warningList.add(String.format("Application cannot create the file %s", logFilePath));
						warningList.add(String.format("Exception on create %s", e.getMessage()));
					}
				}
			}
		}
		else {
			logFilePath = logDirectoryPath.concat(dirSeparator).concat("flexiblerestback.log");
			File logFileHandler = new File(logFilePath);
			if(!logFileHandler.exists()) {
				try {
					if(logFileHandler.createNewFile()) {
						setLogFilenameHandler(logFileHandler);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					warningList.add(String.format("Application cannot create the file %s", logFilePath));
					warningList.add(String.format("Exception on create %s", e.getMessage()));
				}
			}
			else {
				setLogFilenameHandler(logFileHandler);
			}
		}
	}
	
	public void writeLogFilePathToLog4jPropertyFile() {
		if(getLog4jPropertyHandler() != null) {
			try {
				setPropertyValueByKey(getLog4jPropertyHandler(), "log4j.appender.LogFileAppender.file", 
						getLogFilenameHandler().getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				warningList.add( String.format("Application cannot change property %s in file %s",
						"log4j.appender.LogFileAppender.file",
						getLog4jPropertyHandler().getAbsolutePath()) );
			}
		}
	}

	/**
	 * JsonFileHandler
	 */
	public File getJsonFileHandler() {
		return jsonFileHandler;
	}

	public void setJsonFileHandler(File jsonFileHandler) {
		this.jsonFileHandler = jsonFileHandler;
	}
	
	public void initJsonFileHandler() throws Exception {
		ApplicationSettings settings = new ApplicationSettings(getSettingsFileHandler());
		String filePathStr = getWorkspace();
		String jsonFilename = settings.getRestoreJsnoFilename();
		filePathStr = filePathStr.concat(dirSeparator).concat(jsonFilename);
		
		File file = new File(filePathStr);
		if(!file.exists()) {
			if(file.createNewFile()) {
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write("[]");
				fileWriter.close();
			}
			else {
				throw new Exception(String.format("ApplicationFactory cannot create JSON file ", filePathStr));
			}			
		}
		setJsonFileHandler(file);
	}

	/**
	 * 
	 * ChecksumFileHandler
	 */
	public File getChecksumFileHandler() {
		return checksumFileHandler;
	}

	public void setChecksumFileHandler(File checksumFileHandler) {
		this.checksumFileHandler = checksumFileHandler;
	}

	public void initChecksumFileHandler() throws Exception {
		ApplicationSettings settings = new ApplicationSettings(getSettingsFileHandler());
		String checksumFilename = settings.getChecksumFilename();
		String filePathStr = getWorkspace().concat(dirSeparator).concat(checksumFilename);
		File file = new File(filePathStr);
		if(!file.exists()) {
			if(!file.createNewFile()) {
				throw new IOException(String.format("ApplicationFactory cannot create checksum file ", filePathStr));
			}			
		}
		setChecksumFileHandler(file);
	}
	
	/**
	 * 
	 * ApplicationLogger
	 */
	public ApplicationLogger getApplicationLogger() {
		return applicationLogger;
	}

	public void setApplicationLogger() {
		this.applicationLogger = ApplicationLogger.getInstance();
	}
	
	public void initApplicationLogger() {
		if(getLog4jPropertyHandler() != null) {
			String log4jPropertyFilePath = getWorkspace().concat(dirSeparator).concat("log4j.properties");
			PropertyConfigurator.configure(log4jPropertyFilePath);
			setApplicationLogger();
		}
	}
	
	public void setPropertyValueByKey(File file, String key, String value) throws IOException {
		PropertyEditor propsEditor = new PropertyEditor(file);
		propsEditor.writeProperty(key, value);
	}

	private String convertMessageDigestToString(byte[] chars) {
		return HexBin.encode(chars);
	}	
	
	private String getChecksumFromFile(String sourceFileName, long absoluteFileCounter, long totalFilesQuantity) throws IOException, NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
		FileInputStream fileInputStream = null;
		String result = "";
		File sourceFileHandler = new File(sourceFileName);
		try {
			fileInputStream = new FileInputStream(sourceFileHandler);
			
			byte[] readBytes = new byte[BUFFER_SIZE];
			int nread = 0;
			
			System.out.println(String.format("Get file '%s (%dKB)' for checksum.", sourceFileName, (long)(sourceFileHandler.length()/1024) ));
			long countingReadBytes = 0;
			
			double absoluteFilesPersents = calculatePercentage(absoluteFileCounter, totalFilesQuantity);
			
			long sourceFileLength = sourceFileHandler.length();
			while ((nread = fileInputStream.read(readBytes)) != -1) {
				messageDigest.update(readBytes, 0, nread);
				countingReadBytes += (long) nread;
				
				double readFilePercentage = calculatePercentage(countingReadBytes, sourceFileLength);
				
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("[");
				stringBuffer.append( String.format("%.02f", absoluteFilesPersents) );
				stringBuffer.append("%");
				stringBuffer.append( String.format(" %d/%d", absoluteFileCounter, totalFilesQuantity) );
				stringBuffer.append("]");
				stringBuffer.append("\t");
				stringBuffer.append("checksum file");
				stringBuffer.append(" [");
				stringBuffer.append( String.format("%.02f", readFilePercentage) );
				stringBuffer.append("%]");
				stringBuffer.append( String.format(" - %s (%dKB)", sourceFileName, (sourceFileLength / 1024)) );
				
				System.out.println(stringBuffer.toString());
			}
			byte[] messageDigestResult = messageDigest.digest();
			result = this.convertMessageDigestToString(messageDigestResult);
		}
		finally {
			if(fileInputStream != null) {
				fileInputStream.close();
			}
			messageDigest.reset();
		}
		return result;
	} 
	
	private String getPropertyChecksumByKey(String checksumKey) throws IOException {
		PropertyEditor checksumProperties = null;
		checksumProperties = new PropertyEditor(getChecksumFileHandler());
		return checksumProperties.getProperty(checksumKey);
	}

	private void writeChecksumPropertyValue(String checksumKey, String checksumValue) throws IOException {
		PropertyEditor checksumProperties = new PropertyEditor(getChecksumFileHandler());
		checksumProperties.writeProperty(checksumKey, checksumValue);
	}
	
	
	private File getTemporaryZipFilePath(String tmpFolderPath, String timeStamp, String nodeName) throws IOException {
		String zipFilenamePath = tmpFolderPath.concat(dirSeparator).concat(timeStamp);
		File zipFilenamePathHandler = new File(zipFilenamePath);
		if(!zipFilenamePathHandler.exists()) {
			if(zipFilenamePathHandler.mkdirs()) {
				zipFilenamePath = zipFilenamePath.concat(dirSeparator).concat(nodeName)
						.concat(".zip");
				zipFilenamePathHandler = new File(zipFilenamePath);
				if(!zipFilenamePathHandler.exists()) {
					if(!zipFilenamePathHandler.createNewFile()) {
						throw new IOException(String.format("Method getTemporaryZipFilePath cannot create temporary zip file '%s'. Permission denied.", zipFilenamePath));
					}
				}
			}
			else {
				throw new IOException(String.format("Method getTemporaryZipFilePath cannot create temporary path '%s'. Permission denied.", zipFilenamePath));
			}
		}
		else {
			zipFilenamePath = zipFilenamePath.concat(dirSeparator).concat(nodeName)
					.concat(".zip");
			zipFilenamePathHandler = new File(zipFilenamePath);
			if(!zipFilenamePathHandler.exists()) {
				if(!zipFilenamePathHandler.createNewFile()) {
					throw new IOException(String.format("Method getTemporaryZipFilePath cannot create temporary zip file '%s'. Permission denied.", zipFilenamePath));
				}
			}				
		}
		return zipFilenamePathHandler;
	}

	private Map<String, EntryFileModel> getJsonBackupFilePathAndEntryModelFile(RestoreEntryModel restoreEntryModel,
			EntryFileModel entryFileModel,
			boolean isChecksum, 
			String timeStamp) {
		Map<String, EntryFileModel> result = new HashMap<String, EntryFileModel>();
		
		boolean doBackup = true;
		String checksumKey = entryFileModel.getBackupFilePath();
		checksumKey = checksumKey.substring(restoreEntryModel.getSourcePath().length() +1, 
				entryFileModel.getBackupFilePath().length());
		checksumKey = restoreEntryModel.getNodeName().concat(dirSeparator)
				.concat(checksumKey);
		EntryFileModel resultEntryModelFile = new EntryFileModel();
		resultEntryModelFile.setBackupFilePath(checksumKey);
		resultEntryModelFile.setTimeStamp(Long.parseLong(timeStamp));
		
		if(isChecksum) {
			try {
				String checksumValueFromProperiesFileByKey = getPropertyChecksumByKey(checksumKey);
				String checksumValueFromSourceFileHandler = getChecksumFromFile(entryFileModel.getBackupFilePath(),
						getAbsoluteFilesCounter().get(), getTotalFilesQuantity());
				if(checksumValueFromProperiesFileByKey != null) {
					
					if(!checksumValueFromProperiesFileByKey.equals(checksumValueFromSourceFileHandler)) {
						writeChecksumPropertyValue(checksumKey, checksumValueFromSourceFileHandler);
						// JSON entries definition
						resultEntryModelFile.setChecksum(checksumValueFromSourceFileHandler);
					}
					else {
						doBackup = false;
					}
				}
				else {
					writeChecksumPropertyValue(checksumKey, checksumValueFromSourceFileHandler);
					// JSON entries definition
					resultEntryModelFile.setChecksum(checksumValueFromSourceFileHandler);
					doBackup = true;
				}						
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		if(doBackup) {
			result.put(checksumKey, resultEntryModelFile);
		}
		
		return result;
	}

	private Map<String, List<EntryFileModel>> archiveProcessEntryModelFileList(RestoreEntryModel restoreEntryModel, 
			List<EntryFileModel> jsonEntryModelFileList,
			RestoreEntryService restoreEntryService,
			boolean isChecksum, String timeStamp,
			String tmpFolderPath,  long totalFilesQty) throws Exception {
		
		Map<String, List<EntryFileModel>> result = new HashMap<String, List<EntryFileModel>>();
		
		String resultPath = "";
		
		List<EntryFileModel> entryModelFileList =  restoreEntryModel.getEntryFileList();
		if(entryModelFileList.size() > 0) {
			File zipFilenamePathHandler = getTemporaryZipFilePath(tmpFolderPath, timeStamp, restoreEntryModel.getNodeName());
			if(zipFilenamePathHandler != null) {

				FileOutputStream zipFileOutputStream = null;
				ZipOutputStream zipOutputStream = null;
				
				zipFileOutputStream = new FileOutputStream(zipFilenamePathHandler);
				zipOutputStream = new ZipOutputStream(zipFileOutputStream);
				
				for(EntryFileModel entryFileModel : entryModelFileList) {
					getAndIncrementAbsoluteFilesCounter();
					
					boolean doBackup = true;
					String checksumKey = entryFileModel.getBackupFilePath();
					checksumKey = checksumKey.substring(restoreEntryModel.getSourcePath().length() +1, 
							entryFileModel.getBackupFilePath().length());
					checksumKey = restoreEntryModel.getNodeName().concat(dirSeparator)
							.concat(checksumKey);
					
					FileInputStream zipFileInputStream = null;
					ZipEntry zipEntry = null;

					// JSON entries definition
					EntryFileModel jsonEntryModelFile = restoreEntryService.getEntryModelFileByBackupFilePath(jsonEntryModelFileList, checksumKey);
					EntryFileModel jsonEntryModelFileRemover = null;
					if(jsonEntryModelFile == null) {
						jsonEntryModelFile = new EntryFileModel();
						jsonEntryModelFile.setBackupFilePath(checksumKey);
					}
					else {
						jsonEntryModelFileRemover = jsonEntryModelFile;
					}
					
					if(isChecksum) {
						String checksumValueFromProperiesFileByKey = getPropertyChecksumByKey(checksumKey);
						String checksumValueFromSourceFileHandler = getChecksumFromFile(entryFileModel.getBackupFilePath(),
								getAbsoluteFilesCounter().get(), getTotalFilesQuantity());
						if(checksumValueFromProperiesFileByKey != null) {
							
							if(!checksumValueFromProperiesFileByKey.equals(checksumValueFromSourceFileHandler)) {
								writeChecksumPropertyValue(checksumKey, checksumValueFromSourceFileHandler);
								// JSON entries definition
								jsonEntryModelFile.setChecksum(checksumValueFromSourceFileHandler);
							}
							else {
								doBackup = false;
							}
						}
						else {
							writeChecksumPropertyValue(checksumKey, checksumValueFromSourceFileHandler);
							// JSON entries definition
							jsonEntryModelFile.setChecksum(checksumValueFromSourceFileHandler);
							doBackup = true;
						}						
					}
					
					if(doBackup) {
						File sourceBackupFileHandler = new File(entryFileModel.getBackupFilePath());
						
						zipFileInputStream = new FileInputStream(sourceBackupFileHandler);
						
						try {
							
							byte[] bytes = new byte[BUFFER_SIZE];
							int nread = 0;
							long countingReadBytes = 0;
							long sourceFileLength =  sourceBackupFileHandler.length();
							Path sourceBackupFileHandlerPath = sourceBackupFileHandler.toPath();
							BasicFileAttributes fileAtrtibutes = Files.readAttributes(sourceBackupFileHandlerPath, BasicFileAttributes.class);
							
							zipEntry = new ZipEntry(checksumKey);
							zipEntry.setSize(sourceFileLength);
							zipEntry.setLastModifiedTime(fileAtrtibutes.lastModifiedTime());
							zipEntry.setCreationTime(fileAtrtibutes.creationTime());
							zipOutputStream.putNextEntry(zipEntry);
							
							double absoluteFilesPersents = calculatePercentage(getAbsoluteFilesCounter().doubleValue(), getTotalFilesQuantity());
							
							while((nread = zipFileInputStream.read(bytes)) >= 0) {
								zipOutputStream.write(bytes, 0, nread);
								countingReadBytes += (long) nread;
								
								double readFilePercentage = calculatePercentage(countingReadBytes, sourceFileLength);
								
								StringBuffer stringBuffer = new StringBuffer();
								stringBuffer.append("[");
								stringBuffer.append( String.format("%.02f", absoluteFilesPersents) );
								stringBuffer.append("%");
								stringBuffer.append( String.format(" %d/%d", getAbsoluteFilesCounter().get(), getTotalFilesQuantity()) );
								stringBuffer.append("]");
								stringBuffer.append("\t");
								stringBuffer.append("archiving file");
								stringBuffer.append(" [");
								stringBuffer.append( String.format("%.02f", readFilePercentage) );
								stringBuffer.append("%]");
								stringBuffer.append( String.format(" - %s (%dKB)", sourceBackupFileHandler.getAbsolutePath(), (sourceFileLength / 1024)) );
								
								System.out.println(stringBuffer.toString());
							}
						}
						finally {
							if(zipFileInputStream != null)
								zipFileInputStream.close();
							getAndIncrementPositiveFilesCounter();
							resultPath = zipFilenamePathHandler.getAbsolutePath();
							
							// JSON entries definition
							jsonEntryModelFile.setTimeStamp(Long.parseLong(timeStamp));
							if(jsonEntryModelFileRemover != null) {
								jsonEntryModelFileList.remove(jsonEntryModelFileRemover);
							}
							jsonEntryModelFileList.add(jsonEntryModelFile);
							
							StringBuffer stringBuffer = new StringBuffer();
							stringBuffer.append("[archive backup] ");
							stringBuffer.append( String.format("%s", timeStamp));
							stringBuffer.append( String.format("/%s.zip -> ", restoreEntryModel.getNodeName()));
							stringBuffer.append( String.format("%s", checksumKey));
							
							getApplicationLogger().writeInfo(stringBuffer.toString());
						}
					}
					else {
						getAndIncrementNegativeFilesCounter();
					}
				}
				
				if(zipOutputStream != null) 
					zipOutputStream.close();
				if(zipFileOutputStream != null)
					zipFileOutputStream.close();

				// JSON entries definitions to return
				if(!resultPath.isEmpty()) {
					result.put(resultPath, jsonEntryModelFileList);
				}
			}
		}
		
		return result;
	}
	
	private void extractArchive(String zipFilePath, String restorePath, List<String> targetFileList) throws Exception {
		byte[] buffer = new byte[1024];
		
		try {
			FileInputStream fis = new FileInputStream(zipFilePath);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ZipInputStream stream = new ZipInputStream(bis);
			
			ZipEntry entry;
			
			while ((entry = stream.getNextEntry()) != null) {

				if(targetFileList.contains(entry.getName())) {
					
					StringBuffer fileToRestorePath = new StringBuffer();
					fileToRestorePath.append(restorePath);
					fileToRestorePath.append(dirSeparator);
					fileToRestorePath.append( entry.getName().substring(0, entry.getName().lastIndexOf(dirSeparator)) );
					
					File restoreDirHandler = new File(fileToRestorePath.toString());
					if(!restoreDirHandler.exists()) {
						if(!restoreDirHandler.mkdirs()) {
							throw new Exception("extractArchive() cannot create path '" + fileToRestorePath.toString() + "'");
						}
						else {
							System.out.println("extractArchive() create path '" + fileToRestorePath.toString() + "'");
						}
					}
					
					fileToRestorePath.append(dirSeparator);
					fileToRestorePath.append( entry.getName().substring(entry.getName().lastIndexOf(dirSeparator) +1, entry.getName().length()) );
					
					restoreDirHandler = new File(fileToRestorePath.toString());
					// rewrite existing file
					if(restoreDirHandler.exists()) {
						restoreDirHandler.delete();
					}
					if(!restoreDirHandler.createNewFile()) {
						throw new Exception("extractArchive() cannot create new file '" + fileToRestorePath.toString() + "'");
					}
					else {
						restoreDirHandler.setLastModified(entry.getTime());
					}
					
					FileOutputStream fos = new FileOutputStream(restoreDirHandler);
					BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);
					
					double commonFilesPercentage = calculatePercentage(getAndIncrementAbsoluteFilesCounter(), getTotalFilesQuantity());
					
					int len;
					while ((len = stream.read(buffer)) > 0) {
						bos.write(buffer, 0, len);
						
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append("[");
						stringBuffer.append(String.format("%.02f", commonFilesPercentage));
						stringBuffer.append("%]");
						stringBuffer.append("\t");
						stringBuffer.append(String.format("extracting "));
						stringBuffer.append(String.format(" - %s%s%s", zipFilePath, dirSeparator, 
								entry.getName().substring(entry.getName().indexOf(dirSeparator) +1, entry.getName().length())));
						
						System.out.println(stringBuffer.toString());
						
					}
					bos.close();
					fos.close();
					
					StringBuffer logOutput = new StringBuffer();
					logOutput.append("[restore] ");
					logOutput.append(zipFilePath);
					logOutput.append(System.lineSeparator());
					logOutput.append(String.format(" -> %s", fileToRestorePath.toString()));
					getApplicationLogger().writeInfo(logOutput.toString());
				}
			}
			
			stream.close();
			bis.close();
			fis.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private List<String> getTimestampBackupPathList(String timeStapm) throws Exception {
		ApplicationSettings applicationSettings = new ApplicationSettings(getSettingsFileHandler());
		List<String> backupPathList = applicationSettings.getBackupDirections();
		
		for(int i = 0; i < backupPathList.size(); i++) {
			StringBuilder backupPath = new StringBuilder(normalizePath(backupPathList.get(i)));
			backupPath.append(dirSeparator);
			backupPath.append(timeStapm);
			
			File backupPathHandler = new File(backupPath.toString());
			if(!backupPathHandler.exists()) {
				if(!backupPathHandler.mkdirs()) {
					getApplicationLogger().writeError(String.format("Application cannot create backup path '%s'", backupPath.toString()));
					backupPathList.remove(i);
				}
				else {
					backupPathList.set(i, backupPath.toString());
				}
			}
		}
		
		return backupPathList;
	}
	
	private Runnable copyFileTask(final String fromPath, final String toPath) {
		return( new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Path to = Paths.get(toPath);
					Path from = Paths.get(fromPath);
					Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		});
	}

	private Runnable copyFileTaskAndLogOutput(final String absolutePathFrom, final String absolutePathTo,
			final String activityMsg) {
		return( new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					boolean doBackup = true;
					
					synchronized (this.getClass()) {
						String pathToFolder = absolutePathTo.substring(0, absolutePathTo.lastIndexOf(dirSeparator));
						File pathToFolderHandler = new File(pathToFolder);
						if(!pathToFolderHandler.exists()) {
							if(!pathToFolderHandler.mkdirs()) {
								doBackup = false;
								getApplicationLogger().writeError(String.format("Cannot create folder '%s'", pathToFolderHandler.getAbsolutePath()));
							}
						}						
					}
					
					if(doBackup) {
						Path to = Paths.get(absolutePathTo);
						Path from = Paths.get(absolutePathFrom);
						Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
						
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append( "[" + activityMsg + "] ");
						stringBuffer.append( String.format("%s", absolutePathFrom));
						stringBuffer.append(System.lineSeparator());
						stringBuffer.append( String.format(" -> "));
						stringBuffer.append( String.format("%s", absolutePathTo));
						
						getApplicationLogger().writeInfo(stringBuffer.toString());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		});
	}

	public void startBackupProcesses() {
		ApplicationSettings settings = new ApplicationSettings(getSettingsFileHandler());
		RestoreEntryService restoreEntryService = new RestoreEntryService(settings);
		try {
			List<String> backupPathList = getTimestampBackupPathList(getTimeStamp());
			if(backupPathList.size() <= 0) {
				getApplicationLogger().writeWarning("Application does not have backup destination.");
				return;
			}
			
			/// init restoreJSON
			try {
				initJsonFileHandler();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				getApplicationLogger().writeWarning(e.getMessage());
				return;
			}
			
			// init checksum properties file
			try {
				initChecksumFileHandler();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				getApplicationLogger().writeWarning(e.getMessage());
				return;
			}
			
			String tempFolder = getTemporaryPath();
			
			List<Runnable> filesTaskList = new ArrayList<Runnable>();
			
			List<RestoreEntryModel> restoreEntryModelList =  restoreEntryService.getBackupTargetList(getTimeStamp());
			
			long totalEntryModelListFilesQty = restoreEntryService.getRestoreEntryModelListFilesQuantity(restoreEntryModelList);
			setTotalFilesQuantity(totalEntryModelListFilesQty);
			
			// get JSON restoreEntryModelList
			RestoreJSONParser jsonParser = new RestoreJSONParser(getJsonFileHandler());
			List<RestoreEntryModel> jsonProcessEntryModelList = null;
			
			jsonProcessEntryModelList = jsonParser.readRestoreEntryModelListFromJSON(); 

			boolean doDeleteTemporaryFolder = false;
			
			for(RestoreEntryModel restoreEntryModel : restoreEntryModelList) {
				getApplicationLogger().writeInfo( String.format("backup from the %s", restoreEntryModel.getSourcePath()) );
				getApplicationLogger().writeInfo( String.format("%s", restoreEntryModel.toString()) );
				
				// JSON entries definition
				RestoreEntryModel jsonProcessEntryModel = null;
				// JSON details entry files list
				List<EntryFileModel> jsonEntryModelFileList = new ArrayList<EntryFileModel>();
				
				// JSON entries definition
				if(jsonProcessEntryModelList.size() > 0) {
					jsonProcessEntryModel = restoreEntryService.getRestoreEntryModelByNodeNameAndZipType(jsonProcessEntryModelList, 
							restoreEntryModel.getNodeName(), restoreEntryModel.getisZip());
					if(jsonProcessEntryModel == null) {
						
						jsonProcessEntryModel = new RestoreEntryModel(restoreEntryModel.getSourcePath(), 
								restoreEntryModel.getNodeName(),
								restoreEntryModel.getisZip());						
					}
					else {
						jsonProcessEntryModelList.remove(jsonProcessEntryModel);
						jsonProcessEntryModel.setNodeName(restoreEntryModel.getNodeName());
						jsonProcessEntryModel.setisZip(restoreEntryModel.getisZip());
						jsonProcessEntryModel.setSourcePath(restoreEntryModel.getSourcePath());
					}
					
					jsonEntryModelFileList = jsonProcessEntryModel.getEntryFileList();
				}
				else {
					jsonProcessEntryModel = new RestoreEntryModel(restoreEntryModel.getSourcePath(), 
							restoreEntryModel.getNodeName(), 
							restoreEntryModel.getisZip());
				}
				
				if(restoreEntryModel.getisZip()) {
					try {
						doDeleteTemporaryFolder = true;
						
						Map<String, List<EntryFileModel>> archiveProcessResult = archiveProcessEntryModelFileList(
								restoreEntryModel, jsonEntryModelFileList, restoreEntryService,
								settings.isChecksum(), timeStamp, tempFolder, totalEntryModelListFilesQty);
						
						if(!archiveProcessResult.isEmpty()) {
							
							String zipTemporaryFilePath = "";
							List<EntryFileModel> zipJSONEntryModelFileList = new ArrayList<EntryFileModel>();
							
							// get Temporary Zip File Absolute Path from archiveProcessResult Map Key							
							for(Map.Entry<String, List<EntryFileModel>> entry : archiveProcessResult.entrySet()) {
								zipTemporaryFilePath = entry.getKey();
								zipJSONEntryModelFileList.addAll(entry.getValue());
							}
							// JSON entries definition
							jsonProcessEntryModel.setEntryFileList(zipJSONEntryModelFileList);
							jsonProcessEntryModel.setNodeName(restoreEntryModel.getNodeName());
							jsonProcessEntryModel.setSourcePath(restoreEntryModel.getSourcePath());
							jsonProcessEntryModel.setisZip(restoreEntryModel.getisZip());
							
							for(String backupDirectionPath : backupPathList) {
								
								String removablePath = tempFolder.concat(dirSeparator)
										.concat(getTimeStamp());
								String backupTail = zipTemporaryFilePath.substring(removablePath.length() + 1, zipTemporaryFilePath.length());
								String backupDirectionPathTo = backupDirectionPath.concat(dirSeparator)
										.concat(backupTail);
								
								informationList.add(String.format("backup folder path is %s", backupDirectionPathTo));
								filesTaskList.add(copyFileTask(zipTemporaryFilePath, backupDirectionPathTo));
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						getApplicationLogger().writeWarning(e.getMessage());
						return;
					} 
				}
				else { // NON ZIP
					boolean rewriteJsonProcessEntryModel = false;
					for(EntryFileModel entryFileModel : restoreEntryModel.getEntryFileList()) {
						getAndIncrementAbsoluteFilesCounter();
						
						// JSON entries definition
						// perform JSON restore entries
						Map<String, EntryFileModel> jsonEntryModelFileResult = getJsonBackupFilePathAndEntryModelFile(restoreEntryModel,
								entryFileModel,
								settings.isChecksum(), 
								timeStamp);
						if(jsonEntryModelFileResult.size() > 0) {
							String jsonBackupPathKey = "";
							EntryFileModel jsonChangerEntryModelFile = null;
							for(Map.Entry<String, EntryFileModel> entry : jsonEntryModelFileResult.entrySet()) {
								jsonBackupPathKey = entry.getKey();
								jsonChangerEntryModelFile = entry.getValue();
							}
							
							EntryFileModel localJsonEntryModelFile = restoreEntryService.getEntryModelFileByBackupFilePath(jsonEntryModelFileList, jsonBackupPathKey);
							if(localJsonEntryModelFile != null) {
								jsonEntryModelFileList.remove(localJsonEntryModelFile);								
							}
							jsonEntryModelFileList.add(jsonChangerEntryModelFile);
							
							// perform task path targets
							String backupPathTail = entryFileModel.getBackupFilePath();
							backupPathTail = backupPathTail.substring(restoreEntryModel.getSourcePath().length() +1,
									backupPathTail.length());
							
							for(String backupPath : backupPathList) {
								String backupPathTo = backupPath.concat(dirSeparator).concat(restoreEntryModel.getNodeName())
										.concat(dirSeparator)
										.concat(backupPathTail);
								filesTaskList.add( copyFileTaskAndLogOutput(entryFileModel.getBackupFilePath(), backupPathTo, 
										"backup") );
							}
							getAndIncrementPositiveFilesCounter();
							rewriteJsonProcessEntryModel = true;
						}
						else 
							getAndIncrementNegativeFilesCounter();
					}
					
					// JSON entries definition
					if(rewriteJsonProcessEntryModel) {
						jsonProcessEntryModel.setEntryFileList(jsonEntryModelFileList);
						jsonProcessEntryModel.setNodeName(restoreEntryModel.getNodeName());
						jsonProcessEntryModel.setSourcePath(restoreEntryModel.getSourcePath());
						jsonProcessEntryModel.setisZip(restoreEntryModel.getisZip());						
					}
				}
				
				// JSON entries definition | back to work after NON-ZIP will be done
				jsonProcessEntryModelList.add(jsonProcessEntryModel);
			}
			
			if(filesTaskList.size() > 0) {
				
				jsonParser.writeRestoreEntryModelListToJSON(jsonProcessEntryModelList);
				
				ExecutorService threads = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				try {
					final CountDownLatch latch = new CountDownLatch(filesTaskList.size());
					for(final Runnable task : filesTaskList) {
						threads.execute(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									task.run();
								}
								finally {
									latch.countDown();
								}
							}
						});
					}
					latch.await();
				}
				finally {
					threads.shutdown();
				}
				
				// delete temporary folder
				if(doDeleteTemporaryFolder)
					deleteDirectoryRecursively(tempFolder.concat(dirSeparator).concat(getTimeStamp()));
			}
			else {
				// delete empty backup directions
				for(String backupPath : backupPathList) {
					deleteDirectoryRecursively(backupPath);
				}
				// delete temporary folder
				if(doDeleteTemporaryFolder)
					deleteDirectoryRecursively(tempFolder.concat(dirSeparator).concat(getTimeStamp()));				
			}
			// Log messages
			if(informationList.size() > 0) {
				for(String information : informationList) {
					getApplicationLogger().writeInfo(information);
				}
			}
			// Log message
			getApplicationLogger().writeInfo(String.format("Copied files quantity %d/%d.", getPositiveFilesCounter().get(), 
					getTotalFilesQuantity()));
			getApplicationLogger().writeInfo(String.format("Skipped files quantity %d/%d.", getNegativeFilesCounter().get(), 
					getTotalFilesQuantity()));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			getApplicationLogger().writeWarning("ApplicationFactory.startBackupProcesses() " + e.getMessage());
			e.printStackTrace();
			return;
		} 
	}
	
	public void startRestoreProcess() {
		ApplicationSettings settings = new ApplicationSettings(getSettingsFileHandler());
		RestoreEntryService restoreEntryService = new RestoreEntryService(settings);
		
		/// init restoreJSON
		try {
			initJsonFileHandler();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			getApplicationLogger().writeWarning(e.getMessage());
			return;
		}
		
		RestoreJSONParser jsonParser = new RestoreJSONParser(getJsonFileHandler());
		List<RestoreEntryModel> restoreEntryModelList = jsonParser.readRestoreEntryModelListFromJSON();
		
		long totalFilesQty = restoreEntryService.getRestoreEntryModelListFilesQuantity(restoreEntryModelList);
		setTotalFilesQuantity(totalFilesQty);
		
		Map<String, Long> lowestMapKeys = restoreEntryService.sortRestoreEntryModelFileListByLowestTimeStamp(restoreEntryModelList);
		
		String[] sortedLowestMapKeys = restoreEntryService.getSortedRestoreEntryModelNodenameByLowestTimeStamp(lowestMapKeys);
		
		Map<String, List<EntryFileModel>> ascProcessEntryModelFiles = restoreEntryService.getRestoreEntryModelFileListByTimestampASC(
				restoreEntryModelList);
		
		String restoreDirectoryPath = "";
		try {
			// create restore directory
			restoreDirectoryPath = normalizePath( settings.getRestoreDirectory() );
			restoreDirectoryPath = restoreDirectoryPath.concat(dirSeparator).concat(getTimeStamp());
			File restoreDirectoryHandler = new File(restoreDirectoryPath);
			if(!restoreDirectoryHandler.exists()) {
				if(!restoreDirectoryHandler.mkdirs()) {
					String message = String.format("Application cannot create directory by path '%s'. Write permission denied.", restoreDirectoryPath);
					getApplicationLogger().writeWarning(message);
					return;
				}
			}
			
			String rootBackupPath = settings.getBackupDirections().get(0);
			rootBackupPath = normalizePath(rootBackupPath);
			
			File rootBackupPathHandler = new File(rootBackupPath);
			if(rootBackupPathHandler.exists() && rootBackupPathHandler.isDirectory()) {
				
				for(String lowestMapKey : sortedLowestMapKeys) {
					
					List<EntryFileModel> entryModelFileList = ascProcessEntryModelFiles.get(lowestMapKey);
					List<String> restoreFromPathList = new ArrayList<String>();
					Map<String, ArrayList<String>> splitedFileList = restoreEntryService.getFilesSplitedByTimestampList(entryModelFileList);
					
					String catalogValues = lowestMapKey;
					String[]  catalogValue = catalogValues.split(":");
					String nodeName = catalogValue[0];
					boolean isZip = Boolean.valueOf(catalogValue[1].toLowerCase()).booleanValue();

					// Get all keys
					Set<String> splitedFileListKeySet = splitedFileList.keySet();
					for(String timeStampKey : splitedFileListKeySet) {
						
						StringBuffer restoreFromPath = new StringBuffer();
						restoreFromPath.append(rootBackupPath);
						restoreFromPath.append(dirSeparator);
						restoreFromPath.append(timeStampKey);
						restoreFromPath.append(dirSeparator);
						restoreFromPath.append(nodeName);
						if(isZip)
							restoreFromPath.append(".zip");
						
						File restoreFromPathHandler = new File(restoreFromPath.toString());
						if(restoreFromPathHandler.exists()) {
							System.out.println("\t restoreFromPathHandler exists: " + restoreFromPathHandler.getAbsolutePath());
							restoreFromPathList.add(restoreFromPath.toString());
						}
						else {
							String message = String.format("Backup path does not found by path '%s'", restoreFromPathHandler.getAbsolutePath());
							getApplicationLogger().writeWarning(message);
						}
					}
					
					if(restoreFromPathList.size() > 0) {
						for(String restoreFromPath : restoreFromPathList) {
							File restoreFromPathHandler = new File(restoreFromPath);
							if(restoreFromPathHandler.isFile() && restoreFromPathHandler.canRead()) {
								for(String timeStampKey : splitedFileListKeySet) {
									if(restoreFromPath.contains(timeStampKey)) {
										extractArchive(restoreFromPath, restoreDirectoryPath, splitedFileList.get(timeStampKey));
									}
								}
							}
							else if(restoreFromPathHandler.isDirectory() && restoreFromPathHandler.canRead()) {
								List<Runnable> filesTaskList = new ArrayList<Runnable>();
								
								// restore files to restoreDirectoryPath
								for(String timeStampKey : splitedFileListKeySet) {
									if(restoreFromPath.contains(timeStampKey)) {
										List<String> filesList = splitedFileList.get(timeStampKey);
										for(String restoreFrom : filesList) {
											
											StringBuffer pathTO = new StringBuffer();
											pathTO.append(restoreDirectoryPath);
											pathTO.append(dirSeparator);
											pathTO.append(restoreFrom);
											
											StringBuffer pathFROM = new StringBuffer();
											pathFROM.append( restoreFromPath.substring(0, restoreFromPath.lastIndexOf(dirSeparator)) );
											pathFROM.append(dirSeparator);
											pathFROM.append(restoreFrom);
											
											filesTaskList.add( copyFileTaskAndLogOutput(pathFROM.toString(),
													pathTO.toString(), "restore" ) );
										}
									}
									else {
										continue;
									}
								}
								
								if(filesTaskList.size() > 0) {
									ExecutorService threads = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
									try {
										final CountDownLatch latch = new CountDownLatch(filesTaskList.size());
										for(final Runnable task : filesTaskList) {
											threads.execute(new Runnable() {
												@Override
												public void run() {
													// TODO Auto-generated method stub
													try {
														task.run();
													}
													finally {
														latch.countDown();
														long incrementedAbsoluteFilesCounter = getAndIncrementAbsoluteFilesCounter();
														double percentageDone = calculatePercentage(incrementedAbsoluteFilesCounter, getTotalFilesQuantity());
														String percentageDoneMsg = String.format("%.02f", percentageDone);
														System.out.println("[" + String.valueOf(percentageDoneMsg) +"%]");
													}
												}
											});
										}
										latch.await();
									}
									finally {
										threads.shutdown();
									}
								}
								
							}
						}
					}
				}
				
				// Log message
				getApplicationLogger().writeInfo(String.format("Restored files quantity %d/%d.", getAbsoluteFilesCounter().get(), 
						getTotalFilesQuantity()));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			getApplicationLogger().writeError(e.getMessage());
			return;
		}
	} 

	public void displayConsoleManual() {
		StringBuffer terminalHelpMsg = new StringBuffer();
		terminalHelpMsg.append("NAME\r\n");
		terminalHelpMsg.append("\tFlexible Backup Tool\r\n");
		terminalHelpMsg.append("SYNOPSIS\r\n");
		terminalHelpMsg.append("\t$java -jar FlexibleBackupTool-####.jar [ OPTIONS ]\r\n");
		terminalHelpMsg.append("DESCRIPTION\r\n");
		terminalHelpMsg.append("\tFlexible Backup tool is a is a java console application for directory list backup.\r\n");
		terminalHelpMsg.append("\tApplication does not work without the path to folder of required files.\r\n");
		terminalHelpMsg.append("\t\tRequired files:\r\n");
		terminalHelpMsg.append("\t\t\tsettings.xml;\r\n");
		terminalHelpMsg.append("\t\t\tlog4j.properties;\r\n");
		terminalHelpMsg.append("\t\tOther files such as 'checksum.properties' and 'restoredat.json' will be created on a first running.\r\n");
		terminalHelpMsg.append("OPTIONS\r\n");
		terminalHelpMsg.append("\tworkspace=/path/to/read_write/folder - this folder must have required settings files\r\n");
		terminalHelpMsg.append("\trestore - a key to start restoring files from list\r\n");
		terminalHelpMsg.append("EXAMPLES\r\n");
		terminalHelpMsg.append("\tStart backup\r\n");
		terminalHelpMsg.append("\t\t$java -jar FlexibleBackupTool-####.jar workspace=/path/to/read_write/folder\r\n");
		terminalHelpMsg.append("\tRestore backup\r\n");
		terminalHelpMsg.append("\t\t$java -jar FlexibleBackupTool-####.jar workspace=/path/to/read_write/folder restore\r\n");
		terminalHelpMsg.append("\r\nFor more details github link - https://github.com/r3c1us0/FlexibleBackupTool");
		terminalHelpMsg.append("\r\n");
		System.out.println(terminalHelpMsg.toString());		
	}
}

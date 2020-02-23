package com.tool.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.tool.ApplicationSystemTray;
import com.tool.settings.ApplicationSettings;

/***
 * This is a class service.
 * Constructed to perform operations on entries data.
 * 
 * @author r3c1us0
 *
 */

public class RestoreEntryService extends ApplicationSystemTray {
	private ApplicationSettings applicationSettings;
	
	public RestoreEntryService(ApplicationSettings applicationSettings) {
		this.applicationSettings = applicationSettings;
	}
	
	public long getRestoreEntryModelListFilesQuantity(List<RestoreEntryModel> restoreEntryModelList) {
		long counter = 0;
		
		for(RestoreEntryModel restoreEntryModel : restoreEntryModelList) {
			counter += (long)restoreEntryModel.getEntryFileList().size();
		}
		return counter;
	}
	
	public List<RestoreEntryModel> getBackupTargetList(String timeStamp) throws Exception {
		
		List<RestoreEntryModel> result = new ArrayList<RestoreEntryModel>();
		
		int useArchive = 0; // applicationSettings.getisZip();
		boolean useUniqueExtensions = applicationSettings.isFilesExtentionsUses();
		
		List<String> backupSourceList = applicationSettings.getSourceFolders();
		if(backupSourceList.size() > 0) {
			
			for(String backupSource : backupSourceList) {
				backupSource = normalizePath(backupSource);
				String nodeName = backupSource.substring(backupSource.lastIndexOf(dirSeparator) + 1, backupSource.length());
				nodeName = nodeName.replaceAll("\\s+", "_");
				
				useArchive = applicationSettings.getSourceFoldersArchiveActionByPath(backupSource);
				
				if(useArchive == -1) {
					useArchive = 0;
				}
				
				boolean booleanUseArchive = (useArchive > 0) ? true : false; 
				
				RestoreEntryModel restoreEntryModel = new RestoreEntryModel(backupSource, nodeName, booleanUseArchive);
				
				List<String> sourceFilesList = new ArrayList<String>();
				if(useUniqueExtensions) {
					sourceFilesList = getDirectoryAndSubdirectoryFilesList(backupSource, applicationSettings.getBackupFileExtensions());
				}
				else {
					sourceFilesList = getDirectoryAndSubdirectoryFilesList(backupSource);
				}
				
				// skip empty model directory 
				if(sourceFilesList.size() == 0) {
					continue;
				}
				
				List<EntryFileModel> entryModelFileList = new ArrayList<>(); 
				
				for(String sourceFile : sourceFilesList) {
					EntryFileModel entryFileModel = new EntryFileModel();
					entryFileModel.setBackupFilePath(normalizePath(sourceFile));
					entryFileModel.setChecksum("");
					entryFileModel.setTimeStamp(Long.parseLong(timeStamp));
					
					entryModelFileList.add(entryFileModel);
				}
				restoreEntryModel.setEntryFileList(entryModelFileList);
				
				result.add(restoreEntryModel);
			}
		}
		
		return result;
	}
	
	public String[] getSortedRestoreEntryModelNodenameByLowestTimeStamp(Map<String, Long> lowestMapKeys) {
		String[] result = null;
		
		if(lowestMapKeys.size() > 0) {
			result = new String[lowestMapKeys.size()];
			// Create a list from elements of HashMap
			List<Map.Entry<String, Long>> entriesList = new LinkedList<Map.Entry<String, Long> >(lowestMapKeys.entrySet());
			
			// Sort the list
			Collections.sort(entriesList, new Comparator<Map.Entry<String, Long> >() {

				@Override
				public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
					// TODO Auto-generated method stub
					return (o1.getValue()).compareTo(o2.getValue());
				}
				
			});
			
			// put map keys to String array by sorted 
			result = new String[entriesList.size()];
			for(int i = 0; i < entriesList.size(); i++) {
				result[i] = entriesList.get(i).getKey();
			}
		}
		
		return result;
	}
	
	public Map<String, Long> sortRestoreEntryModelFileListByLowestTimeStamp(List<RestoreEntryModel> restoreEntryModelList) {
		Map<String, Long> result = new HashMap<String, Long>();
		
		for(RestoreEntryModel restoreEntryModel : restoreEntryModelList) {
			String modelName = restoreEntryModel.getNodeName().concat(":")
					.concat(String.valueOf(restoreEntryModel.getisZip()).toLowerCase());
			
			List<EntryFileModel> entryFileModelList = restoreEntryModel.getEntryFileList();
			
			if(entryFileModelList.size() > 0) {
				Collections.sort(entryFileModelList);
				result.put(modelName, Long.valueOf( entryFileModelList.get(0).getTimeStamp() ) );
			}
		}
		
		return result;
	}
	
	public Map<String, List<EntryFileModel>> getRestoreEntryModelFileListByTimestampASC(List<RestoreEntryModel> restoreEntryModelList) {
		Map<String, List<EntryFileModel>> result = new HashMap<String, List<EntryFileModel>>();
		for(RestoreEntryModel restoreEntryModel : restoreEntryModelList) {
			String modelName = restoreEntryModel.getNodeName().concat(":")
					.concat(String.valueOf(restoreEntryModel.getisZip()).toLowerCase());
			
			List<EntryFileModel> entryFileModelList = restoreEntryModel.getEntryFileList();
			if(entryFileModelList.size() > 0) {
				Collections.sort(entryFileModelList);
				result.put(modelName, entryFileModelList);
			}
		}		
		
		return result;
	}
	
	public RestoreEntryModel getRestoreEntryModelByNodeName(List<RestoreEntryModel> restoreEntryModelList, String nodeName) {
		RestoreEntryModel result = null;
		for(int i = 0; i < restoreEntryModelList.size(); i++) {
			RestoreEntryModel restoreEntryModel = restoreEntryModelList.get(i);
			if(restoreEntryModel.getNodeName().equals(nodeName)) {
				result = restoreEntryModel;
				break;
			}
		}
		return result;
	}
	
	public RestoreEntryModel getRestoreEntryModelByNodeNameAndZipType(List<RestoreEntryModel> restoreEntryModelList,
			String nodeName,
			boolean isZip) {
		RestoreEntryModel result = null;
		for(int i = 0; i < restoreEntryModelList.size(); i++) {
			RestoreEntryModel restoreEntryModel = restoreEntryModelList.get(i);
			boolean entryZip = restoreEntryModel.getisZip();
			if(restoreEntryModel.getNodeName().equals(nodeName) && isZip == entryZip) {
				result = restoreEntryModel;
				break;
			}
		}
		return result;
	}
	
	
	public EntryFileModel getEntryModelFileByBackupFilePath(List<EntryFileModel> entryModelFileList, String filePath) {
		EntryFileModel result = null;
		
		Iterator<EntryFileModel> iterator = entryModelFileList.iterator();
		while(iterator.hasNext()) {
			EntryFileModel entryFileModel = iterator.next();
			if(entryFileModel.getBackupFilePath().equals(filePath)) {
				result = entryFileModel;
				break;
			}
		}
		return result;
	}
	
	public Map<String, ArrayList<String>> getFilesSplitedByTimestampList(List<EntryFileModel> entryModelFileList) {
		Map<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		
		if(entryModelFileList.size() <= 0)
			return result;
		
		// sort ascending keys 
		Collections.sort(entryModelFileList);
		// unique array
		LinkedHashSet<EntryFileModel> lhSetNumbers = new LinkedHashSet<EntryFileModel>(entryModelFileList);
		
		long timeStampChanger = 0;
		if(lhSetNumbers.size() > 0) {
			// collecting results
//			for(EntryFileModel resultKey : lhSetNumbers) {
//				System.out.println("\t\tresult key: " + resultKey.getTimeStamp());
//			}
			
			for(EntryFileModel entryFileModel : entryModelFileList) {
				if(timeStampChanger == 0) {
					timeStampChanger = entryFileModel.getTimeStamp();
					if(!result.containsKey( String.valueOf(timeStampChanger) )) {
						result.put(String.valueOf(timeStampChanger), new ArrayList<String>());
					}
				}
				else if(timeStampChanger != entryFileModel.getTimeStamp()) {
					timeStampChanger = entryFileModel.getTimeStamp();
					if(!result.containsKey( String.valueOf(timeStampChanger) )) {
						result.put(String.valueOf(timeStampChanger), new ArrayList<String>());
					}
				}
				
				result.get(String.valueOf(timeStampChanger)).add(entryFileModel.getBackupFilePath());
			}
		}
		
		
		return result;
	}
	
//	public Map<String, List<String>> getMappedFilesByTimestamp(List<EntryFileModel> entryModelFileList) {
//		Map<String, List<String>> result = new HashMap<String, List<String>>();
//		
//		System.out.println("RestoreEntryService.getMappedFilesByTimestamp()");
//		List<Long> timestampsList = new ArrayList<Long>();
//		if(entryModelFileList.size() > 0) {
//			for(EntryFileModel entryModelFile : entryModelFileList) {
//				timestampsList.add( Long.valueOf(entryModelFile.getTimeStamp()) );
//			}
//			
//			// sort ascending keys 
//			Collections.sort(timestampsList);
//			// unique array
//			LinkedHashSet<Long> lhSetNumbers = new LinkedHashSet<Long>(timestampsList);
//			
//			// here you are collect a result
//			for(Long resultKey : lhSetNumbers) {
//				System.out.println("\t\tresult key: " + resultKey);
//			}
//		}
//		
//		return result;
//	}
	
}

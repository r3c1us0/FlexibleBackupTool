package com.tool.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.cliftonlabs.json_simple.Jsoner;
import com.tool.model.EntryFileModel;
import com.tool.model.RestoreEntryModel;

/**
 * This is the class to read/write '*.json' file.
 * As the list of object data used RestoreEntryModel class.
 * 
 *@author r3c1us0
 */

public class RestoreJSONParser {
	private File jsonFileHandler;

	public RestoreJSONParser(File jsonFileHandler) {
		this.jsonFileHandler = jsonFileHandler;
	}

	public List<RestoreEntryModel> readRestoreEntryModelListFromJSON() {
		List<RestoreEntryModel> result = new ArrayList<RestoreEntryModel>();
		
		JSONParser parser = new JSONParser();
		FileReader fileReader = null;
		try {
				fileReader = new FileReader(jsonFileHandler);
				Object parserObjectArray = parser.parse(fileReader);
				JSONArray parserArray = (JSONArray) parserObjectArray;

				for(int i = 0; i < parserArray.size(); i++) {
					Object objectProcessEntry = parserArray.get(i);
					JSONObject jsonProcessEntry = (JSONObject) objectProcessEntry; 
					
					JSONArray jsonFilesList = (JSONArray) jsonProcessEntry.get("entryFileList");
					
					List<EntryFileModel> entryModelFileList = new ArrayList<EntryFileModel>();
					
					for(int j = 0; j < jsonFilesList.size(); j++) {
						Object objectEntryFilesModel = jsonFilesList.get(j);
						JSONObject jsonEntryFilesModel = (JSONObject) objectEntryFilesModel;
						EntryFileModel entryFileModel = new EntryFileModel();
						entryFileModel.setBackupFilePath((String)jsonEntryFilesModel.get("backupFilePath"));
						entryFileModel.setChecksum((String)jsonEntryFilesModel.get("checksum"));
						entryFileModel.setTimeStamp((long) jsonEntryFilesModel.get("timeStamp"));
						entryModelFileList.add(entryFileModel);
					}
					
					RestoreEntryModel restoreEntryModel = new RestoreEntryModel((String) jsonProcessEntry.get("sourcePath"),
							(String) jsonProcessEntry.get("nodeName"),
							(boolean) jsonProcessEntry.get("isZip"));
					
					restoreEntryModel.setEntryFileList(entryModelFileList);
					result.add(restoreEntryModel);
				} 
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void writeRestoreEntryModelListToJSON(List<RestoreEntryModel> restoreEntryModelList) throws Exception {
		try(FileWriter fileWriter = new FileWriter(jsonFileHandler)) {
			Jsoner.serialize(restoreEntryModelList, fileWriter);
		} catch (IOException e) {
			throw new Exception( String.format("JSONParser::writeRestoreEntryModelListToJSON IOException by path '%s'", 
					this.jsonFileHandler.getAbsolutePath()) );
		}
	}
}

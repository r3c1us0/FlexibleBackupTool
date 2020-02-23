package com.tool.model;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsonable;

/***
 * This is a model of main Node entry in array of JSON data.
 * 
 * @author r3c1us0
 */


public class RestoreEntryModel implements Jsonable {

	private String nodeName;
	private String sourcePath;
	private boolean isZip;
	private List<EntryFileModel> entryFileList;
	

	public RestoreEntryModel(String sourcePath, String nodeName, boolean isZip) {
		this.sourcePath = sourcePath;
		this.nodeName = nodeName;
		this.isZip = isZip;
		this.entryFileList = new ArrayList<EntryFileModel>();
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public boolean getisZip() {
		return isZip;
	}

	public void setisZip(boolean isZip) {
		this.isZip = isZip;
	}
	
	public boolean isZip() {
		return isZip;
	}

	public void setZip(boolean isZip) {
		this.isZip = isZip;
	}
	
	public List<EntryFileModel> getEntryFileList() {
		return entryFileList;
	}

	public void setEntryFileList(List<EntryFileModel> entryFileList) {
		this.entryFileList = entryFileList;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.nodeName);
		stringBuilder.append("\t\t use archive - " + String.valueOf(isZip).toUpperCase());
		stringBuilder.append("\t files quantity - " + getEntryFileList().size());
		
		return stringBuilder.toString();
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		final StringWriter writable = new StringWriter();
		try {
			this.toJson(writable);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return writable.toString();
	}

	@Override
	public void toJson(Writer writable) throws IOException {
		// TODO Auto-generated method stub
		final JsonObject jsonObject = new JsonObject();
		final JsonArray jsonArray = new JsonArray();
		
		jsonObject.put("nodeName", this.getNodeName());
		jsonObject.put("isZip", this.getisZip());
		jsonObject.put("sourcePath", this.getSourcePath());
		
		for(EntryFileModel entryFile : getEntryFileList()) {
			jsonArray.add(entryFile);
		}
		
		jsonObject.put("entryFileList", jsonArray);
		jsonObject.toJson(writable);
	}
}

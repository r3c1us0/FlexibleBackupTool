package com.tool.model;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsonable;
/***
 * This is a model of saved file information to restore from JSON Node data.
 * 
 * @author r3c1us0
 */
public class EntryFileModel implements Jsonable, Comparable<EntryFileModel> {
	
	public EntryFileModel() {}
	
	private long timeStamp;
	private String checksum;
	private String backupFilePath;

	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	public String getBackupFilePath() {
		return backupFilePath;
	}
	public void setBackupFilePath(String backupFilePath) {
		this.backupFilePath = backupFilePath;
	}

	@Override
	public String toJson() {
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
		final JsonObject json = new JsonObject();
		json.put("timeStamp", this.getTimeStamp());
		json.put("checksum", this.getChecksum());
		json.put("backupFilePath", this.getBackupFilePath());
		json.toJson(writable);
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof EntryFileModel ) {
			return ((EntryFileModel) object).timeStamp == timeStamp;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) this.timeStamp;
	}

	@Override
	public int compareTo(EntryFileModel entryFileModel) {
		return ( this.getTimeStamp() < entryFileModel.getTimeStamp() ? -1 : (this.getTimeStamp() == entryFileModel.getTimeStamp() ? 0 : 1) );		
	}
	
}

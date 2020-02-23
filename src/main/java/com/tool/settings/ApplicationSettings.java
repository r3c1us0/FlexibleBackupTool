package com.tool.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/***
 * This is a class to read/write to 'settings.xml'.  
 * 
 * @author r3c1us0
 */

public class ApplicationSettings {
	private File settingsFile = null;
	private static DocumentBuilderFactory documentBuilderFactory; 

	public ApplicationSettings(File settingsFile) {
		this.settingsFile = settingsFile;
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
	}

	public List<String> getSourceFolders() throws Exception {
		try {
			return this.getStringList("SourceFolders");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot parse properly a tag name 'SourceFolders' from %s file. (ParserConfigurationException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot find a tag name 'SourceFolders' from %s file. (SAXException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot read %s file. (IOException)",
					this.settingsFile.getAbsolutePath()) );
		}
	}
	
	/**
	 * 
	 * @param sourcePath - gets from settings file by tag value is directory
	 * @return -1 if sourcePath or attribute not exists, 0 - if false, 1 - if true. 
	 * @throws Exception
	 */
	public int getSourceFoldersArchiveActionByPath(String sourcePath) throws Exception  {
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(this.settingsFile);
			NodeList nodes = document.getElementsByTagName("SourceFolders");
			Node nodeItem = nodes.item(0);
			if(nodeItem.hasChildNodes() && nodeItem.getNodeType() == Node.ELEMENT_NODE) {
				
				NodeList nodeChildItems = nodeItem.getChildNodes();
				for(int i = 0; i < nodeChildItems.getLength(); i++) {
					
					if(nodeChildItems.item(i).getNodeType() == Node.ELEMENT_NODE) {
						String path = nodeChildItems.item(i).getTextContent().replaceAll("\\\\", "/").trim();
						
						if(path.equalsIgnoreCase(sourcePath)) {
							Boolean archiveFiles = Boolean.valueOf(false);
							if(nodeChildItems.item(i).hasAttributes()) {
								Element element = (Element) nodeChildItems.item(i); 
								archiveFiles = Boolean.valueOf(element.getAttribute("archive_files"));
								return (archiveFiles.booleanValue()) ? 1 : 0;
							} 
						}
					}
				}
			}		
		}
		catch(ParserConfigurationException e) {
			throw new Exception( String.format("Cannot parse properly a tag name 'SourceFolders' from %s file. (ParserConfigurationException)", 
					this.settingsFile.getAbsolutePath()) );
			
		} catch (SAXException e) {
			throw new Exception( String.format("Cannot find a tag name 'SourceFolders' from %s file. (SAXException)", 
					this.settingsFile.getAbsolutePath()) );
			
		} catch (IOException e) {
			throw new Exception( String.format("Cannot read %s file. (IOException)",
					this.settingsFile.getAbsolutePath()) );
		}

		return -1;
	} 
	
	public Map<String, Boolean> getSourceFoldersAndBackupArchiveAction() throws Exception {
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(this.settingsFile);
			NodeList nodes = document.getElementsByTagName("SourceFolders");
			Node nodeItem = nodes.item(0);
			
			if(nodeItem.hasChildNodes() && nodeItem.getNodeType() == Node.ELEMENT_NODE) {
				
				NodeList nodeChildItems = nodeItem.getChildNodes();
				for(int i = 0; i < nodeChildItems.getLength(); i++) {
					
					if(nodeChildItems.item(i).getNodeType() == Node.ELEMENT_NODE) {
						String path = nodeChildItems.item(i).getTextContent().replaceAll("\\\\", "/").trim();
						Boolean archiveFiles = Boolean.valueOf(false);
						if(nodeChildItems.item(i).hasAttributes()) {
							Element element = (Element) nodeChildItems.item(i); 
							archiveFiles = Boolean.valueOf(element.getAttribute("archive_files"));
						} 
						
						result.put(path, archiveFiles);
					}
				}
			}
			
		}
		catch(ParserConfigurationException e) {
			throw new Exception( String.format("Cannot parse properly a tag name 'SourceFolders' from %s file. (ParserConfigurationException)", 
					this.settingsFile.getAbsolutePath()) );
			
		} catch (SAXException e) {
			throw new Exception( String.format("Cannot find a tag name 'SourceFolders' from %s file. (SAXException)", 
					this.settingsFile.getAbsolutePath()) );
			
		} catch (IOException e) {
			throw new Exception( String.format("Cannot read %s file. (IOException)",
					this.settingsFile.getAbsolutePath()) );
		}
		
		return result;
	}
	
	public List<String> getBackupFileExtensions() throws Exception {
		try {
			return this.getStringList("BackupExtensions");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot parse properly a tag name 'BackupExtensions' from %s file. (ParserConfigurationException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot find a tag name 'BackupExtensions' from %s file. (SAXException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot read %s file. (IOException)",
					this.settingsFile.getAbsolutePath()) );
		}
	}
	
	public List<String> getBackupDirections() throws Exception {
		try {
			return this.getStringList("BackupDirections");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot parse properly a tag name 'BackupDirections' from %s file. (ParserConfigurationException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot find a tag name 'BackupDirections' from %s file. (SAXException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot read %s file. (IOException)",
					this.settingsFile.getAbsolutePath()) );
		}
	}
	
	public boolean isFilesExtentionsUses() throws Exception {
		boolean result = false;
		
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(this.settingsFile);
			NodeList nodes = document.getElementsByTagName("BackupExtensions");
			Node nodeItem = nodes.item(0);
			
			if(nodeItem.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)nodeItem;
				if(element.hasAttribute("active")) {
					Boolean boolResult = new Boolean(element.getAttribute("active"));
					result = boolResult.booleanValue();				
				}
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot parse properly a tag name 'BackupExtensions' from %s file. (ParserConfigurationException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot find a tag name 'BackupExtensions' from %s file. (SAXException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot read %s file. (IOException)",
					this.settingsFile.getAbsolutePath()) );
		}
		
		return result;
	}
	
	public boolean getisZip() throws Exception {
		boolean result = false;

		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(this.settingsFile);
			NodeList nodes = document.getElementsByTagName("zip");
			Node nodeItem = nodes.item(0);
			
			if(nodeItem.getNodeType() == Node.ELEMENT_NODE) {
				Boolean strResult = new Boolean(nodeItem.getTextContent());
				result = strResult.booleanValue();
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot parse properly a tag name 'zip' from %s file. (ParserConfigurationException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot find a tag name 'zip' from %s file. (SAXException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot read %s file. (IOException)",
					this.settingsFile.getAbsolutePath()) );
		}
		
		return result;
	}

	public boolean isChecksum() throws Exception {
		boolean result = false;
		
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(this.settingsFile);
			NodeList nodes = document.getElementsByTagName("checksum");
			Node nodeItem = nodes.item(0);
			
			if(nodeItem.getNodeType() == Node.ELEMENT_NODE) {
				Boolean strResult = new Boolean(nodeItem.getTextContent());
				result = strResult.booleanValue();
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot parse properly a tag name 'checksum' from %s file. (ParserConfigurationException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot find a tag name 'checksum' from %s file. (SAXException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot read %s file. (IOException)",
					this.settingsFile.getAbsolutePath()) );
		}
		
		return result;
	}
	
	public String getChecksumFilename() throws Exception {
		String result = "";
		
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(this.settingsFile);
			NodeList nodes = document.getElementsByTagName("checksumfileanme");
			Node nodeItem = nodes.item(0);
			
			if(nodeItem.getNodeType() == Node.ELEMENT_NODE) {
				result = nodeItem.getTextContent();
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot parse properly a tag name 'checksumfileanme' from %s file. (ParserConfigurationException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot find a tag name 'checksumfileanme' from %s file. (SAXException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot read %s file. (IOException)",
					this.settingsFile.getAbsolutePath()) );
		}
		
		return result;
	}
	
	public String getRestoreJsnoFilename() throws Exception {
		String result = "";

		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(this.settingsFile);
			NodeList nodes = document.getElementsByTagName("RestoreJSON");
			Node nodeItem = nodes.item(0);
			
			if(nodeItem.getNodeType() == Node.ELEMENT_NODE) {
				result = nodeItem.getTextContent();
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot parse properly a tag name 'RestoreJSON' from %s file. (ParserConfigurationException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot find a tag name 'RestoreJSON' from %s file. (SAXException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot read %s file. (IOException)",
					this.settingsFile.getAbsolutePath()) );
		}

		return result;
	}

	public String getRestoreDirectory() throws Exception {
		String result = "";

		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(this.settingsFile);
			NodeList nodes = document.getElementsByTagName("RestoreDirectory");
			Node nodeItem = nodes.item(0);
			
			if(nodeItem.getNodeType() == Node.ELEMENT_NODE) {
				result = nodeItem.getTextContent();
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot parse properly a tag name 'RestoreDirectory' from %s file. (ParserConfigurationException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot find a tag name 'RestoreDirectory' from %s file. (SAXException)", 
					this.settingsFile.getAbsolutePath()) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new Exception( String.format("Cannot read %s file. (IOException)",
					this.settingsFile.getAbsolutePath()) );
		}

		return result;
	}
	
	private List<String> getStringList(String nodeName) throws ParserConfigurationException, SAXException, IOException {
		List<String> result = null;
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(this.settingsFile);
		NodeList nodes = document.getElementsByTagName(nodeName);
		Node nodeItem = nodes.item(0);
		
		if(nodeItem.hasChildNodes() && nodeItem.getNodeType() == Node.ELEMENT_NODE) {
			result = new ArrayList<String>();
			
			NodeList nodeChildItems = nodeItem.getChildNodes();
			for(int i = 0; i < nodeChildItems.getLength(); i++) {
				
				if(nodeChildItems.item(i).getNodeType() == Node.ELEMENT_NODE) {
					result.add(nodeChildItems.item(i).getTextContent().replaceAll("\\\\", "/"));
				}
			}
		}
		
		return result;		
	}	
}

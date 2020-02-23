## Introduction  
NAME   
&nbsp;&nbsp;&nbsp;&nbsp;Flexible Backup Tool   

SYNOPSIS   
&nbsp;&nbsp;&nbsp;&nbsp;$java -jar FlexibleBackupTool-2.1.N-shaded.jar [ OPTIONS ]   

DESCRIPTION   
&nbsp;&nbsp;&nbsp;&nbsp;Flexible Backup Tool is a java console application for directory list backup.   
&nbsp;&nbsp;&nbsp;&nbsp;Application does not work without the path to folder of required files.   
&nbsp;&nbsp;&nbsp;&nbsp;Required files: [`settings.xml`](https://github.com/r3c1us0/FlexibleBackupTool/blob/master/backup-workspace/settings.xml); [`log4j.properties`](https://github.com/r3c1us0/FlexibleBackupTool/blob/master/backup-workspace/log4j.properties);   
&nbsp;&nbsp;&nbsp;&nbsp;Other files such as `checksum.properties` and `restoredat.json` will be created on a first running.   

OPTIONS   
&nbsp;&nbsp;&nbsp;&nbsp;`workspace=/path/to/read_write/workspace/folder` - this folder must have required settings files;   
&nbsp;&nbsp;&nbsp;&nbsp;`restore` - a key works together with workspace to restore previous backup files last time modified;   

EXAMPLE   
&nbsp;&nbsp;&nbsp;&nbsp;Start backup console command   
```console
$java -jar FlexibleBackupTool-2.1.N-shaded.jar workspace=/path/to/read_write/workspace/folder
```   
&nbsp;&nbsp;&nbsp;&nbsp;Restore backup console command   
```console
$java -jar FlexibleBackupTool-2.1.N-shaded.jar workspace=/path/to/read_write/workspace/folder restore
```
### Usage   
This is a Java open source [eclipse IDE](https://www.eclipse.org/downloads/) project managed by [Maven](https://maven.apache.org/guides/getting-started/index.html). The Project Object Model 'pom.xml' created to packaging project to the executable JAR file with required libraries inside.   
Before running the code you need to define the full path to workspace folder with the required by application files on your machine. An example of workspace folder located in repository named ['backup-workspace'](https://github.com/r3c1us0/FlexibleBackupTool/tree/master/backup-workspace). The workspace is consist of two file in the beginning: `settings.xml`; `log4j.properties`;   
`settings.xml` - this is a main configuration file for targetting backup/restore points. Must have readable permission.   
`log4j.properties` - this is a [LOG4J](https://www.tutorialspoint.com/log4j/log4j_quick_guide.htm) configuration file which keeps properties in key-value pairs. Please do not rename the key name 'log4j.appender.LogFileAppender' because of key named 'log4j.appender.LogFileAppender.file' will be edited by application in Java runtime. This is why the file must have writable permission.   
The application as a project or as an executable JAR file does not start without main attribute 'workspace='. The argument works as a key-value pair where the value is a reference on full path to workspace folder with read/write permissions settings.   
### The 'settings.xml' in details
settings.xml is main configurate file before start to use this console appllication.
This consentrate direction of types to backup data. This concentrate direction of types to backup data.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Properties>
   <!--
      SourceFolders - element of list of directories for backup
   -->
   <SourceFolders>
      <!--
         tag directory - consist of inner text value of absolute path to source folder and attribute 'archive_files'
         archive_files - a boolean value true/false attribute:
            TRUE - creates a zip archive file named as last folder name;
            FALSE - copy files directly from source folder to backup storage;
            NB!
               in directory tree last folder name must be unique in list;
               source folder may contains from sub-folders;
      -->
      <directory archive_files="true">/home/library/3K_library/3K_FUJI_P1</directory>
      <directory archive_files="true">/home/library/3K_library/3K_FUJI_P1_9S</directory>
      <directory archive_files="true">/home/library/3K_library/3K_FUJI_P2</directory>
      <directory archive_files="false">/home/library/3K_library/3K_FUJI_P2_9S</directory>
   </SourceFolders>

   <!-- 
      BackupExtensions - element of list of required file's extensions for store to backup;
      active - a boolean value true/false attribute:
         TRUE - uses for filtering files by extension;
         FALSE - does not filtering files by extension;
   -->
   <BackupExtensions active="true">
      <!--
         extension - consist of text of file's extensions without any other symbols;
      -->
      <extension>md</extension>
      <extension>mf</extension>
      <extension>bm</extension>
      <extension>csv</extension>
      <extension>svg</extension>
   </BackupExtensions>

   <!--
      The 'checksum' tag consist of boolean value true/false.
         TRUE - uses for check out the HASH the checksum of the file
            if file were changed the HASH of checksum will be different from last entry
            and file will be saved to backup store without overwriting;
         FALSE - always saves all exists files and subdirectories;
   -->
   <checksum>true</checksum>

   <!--
      Java related technologies to store the configurable parameters .properties file
   -->
   <checksumfileanme>checksum.properties</checksumfileanme>

   <!--
      JSON is an open-standard file format or data interchange format to store latest changed file
   -->
   <RestoreJSON>restoredat.json</RestoreJSON>
   <RestoreDirectory>/tmp/restore</RestoreDirectory>

   <!--
      BackupDirections - element of list of full backup storage folder path
   -->
   <BackupDirections>
      <!--
         directory - the inner text of full backup storage folder path;
         Attention!
            As a restore directory by default uses only first element from several.
      -->
      <directory>/home/mnt/sdb1/backup/libraries_storage</directory>
      <directory>/home/mnt/sdd2/backup/libraries_storage</directory>
   </BackupDirections>
</Properties>
```   
### Conclusion   
The application works with Windows OS on the mapped network disks. On the Win7 workstation I have only one executable JAR and several workspace folders with adjusted settings.xml files in my case. The content for backup keeps on the network mapped disks and copying to other mapped disk by task manager.   
You are free to make changes in source code, rename project and compile the code for your needs on your own PC. 

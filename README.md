## Introduction
###### NAME
Flexible Backup Tool
###### SYNOPSIS
>$java -jar FlexibleBackupTool-2.1.#-shaded.jar [ OPTIONS ]
###### DESCRIPTION
Flexible Backup Tool is a java console application for directory list backup. 
Application does not work without the path to folder of required files.
Required files: `settings.xml`; `log4j.properties`;
Other files such as `checksum.properties` and `restoredat.json` will be created on a first running.
###### OPTIONS
`workspace=/path/to/read_write/workspace/folder` - this folder must have required settings files;
`restore` - a key works together with workspace to restore previous backup files last time modified;
###### EXAMPLE
Start backup console command
```bash
$java -jar FlexibleBackupTool-2.1.#-shaded.jar workspace=/path/to/read_write/workspace/folder
```
Restore backup console command
```bash
$java -jar FlexibleBackupTool-2.1.#-shaded.jar workspace=/path/to/read_write/workspace/folder restore
```


### Title text
Lorem ipsum dolor sit amet consectetur adipiscing elit, urna consequat felis vehicula class ultricies mollis dictumst, aenean non a in donec nulla. Phasellus ante pellentesque erat cum risus consequat imperdiet aliquam, integer placerat et turpis mi eros nec lobortis taciti, vehicula nisl litora tellus ligula porttitor metus. 

```bash
arp -a 
```
### Usage
Vivamus integer non suscipit taciti mus etiam at primis tempor sagittis sit, euismod libero facilisi aptent elementum felis blandit cursus gravida sociis erat ante, eleifend lectus nullam dapibus netus feugiat curae curabitur est ad. Massa curae fringilla porttitor quam sollicitudin iaculis aptent leo ligula euismod dictumst, orci penatibus mauris eros etiam praesent erat volutpat posuere hac. Metus fringilla nec ullamcorper odio aliquam lacinia conubia mauris tempor, etiam ultricies proin quisque lectus sociis id tristique, integer phasellus taciti pretium adipiscing tortor sagittis ligula.
```console
$ bundle exec bin/github-linguist --breakdown
68.57%  Ruby
22.90%  C
6.93%   Go
1.21%   Lex
0.39%   Shell
```
### settings.xml IN DETAILS
settings.xml is main configrurate file before start to use this console appllication.
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

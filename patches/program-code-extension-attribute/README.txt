Extension Attribute Example - Program Code

This directory contains a patch to add the program code attribute needed by community colleges.  It also serves as an example 
for all institutions on how to add extension attributes to KFS.

It will:

* Add Program Code to the Account Lookup as a search criteria and results.
* Add a Program link to the main portal page under Sub-Object Code.
* Provide a lookup and document for Program Code.
* Add Program Code to the Account Maintenance document and validate it upon submit.

To Use:

1. Apply the patch via Eclipse.

2. Customize and place the provided kuali-build.properties in your home directory.  (Or take the last property out of the file and put it in your existing property file as it's the important one.)

3. Run the provided SQL scripts in your database (you will need to adapt them for MySQL) to create the needed tables and add the document type.

4. Ingest the ProgramMaintenanceDocument.xml file into the workflow engine.  (See details of this process in the KFS documentation on-line.)


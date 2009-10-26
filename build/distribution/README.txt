*********************
** KFS Release 3.0 **
*********************

Documentation Links:

Main Documentation Home:
    https://test.kuali.org/confluence/display/KULDOC/Home

Technical Setup Documentation:
    https://test.kuali.org/confluence/display/KULDOC/Technical+Documentation+3

Database Setup:
    https://test.kuali.org/confluence/display/KULDOC/Database+3

What's Here:


release-details.txt - What revisions of the code you're working with.

kfs/                   - The main Kuali Financial System project.  Import this into Eclipse.  
                         See the technical documentation on how to build.
rice/                  - The version of Rice used to build this version of KFS.
impex/                 -    Database import/export tool used to create the needed database schema
                            and load the base data for KFS.
                            Currently, Oracle 10g+ and MySQL 5.1+ are supported.
datasets/              - Base directory for the demonstration and bootstrap datasets for KFS.
datasets/demo/         - Demo dataset useful for a quick start with enhancing or demonstrating the
                       - application without having to develop your data imports first.
datasets/bootstrap/    - An almost completely empty database except for the base configuration and
                         reference data.  Use this when you are ready to import your insitution's data.
impex-build.properties - Configuration file for the impex tool to import the demo bootstrap into a 
                         local Oracle XE database.  Update the import.* properties as needed.
                         
                         
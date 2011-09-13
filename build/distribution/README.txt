***********************
** KFS Release 4.1.1 **
***********************

Documentation Links:

Main Documentation Home:
    https://test.kuali.org/confluence/display/KULDOC/Home

Technical Setup Documentation:
    https://test.kuali.org/confluence/display/KULDOC/Technical+Documentation+4

Database Setup:
    https://test.kuali.org/confluence/display/KULDOC/Database+4

What's Here:


release-details.txt - What revisions of the code you're working with.

kfs/                   - The main Kuali Financial System project.  Import this into Eclipse.  
                         See the technical documentation on how to build.
rice/                  - The version of Rice used to build this version of KFS.
kul-cfg-dbs/           -    Database import/export tool used to create the needed database schema
                            and load the base data for KFS.
                            Currently, Oracle 10g+ and MySQL 5.1+ are supported.
datasets/              - Base directory for the demonstration and bootstrap datasets for KFS.
datasets/demo/         - Demo dataset useful for a quick start with enhancing or demonstrating the
                       - application without having to develop your data imports first.
datasets/bootstrap/    - An almost completely empty database except for the base configuration and
                         reference data.  Use this when you are ready to import your insitution's data.
datasets/rice-demo/    - A rice server database onto which either of the above datasets can be overlaid.  
                         This contains some of the rice sampleapp data.
datasets/rice-bootstrap/ - A clean rice server database onto which either of the above datasets can be overlaid.

demo-impex-build-embedded-rice.properties 
					   - Configuration file for the impex tool to import the demo bootstrap into a 
                         local Oracle XE database with an internal Rice server.  This is the easiest
                         way to get up and running to test the system.
                         Update the import.* properties as needed.

bootstrap-impex-build-embedded-rice.properties
                       - Same as the above except with only the bare minimum of information.
                         This could be an implementation starting point if you do not plan
                         on running a separate Rice server.
                                                  
demo-impex-build-standalone-rice.properties
                       - Loads the demo data but excludes any of the Rice server tables.  This
                         version must be used with an existing Rice server which has had the
                         scripts from kfs/work/db/rice-data applied.
                         
bootstrap-impex-build-standalone-rice.properties
                       - Loads the bootstrap data but excludes any of the Rice server tables.  This
                         version must be used with an existing Rice server which has had the
                         scripts from kfs/work/db/rice-data applied.
                         
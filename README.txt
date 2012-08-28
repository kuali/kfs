**************************************************
** KFS Release 5.0 -- INTERNAL PARTNER RELEASE  **
**************************************************

KFS URL: https://svn.kuali.org/repos/kfs/branches/release-5-0

Rice URL: https://svn.kuali.org/repos/rice/branches/rice-2.1@34314

****** Documentation Links:

Main Documentation Home:
	https://wiki.kuali.org/display/KULDOC/Home

Technical Setup Documentation:
	https://wiki.kuali.org/display/KULDOC/Technical+Documentation+5

Database Setup:
	https://wiki.kuali.org/display/KULDOC/Database+5

****** Quick Start:

1) Ensure that you have Java and Ant installed
2) Setup a local MySQL database (see technical document link above for details)
3) Switch to the kfs directory of the distribution
4) Run Ant with no parameters for further details on setting up the database
   and running your server.

****** What's Here:

kfs/                          - The main Kuali Financial System project. 
                                Import this into Eclipse. 
                                See the technical documentation on how to build.
                               
kfs/work/db/kfs-db/db-impex/  - Database import/export tool used to create the 
                                needed database schema
                                and load the base data for KFS.
                                Currently, Oracle 10g+ and MySQL 5.1+ are supported.
                               
kfs/work/db/kfs-db/           - Base directory for the demonstration and bootstrap datasets for KFS.

kfs/work/db/kfs-db/demo/      - Demo dataset useful for a quick start with enhancing or demonstrating the
                                application without having to develop your data imports first.

kfs/work/db/kfs-db/bootstrap/ - An almost completely empty database except for 
                                base configuration and reference data. Use this
                                when you are ready to import your institution's data.
                                
kfs/work/db/kfs-db/rice/      - A clean rice server database onto which either of 
                                the above datasets can be overlaid if you will be
                                running with Rice bundled into the KFS application.

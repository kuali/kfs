***********************
** KFS Release 5.0   **
***********************

****** Documentation Links:

Main Documentation Home:
	https://test.kuali.org/confluence/display/KULDOC/Home

Technical Release Notes / Upgrade Documentation:
	https://docs.google.com/a/kuali.org/#folders/0B1dW7BM9X5-rSlZHdkdLV3JKYjg

****** Quick Start:

1) Ensure that you have Java and Ant installed
2) Setup a local MySQL database (see technical document link above for details)
3) Switch to the kfs directory of the distribution
4) Run Ant with no parameters for further details on setting up the database
   and running your server.

****** What's Here:

release-details.txt - What revisions of the code you're working with.

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

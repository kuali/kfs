These are the scripts/programs used to upgrade a KFS 3.0.1 database to KFS 4.0.

Running Liquibase
liquibase website


-------------------------------------------------------------------------------
Sub-Directories
-------------------------------------------------------------------------------

===================================================       country_code_updates/

These scripts can be used to update the country codes in the KFS tables to the new
ISO country codes.  You only need to run this if you have affected non-US country
codes in your exsiting tables.  (Not including KR_COUNTRY_T, which is handled by
the Rice upgrade scripts.)

===================================================       rice/

Liquibase scripts to upgrade your Rice tables with new and corrected KFS-related 
data since KFS 3.0.1.  This does *not* include the upgrade scripts for Rice itself.
Those need to be obtained from the Rice project using the two URLs below.  (They
are also part of the Rice 1.0.3 distribution from kuali.org.)

https://test.kuali.org/svn/rice/branches/rice-release-1-0-3-br/scripts/upgrades/1.0.1.1%20to%201.0.2/
https://test.kuali.org/svn/rice/branches/rice-release-1-0-3-br/scripts/upgrades/1.0.2%20to%201.0.3/


===================================================       workflow/

This contains the XML files you need to ingest for workflow changes made
since KFS 3.0.1.  You should review these files, since changes to existing documents
will, if ingested, overwrite any local modifications to those document's routing
paths you may have made.  Also, if you are not planning on implementing the
Endowment module, you do not need to ingest those document types.


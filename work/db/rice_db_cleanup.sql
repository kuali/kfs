-- This script should be run against the Rice schema before exporting it to the KFS master datasource.

DELETE FROM krcr_parm_t WHERE nmspc_cd = 'KR-SAP'
/
DELETE FROM krcr_nmspc_t WHERE nmspc_cd = 'KR-SAP'
/

COMMIT
/


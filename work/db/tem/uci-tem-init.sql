
-- update parameters for UCI institution names and travel policy URL
UPDATE KRCR_PARM_T
SET TXT = 'University of California, Irvine'
WHERE NMSPC_CD = 'KFS-TEM' AND PARM_NM = 'TRAVEL_REPORT_INSTITUTION_NAME'
/

UPDATE KRCR_PARM_T
SET TXT = 'University of California, Irvine',
PARM_DESC_TXT = 'University of California, Irvine'
WHERE NMSPC_CD = 'KFS-AR' AND PARM_NM = 'INSTITUTION_NAME'
/

UPDATE KRCR_PARM_T
SET TXT = 'http://www.policies.uci.edu/adm/procs/700/715-22.html'
WHERE NMSPC_CD = 'KFS-TEM' AND PARM_NM = 'TRAVEL_ADVANCES_POLICY_URL'
/

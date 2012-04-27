
-- update the reason code parameter to T (instead of C) to get away with numeric reason code for DV
UPDATE KRNS_PARM_T 
SET TXT = 'T' 
WHERE NMSPC_CD = 'KFS-AR' AND PARM_NM = 'TRAVEL_ADVANCE_DV_PAYMENT_REASON_CODE';
/

-- update parameters for UCI institution names and travel policy URL
UPDATE KRNS_PARM_T
SET TXT = 'University of California, Irvine'
WHERE NMSPC_CD = 'KFS-TEM' AND PARM_NM = 'TRAVEL_REPORT_INSTITUTION_NAME';
/

UPDATE KRNS_PARM_T
SET TXT = 'University of California, Irvine'
WHERE NMSPC_CD = 'KFS-AR' AND PARM_NM = 'INSTITUTION_NAME';
/

UPDATE KRNS_PARM_T
SET TXT = 'http://www.policies.uci.edu/adm/procs/700/715-22.html'
WHERE NMSPC_CD = 'KFS-TEM' AND PARM_NM = 'TRAVEL_ADVANCES_POLICY_URL';
/

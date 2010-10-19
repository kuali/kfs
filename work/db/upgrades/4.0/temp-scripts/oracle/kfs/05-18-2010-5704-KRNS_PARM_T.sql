UPDATE krns_parm_t 
SET txt='[0-9]{3}-[0-9]{2}-[0-9]{4};\d{4}-\d{4}-\d{4}-\d{4};\d{4}\s\d{4}\s\d{4}\s\d{4};\d{2}-\d{7};\d{9}' 
WHERE nmspc_cd='KR-NS' AND PARM_DTL_TYP_CD='All' AND PARM_NM='SENSITIVE_DATA_PATTERNS';
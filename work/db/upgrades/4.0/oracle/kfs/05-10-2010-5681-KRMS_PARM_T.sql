UPDATE krns_parm_t
SET txt='yyyy-MM-dd;MM/dd/yy;MM/dd/yyyy;MM-dd-yy;MMddyy;MMMM dd;yyyy;MM/dd/yy HH:mm:ss;MM/dd/yyyy HH:mm:ss;MM-dd-yy HH:mm:ss;MMddyy HH:mm:ss;MMMM dd HH:mm:ss;yyyy HH:mm:ss;MM/dd/yyyy hh:mm a'
WHERE nmspc_cd='KR-NS' AND PARM_DTL_TYP_CD='All' AND PARM_NM='STRING_TO_DATE_FORMATS';

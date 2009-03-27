DROP TABLE krim_role_rel_t
/
DROP SEQUENCE KRIM_ROLE_REL_ID_S
/

INSERT INTO KR_POSTAL_CODE_T
(SELECT '91010','US',sys_guid(),1,
'CA','37','Duarte','Y' from dual)
/
update krim_role_rsp_actn_t set actn_typ_cd = 'F' where role_rsp_actn_id = 132
/
update krns_parm_t
set txt = 'Y'
where nmspc_cd = 'KFS-PURAP'
and parm_dtl_typ_cd = 'Document'
and parm_nm = 'ENABLE_SALES_TAX_IND'
/
INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A semi-colon delimted list of strings representing date formats that the DateTimeService will use to parse dates when DateTimeServiceImpl.convertToSqlDate(String) or DateTimeServiceImpl.convertToDate(String) is called. Note that patterns will be applied in the order listed (and the first applicable one will be used). For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'STRING_TO_DATE_FORMATS',
'CONFG',
'MM/dd/yy;MM-dd-yy;MMMM dd, yyyy;MMddyy',
1
)
/
INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format dates to be used in a file name when DateTimeServiceImpl.toDateStringForFilename(Date) is called. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'DATE_TO_STRING_FORMAT_FOR_FILE_NAME',
'CONFG',
'yyyyMMdd',
1
)
/
INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format a date and time string to be used in a file name when DateTimeServiceImpl.toDateTimeStringForFilename(Date) is called.. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'TIMESTAMP_TO_STRING_FORMAT_FOR_FILE_NAME',
'CONFG',
'yyyyMMdd-HH-mm-ss-S',
1
)
/
INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format a date to be displayed on a web page. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'DATE_TO_STRING_FORMAT_FOR_USER_INTERFACE',
'CONFG',
'MM/dd/yyyy',
1
)
/
INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format a date and time to be displayed on a web page. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE',
'CONFG',
'MM/dd/yyyy hh:mm a',
1
)
/
INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A semi-colon delimted list of strings representing date formats that the DateTimeService will use to parse date and times when DateTimeServiceImpl.convertToDateTime(String) or DateTimeServiceImpl.convertToSqlTimestamp(String) is called. Note that patterns will be applied in the order listed (and the first applicable one will be used). For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'STRING_TO_TIMESTAMP_FORMATS',
'CONFG',
'MM/dd/yyyy hh:mm a',
1
)
/

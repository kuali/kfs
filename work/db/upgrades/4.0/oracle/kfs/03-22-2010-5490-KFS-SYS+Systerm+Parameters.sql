INSERT INTO KRNS_PARM_T
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD)
VALUES ('KFS-SYS', 'AutoDisapproveDocumentsStep','YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES', SYS_GUID(), 1, 'CONFG', 
	'AV;CR;ND;PCDO;DV;CCR;AD;CMD', 'The document type(s) that are not allowed to be automatically disapproved.', 'D', 'KFS');

INSERT INTO KRNS_PARM_T
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD)
VALUES ('KFS-SYS', 'AutoDisapproveDocumentsStep', 'YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE', SYS_GUID(), 1, 'CONFG', 
	'6/30/2010', 'The automatic disapproval job will disapprove enroute documents with a create date equal to or earlier than this date.', 'A', 'KFS');

INSERT INTO KRNS_PARM_T
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD)
VALUES ('KFS-SYS', 'AutoDisapproveDocumentsStep', 'YEAR_END_AUTO_DISAPPROVE_DOCUMENT_RUN_DATE', SYS_GUID(), 1, 'CONFG', 
	'6/30/2010', 'Controls the date on which the auto disapproval step should run.', 'A', 'KFS');

INSERT INTO KRNS_PARM_T
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD)
VALUES ('KFS-SYS', 'AutoDisapproveDocumentsStep', 'YEAR_END_AUTO_DISAPPROVE_ANNOTATION', SYS_GUID(), 1, 'CONFG', 
	'This document has been automatically disapproved as part of year-end closing.  If these are valid  transactions they should be recreated using a year-end closing document.',
	'The annotation that will be added to the route log when a document is auto disapproved.', 'A', 'KFS');

INSERT INTO KRNS_PARM_T
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD)
VALUES ('KFS-SYS', 'AutoDisapproveDocumentsStep', 'YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE', SYS_GUID(), 1, 'CONFG', 
	'FP', 'Documents that are children of this document type are eligible for automatic disapproval.', 'A', 'KFS');

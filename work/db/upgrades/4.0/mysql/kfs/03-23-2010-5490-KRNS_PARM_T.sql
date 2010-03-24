-- Changeset updates/2010-03-22-5490-1-KFS-SYS+Systerm+Parameters.xml::5490-1-1::Muddu::(MD5Sum: 254a1981622dfee734bdcc310f2867b)
-- create the system parameters identified in the attached text file for the autoDisapproval job to run.
INSERT INTO `KRNS_PARM_T` (`VER_NBR`, `PARM_DESC_TXT`, `OBJ_ID`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `APPL_NMSPC_CD`, `TXT`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES (1, 'The document types that are not allowed to be automatically disapproved', uuid(), 'YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES', 'AutoDisapproveDocumentsStep', 'D', 'KFS', 'AV;CR;ND;PCDO;DV;CCR;AD;CMD', 'CONFG', 'KFS-SYS');

-- Changeset updates/2010-03-22-5490-1-KFS-SYS+Systerm+Parameters.xml::5490-1-2::Muddu::(MD5Sum: 92c279637a79779ebf4d65465ee4acf)
-- create the system parameters identified in the attached text file for the autoDisapproval job to run.
INSERT INTO `KRNS_PARM_T` (`VER_NBR`, `PARM_DESC_TXT`, `OBJ_ID`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `APPL_NMSPC_CD`, `TXT`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES (1, 'The automatic disapproval job will disapprove enroute documents with a create date equal to or earlier than this date', uuid(), 'YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE', 'AutoDisapproveDocumentsStep', 'A', 'KFS', '6302010', 'CONFG', 'KFS-SYS');

-- Changeset updates/2010-03-22-5490-1-KFS-SYS+Systerm+Parameters.xml::5490-1-3::Muddu::(MD5Sum: 8b258332cf5021366d79d438c528269f)
-- create the system parameters identified in the attached text file for the autoDisapproval job to run.
INSERT INTO `KRNS_PARM_T` (`VER_NBR`, `PARM_DESC_TXT`, `OBJ_ID`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `APPL_NMSPC_CD`, `TXT`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES (1, 'Controls the date on which the auto disapproval step should run', uuid(), 'YEAR_END_AUTO_DISAPPROVE_DOCUMENT_RUN_DATE', 'AutoDisapproveDocumentsStep', 'A', 'KFS', '6302010', 'CONFG', 'KFS-SYS');

-- Changeset updates/2010-03-22-5490-1-KFS-SYS+Systerm+Parameters.xml::5490-1-4::Muddu::(MD5Sum: 7b2546e1b172ae10128fae244cf8874c)
-- create the system parameters identified in the attached text file for the autoDisapproval job to run.
INSERT INTO `KRNS_PARM_T` (`VER_NBR`, `PARM_DESC_TXT`, `OBJ_ID`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `APPL_NMSPC_CD`, `TXT`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES (1, 'The annotation that will be added to the route log when a document is auto disapproved', uuid(), 'YEAR_END_AUTO_DISAPPROVE_ANNOTATION', 'AutoDisapproveDocumentsStep', 'A', 'KFS', 'This document has been automatically disapproved as part of year-end closing  If these are valid  transactions they should be recreated using a year-end closing document', 'CONFG', 'KFS-SYS');

-- Changeset updates/2010-03-22-5490-1-KFS-SYS+Systerm+Parameters.xml::5490-1-5::Muddu::(MD5Sum: 8ab825d1a5c7669a1a45cc48c48150)
-- create the system parameters identified in the attached text file for the autoDisapproval job to run.
INSERT INTO `KRNS_PARM_T` (`VER_NBR`, `PARM_DESC_TXT`, `OBJ_ID`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `APPL_NMSPC_CD`, `TXT`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES (1, 'Documents that are children of this document type are eligible for automatic disapproval', uuid(), 'YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE', 'AutoDisapproveDocumentsStep', 'A', 'KFS', 'FP', 'CONFG', 'KFS-SYS');


-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 8/19/10 9:05 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-08-19-5955-1-KRNS_PARM_T.xml::5955-1-1::Travis::(MD5Sum: f631717d90bfc9495348edc1353dea1d)
-- Create Parameter: TRANSACTION_ARCHIVE_DOCUMENT_TYPE_NAMES
INSERT INTO `KRNS_PARM_T` (`appl_nmspc_cd`, `cons_cd`, `nmspc_cd`, `parm_dtl_typ_cd`, `parm_nm`, `obj_id`, `parm_desc_txt`, `ver_nbr`, `txt`, `parm_typ_cd`) VALUES ('KFS', 'A', 'KFS-ENDOW', 'EndowmentTransactionCode', 'TRANSACTION_ARCHIVE_DOCUMENT_TYPE_NAMES', uuid(), 'The values of this parameter represent a complete list of document type names for the endowment transactional documents that can be posted to the endowment transaction archive table The application maps this list to the document types that have been registered with the Workflow Engine', 1, 'EAD;EAI;ECDD;ECI;ECT;ECA;EUSA;ELD;ELI;EST;EHA;EGLT;GLET', 'CONFG');


-- Release Database Lock

-- Release Database Lock


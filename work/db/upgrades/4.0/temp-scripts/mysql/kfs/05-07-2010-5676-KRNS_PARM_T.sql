-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 5/7/10 1:16 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-05-07-5675-2-KRNS_PARM_T.xml::5675-2-1::Travis::(MD5Sum: cc3cb59ecdccb3cda6c6c7d1db1a686d)
-- Add New LD Parameter Type for Demerger
INSERT INTO `KRNS_PARM_T` (`VER_NBR`, `PARM_DESC_TXT`, `OBJ_ID`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `APPL_NMSPC_CD`, `TXT`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES (1, 'Document types that will demerge remove entire document from processing all entries with the same document number when an error is detected on one entry with that document number Document types not listed here will only have error entries removed from processing; no other records will be demerged', uuid(), 'DEMERGE_DOCUMENT_TYPES', 'LaborScrubberStep', 'A', 'KFS', 'BT;ST', 'CONFG', 'KFS-LD');


-- Release Database Lock

-- Release Database Lock


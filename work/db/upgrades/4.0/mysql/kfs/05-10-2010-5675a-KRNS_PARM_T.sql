-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 5/10/10 7:56 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-05-10-5675-3-KRNS_PARM_T.xml::5675-3-1::Travis::(MD5Sum: f090c722fbfdfc4edc867d1e6dfc1345)
-- Add New LD Parameter Type for Demerger
UPDATE `krns_parm_t` SET `txt` = 'BT;YEBT;ST;YEST' WHERE nmspc_cd='KFS-LD' AND PARM_DTL_TYP_CD='LaborScrubberStep' AND PARM_NM='DEMERGE_DOCUMENT_TYPES';


-- Release Database Lock

-- Release Database Lock


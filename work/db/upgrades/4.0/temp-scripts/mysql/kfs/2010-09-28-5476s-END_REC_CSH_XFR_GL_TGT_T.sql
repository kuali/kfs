-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/28/10 10:55 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-28-5476s-1-END_REC_CSH_XFR_GL_TGT_T.xml::5476s-1-1::Shawn::(MD5Sum: cf4e7379eb32fe55e0e2e393abe2a542)
-- need to have a field for TGT_ORG_REF_ID in END_REC_CSH_XFR_GL_TGT_T .
ALTER TABLE `END_REC_CSH_XFR_GL_TGT_T` ADD `TGT_ORG_REF_ID` VARCHAR(8);


-- Release Database Lock

-- Release Database Lock


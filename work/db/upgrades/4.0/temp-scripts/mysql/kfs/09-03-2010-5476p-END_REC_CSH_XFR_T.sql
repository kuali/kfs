-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/3/10 10:15 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-03-5476p-1-END_REC_CSH_XFT_T.xml::5476p-1-1::Shawn::(MD5Sum: ca82a71e7c786f1edfbafe146949efe)
-- have some wrong table names.
ALTER TABLE `END_REC_CSH_XFT_T` RENAME `END_REC_CSH_XFR_T`;


-- Changeset updates/2010-09-03-5476p-1-END_REC_CSH_XFT_T.xml::5476p-1-2::Shawn::(MD5Sum: 8e9dc16e41f8fa9ed56961ac58f9610)
-- have some wrong table names.
ALTER TABLE `END_REC_CSH_XFT_KEMID_TGT_T` RENAME `END_REC_CSH_XFR_KEMID_TGT_T`;


-- Changeset updates/2010-09-03-5476p-1-END_REC_CSH_XFT_T.xml::5476p-1-3::Shawn::(MD5Sum: 30e81531a0bd82367f22aed76599465)
-- have some wrong table names.
ALTER TABLE `END_REC_CSH_XFT_GL_TGT_T` RENAME `END_REC_CSH_XFR_GL_TGT_T`;


-- Release Database Lock

-- Release Database Lock


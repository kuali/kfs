-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 7/2/10 5:51 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-07-02-5817e-1-END_HIST_VAL_ADJ_DOC_T.xml::5817e-1-1::Muddu::(MD5Sum: 13ff42f61546d5221fe67f7694a18)
-- The specifications change by the functional user requires us to remove two columns from this table.
ALTER TABLE `END_HIST_VAL_ADJ_DOC_T` DROP COLUMN `SEC_CLS_CD`;


-- Changeset updates/2010-07-02-5817e-1-END_HIST_VAL_ADJ_DOC_T.xml::5817e-1-2::Muddu::(MD5Sum: c01a7bc8cfc937e08f65c84c4f63b36a)
-- The specifications change by the functional user requires us to remove two columns from this table.
ALTER TABLE `END_HIST_VAL_ADJ_DOC_T` DROP COLUMN `SEC_VLTN_MTHD`;


-- Release Database Lock

-- Release Database Lock


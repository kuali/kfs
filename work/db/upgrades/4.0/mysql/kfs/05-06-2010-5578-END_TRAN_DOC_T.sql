-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 5/6/10 12:38 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-05-06-5578a-1-END_TRAN_DOC.xml::5578a-1-1::Bonnie::(MD5Sum: 2347b6b97efce4d5d35ca6531a3bc9b)
-- add two additional fields for the END_TRAN_DOC_T table
ALTER TABLE `END_TRAN_DOC_T` ADD `FDOC_NXT_SRC_LN_NBR` DECIMAL(7,0);


-- Changeset updates/2010-05-06-5578a-1-END_TRAN_DOC.xml::5578a-1-2::Bonnie::(MD5Sum: 38a2f7968f716cdf096b6636617e0d9)
-- add two additional fields for the END_TRAN_DOC_T table
ALTER TABLE `END_TRAN_DOC_T` ADD `FDOC_NXT_TAR_LN_NBR` DECIMAL(7,0);


-- Release Database Lock

-- Release Database Lock


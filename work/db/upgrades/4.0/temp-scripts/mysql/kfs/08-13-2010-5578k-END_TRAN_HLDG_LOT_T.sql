-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 8/13/10 7:23 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-08-13-5578k-1-END_TRAN_HLDG_LOT_T.xml::5578k-1-1::Bonnie::(MD5Sum: c0a0be826fe40c533daa1256cbfd9)
-- create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_HLDG_LOT_T` ADD `NEW_LOT_IND` VARCHAR(1) NOT NULL DEFAULT 'N';


-- Release Database Lock

-- Release Database Lock


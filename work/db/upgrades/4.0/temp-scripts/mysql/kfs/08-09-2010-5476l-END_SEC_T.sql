-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 8/9/10 7:06 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-08-09-5476l-1-END_SEC_T.xml::5476l-1-1::Daniela::(MD5Sum: 49e397d0ff243fd862ed07b84f33214)
-- remove FRGN_WITH_PCT field from END_SEC_T table and add SEC_VAL_BY_MKT
ALTER TABLE `END_SEC_T` DROP COLUMN `FRGN_WITH_PCT`;


-- Changeset updates/2010-08-09-5476l-1-END_SEC_T.xml::5476l-1-2::Daniela::(MD5Sum: 346223394ad1e1df4aa06a1aa121d2c)
-- remove FRGN_WITH_PCT field from END_SEC_T table and add SEC_VAL_BY_MKT
ALTER TABLE `END_SEC_T` ADD `SEC_VAL_BY_MKT` DECIMAL(19,2);


-- Release Database Lock

-- Release Database Lock


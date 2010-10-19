-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 8/19/10 8:29 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-08-19-5476m-1-END_KEMID_PAY_INC_T.xml::5476m-1-1::Daniela::(MD5Sum: 59332a6544d408e865dd38b4c7aa76)
-- remove the PAY_INC_AMT field from END_KEMID_PAY_INC_T table
ALTER TABLE `END_KEMID_PAY_INC_T` DROP COLUMN `PAY_INC_AMT`;


-- Release Database Lock

-- Release Database Lock


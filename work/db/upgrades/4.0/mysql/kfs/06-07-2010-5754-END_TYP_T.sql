-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 6/7/10 1:53 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-06-07-5754-1-END_TYPE_T.xml::5754-1-1::Bonnie::(MD5Sum: 851f9d62a5741dfa79d2e75678bc8ad)
-- Endowment RFC: Move Income & Principal Restriction Code out of Type Cod and put them into KEMID
ALTER TABLE `END_TYP_T` DROP COLUMN `TYP_INC_RESTR_CD`;


-- Changeset updates/2010-06-07-5754-1-END_TYPE_T.xml::5754-1-2::Bonnie::(MD5Sum: 483fb9328aba4a4cd65f8ce7625205)
-- Endowment RFC: Move Income & Principal Restriction Code out of Type Cod and put them into KEMID
ALTER TABLE `END_TYP_T` DROP COLUMN `TYP_PRIN_RESTR_CD`;


-- Release Database Lock

-- Release Database Lock


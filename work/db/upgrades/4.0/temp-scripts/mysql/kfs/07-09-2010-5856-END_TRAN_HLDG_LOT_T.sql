-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 7/9/10 9:50 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-07-09-5856-1-END_TRAN_HLDG_LOT_T.xml::5856-1-1::Daniella::(MD5Sum: 5ed8452e5e9f53624f2b8af069524224)
-- Add new fields to END_TRAN_HLDG_LOT
DELETE FROM `END_TRAN_HLDG_LOT_T`;


-- Changeset updates/2010-07-09-5856-1-END_TRAN_HLDG_LOT_T.xml::5856-1-2::Daniella::(MD5Sum: 9f30e4a9ba1a81e392efce8045cdcca)
-- Add new fields to END_TRAN_HLDG_LOT
ALTER TABLE `END_TRAN_HLDG_LOT_T` ADD `KEMID` VARCHAR(10) NOT NULL;


-- Changeset updates/2010-07-09-5856-1-END_TRAN_HLDG_LOT_T.xml::5856-1-3::Daniella::(MD5Sum: 4bead13cb97b77c31ebffc2b3f2052)
-- Add new fields to END_TRAN_HLDG_LOT
ALTER TABLE `END_TRAN_HLDG_LOT_T` ADD `SEC_ID` VARCHAR(9) NOT NULL;


-- Changeset updates/2010-07-09-5856-1-END_TRAN_HLDG_LOT_T.xml::5856-1-4::Daniella::(MD5Sum: 158d5aae360eee2e05460e257b2f32a)
-- Add new fields to END_TRAN_HLDG_LOT
ALTER TABLE `END_TRAN_HLDG_LOT_T` ADD `REGIS_CD` VARCHAR(4) NOT NULL;


-- Changeset updates/2010-07-09-5856-1-END_TRAN_HLDG_LOT_T.xml::5856-1-5::Daniella::(MD5Sum: 8a52d3c99a817622df6512eea9c229a)
-- Add new fields to END_TRAN_HLDG_LOT
ALTER TABLE `END_TRAN_HLDG_LOT_T` ADD `HLDG_IP_IND` VARCHAR(1) NOT NULL;


-- Changeset updates/2010-07-09-5856-1-END_TRAN_HLDG_LOT_T.xml::5856-1-6::Daniella::(MD5Sum: d18111f5289dcac1d15f31dedc529c)
-- Add new fields to END_TRAN_HLDG_LOT
ALTER TABLE `END_TRAN_HLDG_LOT_T` ADD CONSTRAINT `END_TRAN_HLDG_LOT_TR2` FOREIGN KEY (`KEMID`, `SEC_ID`, `REGIS_CD`, `HLDG_IP_IND`, `TRAN_HLDG_LT_NBR`) REFERENCES `END_HLDG_TAX_LOT_T`(`KEMID`, `SEC_ID`, `REGIS_CD`, `HLDG_IP_IND`, `HLDG_LOT_NBR`);


-- Release Database Lock

-- Release Database Lock


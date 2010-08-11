-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 8/10/10 11:42 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-08-10-5926-1-FP_DV_NRA_TAX_T.xml::5926-1-1::Sam::(MD5Sum: 25481cecc8313651588774d521cea4)
-- Add new fields to table FP_DV_NRA_TAX_T
ALTER TABLE `FP_DV_NRA_TAX_T` ADD `SPCL_W4_INC_AMT` DECIMAL(19,2);


-- Changeset updates/2010-08-10-5926-1-FP_DV_NRA_TAX_T.xml::5926-1-2::Sam::(MD5Sum: 65b28b2def4258e4c21c62962b1d63)
-- Add new fields to table FP_DV_NRA_TAX_T
ALTER TABLE `FP_DV_NRA_TAX_T` ADD `USAID_DIEM_IND` VARCHAR(1);


-- Changeset updates/2010-08-10-5926-1-FP_DV_NRA_TAX_T.xml::5926-1-3::Sam::(MD5Sum: 903b1a7d3df9221f6dc312ce4da61f76)
-- Add new fields to table FP_DV_NRA_TAX_T
ALTER TABLE `FP_DV_NRA_TAX_T` ADD `INC_TAX_EXMPT_CD_OTHR_IND` VARCHAR(1);


-- Changeset updates/2010-08-10-5926-1-FP_DV_NRA_TAX_T.xml::5926-1-4::Sam::(MD5Sum: 37aa97b9092b274da4db2e566e1531)
-- Add new fields to table FP_DV_NRA_TAX_T
ALTER TABLE `FP_DV_NRA_TAX_T` ADD `NQI_CUST_TAX_ID` VARCHAR(50);


-- Release Database Lock

-- Release Database Lock


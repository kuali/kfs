-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 7/23/10 5:21 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-07-23-5578h-1-END_ENDOW_ACCT_LINES_T.xml::5578h-1-1::Daniela::(MD5Sum: 5ca647a88f82f3f8861d2a2db3dae465)
-- Create transactional document basic tables
CREATE TABLE `END_ENDOW_ACCT_LINES_T` (`FDOC_NBR` VARCHAR(14) NOT NULL, `FDOC_LINE_NBR` DECIMAL(7 , 0) NOT NULL, `FDOC_LN_TYP_CD` VARCHAR(1) NOT NULL, `OBJ_ID` VARCHAR(36) NOT NULL, `VER_NBR` DECIMAL(8 , 0) DEFAULT '1' NOT NULL, `FIN_COA_CD` VARCHAR(2), `ACCOUNT_NBR` VARCHAR(7), `FDOC_POST_YR` DECIMAL(4 , 0), `FIN_OBJECT_CD` VARCHAR(4), `FDOC_LINE_AMT` DECIMAL(19 , 2), `SUB_ACCT_NBR` VARCHAR(5), `FIN_SUB_OBJ_CD` VARCHAR(3), `PROJECT_CD` VARCHAR(10), `ORG_REFERENCE_ID` VARCHAR(8));


-- Changeset updates/2010-07-23-5578h-1-END_ENDOW_ACCT_LINES_T.xml::5578h-1-2::Daniela::(MD5Sum: cccca8ca4eb2366037b4a15279d7a6c)
-- Create transactional document basic tables
ALTER TABLE `END_ENDOW_ACCT_LINES_T` ADD PRIMARY KEY (`FDOC_NBR`, `FDOC_LINE_NBR`, `FDOC_LN_TYP_CD`);


-- Changeset updates/2010-07-23-5578h-1-END_ENDOW_ACCT_LINES_T.xml::5578h-1-3::Daniela::(MD5Sum: 27a48fe29865ff50b7994cfcac48c029)
-- Create transactional document basic tables
ALTER TABLE `END_ENDOW_ACCT_LINES_T` ADD CONSTRAINT `ENDOW_ACCT_LINES_TR2` FOREIGN KEY (`PROJECT_CD`) REFERENCES `CA_PROJECT_T`(`PROJECT_CD`);


-- Changeset updates/2010-07-23-5578h-1-END_ENDOW_ACCT_LINES_T.xml::5578h-1-4::Daniela::(MD5Sum: cecc9158665c43249abc7e4b8f35f12d)
-- Create transactional document basic tables
ALTER TABLE `END_ENDOW_ACCT_LINES_T` ADD CONSTRAINT `ENDOW_ACCT_LINES_TR4` FOREIGN KEY (`FDOC_POST_YR`, `FIN_COA_CD`, `FIN_OBJECT_CD`) REFERENCES `CA_OBJECT_CODE_T`(`UNIV_FISCAL_YR`, `FIN_COA_CD`, `FIN_OBJECT_CD`);


-- Changeset updates/2010-07-23-5578h-1-END_ENDOW_ACCT_LINES_T.xml::5578h-1-5::Daniela::(MD5Sum: 301b5c8baaf434d46e1c43efe243e8b5)
-- Create transactional document basic tables
ALTER TABLE `END_ENDOW_ACCT_LINES_T` ADD CONSTRAINT `ENDOW_ACCT_LINES_TR3` FOREIGN KEY (`FDOC_POST_YR`, `FIN_COA_CD`, `ACCOUNT_NBR`, `FIN_OBJECT_CD`, `FIN_SUB_OBJ_CD`) REFERENCES `CA_SUB_OBJECT_CD_T`(`UNIV_FISCAL_YR`, `FIN_COA_CD`, `ACCOUNT_NBR`, `FIN_OBJECT_CD`, `FIN_SUB_OBJ_CD`);


-- Changeset updates/2010-07-23-5578h-1-END_ENDOW_ACCT_LINES_T.xml::5578h-1-6::Daniela::(MD5Sum: e882c1cf715c109df637f90a942883)
-- Create transactional document basic tables
ALTER TABLE `END_ENDOW_ACCT_LINES_T` ADD CONSTRAINT `ENDOW_ACCT_LINES_TR6` FOREIGN KEY (`FIN_COA_CD`) REFERENCES `CA_CHART_T`(`FIN_COA_CD`);


-- Changeset updates/2010-07-23-5578h-1-END_ENDOW_ACCT_LINES_T.xml::5578h-1-7::Daniela::(MD5Sum: 85fec8d8f66c4f83ae6cf670f5f1f4)
-- Create transactional document basic tables
ALTER TABLE `END_ENDOW_ACCT_LINES_T` ADD CONSTRAINT `ENDOW_ACCT_LINES_TR7` FOREIGN KEY (`FIN_COA_CD`, `ACCOUNT_NBR`) REFERENCES `CA_ACCOUNT_T`(`FIN_COA_CD`, `ACCOUNT_NBR`);


-- Changeset updates/2010-07-23-5578h-1-END_ENDOW_ACCT_LINES_T.xml::5578h-1-8::Daniela::(MD5Sum: 0e1255e8be4d0509e1dd2ac4dd3ec4e)
-- Create transactional document basic tables
ALTER TABLE `END_ENDOW_ACCT_LINES_T` ADD CONSTRAINT `ENDOW_ACCT_LINES_TR5` FOREIGN KEY (`FIN_COA_CD`, `ACCOUNT_NBR`, `SUB_ACCT_NBR`) REFERENCES `CA_SUB_ACCT_T`(`FIN_COA_CD`, `ACCOUNT_NBR`, `SUB_ACCT_NBR`);


-- Changeset updates/2010-07-23-5578h-1-END_ENDOW_ACCT_LINES_T.xml::5578h-1-9::Daniela::(MD5Sum: a78b43ff34c3a9f1882f2aff6d8db316)
-- Create transactional document basic tables
ALTER TABLE `END_TRAN_DOC_T` ADD `FDOC_NXT_SRC_ACCT_LN_NBR` DECIMAL(7,0);


-- Changeset updates/2010-07-23-5578h-1-END_ENDOW_ACCT_LINES_T.xml::5578h-1-10::Daniela::(MD5Sum: ce1c4f49ea2a502e878abc8bb274ea1)
-- Create transactional document basic tables
ALTER TABLE `END_TRAN_DOC_T` ADD `FDOC_NXT_TAR_ACCT_LN_NBR` DECIMAL(7,0);


-- Release Database Lock

-- Release Database Lock


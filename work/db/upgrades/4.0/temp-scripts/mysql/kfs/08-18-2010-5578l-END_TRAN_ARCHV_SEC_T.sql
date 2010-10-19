-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 8/18/10 8:25 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-1::Bonnie::(MD5Sum: 8f4675e7b7a8cd6c945da942c62eda9)
-- update the transaction archive table and transaction archive security table
ALTER TABLE `END_HLDG_TAX_LOT_T` MODIFY `HLDG_FRGN_TAX_WITH` DECIMAL(19,5) DEFAULT null;


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-2::Bonnie::(MD5Sum: 32702fb72f1208e107c45e18c5857fe)
-- update the transaction archive table and transaction archive security table
DROP TABLE `END_TRAN_ARCHV_SEC_T`;


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-3::Bonnie::(MD5Sum: de01b7dcd4aef52536d1c4bde3c7f)
-- update the transaction archive table and transaction archive security table
DROP TABLE `END_TRAN_ARCHV_T`;


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-4::Bonnie::(MD5Sum: 9c4a6862b3322377098ef3ce59b4e19)
-- update the transaction archive table and transaction archive security table
CREATE TABLE `END_TRAN_ARCHV_T` (`FDOC_NBR` VARCHAR(14) NOT NULL, `FDOC_LN_NBR` DECIMAL(7) NOT NULL, `FDOC_LN_TYP_CD` VARCHAR(1) NOT NULL, `TRAN_DESC` VARCHAR(40), `DOC_TYP_NM` VARCHAR(64) NOT NULL, `TRAN_SUB_TYP_CD` VARCHAR(1) NOT NULL, `TRAN_SRC_TYP_CD` VARCHAR(1) NOT NULL, `TRAN_KEMID` VARCHAR(10) NOT NULL, `TRAN_ETRAN_CD` VARCHAR(9) NOT NULL, `TRAN_LN_DESC` VARCHAR(40), `TRAN_IP_IND_CD` VARCHAR(1) NOT NULL, `TRAN_INC_CSH_AMT` DECIMAL(19,2) NOT NULL, `TRAN_PRIN_CSH_AMT` DECIMAL(19,2) NOT NULL, `TRAN_CORPUS_IND` VARCHAR(1) NOT NULL, `TRAN_CORPUS_AMT` DECIMAL(19,2), `TRAN_PSTD_DT` DATE, `VER_NBR` DECIMAL(8,0) DEFAULT '1' NOT NULL, `OBJ_ID` VARCHAR(36) NOT NULL, CONSTRAINT `PK_END_TRAN_ARCHV_T` PRIMARY KEY (`FDOC_NBR`, `FDOC_LN_NBR`, `FDOC_LN_TYP_CD`));


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-5::Bonnie::(MD5Sum: baf1c7fc7a6a5a8968e512a773d851dc)
-- update the transaction archive table and transaction archive security table
ALTER TABLE `END_TRAN_ARCHV_T` ADD CONSTRAINT `END_TRAN_ARCHV_TR1` FOREIGN KEY (`FDOC_NBR`) REFERENCES `KRNS_DOC_HDR_T`(`DOC_HDR_ID`);


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-6::Bonnie::(MD5Sum: 423fbc8e6080b918798954414ddcbda4)
-- update the transaction archive table and transaction archive security table
ALTER TABLE `END_TRAN_ARCHV_T` ADD CONSTRAINT `END_TRAN_ARCHV_TR2` FOREIGN KEY (`TRAN_SUB_TYP_CD`) REFERENCES `END_TRAN_SUB_TYP_T`(`TRAN_SUB_TYP_CD`);


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-7::Bonnie::(MD5Sum: 73e954e9212a4e71a64064cf46b1385c)
-- update the transaction archive table and transaction archive security table
ALTER TABLE `END_TRAN_ARCHV_T` ADD CONSTRAINT `END_TRAN_ARCHV_TR3` FOREIGN KEY (`TRAN_SRC_TYP_CD`) REFERENCES `END_TRAN_SRC_TYP_T`(`TRAN_SRC_TYP_CD`);


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-8::Bonnie::(MD5Sum: 77722cea8cfe1bb5e136389a968a2f83)
-- update the transaction archive table and transaction archive security table
ALTER TABLE `END_TRAN_ARCHV_T` ADD CONSTRAINT `END_TRAN_ARCHV_TR4` FOREIGN KEY (`TRAN_KEMID`) REFERENCES `END_KEMID_T`(`KEMID`);


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-9::Bonnie::(MD5Sum: 322662bfd478ffb013d1979e7ebdbdd7)
-- update the transaction archive table and transaction archive security table
ALTER TABLE `END_TRAN_ARCHV_T` ADD CONSTRAINT `END_TRAN_ARCHV_TR5` FOREIGN KEY (`TRAN_ETRAN_CD`) REFERENCES `END_ETRAN_CD_T`(`ETRAN_CD`);


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-10::Bonnie::(MD5Sum: f3f5f2efdcccaf3795f55e9646739e2a)
-- update the transaction archive table and transaction archive security table
ALTER TABLE `END_TRAN_ARCHV_T` ADD CONSTRAINT `END_TRAN_ARCHV_TR6` FOREIGN KEY (`TRAN_IP_IND_CD`) REFERENCES `END_IP_IND_T`(`IP_IND_CD`);


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-11::Bonnie::(MD5Sum: c2b9d826d20f928d862be4142ac450)
-- update the transaction archive table and transaction archive security table
CREATE TABLE `END_TRAN_ARCHV_SEC_T` (`FDOC_NBR` VARCHAR(14) NOT NULL, `FDOC_LN_NBR` DECIMAL(7) NOT NULL, `FDOC_LN_TYP_CD` VARCHAR(1) NOT NULL, `TRAN_SEC_ID` VARCHAR(9) NOT NULL, `TRAN_SEC_REGIS_CD` VARCHAR(4) NOT NULL, `TRAN_SEC_ETRAN_CD` VARCHAR(9), `TRAN_SEC_UNITS` DECIMAL(16,4), `TRAN_SEC_UNIT_VAL` DECIMAL(19,5), `TRAN_SEC_COST` DECIMAL(19,2), `TRAN_SEC_LT_GAIN_LOSS` DECIMAL(19,2), `TRAN_SEC_ST_GAIN_LOSS` DECIMAL(19,2), `VER_NBR` DECIMAL(8,0) DEFAULT '1' NOT NULL, `OBJ_ID` VARCHAR(36) NOT NULL, CONSTRAINT `PK_END_TRAN_ARCHV_SEC_T` PRIMARY KEY (`FDOC_NBR`, `FDOC_LN_NBR`, `FDOC_LN_TYP_CD`));


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-12::Bonnie::(MD5Sum: a82af39e74583977f77ce38dea36f9)
-- update the transaction archive table and transaction archive security table
ALTER TABLE `END_TRAN_ARCHV_SEC_T` ADD CONSTRAINT `END_TRAN_ARCHV_SEC_TR1` FOREIGN KEY (`FDOC_NBR`, `FDOC_LN_TYP_CD`, `FDOC_LN_NBR`) REFERENCES `END_TRAN_ARCHV_T`(`FDOC_NBR`, `FDOC_LN_TYP_CD`, `FDOC_LN_NBR`);


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-13::Bonnie::(MD5Sum: 8d4fd5eb28cac4e63c8cbb631bd76c8c)
-- update the transaction archive table and transaction archive security table
ALTER TABLE `END_TRAN_ARCHV_SEC_T` ADD CONSTRAINT `END_TRAN_ARCHV_SEC_TR2` FOREIGN KEY (`TRAN_SEC_ID`) REFERENCES `END_SEC_T`(`SEC_ID`);


-- Changeset updates/2010-08-18-5578l-1-END_TRAN_ARCHV_SEC_T.xml::5578l-1-14::Bonnie::(MD5Sum: 56278724fe7bd15a496c08ead7a6c7a)
-- update the transaction archive table and transaction archive security table
ALTER TABLE `END_TRAN_ARCHV_SEC_T` ADD CONSTRAINT `END_TRAN_ARCHV_SEC_TR3` FOREIGN KEY (`TRAN_SEC_REGIS_CD`) REFERENCES `END_REGIS_CD_T`(`REGIS_CD`);


-- Release Database Lock

-- Release Database Lock




-- Changeset updates/2010-04-09-5578-0-dropEndowTables.xml::2010-04-09-5578d-1::Bonnie::(MD5Sum: 81f9f278847d2b4fd953ad1ee7e7be)
-- drop all tables and recreate them
DROP TABLE `END_TRAN_SEC_T`;


-- Changeset updates/2010-04-09-5578-0-dropEndowTables.xml::2010-04-09-5578d-2::Bonnie::(MD5Sum: e6ef44bf4edf7677f47a2d525875402a)
-- drop all tables and recreate them
DROP TABLE `END_TRAN_HLDG_LOT_T`;


-- Changeset updates/2010-04-09-5578-0-dropEndowTables.xml::2010-04-09-5578d-3::Bonnie::(MD5Sum: 43d010bf7e7454556b5b3c7fdd5)
-- drop all tables and recreate them
DROP TABLE `END_TRAN_LN_T`;


-- Changeset updates/2010-04-09-5578-0-dropEndowTables.xml::2010-04-09-5578d-4::Bonnie::(MD5Sum: 669233d8ae3bfe616c89b49ee456eac)
-- drop all tables and recreate them
DROP TABLE `END_TRAN_DOC_T`;


-- Changeset updates/2010-04-09-5578-0-dropEndowTables.xml::2010-04-09-5578d-5::Bonnie::(MD5Sum: 31c8d9915ed7d4efeb32448c481f79e1)
-- drop all tables and recreate them
DROP TABLE `END_TRAN_SUB_TYP_T`;


-- Changeset updates/2010-04-09-5578-0-dropEndowTables.xml::2010-04-09-5578d-6::Bonnie::(MD5Sum: 359c81b27660333098f2aea3a6e334f)
-- drop all tables and recreate them
DROP TABLE `END_TRAN_SRC_TYP_T`;


-- Changeset updates/2010-04-08-5578-Endow.xml::2010-4-08-5578c-1::Bonnie::(MD5Sum: fa649feba5edeb45b73adc20412aca2)
-- Endowment request: create transactional document basic tables in KULDBA
CREATE TABLE `END_TRAN_SUB_TYP_T` (`TRAN_SUB_TYP_CD` VARCHAR(1) NOT NULL, `TRAN_SUB_TYP_DESC` VARCHAR(40), `OBJ_ID` VARCHAR(36) NOT NULL, `VER_NBR` DECIMAL(8, 0) DEFAULT '1' NOT NULL, CONSTRAINT `PK_END_TRAN_SUB_TYP_T` PRIMARY KEY (`TRAN_SUB_TYP_CD`));


-- Changeset updates/2010-04-08-5578-Endow.xml::2010-4-08-5578c-2::Bonnie::(MD5Sum: d254539ca34145f9e144b677e918ef2e)
-- Endowment request: create transactional document basic tables in KULDBA
CREATE TABLE `END_TRAN_SRC_TYP_T` (`TRAN_SRC_TYP_CD` VARCHAR(1) NOT NULL, `TRAN_SRC_TYP_DESC` VARCHAR(40), `OBJ_ID` VARCHAR(36) NOT NULL, `VER_NBR` DECIMAL(8, 0) DEFAULT '1' NOT NULL, CONSTRAINT `PK_END_TRAN_SRC_TYP_T` PRIMARY KEY (`TRAN_SRC_TYP_CD`));


-- Changeset updates/2010-04-08-5578-Endow.xml::2010-4-08-5578c-3::Bonnie::(MD5Sum: cf76fa305344a7fd388f9572acc4485)
-- Endowment request: create transactional document basic tables in KULDBA
CREATE TABLE `END_TRAN_DOC_T` (`FDOC_NBR` VARCHAR(14) NOT NULL, `TRAN_SUB_TYP_CD` VARCHAR(1) NOT NULL, `TRAN_SRC_TYP_CD` VARCHAR(1) NOT NULL, `TRAN_PSTD` VARCHAR(1) DEFAULT 'N' NOT NULL, `OBJ_ID` VARCHAR(36) NOT NULL, `VER_NBR` DECIMAL(8, 0) DEFAULT '1' NOT NULL, CONSTRAINT `PK_END_TRAN_DOC_T` PRIMARY KEY (`FDOC_NBR`));


-- Changeset updates/2010-04-08-5578-Endow.xml::2010-4-08-5578c-4::Bonnie::(MD5Sum: d2332da770ae4b7d3c5642d1bae83ff)
-- Endowment request: create transactional document basic tables in KULDBA
CREATE TABLE `END_TRAN_LN_T` (`FDOC_NBR` VARCHAR(14) NOT NULL, `FDOC_LN_TYP_CD` VARCHAR(1) NOT NULL, `FDOC_LN_NBR` DECIMAL(7) NOT NULL, `KEMID` VARCHAR(10) NOT NULL, `ETRAN_CD` VARCHAR(9) NOT NULL, `TRAN_LN_DESC` VARCHAR(40), `TRAN_IP_IND_CD` VARCHAR(1) NOT NULL, `TRAN_AMT` DECIMAL(19,2) NOT NULL, `CORPUS_IND` VARCHAR(1) DEFAULT 'N' NOT NULL, `TRAN_UNITS` DECIMAL(16,4), `LN_PSTD` VARCHAR(1) DEFAULT 'N' NOT NULL, `OBJ_ID` VARCHAR(36) NOT NULL, `VER_NBR` DECIMAL(8, 0) DEFAULT '1' NOT NULL, CONSTRAINT `PK_END_TRAN_LN_T` PRIMARY KEY (`FDOC_NBR`, `FDOC_LN_TYP_CD`, `FDOC_LN_NBR`));


-- Changeset updates/2010-04-08-5578-Endow.xml::2010-4-08-5578c-5::Bonnie::(MD5Sum: ec21951ee517872643b8645c4661f4e2)
-- Endowment request: create transactional document basic tables in KULDBA
CREATE TABLE `END_TRAN_SEC_T` (`FDOC_NBR` VARCHAR(14) NOT NULL, `SEC_LN_TYP_CD` VARCHAR(1) NOT NULL, `SEC_ID` VARCHAR(9) NOT NULL, `SEC_REGIS_CD` VARCHAR(4) NOT NULL, `OBJ_ID` VARCHAR(36) NOT NULL, `VER_NBR` DECIMAL(8, 0) DEFAULT '1' NOT NULL, CONSTRAINT `PK_END_TRAN_SEC_T` PRIMARY KEY (`FDOC_NBR`, `SEC_LN_TYP_CD`));


-- Changeset updates/2010-04-08-5578-Endow.xml::2010-4-08-5578c-6::Bonnie::(MD5Sum: f420d5ee7ab1895dd754bee5f764be)
-- Endowment request: create transactional document basic tables in KULDBA
CREATE TABLE `END_TRAN_HLDG_LOT_T` (`FDOC_NBR` VARCHAR(14) NOT NULL, `FDOC_LN_NBR` DECIMAL(7) NOT NULL, `FDOC_LN_TYP_CD` VARCHAR(1) NOT NULL, `TRAN_HLDG_LT_NBR` DECIMAL(4) NOT NULL, `LOT_UNITS` DECIMAL(16,4) NOT NULL, `LOT_HLDG_COST` DECIMAL(19,2), `LOT_LT_GAIN_LOSS` DECIMAL(19,2), `LOT_ST_GAIN_LOSS` DECIMAL(19,2), `OBJ_ID` VARCHAR(36) NOT NULL, `VER_NBR` DECIMAL(8, 0) DEFAULT '1' NOT NULL, CONSTRAINT `PK_END_TRAN_HLDG_LOT_T` PRIMARY KEY (`FDOC_NBR`, `FDOC_LN_NBR`, `FDOC_LN_TYP_CD`, `TRAN_HLDG_LT_NBR`));


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-1::Bonnie::(MD5Sum: cb4fa72f9694d21324dbe4f6d7625)
-- Endowment request: create transactional document basic tables in KULDBA
INSERT INTO `END_TRAN_SUB_TYP_T` (`TRAN_SUB_TYP_DESC`, `OBJ_ID`, `TRAN_SUB_TYP_CD`) VALUES ('Cash', uuid(), 'C');


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-2::Bonnie::(MD5Sum: 877dc1d58c6ba0d397bce252c631681)
-- Endowment request: create transactional document basic tables in KULDBA
INSERT INTO `END_TRAN_SUB_TYP_T` (`TRAN_SUB_TYP_DESC`, `OBJ_ID`, `TRAN_SUB_TYP_CD`) VALUES ('Non-Cash', uuid(), 'N');


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-3::Bonnie::(MD5Sum: 2fdef8fe82c6bc737c05c7b3d5618c2)
-- Endowment request: create transactional document basic tables in KULDBA
INSERT INTO `END_TRAN_SRC_TYP_T` (`TRAN_SRC_TYP_DESC`, `OBJ_ID`, `TRAN_SRC_TYP_CD`) VALUES ('Automated Entry', uuid(), 'A');


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-4::Bonnie::(MD5Sum: 23301c64a5e4417963cf23cf22b6ff3a)
-- Endowment request: create transactional document basic tables in KULDBA
INSERT INTO `END_TRAN_SRC_TYP_T` (`TRAN_SRC_TYP_DESC`, `OBJ_ID`, `TRAN_SRC_TYP_CD`) VALUES ('Manual Entry', uuid(), 'M');


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-5::Bonnie::(MD5Sum: 5cf0664a7a4f4c0f6a3112b4db4ba38)
-- Endowment request: create transactional document basic tables in KULDBA
INSERT INTO `END_TRAN_SRC_TYP_T` (`TRAN_SRC_TYP_DESC`, `OBJ_ID`, `TRAN_SRC_TYP_CD`) VALUES ('Recurring Entry', uuid(), 'R');


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-6::Bonnie::(MD5Sum: 44262cf02fc8aa8f79594476757e8e4e)
-- Endowment request: create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_DOC_T` ADD CONSTRAINT `END_TRAN_DOC_TR1` FOREIGN KEY (`FDOC_NBR`) REFERENCES `KRNS_DOC_HDR_T`(`DOC_HDR_ID`);


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-7::Bonnie::(MD5Sum: fcad2148471b49a7e74f9578f395be6)
-- Endowment request: create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_DOC_T` ADD CONSTRAINT `END_TRAN_DOC_TR2` FOREIGN KEY (`TRAN_SUB_TYP_CD`) REFERENCES `END_TRAN_SUB_TYP_T`(`TRAN_SUB_TYP_CD`);


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-8::Bonnie::(MD5Sum: fd7dd669668c53991a2f8588bddd763a)
-- Endowment request: create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_DOC_T` ADD CONSTRAINT `END_TRAN_DOC_TR3` FOREIGN KEY (`TRAN_SRC_TYP_CD`) REFERENCES `END_TRAN_SRC_TYP_T`(`TRAN_SRC_TYP_CD`);


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-9::Bonnie::(MD5Sum: 521dd2a0955f5b27dd4dad9ef18cfd49)
-- Endowment request: create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_LN_T` ADD CONSTRAINT `END_TRAN_LN_TR1` FOREIGN KEY (`FDOC_NBR`) REFERENCES `END_TRAN_DOC_T`(`FDOC_NBR`);


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-10::Bonnie::(MD5Sum: ca2cd24c1772ec92d91cc1ecd185c9b)
-- Endowment request: create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_LN_T` ADD CONSTRAINT `END_TRAN_LN_TR2` FOREIGN KEY (`KEMID`) REFERENCES `END_KEMID_T`(`KEMID`);


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-11::Bonnie::(MD5Sum: 779c59f684f080c041829a10c63b5)
-- Endowment request: create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_LN_T` ADD CONSTRAINT `END_TRAN_LN_TR3` FOREIGN KEY (`ETRAN_CD`) REFERENCES `END_ETRAN_CD_T`(`ETRAN_CD`);


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-12::Bonnie::(MD5Sum: 7e33c86167e426d5ea5df5fcbcf9b0)
-- Endowment request: create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_LN_T` ADD CONSTRAINT `END_TRAN_LN_TR4` FOREIGN KEY (`TRAN_IP_IND_CD`) REFERENCES `END_IP_IND_T`(`IP_IND_CD`);


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-13::Bonnie::(MD5Sum: 9cdbc57ddca3fee3aea3e985f08b22)
-- Endowment request: create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_SEC_T` ADD CONSTRAINT `END_TRAN_SEC_TR1` FOREIGN KEY (`FDOC_NBR`) REFERENCES `END_TRAN_DOC_T`(`FDOC_NBR`);


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-14::Bonnie::(MD5Sum: 447632d98f276a5124828147d1fe32)
-- Endowment request: create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_SEC_T` ADD CONSTRAINT `END_TRAN_SEC_TR2` FOREIGN KEY (`SEC_ID`) REFERENCES `END_SEC_T`(`SEC_ID`);


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-15::Bonnie::(MD5Sum: 9676142919f377a87c1747888e1b75c7)
-- Endowment request: create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_SEC_T` ADD CONSTRAINT `END_TRAN_SEC_TR3` FOREIGN KEY (`SEC_REGIS_CD`) REFERENCES `END_REGIS_CD_T`(`REGIS_CD`);


-- Changeset updates/2010-04-09-5578-1-createEndowTransactionDocumentBasicTables.xml::5578-1-16::Bonnie::(MD5Sum: 39727aef119b86589772d97b19cf587)
-- Endowment request: create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_HLDG_LOT_T` ADD CONSTRAINT `END_TRAN_HLDG_LOT_TR1` FOREIGN KEY (`FDOC_NBR`, `FDOC_LN_NBR`, `FDOC_LN_TYP_CD`) REFERENCES `END_TRAN_LN_T`(`FDOC_NBR`, `FDOC_LN_NBR`, `FDOC_LN_TYP_CD`);


-- Release Database Lock

-- Release Database Lock


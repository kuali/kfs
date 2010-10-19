-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/27/10 10:08 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-27-5476r-1-END_KEMID_ROLE_T.xml::5476r-1-2::Bonnie::(MD5Sum: e2525864fce58f406075da1c28c87e3)
-- create Endowment Tables and Sequences
DROP TABLE `XFR_NBR_SEQ`;


-- Changeset updates/2010-09-27-5476r-1-END_KEMID_ROLE_T.xml::5476r-1-4::Bonnie::(MD5Sum: 3b6989a4b46646174a54afd8363ab16)
-- create Endowment Tables and Sequences
CREATE TABLE `END_REC_CSH_XFR_SEQ` (`id` BIGINT(19) AUTO_INCREMENT  NOT NULL, CONSTRAINT `id` PRIMARY KEY (`id`));

ALTER TABLE END_REC_CSH_XFR_SEQ auto_increment=1;


-- Changeset updates/2010-09-27-5476r-1-END_KEMID_ROLE_T.xml::5476r-1-5::Bonnie::(MD5Sum: dd5ce4cc925067f0309eed70696f83ae)
-- create Endowment Tables and Sequences
CREATE TABLE `END_KEMID_ROLE_T` (`KEMID` VARCHAR(10) NOT NULL, `ROLE_SEQ_NBR` VARCHAR(4) NOT NULL, `ROLE_ID` VARCHAR(40) NOT NULL, `ROLE_PRNCPL_ID` VARCHAR(40) NOT NULL, `ROLE_TERM_DT` DATE, `ROW_ACTV_IND` VARCHAR(1) DEFAULT 'Y', `OBJ_ID` VARCHAR(36) NOT NULL, `VER_NBR` DECIMAL(8) DEFAULT '1' NOT NULL, CONSTRAINT `PK_END_KEMID_ROLE_T` PRIMARY KEY (`KEMID`, `ROLE_SEQ_NBR`, `ROLE_ID`, `ROLE_PRNCPL_ID`));


-- Changeset updates/2010-09-27-5476r-1-END_KEMID_ROLE_T.xml::5476r-1-6::Bonnie::(MD5Sum: 53ee6c956ca2ef3411f47bec0e86b28)
-- create Endowment Tables and Sequences
ALTER TABLE `END_KEMID_ROLE_T` ADD CONSTRAINT `END_KEMID_ROLE_TR1` FOREIGN KEY (`KEMID`) REFERENCES `END_KEMID_T`(`KEMID`);


-- Changeset updates/2010-09-27-5476r-1-END_KEMID_ROLE_T.xml::5476r-1-7::Bonnie::(MD5Sum: 46d494183e2a33046228a75c94d2bf6)
-- create Endowment Tables and Sequences
DROP TABLE `END_ROLE_CD_T`;


-- Release Database Lock

-- Release Database Lock


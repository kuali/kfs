-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 6/22/10 7:43 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-06-22-5817a-1-END_HIST_VAL_ADJ_DOC_T.xml::5817a-1-1::Muddu::(MD5Sum: 65a355f2c13cfe6b63b11cb62316c96)
-- Create END_HIST_VAL_ADJ_DOC_T
DROP TABLE `END_HIST_VAL_ADJ_DOC_T`;


-- Changeset updates/2010-06-22-5817a-1-END_HIST_VAL_ADJ_DOC_T.xml::5817a-1-2::Muddu::(MD5Sum: 47361ae6d390b864bee6e9b9c20d48)
-- Create END_HIST_VAL_ADJ_DOC_T
CREATE TABLE `END_HIST_VAL_ADJ_DOC_T` (`FDOC_NBR` VARCHAR(14) NOT NULL, `SEC_ID` VARCHAR(9) NOT NULL, `SEC_CLS_CD` VARCHAR(3), `SEC_VLTN_MTHD` VARCHAR(1), `HLDG_ME_DT_ID` DECIMAL(3, 0) NOT NULL, `SEC_UNIT_VAL` DECIMAL(19 , 5) NOT NULL, `SEC_MVAL` DECIMAL(19 , 2), `TRAN_PSTD` VARCHAR(1) DEFAULT 'N' NOT NULL, `OBJ_ID` VARCHAR(36) NOT NULL, `VER_NBR` DECIMAL(8, 0) DEFAULT '1' NOT NULL);


-- Changeset updates/2010-06-22-5817a-1-END_HIST_VAL_ADJ_DOC_T.xml::5817a-1-3::Muddu::(MD5Sum: 27dcbc5ffa3cf3bfb2361d1f8efee6ca)
-- Create END_HIST_VAL_ADJ_DOC_T
ALTER TABLE `END_HIST_VAL_ADJ_DOC_T` ADD PRIMARY KEY (`FDOC_NBR`);


-- Changeset updates/2010-06-22-5817a-1-END_HIST_VAL_ADJ_DOC_T.xml::5817a-1-4::Muddu::(MD5Sum: 59d9fd54411a62f2ea5c39cdbe3886a)
-- Create END_HIST_VAL_ADJ_DOC_T
ALTER TABLE `END_HIST_VAL_ADJ_DOC_T` ADD CONSTRAINT `END_HIST_VAL_ADJ_DOC_TR1` FOREIGN KEY (`FDOC_NBR`) REFERENCES `KRNS_DOC_HDR_T`(`DOC_HDR_ID`);


-- Changeset updates/2010-06-22-5817a-1-END_HIST_VAL_ADJ_DOC_T.xml::5817a-1-5::Muddu::(MD5Sum: f4dd1fdba4e53dab309a97c9ed5b681)
-- Create END_HIST_VAL_ADJ_DOC_T
ALTER TABLE `END_HIST_VAL_ADJ_DOC_T` ADD CONSTRAINT `END_HIST_VAL_ADJ_DOC_TR2` FOREIGN KEY (`SEC_ID`) REFERENCES `END_SEC_T`(`SEC_ID`);


-- Changeset updates/2010-06-22-5817a-1-END_HIST_VAL_ADJ_DOC_T.xml::5817a-1-6::Muddu::(MD5Sum: 4b27785956179274aaf2c1749e9352b)
-- Create END_HIST_VAL_ADJ_DOC_T
ALTER TABLE `END_HIST_VAL_ADJ_DOC_T` ADD CONSTRAINT `END_HIST_VAL_ADJ_DOC_TR3` FOREIGN KEY (`HLDG_ME_DT_ID`) REFERENCES `END_ME_DT_T`(`ME_DT_ID`);


-- Release Database Lock

-- Release Database Lock


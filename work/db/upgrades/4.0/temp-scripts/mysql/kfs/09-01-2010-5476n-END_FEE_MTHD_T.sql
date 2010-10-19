-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/1/10 10:26 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-01-5476n-1-END_FEE_TRAN_DOC_T.xml::5476n-1-1::Muddu::(MD5Sum: c528355a548d5977456721da2479ca3)
-- need to make column changes to END_FEE_MTHD_T table.  alter table END_FEE_MTHD_T change column FEE_BY_TRAN_TYP FEE_BY_TRAN_DOC_TYP varchar(1);
ALTER TABLE `END_FEE_MTHD_T` CHANGE `FEE_BY_TRAN_TYP` `FEE_BY_TRAN_DOC_TYP` varchar(1);


-- Changeset updates/2010-09-01-5476n-1-END_FEE_TRAN_DOC_T.xml::5476n-1-2::Muddu::(MD5Sum: 4879c134162d4adf4bb782b5ff895ab)
-- need to make column changes to END_FEE_MTHD_T table.
ALTER TABLE `END_FEE_MTHD_T` ADD `FEE_MIN_THRSHLD` DECIMAL(4,2);


-- Changeset updates/2010-09-01-5476n-1-END_FEE_TRAN_DOC_T.xml::5476n-1-3::Muddu::(MD5Sum: c418851615d81facb5179bc176b05f26)
-- need to make column changes to END_FEE_MTHD_T table.
CREATE TABLE `END_FEE_TRAN_DOC_TYP_T` (`FEE_MTHD` VARCHAR(12) NOT NULL, `DOC_TYP_NM` VARCHAR(64) NOT NULL, `INCL` VARCHAR(1) DEFAULT 'Y' NOT NULL, `OBJ_ID` VARCHAR(36) NOT NULL, `VER_NBR` DECIMAL(8,0) DEFAULT '1' NOT NULL);


-- Changeset updates/2010-09-01-5476n-1-END_FEE_TRAN_DOC_T.xml::5476n-1-4::Muddu::(MD5Sum: 69a1d4bbce5b7427947ba71f1c5db2e)
-- need to make column changes to END_FEE_MTHD_T table.
ALTER TABLE `END_FEE_TRAN_DOC_TYP_T` ADD PRIMARY KEY (`FEE_MTHD`, `DOC_TYP_NM`);


-- Changeset updates/2010-09-01-5476n-1-END_FEE_TRAN_DOC_T.xml::5476n-1-5::Muddu::(MD5Sum: e458dee852e8ae28f718e7ad4a37f7)
-- need to make column changes to END_FEE_MTHD_T table.
ALTER TABLE `END_FEE_TRAN_DOC_TYP_T` ADD CONSTRAINT `END_FEE_TRAN_DOC_TYP_TR1` FOREIGN KEY (`FEE_MTHD`) REFERENCES `END_FEE_MTHD_T`(`FEE_MTHD`);


-- Changeset updates/2010-09-01-5476n-1-END_FEE_TRAN_DOC_T.xml::5476n-1-6::Muddu::(MD5Sum: e6433b7320a6b07182e6899376261)
-- need to make column changes to END_FEE_MTHD_T table.
DROP TABLE `END_FEE_TRAN_TYP_T`;


-- Release Database Lock

-- Release Database Lock


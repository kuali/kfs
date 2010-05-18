-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 5/17/10 2:51 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-05-17-5693-FP_DV_DOC_T.xml::5693-1-1::Muddu::(MD5Sum: 904a9432e15016a8d3610e729c1ff5)
-- Need to add a column for Exception Attached to FP_DV_DOC_T Table.
ALTER TABLE `FP_DV_DOC_T` ADD `DV_EXCPT_IND` VARCHAR(1) NOT NULL;


-- Changeset updates/2010-05-17-5693-FP_DV_DOC_T.xml::5693-1-2::Muddu::(MD5Sum: d4c6da74189bc47e70fd8784d8ef2daf)
-- Need to add a column for Exception Attached to FP_DV_DOC_T Table.
ALTER TABLE `FP_DV_DOC_T` MODIFY `DV_EXCPT_IND` varchar(1) DEFAULT 'N';


-- Release Database Lock

-- Release Database Lock


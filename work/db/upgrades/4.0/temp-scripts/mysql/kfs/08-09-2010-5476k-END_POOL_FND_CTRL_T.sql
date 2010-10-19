-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 8/9/10 5:56 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-08-09-5476k-1-END_POOL_FND_CTRL_T.xml::5476k-1-1::Daniela::(MD5Sum: 27251db0ce4921d7742c6a438eb8f3)
-- remove PF_BATCH_ID field from END_POOL_FND_CTRL_T and add FRXNL_SHS_IND
ALTER TABLE `END_POOL_FND_CTRL_T` DROP COLUMN `PF_BATCH_ID`;


-- Changeset updates/2010-08-09-5476k-1-END_POOL_FND_CTRL_T.xml::5476k-1-2::Daniela::(MD5Sum: 8d2ce121b4e345ce93641f395e12bdd4)
-- remove PF_BATCH_ID field from END_POOL_FND_CTRL_T and add FRXNL_SHS_IND
ALTER TABLE `END_POOL_FND_CTRL_T` ADD `FRXNL_SHS_IND` VARCHAR(1) NOT NULL DEFAULT 'N';


-- Release Database Lock

-- Release Database Lock


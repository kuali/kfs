-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/24/10 12:54 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-24-5476q-1-END_AUTO_CSH_INVEST_MDL_T.xml::5476q-1-1::Bonnie::(MD5Sum: c77765f5c6b78426af656e2e6140a697)
-- need to remove all registration code fields and constraits from END_AUTO_CSH_INVEST_MDL_T and add POOL_REGIS_CD to END_POOL_FND_CTRL_T.
ALTER TABLE `END_AUTO_CSH_INVEST_MDL_T` DROP FOREIGN KEY `END_AUTO_CSH_INVEST_MDL_TR6`;


-- Changeset updates/2010-09-24-5476q-1-END_AUTO_CSH_INVEST_MDL_T.xml::5476q-1-2::Bonnie::(MD5Sum: 41e6e84cc5c79d5cb55cdc8dcc1a222f)
-- need to remove all registration code fields and constraits from END_AUTO_CSH_INVEST_MDL_T and add POOL_REGIS_CD to END_POOL_FND_CTRL_T.
ALTER TABLE `END_AUTO_CSH_INVEST_MDL_T` DROP FOREIGN KEY `END_AUTO_CSH_INVEST_MDL_TR7`;


-- Changeset updates/2010-09-24-5476q-1-END_AUTO_CSH_INVEST_MDL_T.xml::5476q-1-3::Bonnie::(MD5Sum: 23aa4959cbe0cbbd1adfdbf425a7c40)
-- need to remove all registration code fields and constraits from END_AUTO_CSH_INVEST_MDL_T and add POOL_REGIS_CD to END_POOL_FND_CTRL_T.
ALTER TABLE `END_AUTO_CSH_INVEST_MDL_T` DROP FOREIGN KEY `END_AUTO_CSH_INVEST_MDL_TR8`;


-- Changeset updates/2010-09-24-5476q-1-END_AUTO_CSH_INVEST_MDL_T.xml::5476q-1-4::Bonnie::(MD5Sum: 10f074fbc54a6a04ad5f1d691be8)
-- need to remove all registration code fields and constraits from END_AUTO_CSH_INVEST_MDL_T and add POOL_REGIS_CD to END_POOL_FND_CTRL_T.
ALTER TABLE `END_AUTO_CSH_INVEST_MDL_T` DROP FOREIGN KEY `END_AUTO_CSH_INVEST_MDL_TR9`;


-- Changeset updates/2010-09-24-5476q-1-END_AUTO_CSH_INVEST_MDL_T.xml::5476q-1-5::Bonnie::(MD5Sum: b64cf459aace39c382315136aff490ef)
-- need to remove all registration code fields and constraits from END_AUTO_CSH_INVEST_MDL_T and add POOL_REGIS_CD to END_POOL_FND_CTRL_T.
ALTER TABLE `END_AUTO_CSH_INVEST_MDL_T` DROP COLUMN `ACI_POOL1_REGIS_CD`;


-- Changeset updates/2010-09-24-5476q-1-END_AUTO_CSH_INVEST_MDL_T.xml::5476q-1-6::Bonnie::(MD5Sum: c8a29c87a4dceac648f95bdd909f455b)
-- need to remove all registration code fields and constraits from END_AUTO_CSH_INVEST_MDL_T and add POOL_REGIS_CD to END_POOL_FND_CTRL_T.
ALTER TABLE `END_AUTO_CSH_INVEST_MDL_T` DROP COLUMN `ACI_POOL2_REGIS_CD`;


-- Changeset updates/2010-09-24-5476q-1-END_AUTO_CSH_INVEST_MDL_T.xml::5476q-1-7::Bonnie::(MD5Sum: 478b38dda6b848137aebc47b3e07595)
-- need to remove all registration code fields and constraits from END_AUTO_CSH_INVEST_MDL_T and add POOL_REGIS_CD to END_POOL_FND_CTRL_T.
ALTER TABLE `END_AUTO_CSH_INVEST_MDL_T` DROP COLUMN `ACI_POOL3_REGIS_CD`;


-- Changeset updates/2010-09-24-5476q-1-END_AUTO_CSH_INVEST_MDL_T.xml::5476q-1-8::Bonnie::(MD5Sum: 9d66a82c70accf8924489ab692d2be9)
-- need to remove all registration code fields and constraits from END_AUTO_CSH_INVEST_MDL_T and add POOL_REGIS_CD to END_POOL_FND_CTRL_T.
ALTER TABLE `END_AUTO_CSH_INVEST_MDL_T` DROP COLUMN `ACI_POOL4_REGIS_CD`;


-- Changeset updates/2010-09-24-5476q-1-END_AUTO_CSH_INVEST_MDL_T.xml::5476q-1-9::Bonnie::(MD5Sum: 4ad8fae5b329d13057c842113e6139)
-- need to remove all registration code fields and constraits from END_AUTO_CSH_INVEST_MDL_T and add POOL_REGIS_CD to END_POOL_FND_CTRL_T.
ALTER TABLE `END_POOL_FND_CTRL_T` ADD `POOL_REGIS_CD` VARCHAR(4);


-- Changeset updates/2010-09-24-5476q-1-END_AUTO_CSH_INVEST_MDL_T.xml::5476q-1-10::Bonnie::(MD5Sum: a646efc67763f2f4babc7db83bcec488)
-- need to remove all registration code fields and constraits from END_AUTO_CSH_INVEST_MDL_T and add POOL_REGIS_CD to END_POOL_FND_CTRL_T.
ALTER TABLE `END_POOL_FND_CTRL_T` ADD CONSTRAINT `END_POOL_FND_CTRL_TR7` FOREIGN KEY (`POOL_REGIS_CD`) REFERENCES `END_REGIS_CD_T`(`REGIS_CD`);


-- Release Database Lock

-- Release Database Lock


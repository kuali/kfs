-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 5/26/10 2:37 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-05-26-5477-a2-Endow.xml::5477-a2-1::Bonnie::(MD5Sum: 8d82e86e5503551597933208a172dc5)
-- Endowment request: insert Endowment sample data to KULDBA
DELETE FROM `END_HLDG_HIST_T`  WHERE END_SEC_T.SEC_ID=END_HLDG_HIST_T.SEC_ID AND END_SEC_T.SEC_CLS_CD='580');


-- Changeset updates/2010-05-26-5477-a2-Endow.xml::5477-a2-2::Bonnie::(MD5Sum: d6bd4eb65dc2ce1eadde428faa5d292)
-- Endowment request: insert Endowment sample data to KULDBA
DELETE FROM `END_HLDG_TAX_LOT_T`  WHERE END_SEC_T.SEC_ID=END_HLDG_TAX_LOT_T.SEC_ID AND END_SEC_T.SEC_CLS_CD='580');


-- Changeset updates/2010-05-26-5477-a2-Endow.xml::5477-a2-3::Bonnie::(MD5Sum: 2b5b2845494612bb73a5a27193d7d6)
-- Endowment request: insert Endowment sample data to KULDBA
DELETE FROM `END_CRNT_TAX_LOT_BAL_T`  WHERE END_SEC_T.SEC_ID=END_CRNT_TAX_LOT_BAL_T.SEC_ID AND END_SEC_T.SEC_CLS_CD='580');


-- Changeset updates/2010-05-26-5477-a2-Endow.xml::5477-a2-4::Bonnie::(MD5Sum: d193c2e8d5bf596428eda82eb6f3725)
-- Endowment request: insert Endowment sample data to KULDBA
DELETE FROM `END_SEC_T`  WHERE END_SEC_T.SEC_CLS_CD='580';


-- Changeset updates/2010-05-26-5478-1-END_TRAN_LN_T.xml::5478-1-1::Bonnie::(MD5Sum: 726188362da41a82426a36a82e295b45)
-- Endowment request: create transactional document basic tables in KULDBA
ALTER TABLE `END_TRAN_LN_T` ADD `UNIT_ADJ_AMT` DECIMAL(19,5);


-- Release Database Lock

-- Release Database Lock


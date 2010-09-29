-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/28/10 2:02 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-28-6031-1-FS_ORIGIN_CODE_T.xml::6031-1-1::Dan::(MD5Sum: 9eb3dd1a18303750d551e67e7672cd)
-- Persist Kuali Endowment Origination Code to the base data
INSERT INTO `FS_ORIGIN_CODE_T` (`VER_NBR`, `FS_ORIGIN_CD`, `OBJ_ID`, `FS_DATABASE_DESC`, `FS_DATABASE_NM`, `FS_SERVER_NM`, `ROW_ACTV_IND`) VALUES (1, 'EM', uuid(), 'KFS', 'KFS', 'KULDBA', 'Y');


-- Release Database Lock

-- Release Database Lock


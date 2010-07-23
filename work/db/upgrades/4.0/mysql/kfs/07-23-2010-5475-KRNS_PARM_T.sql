-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 7/23/10 5:02 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-07-23-5475e-1-KRNS_PARM_T.xml::5475e-1-1::Muddu::(MD5Sum: 9066b299aa2173e88e9ac2dfbd873b)
-- Endowment need to create two more system parameters to the master data source.
INSERT INTO `KRNS_PARM_T` (`VER_NBR`, `PARM_DESC_TXT`, `OBJ_ID`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `APPL_NMSPC_CD`, `TXT`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES (1, 'Used to calculate the percent of the market value of pooled funds considered available for spending', uuid(), 'AVAILABLE_CASH_PERCENT', 'AvailableCashUpdateStep', 'A', 'KUALI', '90', 'CONFG', 'KFS-ENDOW');


-- Changeset updates/2010-07-23-5475e-1-KRNS_PARM_T.xml::5475e-1-2::Muddu::(MD5Sum: 748d4273e9e85d2d774329959f75696)
-- Endowment need to create two more system parameters to the master data source.
INSERT INTO `KRNS_PARM_T` (`VER_NBR`, `PARM_DESC_TXT`, `OBJ_ID`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `APPL_NMSPC_CD`, `TXT`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES (1, 'The month and day marking the last date of the fiscal year', uuid(), 'FISCAL_YEAR_END_DAY_AND_MONTH', 'Batch', 'A', 'KUALI', '0630', 'CONFG', 'KFS-ENDOW');


-- Release Database Lock

-- Release Database Lock


-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 5/22/10 5:55 PM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-05-21-5475-KRNS_PARM_T.xml::5475-2-1::Bonnie::(MD5Sum: e547de42f5f69e53e5f45816c89319)
-- Endowment request: create Endowment related parameters in RICE tables
INSERT INTO `KRNS_PARM_T` (`VER_NBR`, `PARM_DESC_TXT`, `OBJ_ID`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES (1, 'This parameter is used to identify the institutional selection for tracking security Tax Lots and transactions involving cash The possible values for this parameter are: Average Balance, FIFO First In - First Out and LIFO Last In - First Out', 'uuid()', 'TAX_LOTS_ACCOUNTING_METHOD', 'All', 'A', 'Average Balance', 'CONFG', 'KFS-ENDOW');


-- Release Database Lock

-- Release Database Lock


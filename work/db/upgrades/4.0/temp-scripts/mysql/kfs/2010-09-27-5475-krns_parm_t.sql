-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 9/27/10 9:02 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-09-27-5475h-1-KRNS_PARM_T.xml::5475h-1-1::Bonnie::(MD5Sum: 8f5dbc8aa75fc43442e214521d697d4b)
-- need to add one more system parameter.
INSERT INTO `KRNS_PARM_T` (`PARM_DESC_TXT`, `PARM_NM`, `PARM_DTL_TYP_CD`, `CONS_CD`, `TXT`, `obj_id`, `APPL_NMSPC_CD`, `PARM_TYP_CD`, `NMSPC_CD`) VALUES ('If this value is Yes,then for each KEMID record created there must be at least one active record in END_KEMID_ROLE_T If this value is No,then no associated records are required in END_KEMID_ROLE_T', 'ROLE_REQUIRED_IND', 'KEMID', 'A', 'N', uuid(), 'KFS', 'CONFG', 'KFS-ENDOW');


-- Release Database Lock

-- Release Database Lock


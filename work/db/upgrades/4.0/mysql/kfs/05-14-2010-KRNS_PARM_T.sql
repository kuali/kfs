-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 5/14/10 7:56 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-05-14-5692-1-KNS_PARM_T.xml::5692-1-1::Travis::(MD5Sum: 698ea5e3897548fe334bac77af1475f)
-- Update Description Text for SENSITIVE_DATA_PATTERNS Parameter
UPDATE `krns_parm_t` SET `parm_desc_txt` = 'A semi-colon delimted list of regular expressions that identify potentially sensitive data in strings. These patterns will be matched against notes, document explanations, and routing annotations (i.e. the document disapproval reason). The default value excludes all strings exceeding 9 digits. To exclude only those of exactly 9 or 16 digits change the parameter value to: [0-9]{3}-[0-9]{2}-[0-9]{4};^([0-9]{9}|[0-9]{16})$' WHERE nmspc_cd='KR-NS' AND PARM_DTL_TYP_CD='All' AND PARM_NM='SENSITIVE_DATA_PATTERNS';


-- Release Database Lock

-- Release Database Lock


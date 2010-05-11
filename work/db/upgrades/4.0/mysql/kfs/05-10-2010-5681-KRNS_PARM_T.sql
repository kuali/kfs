-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 5/10/10 11:54 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-05-10-5681-1-KRNS_PARM_T.xml::5681-1-1::Travis::(MD5Sum: fb8adc10a243814cc7fd259a6cac7dd)
-- Add 'yyyy-MM-dd' date format to STRING_TO_DATE_FORMATS parameter text
UPDATE `krns_parm_t` SET `txt` = 'yyyy-MM-dd;MM/dd/yy;MM/dd/yyyy;MM-dd-yy;MMddyy;MMMM dd;yyyy;MM/dd/yy HH:mm:ss;MM/dd/yyyy HH:mm:ss;MM-dd-yy HH:mm:ss;MMddyy HH:mm:ss;MMMM dd HH:mm:ss;yyyy HH:mm:ss;MM/dd/yyyy hh:mm a' WHERE nmspc_cd='KR-NS' AND PARM_DTL_TYP_CD='All' AND PARM_NM='STRING_TO_DATE_FORMATS';


-- Release Database Lock

-- Release Database Lock


-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 6/17/10 9:02 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-06-17-5813-1-krim_role_perm_t.xml::5813-1-1::David::(MD5Sum: d429a57341250979602d16ba2e7f10)
-- Add permission 306 to role 49 (Tax Identification Number User)
INSERT INTO `KRIM_ROLE_PERM_T` (`actv_ind`, `perm_id`, `role_id`, `role_perm_id`, `obj_id`, `ver_nbr`) VALUES ('Y', '306', '49', '1004', uuid(), 1);


-- Release Database Lock

-- Release Database Lock


-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: ./updates/update.xml
-- Ran at: 7/27/10 9:44 AM
-- Against: kuldemo@jdbc:mysql://localhost:3306/kuldemo
-- LiquiBase version: 1.9.5
-- *********************************************************************



-- Changeset updates/2010-07-27-5869-1-CB_PRTG_T.xml::5869-1-1::Bob::(MD5Sum: 69fd9f88c6d2cce1a8beb524026efda)
-- KFSMI-5869  CAB doesn't accept a vendor name > 40 characters
ALTER TABLE `CB_PRTG_T` MODIFY `CPTLAST_MFR_NM` VARCHAR(45);


-- Changeset updates/2010-07-27-5869-1-CB_PRTG_T.xml::5869-1-2::Bob::(MD5Sum: 37e45343ceee49b4690a55768fce038)
-- KFSMI-5869  CAB doesn't accept a vendor name > 40 characters
ALTER TABLE `CB_PRTG_T` MODIFY `VENDOR_NAME` VARCHAR(45);


-- Changeset updates/2010-07-27-5869-1-CB_PRTG_T.xml::5869-1-3::Bob::(MD5Sum: 27a069786e74b0baf324b27ad3ec9114)
-- KFSMI-5869  CAB doesn't accept a vendor name > 40 characters
ALTER TABLE `CM_AST_COMPONENT_T` MODIFY `CACMP_MFR_NM` VARCHAR(45);


-- Changeset updates/2010-07-27-5869-1-CB_PRTG_T.xml::5869-1-4::Bob::(MD5Sum: a752a788b23331974218e9b49cf85b2)
-- KFSMI-5869  CAB doesn't accept a vendor name > 40 characters
ALTER TABLE `CM_AST_COMPONENT_T` MODIFY `CACMP_VENDOR_NM` VARCHAR(45);


-- Changeset updates/2010-07-27-5869-1-CB_PRTG_T.xml::5869-1-5::Bob::(MD5Sum: bf7c39387ed2c48f8b3a67ca9651c79)
-- KFSMI-5869  CAB doesn't accept a vendor name > 40 characters
ALTER TABLE `CM_CPTLAST_DOC_T` MODIFY `CPTLAST_MFR_NM` VARCHAR(45);


-- Changeset updates/2010-07-27-5869-1-CB_PRTG_T.xml::5869-1-6::Bob::(MD5Sum: bf7c39387ed2c48f8b3a67ca9651c79)
-- KFSMI-5869  CAB doesn't accept a vendor name > 40 characters
ALTER TABLE `CM_CPTLAST_DOC_T` MODIFY `CPTLAST_MFR_NM` VARCHAR(45);


-- Changeset updates/2010-07-27-5869-1-CB_PRTG_T.xml::5869-1-7::Bob::(MD5Sum: 30968ddd1f8c87c4c66ca3a045cfaeb9)
-- KFSMI-5869  CAB doesn't accept a vendor name > 40 characters
ALTER TABLE `CM_CPTLAST_T` MODIFY `CPTLAST_MFR_NM` VARCHAR(45);


-- Changeset updates/2010-07-27-5869-1-CB_PRTG_T.xml::5869-1-8::Bob::(MD5Sum: 17aedf3681e2b21b3bec587b5643c0d0)
-- KFSMI-5869  CAB doesn't accept a vendor name > 40 characters
ALTER TABLE `CM_CPTLAST_T` MODIFY `CPTLAST_VENDOR_NM` VARCHAR(45);


-- Release Database Lock

-- Release Database Lock


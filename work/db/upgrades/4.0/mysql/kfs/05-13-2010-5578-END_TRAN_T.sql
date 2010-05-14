-- Changeset updates/2010-05-13-5578-1-END_TRAN_SEC_T.xml::5578-1-1::Bonnie::(MD5Sum: 14d9a2dc58d2e913e0f97ad3886bcf4e)
-- update on the tables support endowment transactional documents:
ALTER TABLE `END_TRAN_SEC_T` MODIFY `SEC_REGIS_CD` VARCHAR(4) DEFAULT null;


-- Changeset updates/2010-05-13-5578-1-END_TRAN_SEC_T.xml::5578-1-2::Bonnie::(MD5Sum: fb6fd0f1a2c57774aee2c11445fb963b)
-- update on the tables support endowment transactional documents:
ALTER TABLE `END_TRAN_LN_T` MODIFY `ETRAN_CD` VARCHAR(9) DEFAULT null;


-- Changeset updates/2010-05-13-5578-1-END_TRAN_SEC_T.xml::5578-1-3::Bonnie::(MD5Sum: 4049814b4da320a0a364678517b34)
-- update on the tables support endowment transactional documents:
ALTER TABLE `END_TRAN_HLDG_LOT_T` ADD `LOT_ACQD_DT` DATE;

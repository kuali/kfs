--KC Integation: CA_ACCT_CREATE_DEFAULT_T
-- Changeset updates/2010-09-27-none-1-CA_ACCT_AUTO_CREATE_DFLT_T.xml::none-1-1::Winston::(MD5Sum: 72dcf55039e93219aa6bdda19f5946d7)
-- change ICR_TYPE field 3 chars
ALTER TABLE `ca_acct_auto_create_dflt_t` MODIFY `fin_series_id` VARCHAR(3);

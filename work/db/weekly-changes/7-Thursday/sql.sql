alter table fs_doc_header_t drop column fs_doc_typ_cd
/

update ca_acct_delegate_t set fdoc_typ_cd = 'KFS' where fdoc_typ_cd = 'ALL'
/
update ca_dlgt_chg_doc_t set fdoc_typ_cd = 'KFS' where fdoc_typ_cd = 'ALL'
/
update ca_org_rtng_mdl_t set fdoc_typ_cd = 'KFS' where fdoc_typ_cd = 'ALL'
/
Insert into CA_ACCOUNT_T (SELECT 'UA', '1000000', sys_guid(), 2, 'UA Bank 1', '4318506633', '1394308640', '1110101123', 'ACCT', 'NA', 'UA', 'OTHFDS', 'Y', 'NA', 'N', NULL, 'Tucson', 'AZ', '888 N. Euclid', '85721', NULL, NULL, to_date('2009-04-22','yyyy-mm-dd'), to_date('2009-04-22','yyyy-mm-dd'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', NULL, 'N', 'N',NULL from DUAL);
Insert into CA_ACCOUNT_T (SELECT 'UA', '1100000', sys_guid(), 3, 'UA Bank 2', '4318506633', '1394308640', '1110101123', 'ACCT', 'NA', 'UA', 'OTHFDS', 'Y', 'NA', 'N', NULL, 'Tucson', 'AZ', '888 N. Euclid', '85721', NULL, NULL, to_date('2009-04-22','yyyy-mm-dd'), to_date('2009-04-22','yyyy-mm-dd'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', NULL, 'N', 'N',NULL from DUAL);
Insert into CA_ACCOUNT_T (SELECT 'UA', '1300000', sys_guid(), 3, 'UA Bank 13', '4318506633', '1394308640', '1110101123', 'ACCT', 'NA', 'UA', 'OTHFDS', 'Y', 'NA', 'N', NULL, 'Tucson', 'AZ', '888 N. Euclid', '85721', NULL, NULL, to_date('2009-04-22','yyyy-mm-dd'), to_date('2009-04-22','yyyy-mm-dd'), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', NULL, 'N', 'N',NULL from DUAL);
Insert into FP_BANK_T (select '0001', sys_guid(), 1, 'UA Depository Account', 'UA Bank 1', '111111111', 'Y', '1N7hv99/CqI=', 'UA Bank 1 Depository Account', 'UA', '1000000', NULL, '8006', NULL, NULL, 'Y', 'Y', 'N', 'N' from DUAL);
Insert into FP_BANK_T (select '0002', sys_guid(), 1, 'UA Computer Checks', 'UA Bank 2', '222222222', 'Y', 'b31JPNj45atex9TeFJY2Gg==', 'UA Bank 2 Computer Checks', 'UA', '1100000', NULL, '8006', NULL, NULL, 'N', 'Y', 'N', 'Y' from DUAL);
Insert into FP_BANK_T (select '0013', sys_guid(), 1, 'UA Outgoing ACH', 'UA Bank 13', '333333333', 'Y', 'b31JPNj45as/i2rh3or6lQ==', 'UA Bank 13 Outgoing ACH', 'UA', '1300000', NULL, '8006', NULL, NULL, 'N', 'Y', 'Y', 'N' from DUAL);
Insert into CA_OBJECT_CODE_T (SELECT '2009', 'UA', '8006', sys_guid(), 2, 'CASH IN BANK', 'CASH IN BNK', 'CASH', 'IU', '8000', 'AS', 'CA', NULL, 'Y', 'O', 'N', 'N', NULL FROM DUAL);
commit;
update PUR_VNDR_DFLT_ADDR_T
set VNDR_CMP_CD = 'EA'
where VNDR_DFLT_ADDR_GNRTD_ID = 1000;
update PUR_VNDR_DFLT_ADDR_T
set VNDR_CMP_CD = 'CO'
where VNDR_DFLT_ADDR_GNRTD_ID = 1003;
update PUR_VNDR_DFLT_ADDR_T
set VNDR_CMP_CD = 'IN'
where VNDR_DFLT_ADDR_GNRTD_ID = 2004;
commit;
drop sequence pdp_cust_id_seq;
create sequence pdp_cust_id_seq
start with 10000200        
increment by 1
nocache
nocycle;
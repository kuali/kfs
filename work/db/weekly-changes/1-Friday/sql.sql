-- the float doc header extensions table really needs to be of type NUMBER which has a precision of 38
-- this results in proper conversion to mysql as part of the impex process
ALTER TABLE KREW_DOC_HDR_EXT_FLT_T MODIFY VAL NUMBER
/
INSERT INTO KRNS_PARM_T(
SELECT 'KFS-BC', 'GenesisBatchStep', 'SOURCE_FISCAL_YEAR', sys_guid(), 1,
       'CONFG', '2007', 
       'Indicates the base fiscal year from which Genesis will create the new fiscal year budget documents.'
       , 'A'
  FROM DUAL)
/
insert into krns_parm_t
(select 'KFS-FP','DisbursementVoucher', 'CAMPUSES_TAXED_FOR_MOVING_REIMBURSEMENTS',sys_guid(),1,
'CONFG', 'BL;EA;IN;KO;NW;SB;SE',
'The campuses which are taxed for moving reimbursements carried out via Disbursement Vouchers.',
'A' from dual)
/
delete from pdp_pmt_stat_cd_t where pmt_stat_cd = 'HTXA'
/
delete PUR_AP_ITM_TYP_T@kuldba
/
insert into PUR_AP_ITM_TYP_T@kuldba
select * from PUR_AP_ITM_TYP_T@kuldev
/
commit
/
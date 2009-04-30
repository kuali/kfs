drop index fp_maint_lock_tp1;
update ca_account_t
set ACCT_SPVSR_UNVL_ID = '6276904397'
where ACCT_SPVSR_UNVL_ID not in
(select prncpl_id from krim_prncpl_t)
;
update ca_account_t
set ACCT_MGR_UNVL_ID = '1781003865'
where ACCT_MGR_UNVL_ID not in
(select prncpl_id from krim_prncpl_t)
;
update ca_prior_yr_acct_t
set ACCT_SPVSR_UNVL_ID = '6276904397'
where ACCT_SPVSR_UNVL_ID not in
(select prncpl_id from krim_prncpl_t)
;
update ca_prior_yr_acct_t
set ACCT_MGR_UNVL_ID = '1781003865'
where ACCT_MGR_UNVL_ID not in
(select prncpl_id from krim_prncpl_t)
;
commit;
update krim_perm_attr_data_t set attr_val = 'KualiDocument' where perm_id in (select perm_id from krim_perm_t where nm = 'Ad Hoc Review Document') and attr_val = 'Kuali Document'
/
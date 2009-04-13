drop view KRIM_GRP_MBR_V
/
drop view KRIM_GRP_V
/           
drop view KRIM_PERM_ATTR_V     
/
drop view KRIM_PERM_V          
/
drop view KRIM_PRNCPL_V        
/
drop view KRIM_ROLE_GRP_V      
/
drop view KRIM_ROLE_PERM_V     
/
drop view KRIM_ROLE_PRNCPL_V   
/
drop view KRIM_ROLE_ROLE_V
/
drop view KRIM_ROLE_V          
/
drop view KRIM_RSP_ATTR_V      
/
drop view KRIM_RSP_ROLE_ACTN_V 
/
drop view KRIM_RSP_ROLE_V
/

update krim_role_rsp_actn_t set ignore_prev_ind = 'Y' where role_rsp_actn_id = '33'
/

delete AR_LOCKBOX_T;
commit;
UPDATE CA_ACCOUNT_T set ACCT_FSC_OFC_UID=(select prncpl_id
from krim_prncpl_t where prncpl_nm = 'rorenfro'),
ACCT_SPVSR_UNVL_ID=(select prncpl_id
from krim_prncpl_t where prncpl_nm = 'heagle'),
ACCT_MGR_UNVL_ID=(select prncpl_id
from krim_prncpl_t where prncpl_nm = 'warriaga')
WHERE (FIN_COA_CD = 'BA' AND ACCOUNT_NBR = '1111111')
or (FIN_COA_CD = 'BA' AND ACCOUNT_NBR = '6628000')
or (FIN_COA_CD = 'BL' AND ACCOUNT_NBR = '2224604')
or (FIN_COA_CD = 'BL' AND ACCOUNT_NBR = '2336320')
or (FIN_COA_CD = 'UA' AND ACCOUNT_NBR = '6812950');
commit;
alter table ca_org_rvrsn_dtl_t
add  (ACTV_IND varchar2(1));

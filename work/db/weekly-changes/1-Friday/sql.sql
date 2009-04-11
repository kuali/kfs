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

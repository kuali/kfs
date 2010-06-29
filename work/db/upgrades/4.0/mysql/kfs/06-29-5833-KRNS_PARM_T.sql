
-- Changeset updates/2010-06-29-5833-1-KRNS_PARM_T.xml::5833-1-1::David::(MD5Sum: 8e8fd3ab2f19c7d545df80512f3ef1)
-- needs to add system parameters
INSERT INTO `KRNS_PARM_T` (`appl_nmspc_cd`, `cons_cd`, `nmspc_cd`, `parm_dtl_typ_cd`, `parm_nm`, `obj_id`, `parm_desc_txt`, `ver_nbr`, `txt`, `parm_typ_cd`) VALUES ('KFS', 'A', 'KFS-COA', 'Account', 'ACCOUNT_ADDRESS_TYPE', uuid(), 'Defines the cascading order for extracting the addresses', 1, 'ADMIN;PAYMENT;DEFAULT', 'CONFG');


-- Changeset updates/2010-06-29-5833-1-KRNS_PARM_T.xml::5833-1-2::David::(MD5Sum: e6a1a822632a626fd7edb97ee36724f1)
-- needs to add system parameters
INSERT INTO `KRNS_PARM_T` (`appl_nmspc_cd`, `cons_cd`, `nmspc_cd`, `parm_dtl_typ_cd`, `parm_nm`, `obj_id`, `parm_desc_txt`, `ver_nbr`, `txt`, `parm_typ_cd`) VALUES ('KFS', 'A', 'KFS-COA', 'ObjectCode', 'ENABLE_RESEARCH_ADMIN_OBJECT_CODE_ATTRIBUTE_IND', uuid(), 'Set this value to Y if you are using both Research Admin System and KFS and wish for KC budgets to use KFS Object Codes This will cause the Research Admin Attributes tab to appear on the Object Code document Set this value to N if you are not using Research Admin System and KFS or if you wish Research Admin System to maintain a distinct list of object codes', 1, 'Y', 'CONFG');


DROP TABLE krim_prsn_cache_t
/ 
alter table PUR_VNDR_CONTR_T drop constraint PUR_VNDR_CONTR_TR4
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='CICP'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='CON'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='COOP'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='EI'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='EST'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='GSA'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='ION'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='MCTA'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='MED'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='MHEC'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='MINN'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='NOA'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='QUOT'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='SSA'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='STAT'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='UCS'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='UTIL'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='VAR'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='VEN'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='VHA'
/
delete pur_po_cst_src_t where PO_CST_SRC_CD='WSA'
/
delete PUR_DFLT_PRNCPL_ADDR_T where PRNCPL_ID='3012703100'
/
delete PUR_DFLT_PRNCPL_ADDR_T where PRNCPL_ID='4217300016'
/
delete PUR_DFLT_PRNCPL_ADDR_T where PRNCPL_ID='5175504470'
/
delete PUR_DFLT_PRNCPL_ADDR_T where PRNCPL_ID='1545104915'
/
delete PUR_DFLT_PRNCPL_ADDR_T where PRNCPL_ID='3577005213'
/
delete PUR_DFLT_PRNCPL_ADDR_T where PRNCPL_ID='5151809337'
/
delete PUR_DFLT_PRNCPL_ADDR_T where PRNCPL_ID='2664109901'
/
delete PUR_SNSTV_DTA_T where SNSTV_DTA_CD = 'ANIM'
/
delete PUR_SNSTV_DTA_T where SNSTV_DTA_CD = 'RADI'
/
insert into pur_po_cst_src_t (select 'CICP', sys_guid(), 1, 'COMM ON INST COOP PUR CONSORTIUM (CICPC)', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'CON', sys_guid(), 1, 'PRICING AGREEMENT FIRM', 'Y', '0', '0' FROM DUAL)
/
insert into pur_po_cst_src_t (select 'COOP', sys_guid(), 1, 'COOPERATIVE AGREEMENT', 'Y', '0', '0' FROM DUAL)
/
insert into pur_po_cst_src_t (select 'EI', sys_guid(), 1, 'EDUCATIONAL & INSTITUTIONAL COOPERATIVE (E&I)', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'EST', sys_guid(), 1, 'ESTIMATE', 'Y', '10', '50' FROM DUAL)
/
insert into pur_po_cst_src_t (select 'GSA', sys_guid(), 1, 'GSA', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'ION', sys_guid(), 1, 'INTERNATIONAL ONCOLOGY NETWORK (ION)', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'MCTA', sys_guid(), 1, 'MICH COLLEGIATE TELECOMM ASSOC (MICTA)', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'MED', sys_guid(), 1, 'MED ASSETS', 'Y', '100', '100' FROM DUAL)
/
insert into pur_po_cst_src_t (select 'MHEC', sys_guid(), 1, 'MIDWEST HIGHER EDUCATION CONSORTIUM (MHEC)', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'MINN', sys_guid(), 1, 'MINNESOTA MULTISTATE CONSORTIUM', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'NOA', sys_guid(), 1, 'NATIONAL ONCOLOGY ALLIANCE (NOA)', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'QUOT', sys_guid(), 1, 'QUOTE', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'SSA', sys_guid(), 1, 'STATE SCHOOL AGREEMENTS', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'STAT', sys_guid(), 1, 'STATE QUANTITY PURCHASE ORDERS (QPA)', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'UCS', sys_guid(), 1, 'US COMMUNITIES', 'Y', '1', '1' FROM DUAL)
/
insert into pur_po_cst_src_t (select 'UTIL', sys_guid(), 1, 'UTILITY', 'Y', '50', '50' FROM DUAL)
/
insert into pur_po_cst_src_t (select 'VAR', sys_guid(), 1, 'PRICING AGREEMENT VARIABLE', 'Y', '20', '50' FROM DUAL)
/
insert into pur_po_cst_src_t (select 'VEN', sys_guid(), 1, 'VENDOR PRICE', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'VHA', sys_guid(), 1, 'NOVATION/VHA', 'Y', NULL, NULL FROM DUAL)
/
insert into pur_po_cst_src_t (select 'WSA', sys_guid(), 1, 'WESTERN STATES CONTRACTING ALLIANCE (WSCA)', 'Y', NULL, NULL FROM DUAL)
/
insert into PUR_DFLT_PRNCPL_ADDR_T (select '3012703100', sys_guid(), 1, 'BL', 'BL025B','45' from dual)
/
insert into PUR_DFLT_PRNCPL_ADDR_T (select '4217300016', sys_guid(), 1, 'BL', 'BL109','867' from dual)
/
insert into PUR_DFLT_PRNCPL_ADDR_T (select '5175504470', sys_guid(), 1, 'BL', 'BL009','A-3' from dual)
/
insert into PUR_DFLT_PRNCPL_ADDR_T (select '1545104915', sys_guid(), 1, 'BL', 'BL165','201 West' from dual)
/
insert into PUR_DFLT_PRNCPL_ADDR_T (select '3577005213', sys_guid(), 1, 'IN', 'IN062','1' from dual)
/
insert into PUR_DFLT_PRNCPL_ADDR_T (select '5151809337', sys_guid(), 1, 'BL', 'BL066','Lobby' from dual)
/
insert into PUR_DFLT_PRNCPL_ADDR_T (select '2664109901', sys_guid(), 1, 'BL', 'BL320','65' from dual)
/
insert into PUR_SNSTV_DTA_T (select 'ANIM', sys_guid(), 1, 'Animal', 'Y' from dual)
/
insert into PUR_SNSTV_DTA_T (select 'RADI', sys_guid(), 1, 'Radio Active Material', 'Y' from dual)
/
insert into pur_thrshld_t (select PUR_THRSHLD_ID.nextval, sys_guid(), 1, 'EA', NULL, NULL, NULL, NULL, '3000', NULL, NULL, NULL, 'Y' from dual)
/
insert into pur_thrshld_t (select PUR_THRSHLD_ID.nextval, sys_guid(), 1, 'IN', NULL, 'AUXAMB', NULL, NULL, '1000', NULL, NULL, NULL, 'Y' from dual)
/
insert into pur_thrshld_t (select PUR_THRSHLD_ID.nextval, sys_guid(), 1, 'BL', NULL, NULL, NULL, NULL, '1', NULL, NULL, '113', 'Y' from dual)
/
insert into pur_thrshld_t (select PUR_THRSHLD_ID.nextval, sys_guid(), 1, 'BL', NULL, NULL, '5300', NULL, '1', NULL, NULL, NULL, 'Y' from dual)
/
insert into pur_thrshld_t (select PUR_THRSHLD_ID.nextval, sys_guid(), 1, 'BL', NULL, NULL, NULL, 'PSY', '5000', NULL, NULL, NULL, 'Y' from dual)
/
insert into pur_thrshld_t (select PUR_THRSHLD_ID.nextval, sys_guid(), 1, 'IN', NULL, NULL, NULL, NULL, '500', '1000', 0, NULL, 'Y' from dual)
/
insert into pur_thrshld_t (select PUR_THRSHLD_ID.nextval, sys_guid(), 1, 'BL', 'DT', NULL, NULL, NULL, '4999.99', NULL, NULL, NULL, 'Y' from dual)
/
ALTER TABLE pur_vndr_contr_t
ADD CONSTRAINT pur_vndr_contr_tr4 FOREIGN KEY (po_cst_src_cd)
REFERENCES pur_po_cst_src_t (po_cst_src_cd)
/
alter table AP_CRDT_MEMO_ITM_USE_TAX_T modify (fin_object_cd varchar2(4))
/
alter table AP_PMT_RQST_ITM_USE_TAX_T modify (fin_object_cd varchar2(4))
/
alter table PUR_PO_ITM_USE_TAX_T modify (fin_object_cd varchar2(4))
/
alter table PUR_REQS_ITM_USE_TAX_T modify (fin_object_cd varchar2(4))
/


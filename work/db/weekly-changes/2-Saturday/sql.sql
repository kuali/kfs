update krew_doc_typ_t set lbl='Organization Review' where doc_typ_nm = 'ORR'
/

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('358', sys_guid(), 1, '41', 'Modify Accounting Lines', null, 'Y', 'KFS-FP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('517', sys_guid(), 1, '358', '52', '13', 'DV')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('518', sys_guid(), 1, '358', '52', '16', 'Travel')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('519', sys_guid(), 1, '358', '52', '6', 'sourceAccountingLines.financialObjectCode')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('661', sys_guid(), 1, '66', '358', 'Y')
/

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('359', sys_guid(), 1, '29', 'Use Screen', null, 'Y', 'KFS-FP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('520', sys_guid(), 1, '359', '12', '2', 'org.kuali.kfs.fp.document.web.struts.DisbursementVoucherHelpAction')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('662', sys_guid(), 1, '83', '359', 'Y')
/

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('360', sys_guid(), 1, '10', 'Initiate Document', null, 'Y', 'KFS-AR')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('521', sys_guid(), 1, '360', '3', '13', 'INVR')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('663', sys_guid(), 1, '62', '360', 'Y')
/

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) 
    VALUES('361', sys_guid(), 1, '10', 'Initiate Document', null, 'Y', 'KFS-FP')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('522', sys_guid(), 1, '361', '3', '13', 'CDS')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('664', sys_guid(), 1, '11', '361', 'Y')
/

update krns_parm_t set   parm_nm = 'EMAIL_NOTIFICATION_TEST_ADDRESS'
where parm_nm = 'EMAIL_NOTIFICATION_TEST_ADDRESS '
/
CREATE OR REPLACE VIEW KRIM_GRP_MBR_V ( NMSPC_CD, GRP_NM, GRP_ID, PRNCPL_NM, PRNCPL_ID, MBR_GRP_NM, MBR_GRP_ID ) AS SELECT g.NMSPC_CD , g.grp_nm , g.GRP_ID , p.PRNCPL_NM , p.PRNCPL_ID , mg.GRP_NM AS mbr_grp_nm , mg.GRP_ID AS mbr_grp_id FROM KRIM_GRP_MBR_T gm LEFT JOIN krim_grp_t g ON g.GRP_ID = gm.GRP_ID LEFT OUTER JOIN krim_grp_t mg ON mg.GRP_ID = gm.MBR_ID AND gm.MBR_TYP_CD = 'G'
LEFT OUTER JOIN krim_prncpl_t p
ON p.PRNCPL_ID = gm.MBR_ID
AND gm.MBR_TYP_CD = 'P'
LEFT OUTER JOIN krim_entity_nm_t en
ON en.ENTITY_ID = p.ENTITY_ID
AND en.DFLT_IND = 'Y'
AND en.ACTV_IND = 'Y'
ORDER BY nmspc_cd, grp_nm, prncpl_nm
/
CREATE OR REPLACE VIEW KRIM_GRP_V ( NMSPC_CD, GRP_NM, GRP_ID, GRP_TYP_NM, ATTR_NM, ATTR_VAL ) AS SELECT g.NMSPC_CD , g.grp_nm , g.GRP_ID , t.NM AS grp_typ_nm , a.NM AS attr_nm , d.ATTR_VAL AS attr_val FROM krim_grp_t g LEFT OUTER JOIN KRIM_GRP_ATTR_DATA_T d ON d.grp_id = g.GRP_ID LEFT OUTER JOIN KRIM_ATTR_DEFN_T a ON a.KIM_ATTR_DEFN_ID = d.KIM_ATTR_DEFN_ID LEFT OUTER JOIN KRIM_TYP_T t ON g.KIM_TYP_ID = t.KIM_TYP_ID / CREATE OR REPLACE VIEW KRIM_PERM_ATTR_V ( TMPL_NMSPC_CD, TMPL_NM, PERM_TMPL_ID, PERM_NMSPC_CD, PERM_NM, PERM_ID, ATTR_NM, ATTR_VAL ) AS SELECT t.nmspc_cd AS tmpl_nmspc_cd , t.NM AS tmpl_nm , t.PERM_TMPL_ID , p.nmspc_cd AS perm_nmspc_cd , p.NM AS perm_nm , p.PERM_ID , a.NM AS attr_nm , ad.ATTR_VAL AS attr_val FROM KRIM_PERM_T p LEFT JOIN KRIM_PERM_TMPL_T t ON p.PERM_TMPL_ID = t.PERM_TMPL_ID LEFT OUTER JOIN KRIM_PERM_ATTR_DATA_T ad ON p.PERM_ID = ad.perm_id LEFT OUTER JOIN KRIM_ATTR_DEFN_T a ON ad.KIM_ATTR_DEFN_ID = a.KIM_ATTR_DEFN_ID ORDER BY tmpl_nmspc_cd, tmpl_nm, perm_nmspc_cd, perm_id, attr_nm / CREATE OR REPLACE VIEW KRIM_PERM_V ( TMPL_NMSPC_CD, TMPL_NM, PERM_TMPL_ID, PERM_NMSPC_CD, PERM_NM, PERM_ID, PERM_TYP_NM, SRVC_NM ) AS SELECT t.nmspc_cd AS tmpl_nmspc_cd , t.NM AS tmpl_nm , t.PERM_TMPL_ID , p.nmspc_cd AS perm_nmspc_cd , p.NM AS perm_nm , p.PERM_ID , typ.NM AS perm_typ_nm , typ.SRVC_NM FROM KRIM_PERM_T p INNER JOIN KRIM_PERM_TMPL_T t ON p.PERM_TMPL_ID = t.PERM_TMPL_ID LEFT OUTER JOIN KRIM_TYP_T typ ON t.KIM_TYP_ID = typ.KIM_TYP_ID / CREATE OR REPLACE VIEW KRIM_PRNCPL_V ( PRNCPL_ID, PRNCPL_NM, FIRST_NM, LAST_NM, AFLTN_TYP_CD, CAMPUS_CD, EMP_STAT_CD, EMP_TYP_CD ) AS SELECT p.PRNCPL_ID ,p.PRNCPL_NM ,en.FIRST_NM ,en.LAST_NM ,ea.AFLTN_TYP_CD ,ea.CAMPUS_CD ,eei.EMP_STAT_CD ,eei.EMP_TYP_CD FROM krim_prncpl_t p LEFT OUTER JOIN krim_entity_emp_info_t eei ON eei.ENTITY_ID = p.ENTITY_ID LEFT OUTER JOIN krim_entity_afltn_t ea ON ea.ENTITY_ID = p.ENTITY_ID LEFT OUTER JOIN krim_entity_nm_t en ON p.ENTITY_ID = en.ENTITY_ID AND 'Y' = en.DFLT_IND / CREATE OR REPLACE VIEW KRIM_ROLE_GRP_V ( NMSPC_CD, ROLE_NM, ROLE_ID, GRP_NMSPC_CD, GRP_NM, ROLE_MBR_ID, ATTR_NM, ATTR_VAL ) AS SELECT r.NMSPC_CD , r.ROLE_NM , r.role_id , g.NMSPC_CD AS grp_nmspc_cd , g.GRP_NM , rm.ROLE_MBR_ID , a.NM AS attr_nm , d.ATTR_VAL AS attr_val FROM KRIM_ROLE_MBR_T rm LEFT JOIN KRIM_ROLE_T r ON r.ROLE_ID = rm.ROLE_ID LEFT JOIN KRIM_GRP_T g ON g.GRP_ID = rm.MBR_ID LEFT OUTER JOIN KRIM_ROLE_MBR_ATTR_DATA_T d ON d.role_mbr_id = rm.ROLE_MBR_ID LEFT OUTER JOIN KRIM_ATTR_DEFN_T a ON a.KIM_ATTR_DEFN_ID = d.KIM_ATTR_DEFN_ID WHERE rm.MBR_TYP_CD = 'G' 
ORDER BY nmspc_cd, role_nm, grp_nmspc_cd, grp_nm, role_mbr_id, attr_nm / CREATE OR REPLACE VIEW KRIM_ROLE_PERM_V ( NMSPC_CD, ROLE_NM, ROLE_ID, TMPL_NMSPC_CD, TMPL_NM, PERM_TMPL_ID, PERM_NMPSC_CD, PERM_NM, PERM_ID, ATTR_NM, ATTR_VAL ) AS SELECT r.NMSPC_CD , r.ROLE_NM , r.role_id , pt.NMSPC_CD AS tmpl_nmspc_cd , pt.NM AS tmpl_nm , pt.PERM_TMPL_ID , p.NMSPC_CD AS perm_nmpsc_cd , p.NM AS perm_nm , p.PERM_ID , a.NM AS attr_nm , ad.ATTR_VAL AS attr_val FROM KRIM_PERM_T p LEFT JOIN KRIM_PERM_TMPL_T pt ON p.PERM_TMPL_ID = pt.PERM_TMPL_ID LEFT OUTER JOIN KRIM_PERM_ATTR_DATA_T ad ON p.PERM_ID = ad.perm_id LEFT OUTER JOIN KRIM_ATTR_DEFN_T a ON ad.KIM_ATTR_DEFN_ID = a.KIM_ATTR_DEFN_ID LEFT OUTER JOIN KRIM_ROLE_PERM_T rp ON rp.PERM_ID = p.PERM_ID LEFT OUTER JOIN KRIM_ROLE_T r ON rp.ROLE_ID = r.ROLE_ID ORDER BY NMSPC_CD, role_nm, tmpl_nmspc_cd, tmpl_nm, perm_id, attr_nm / CREATE OR REPLACE VIEW KRIM_ROLE_PRNCPL_V ( NMSPC_CD, ROLE_NM, ROLE_ID, PRNCPL_NM, PRNCPL_ID, FIRST_NM, LAST_NM, ROLE_MBR_ID, ATTR_NM, ATTR_VAL
)
AS
SELECT r.NMSPC_CD
, r.ROLE_NM
, r.ROLE_ID
, p.PRNCPL_NM
, p.PRNCPL_ID
, en.FIRST_NM
, en.LAST_NM
, rm.ROLE_MBR_ID
, ad.NM AS attr_nm
, rmad.ATTR_VAL AS attr_val
FROM KRIM_ROLE_T r
LEFT OUTER JOIN KRIM_ROLE_MBR_T rm
ON r.ROLE_ID = rm.ROLE_ID
LEFT OUTER JOIN KRIM_ROLE_MBR_ATTR_DATA_T rmad ON rm.ROLE_MBR_ID = rmad.role_mbr_id LEFT OUTER JOIN KRIM_ATTR_DEFN_T ad ON rmad.KIM_ATTR_DEFN_ID = ad.KIM_ATTR_DEFN_ID LEFT OUTER JOIN KRIM_PRNCPL_T p ON rm.MBR_ID = p.PRNCPL_ID AND rm.mbr_typ_cd = 'P'
LEFT OUTER JOIN KRIM_ENTITY_NM_T en
ON p.ENTITY_ID = en.ENTITY_ID
WHERE (en.DFLT_IND = 'Y')
ORDER BY nmspc_cd, role_nm, prncpl_nm, rm.ROLE_MBR_ID, attr_nm / CREATE OR REPLACE VIEW KRIM_ROLE_ROLE_V ( NMSPC_CD, ROLE_NM, ROLE_ID, MBR_ROLE_NMSPC_CD, MBR_ROLE_NM, MBR_ROLE_ID, ROLE_MBR_ID, ATTR_NM, ATTR_VAL ) AS SELECT r.NMSPC_CD , r.ROLE_NM , r.role_id , mr.NMSPC_CD AS mbr_role_nmspc_cd , mr.role_NM AS mbr_role_nm , mr.role_id AS mbr_role_id , rm.role_mbr_id , a.NM AS attr_nm , d.ATTR_VAL AS attr_val FROM KRIM_ROLE_MBR_T rm LEFT JOIN KRIM_ROLE_T r ON r.ROLE_ID = rm.ROLE_ID LEFT JOIN KRIM_role_T mr ON mr.role_ID = rm.MBR_ID LEFT OUTER JOIN KRIM_ROLE_MBR_ATTR_DATA_T d ON d.role_mbr_id = rm.ROLE_MBR_ID LEFT OUTER JOIN KRIM_ATTR_DEFN_T a ON a.KIM_ATTR_DEFN_ID = d.KIM_ATTR_DEFN_ID WHERE rm.MBR_TYP_CD = 'R'
ORDER BY nmspc_cd, role_nm, mbr_role_nmspc_cd, mbr_role_nm, role_mbr_id, attr_nm / CREATE OR REPLACE VIEW KRIM_ROLE_V ( NMSPC_CD, ROLE_NM, ROLE_ID, ROLE_TYP_NM, SRVC_NM, KIM_TYP_ID ) AS SELECT r.NMSPC_CD , r.ROLE_NM , r.ROLE_ID , t.nm AS role_typ_nm , t.SRVC_NM , t.KIM_TYP_ID FROM KRIM_ROLE_T r , KRIM_TYP_T t WHERE t.KIM_TYP_ID = r.KIM_TYP_ID AND r.ACTV_IND = 'Y'
ORDER BY nmspc_cd
, role_nm
/
CREATE OR REPLACE VIEW KRIM_RSP_ATTR_V ( RESPONSIBILITY_TYPE_NAME, RSP_TEMPLATE_NAME, RSP_NAMESPACE_CODE, RSP_NAME, RSP_ID, ATTRIBUTE_NAME, ATTRIBUTE_VALUE ) AS SELECT krim_typ_t.NM AS responsibility_type_name , KRIM_rsp_TMPL_T.NM AS rsp_TEMPLATE_NAME , KRIM_rsp_T.nmspc_cd AS rsp_namespace_code , KRIM_rsp_T.NM AS rsp_NAME , krim_rsp_t.RSP_ID AS rsp_id , KRIM_ATTR_DEFN_T.NM AS attribute_name , KRIM_rsp_ATTR_DATA_T.ATTR_VAL AS attribute_value FROM KRIM_rsp_T KRIM_rsp_T INNER JOIN KRIM_rsp_ATTR_DATA_T KRIM_rsp_ATTR_DATA_T ON KRIM_rsp_T.rsp_ID = KRIM_rsp_ATTR_DATA_T.rsp_id INNER JOIN KRIM_ATTR_DEFN_T KRIM_ATTR_DEFN_T ON KRIM_rsp_ATTR_DATA_T.KIM_ATTR_DEFN_ID = KRIM_ATTR_DEFN_T.KIM_ATTR_DEFN_ID INNER JOIN KRIM_rsp_TMPL_T KRIM_rsp_TMPL_T ON KRIM_rsp_T.rsp_TMPL_ID = KRIM_rsp_TMPL_T.rsp_TMPL_ID INNER JOIN KRIM_TYP_T KRIM_TYP_T ON KRIM_rsp_TMPL_T.KIM_TYP_ID = KRIM_TYP_T.KIM_TYP_ID ORDER BY rsp_TEMPLATE_NAME, rsp_NAME, attribute_name / CREATE OR REPLACE VIEW KRIM_RSP_ROLE_ACTN_V ( RSP_NMSPC_CD, RSP_ID, NMSPC_CD, ROLE_NM, ROLE_ID, MBR_ID, MBR_TYP_CD, ROLE_MBR_ID, ACTN_TYP_CD, ACTN_PLCY_CD, IGNORE_PREV_IND, PRIORITY_NBR ) AS select rsp.nmspc_cd as rsp_nmspc_cd , rsp.rsp_id , r.NMSPC_CD , r.ROLE_NM , rr.ROLE_ID , rm.MBR_ID , rm.MBR_TYP_CD , rm.ROLE_MBR_ID , actn.ACTN_TYP_CD , actn.ACTN_PLCY_CD , actn.IGNORE_PREV_IND , actn.PRIORITY_NBR from krim_rsp_t rsp left join krim_rsp_tmpl_t rspt on rsp.rsp_tmpl_id = rspt.rsp_tmpl_id left outer join krim_role_rsp_t rr on rr.rsp_id = rsp.rsp_id left outer join KRIM_ROLE_MBR_T rm ON rm.ROLE_ID = rr.ROLE_ID left outer join KRIM_ROLE_RSP_ACTN_T actn ON actn.ROLE_RSP_ID = rr.ROLE_RSP_ID AND (actn.ROLE_MBR_ID = rm.ROLE_MBR_ID OR actn.ROLE_MBR_ID = '*') left outer join krim_role_t r on rr.role_id = r.role_id order by rsp_nmspc_cd , rsp_id , role_id , role_mbr_id / CREATE OR REPLACE VIEW KRIM_RSP_ROLE_V ( RSP_TMPL_NMSPC_CD, RSP_TMPL_NM, RSP_NMSPC_CD, RSP_NM, RSP_ID, ATTR_NM, ATTR_VAL, NMSPC_CD, ROLE_NM, ROLE_ID ) AS select rspt.nmspc_cd as rsp_tmpl_nmspc_cd , rspt.nm as rsp_tmpl_nm , rsp.nmspc_cd as rsp_nmspc_cd , rsp.nm as rsp_nm , rsp.rsp_id , a.nm as attr_nm , d.attr_val , r.NMSPC_CD , r.ROLE_NM , rr.ROLE_ID from krim_rsp_t rsp left join krim_rsp_tmpl_t rspt on rsp.rsp_tmpl_id = rspt.rsp_tmpl_id left outer join krim_rsp_attr_data_t d on rsp.rsp_id = d.rsp_id left outer join krim_attr_defn_t a on d.kim_attr_defn_id = a.kim_attr_defn_id left outer join krim_role_rsp_t rr on rr.rsp_id = rsp.rsp_id left outer join krim_role_t r on rr.role_id = r.role_id order by rsp_tmpl_nmspc_cd, rsp_tmpl_nm, rsp_nmspc_cd, rsp_nm, rsp_id, attr_nm, attr_val
/

ALTER TABLE KREW_ACTN_RQST_T RENAME COLUMN IGN_PREV_ACTN_IND TO FRC_ACTN
/
ALTER TABLE KREW_RULE_T RENAME COLUMN IGNR_PRVS TO FRC_ACTN
/
ALTER TABLE KRIM_PND_ROLE_RSP_ACTN_MT RENAME COLUMN IGNORE_PREV_IND TO FRC_ACTN
/
ALTER TABLE KRIM_ROLE_RSP_ACTN_T RENAME COLUMN IGNORE_PREV_IND TO FRC_ACTN
/
CREATE OR REPLACE VIEW KRIM_RSP_ROLE_ACTN_V ( RSP_NMSPC_CD, RSP_ID, NMSPC_CD, ROLE_NM, ROLE_ID, MBR_ID, MBR_TYP_CD, ROLE_MBR_ID, ACTN_TYP_CD, ACTN_PLCY_CD, FRC_ACTN, PRIORITY_NBR )
AS
select rsp.nmspc_cd as rsp_nmspc_cd, rsp.rsp_id, r.NMSPC_CD, r.ROLE_NM, rr.ROLE_ID, rm.MBR_ID, rm.MBR_TYP_CD, rm.ROLE_MBR_ID, actn.ACTN_TYP_CD, actn.ACTN_PLCY_CD, actn.FRC_ACTN, actn.PRIORITY_NBR
from krim_rsp_t rsp
left join krim_rsp_tmpl_t rspt
on rsp.rsp_tmpl_id = rspt.rsp_tmpl_id
left outer join krim_role_rsp_t rr
on rr.rsp_id = rsp.rsp_id
left outer join KRIM_ROLE_MBR_T rm
ON rm.ROLE_ID = rr.ROLE_ID
left outer join KRIM_ROLE_RSP_ACTN_T actn
ON actn.ROLE_RSP_ID = rr.ROLE_RSP_ID
AND (actn.ROLE_MBR_ID = rm.ROLE_MBR_ID OR actn.ROLE_MBR_ID = '*')
left outer join krim_role_t r
on rr.role_id = r.role_id
order by rsp_nmspc_cd, rsp_id, role_id, role_mbr_id
/
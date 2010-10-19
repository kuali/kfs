-- KIM Permission Templates
SELECT PERM_TMPL_ID, PERM_TMPL_ID AS OBJ_ID, 1 AS VER_NBR, NMSPC_CD, NM, DESC_TXT, KIM_TYP_ID, ACTV_IND 
    FROM KRIM_PERM_TMPL_T
    WHERE nmspc_cd LIKE 'KFS%'
       OR perm_tmpl_id = '50'
    ORDER BY PERM_TMPL_ID
/
-- KIM Permissions
SELECT p.PERM_ID, p.perm_id AS OBJ_ID, 1 AS VER_NBR, p.PERM_TMPL_ID, p.NMSPC_CD, p.NM, p.DESC_TXT, p.ACTV_IND 
    FROM KRIM_PERM_T p, KRIM_PERM_TMPL_T t
    WHERE p.PERM_TMPL_ID = t.PERM_TMPL_ID
       AND ( p.nmspc_cd LIKE 'KFS%' OR 
             t.NMSPC_CD LIKE 'KFS%' OR
             p.perm_tmpl_id = '50'
            )
    ORDER BY perm_id
/
-- KIM Permission Attributes
SELECT 'KFS'||ATTR_DATA_ID AS ATTR_DATA_ID, 'KFS'||attr_data_id AS OBJ_ID, 1 AS VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL 
    FROM KRIM_PERM_ATTR_DATA_T
    WHERE perm_id IN (
        SELECT p.PERM_ID
            FROM KRIM_PERM_T p, KRIM_PERM_TMPL_T t
            WHERE p.PERM_TMPL_ID = t.PERM_TMPL_ID
               AND ( p.nmspc_cd LIKE 'KFS%' OR 
                     t.NMSPC_CD LIKE 'KFS%' OR
                     p.perm_tmpl_id = '50'
                    )
    )
    ORDER BY perm_id, ATTR_DATA_ID
/

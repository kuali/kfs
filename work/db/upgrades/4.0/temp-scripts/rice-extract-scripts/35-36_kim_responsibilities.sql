-- KIM Responsibility
SELECT RSP_ID, 'KFS'||rsp_id AS OBJ_ID, 1 AS VER_NBR, RSP_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND 
    FROM KRIM_RSP_T
    WHERE NMSPC_CD LIKE 'KFS%'
      --AND rsp_id NOT LIKE '#%'
    ORDER BY rsp_id
/

-- KIM Responsibility Details
SELECT 'KFS'||ATTR_DATA_ID AS ATTR_DATA_ID, 'KFS'||ATTR_DATA_ID AS OBJ_ID, 1 AS VER_NBR, RSP_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL 
    FROM KRIM_RSP_ATTR_DATA_T
    WHERE rsp_id IN (
        SELECT RSP_ID
            FROM KRIM_RSP_T
            WHERE NMSPC_CD LIKE 'KFS%'
              --AND rsp_id NOT LIKE '#%'
        )
    ORDER BY rsp_id, ATTR_DATA_ID
/

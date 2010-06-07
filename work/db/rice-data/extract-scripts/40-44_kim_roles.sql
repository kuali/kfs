-- KFS Roles
SELECT ROLE_ID, 'KFS'||ROLE_ID AS OBJ_ID, 1 AS VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND 
    FROM KRIM_ROLE_T
    WHERE nmspc_cd LIKE 'KFS%'
    ORDER BY NMSPC_CD, ROLE_NM
/

-- KFS Role Members and KFS Members of Rice Roles
SELECT ROLE_MBR_ID, 1 AS VER_NBR, 'KFS'||ROLE_MBR_ID AS OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD
    FROM KRIM_ROLE_MBR_T
    WHERE role_id IN (
        SELECT ROLE_ID
            FROM KRIM_ROLE_T
            WHERE nmspc_cd LIKE 'KFS%'
    )
    AND NOT ( mbr_typ_cd = 'P' AND MBR_ID = '3' ) -- exclude the "admin" user
    --AND role_id IN ( '81', '28', '18' ) -- bootstrap data that should not be there
UNION ALL -- KFS roles (as members) assigned to rice roles
    SELECT ROLE_MBR_ID, 1 AS VER_NBR, 'KFS'||ROLE_MBR_ID AS OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD
        FROM KRIM_ROLE_MBR_T m
        WHERE role_id NOT IN (
            SELECT ROLE_ID
                FROM KRIM_ROLE_T
                WHERE nmspc_cd LIKE 'KFS%'
        )
        AND mbr_typ_cd = 'R'
 --       AND role_id NOT IN ( '81', '28', '18' ) -- bootstrap data that should not be there
        AND EXISTS ( SELECT 'x' FROM krim_role_t r WHERE r.ROLE_ID = m.mbr_id AND r.NMSPC_CD LIKE 'KFS%' )
UNION ALL -- KFS groups assigned to rice roles (none at present)
    SELECT ROLE_MBR_ID, 1 AS VER_NBR, 'KFS'||ROLE_MBR_ID AS OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD
        FROM KRIM_ROLE_MBR_T m
        WHERE role_id NOT IN (
            SELECT ROLE_ID
                FROM KRIM_ROLE_T
                WHERE nmspc_cd LIKE 'KFS%'
        )
        --AND role_id NOT IN ( '81', '28', '18' ) -- bootstrap data that should not be there
        AND mbr_typ_cd = 'G'
        AND EXISTS ( SELECT 'x' FROM krim_grp_t r WHERE r.grp_ID = m.mbr_id AND r.NMSPC_CD LIKE 'KFS%' )
ORDER BY ROLE_ID, ROLE_MBR_ID
/

-- Delegations
SELECT DLGN_ID, 1 AS VER_NBR, 'KFS'||DLGN_ID AS OBJ_ID, ROLE_ID, ACTV_IND, KIM_TYP_ID, DLGN_TYP_CD 
    FROM KRIM_DLGN_T
    WHERE role_id IN (
        SELECT ROLE_ID
            FROM KRIM_ROLE_T
            WHERE nmspc_cd LIKE 'KFS%'
    )
    ORDER BY DLGN_ID
/

-- Role member attribute data
SELECT 'KFS'||ATTR_DATA_ID AS ATTR_DATA_ID, 'KFS'||ATTR_DATA_ID AS OBJ_ID, 1 AS VER_NBR, ROLE_MBR_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL 
    FROM KRIM_ROLE_MBR_ATTR_DATA_T
    WHERE role_mbr_id IN (
        SELECT ROLE_MBR_ID
            FROM KRIM_ROLE_MBR_T
            WHERE role_id IN (
                SELECT ROLE_ID
                    FROM KRIM_ROLE_T
                    WHERE nmspc_cd LIKE 'KFS%'
            )
--            AND role_id NOT IN ( '81', '28', '18' ) -- bootstrap data that should not be there
		    AND NOT ( mbr_typ_cd = 'P' AND MBR_ID = '3' )
        UNION ALL
            SELECT ROLE_MBR_ID
                FROM KRIM_ROLE_MBR_T m
                WHERE role_id NOT IN (
                    SELECT ROLE_ID
                        FROM KRIM_ROLE_T
                        WHERE nmspc_cd LIKE 'KFS%'
                )
                AND mbr_typ_cd = 'R'
--                AND role_id NOT IN ( '81', '28', '18' ) -- bootstrap data that should not be there
                AND EXISTS ( SELECT 'x' FROM krim_role_t r WHERE r.ROLE_ID = m.mbr_id AND r.NMSPC_CD LIKE 'KFS%' )
        UNION ALL -- KFS groups assigned to rice roles
            SELECT ROLE_MBR_ID
                FROM KRIM_ROLE_MBR_T m
                WHERE role_id NOT IN (
                    SELECT ROLE_ID
                        FROM KRIM_ROLE_T
                        WHERE nmspc_cd LIKE 'KFS%'
                )
                AND mbr_typ_cd = 'G'
--                AND role_id NOT IN ( '81', '28', '18' ) -- bootstrap data that should not be there
                AND EXISTS ( SELECT 'x' FROM krim_grp_t r WHERE r.grp_ID = m.mbr_id AND r.NMSPC_CD LIKE 'KFS%' )
    )
    ORDER BY ROLE_MBR_ID, ATTR_DATA_ID
/

-- Delegation Members
SELECT DLGN_MBR_ID, 1 AS VER_NBR, 'KFS'||DLGN_MBR_ID AS OBJ_ID, DLGN_ID, MBR_ID, MBR_TYP_CD, ROLE_MBR_ID 
    FROM KRIM_DLGN_MBR_T
    WHERE dlgn_id IN (
        SELECT DLGN_ID
            FROM KRIM_DLGN_T
            WHERE role_id IN (
                SELECT ROLE_ID
                    FROM KRIM_ROLE_T
                    WHERE nmspc_cd LIKE 'KFS%'
            )
--            AND role_id NOT IN ( '7', '28', '37', '45', '54', '48', 'KFS5014' ) -- dynamically loaded roles, only at migration-time
    )
    AND NOT ( mbr_typ_cd = 'P' AND MBR_ID = '3' )
    ORDER BY DLGN_ID, DLGN_MBR_ID
/


SELECT 'KFS'||ATTR_DATA_ID AS ATTR_DATA_ID, 'KFS'||ATTR_DATA_ID AS OBJ_ID, 1 AS VER_NBR, DLGN_MBR_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL 
    FROM KRIM_DLGN_MBR_ATTR_DATA_T
    WHERE DLGN_MBR_ID IN (
        SELECT DLGN_MBR_ID
            FROM KRIM_DLGN_MBR_T
            WHERE dlgn_id IN (
                SELECT DLGN_ID
                    FROM KRIM_DLGN_T
                    WHERE role_id IN (
                        SELECT ROLE_ID
                            FROM KRIM_ROLE_T
                            WHERE nmspc_cd LIKE 'KFS%'
                    )
                    --AND role_id NOT IN ( '7', '28', '37', '45', '54', '48', 'KFS5014' ) -- dynamically loaded roles, only at migration-time
            )
            AND NOT ( mbr_typ_cd = 'P' AND MBR_ID = '3' )
        )
    ORDER BY DLGN_MBR_ID, ATTR_DATA_ID
/


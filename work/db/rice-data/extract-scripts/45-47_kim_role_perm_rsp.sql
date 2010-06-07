-- KIM Role/Permission Relationships
SELECT 'KFS'||ROLE_PERM_ID AS ROLE_PERM_ID, 'KFS'||ROLE_PERM_ID AS OBJ_ID, 1 AS VER_NBR, ROLE_ID, PERM_ID, ACTV_IND 
    FROM KRIM_ROLE_PERM_T
    WHERE 
      role_id IN ( -- KFS Roles
        SELECT ROLE_ID
            FROM KRIM_ROLE_T
            WHERE nmspc_cd LIKE 'KFS%'
    )
    OR  
      perm_id IN ( -- KFS Permissions
        SELECT p.PERM_ID
            FROM KRIM_PERM_T p, KRIM_PERM_TMPL_T t
            WHERE p.PERM_TMPL_ID = t.PERM_TMPL_ID
               AND ( p.nmspc_cd LIKE 'KFS%' OR 
                     t.NMSPC_CD LIKE 'KFS%'
                    )
    )
    ORDER BY role_perm_id
/

-- KIM Role/Responsibility Relationships
SELECT ROLE_RSP_ID AS ROLE_RSP_ID, 'KFS'||ROLE_RSP_ID AS OBJ_ID, 1 AS VER_NBR, ROLE_ID, RSP_ID, ACTV_IND 
    FROM KRIM_ROLE_RSP_T
    WHERE role_id IN ( -- KFS Roles
        SELECT ROLE_ID
            FROM KRIM_ROLE_T
            WHERE nmspc_cd LIKE 'KFS%'
    )
    OR rsp_id IN ( -- KFS responsibilities
        SELECT RSP_ID
            FROM KRIM_RSP_T
            WHERE NMSPC_CD LIKE 'KFS%'
        )
    ORDER BY ROLE_RSP_ID
/

-- Role-Responisibilty Actions
SELECT 'KFS'||ROLE_RSP_ACTN_ID AS ROLE_RSP_ACTN_ID, 'KFS'||ROLE_RSP_ACTN_ID AS OBJ_ID, 1 AS VER_NBR, ACTN_TYP_CD, PRIORITY_NBR, ACTN_PLCY_CD, ROLE_MBR_ID, ROLE_RSP_ID, FRC_ACTN 
    FROM KRIM_ROLE_RSP_ACTN_T
    WHERE role_rsp_id IN ( 
        SELECT ROLE_RSP_ID
            FROM KRIM_ROLE_RSP_T
            WHERE role_id IN ( -- KFS Roles
                SELECT ROLE_ID
                    FROM KRIM_ROLE_T
                    WHERE nmspc_cd LIKE 'KFS%'
            )
            OR rsp_id IN (
                SELECT RSP_ID
                    FROM KRIM_RSP_T
                    WHERE NMSPC_CD LIKE 'KFS%'
                )
        )
      OR role_mbr_id IN (
        SELECT ROLE_MBR_ID
            FROM KRIM_ROLE_MBR_T
            WHERE role_id IN (
                SELECT ROLE_ID
                    FROM KRIM_ROLE_T
                    WHERE nmspc_cd LIKE 'KFS%'
            )
        UNION ALL
            SELECT ROLE_MBR_ID
                FROM KRIM_ROLE_MBR_T m
                WHERE role_id NOT IN (
                    SELECT ROLE_ID
                        FROM KRIM_ROLE_T
                        WHERE nmspc_cd LIKE 'KFS%'
                )
                AND mbr_typ_cd = 'R'
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
                AND EXISTS ( SELECT 'x' FROM krim_grp_t r WHERE r.grp_ID = m.mbr_id AND r.NMSPC_CD LIKE 'KFS%' )
    )    
    ORDER BY ROLE_RSP_ACTN_ID
/


--1. create a new permission

set @newPermId =(SELECT MAX(CAST(perm_id as UNSIGNED))+1 FROM krim_perm_t);
select @newPermId;

INSERT INTO KRIM_PERM_T
(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
VALUES (@newPermId, uuid(), 1, 10, 'KFS-AR', 'Initiate Document', 'Authorizes the initiation of the the Organization Options', 'Y');
--* next_perm_id: eg) '10002'

--2. Add the new permission details
set @newAttrDataId =(SELECT MAX(CAST(attr_data_id as UNSIGNED))+1 FROM KRIM_PERM_ATTR_DATA_T);
INSERT INTO KRIM_PERM_ATTR_DATA_T
(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
VALUES (@newAttrDataId, uuid(), 1, @newPermId, '3', '13', 'OOPT');
--* next_attr_data_id: eg) '10008'
--* next_perm_id: eg) '10002'

--3. Add the new permission to role 31 (Accounts Receivable Manager)
set @newRolePermId =(SELECT MAX(CAST(role_perm_id as UNSIGNED))+1 FROM KRIM_ROLE_PERM_T);
INSERT INTO KRIM_ROLE_PERM_T
(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
VALUES (@newRolePermId, uuid(), 1, '31', @newPermId, 'Y') ;
--* next_role_perm_id: eg) '1000'

--4. Add permission 248 to role 31
set @newRolePermId =(SELECT MAX(CAST(role_perm_id as UNSIGNED))+1 FROM KRIM_ROLE_PERM_T);
INSERT INTO KRIM_ROLE_PERM_T
(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
VALUES (@newRolePermId, uuid(), 1, '31', '248', 'Y') ;
--* next_role_perm_id: eg) '1001'
SET SERVEROUTPUT ON

DECLARE
  new_perm_id NUMBER;
  new_attr_data_id NUMBER;
  new_role_perm_id NUMBER;
  target_perm_id VARCHAR2(5);
  target_role_id VARCHAR2(5);
  
BEGIN

  target_perm_id := '248';
  target_role_id := '31';
   
  -- create a new permission for initiate document OOPT
  select max(to_number(perm_id)) into new_perm_id from krim_perm_t;
  new_perm_id := new_perm_id + 1;
  DBMS_OUTPUT.PUT_LINE('new_perm_id: ' || new_perm_id);

  INSERT INTO KRIM_PERM_T
    (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
    VALUES (TO_CHAR(new_perm_id), SYS_GUID(), 1, 10, 'KFS-AR', 'Initiate Document', 'Authorizes the initiation of the the Organization Options', 'Y');

  -- add the new permission details
  select max(to_number(attr_data_id)) into new_attr_data_id from KRIM_PERM_ATTR_DATA_T;
  new_attr_data_id := new_attr_data_id + 1;
  DBMS_OUTPUT.PUT_LINE('new_attr_data_id: ' || new_attr_data_id);
  
  INSERT INTO KRIM_PERM_ATTR_DATA_T
    (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES (TO_CHAR(new_attr_data_id), SYS_GUID(), 1, TO_CHAR(new_perm_id), '3', '13', 'OOPT');
  
  -- add the new permission to the role 31 - Accounts Receivable Manager
  select max(to_number(role_perm_id)) into new_role_perm_id from KRIM_ROLE_PERM_T;
  new_role_perm_id := new_role_perm_id + 1;
  DBMS_OUTPUT.PUT_LINE('new_role_perm_id_1: ' || new_role_perm_id);
  
  INSERT INTO KRIM_ROLE_PERM_T
    (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES (TO_CHAR(new_role_perm_id), SYS_GUID(), 1, target_role_id, TO_CHAR(new_perm_id), 'Y') ;

  -- add permission 248 to the role 31
  select max(to_number(role_perm_id)) into new_role_perm_id from KRIM_ROLE_PERM_T;
  new_role_perm_id := new_role_perm_id + 1;
  DBMS_OUTPUT.PUT_LINE('new_role_perm_id_2: ' || new_role_perm_id);
  
  INSERT INTO KRIM_ROLE_PERM_T
    (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES (TO_CHAR(new_role_perm_id), SYS_GUID(), 1, target_role_id, target_perm_id, 'Y') ;

  DBMS_OUTPUT.PUT_LINE('Done');
END;
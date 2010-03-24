
-- CREATE THE PERMISSION FIRST BEFORE ASSIGNING TO THE ROLES.  ALSO, PERM_ID IS CHOSEN 1100...
INSERT INTO KRIM_PERM_T
(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
VALUES ('1100', 'SYS_GUID()', 1, '3', 'KFS-SYS', 'Administer Routing for Document', 
'Allows users to open Financial System Documents via the Super search option in Document Search and take Administrative workflow actions on them (such as approving the document, approving individual requests, or sending the document to a specified route node).", 'Y');

-- NOW ADD THE NEW PERMISSION DETAIL VALUES..
INSERT INTO KRIM_PERM_ATTR_DATA_T
(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
VALUES ('10000', ''SYS_GUID()', 1, '1100', '3', '13', 'KFST');

-- NOW ASSIGN TO THE ROLES: KFS-SYS System Use (Role 62)
INSERT INTO KRIM_ROLE_PERM_T
(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
VALUES ('1106', 'SYS_GUID()', 1, '62', '1100', 'Y') ;

-- NOW ASSIGN TO THE ROLES: Workflow Administrator (Role 55)
INSERT INTO KRIM_ROLE_PERM_T
(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
VALUES ('1106', 'SYS_GUID()', 1, '55', '1100', 'Y') ;


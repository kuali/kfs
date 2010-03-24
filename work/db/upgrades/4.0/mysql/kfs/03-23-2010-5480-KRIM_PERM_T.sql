-- Changeset updates/2010-03-22-5480-PermissionForAutoDisapprovalStepJob.xml::5480-1-1::Muddu::(MD5Sum: c98d16b7dce8106064fe499ff3990d1)
-- Setup a new permission for document type KFST
INSERT INTO `KRIM_PERM_T` (`DESC_TXT`, `VER_NBR`, `PERM_TMPL_ID`, `OBJ_ID`, `ACTV_IND`, `PERM_ID`, `NM`, `NMSPC_CD`) VALUES ('Allows users to open Financial System Documents via the Super search option in Document Search and take Administrative workflow actions on them such as approving the document, approving individual requests, or sending the document to a specified route node', 1, '3', 'SYS_GUID', 'Y', '1100', 'Administer Routing for Document', 'KFS-SYS');

-- Changeset updates/2010-03-22-5480-PermissionForAutoDisapprovalStepJob.xml::5480-1-2::Muddu::(MD5Sum: f51120927622fa5f175fcdd8a1c5ef8)
-- Setup a new permission for document type KFST
INSERT INTO `KRIM_PERM_ATTR_DATA_T` (`VER_NBR`, `OBJ_ID`, `ATTR_VAL`, `PERM_ID`, `KIM_ATTR_DEFN_ID`, `ATTR_DATA_ID`, `KIM_TYP_ID`) VALUES (1, 'SYS_GUID', 'KFST', '1100', '13', '10000', '3');

-- Changeset updates/2010-03-22-5480-PermissionForAutoDisapprovalStepJob.xml::5480-1-3::Muddu::(MD5Sum: 248847b4eeb46c18a98f73aa67f3d71)
-- Setup a new permission for document type KFST
INSERT INTO `KRIM_ROLE_PERM_T` (`VER_NBR`, `OBJ_ID`, `ACTV_IND`, `PERM_ID`, `ROLE_PERM_ID`, `ROLE_ID`) VALUES (1, 'SYS_GUID', 'Y', '1100', '1106', '62');

-- Changeset updates/2010-03-22-5480-PermissionForAutoDisapprovalStepJob.xml::5480-1-4::Muddu::(MD5Sum: 34e330a646744efcb5e9dbdb2beb5f4)
-- Setup a new permission for document type KFST
INSERT INTO `KRIM_ROLE_PERM_T` (`VER_NBR`, `OBJ_ID`, `ACTV_IND`, `PERM_ID`, `ROLE_PERM_ID`, `ROLE_ID`) VALUES (1, 'SYS_GUID', 'Y', '1100', '1106', '55');

-- Changeset updates/2010-03-22-5480-PermissionForAutoDisapprovalStepJob.xml::5480-1-5::Muddu::(MD5Sum: b8e36db46b85956b9ebde781aa8299)
-- Setup a new permission for document type KFST
DELETE FROM `KRIM_ROLE_PERM_T`  WHERE ROLE_ID IN ('62', '55') AND PERM_ID IN ('68', '203', '204');

-- Changeset updates/2010-03-22-5480-PermissionForAutoDisapprovalStepJob.xml::5480-1-6::Muddu::(MD5Sum: c341b543877d22342d35f8fa0ea9b)
-- Setup a new permission for document type KFST
DELETE FROM `KRIM_PERM_T`  WHERE NM = 'Administer Routing for Document' AND PERM_ID IN ('68', '203', '204');


-- Changeset updates/2010-03-22-5480-PermissionForAutoDisapprovalStepJob.xml::5480-1-2::Muddu::(MD5Sum: 35a49d486e238ce6cb859248171d570)
-- Setup a new permission for document type KFST
INSERT INTO `KRIM_PERM_ATTR_DATA_T` (`VER_NBR`, `OBJ_ID`, `ATTR_VAL`, `PERM_ID`, `KIM_ATTR_DEFN_ID`, `ATTR_DATA_ID`, `KIM_TYP_ID`) VALUES (1, uuid(), 'KFST', '1100', '13', '10000', '3');

-- Changeset updates/2010-03-22-5480-PermissionForAutoDisapprovalStepJob.xml::5480-1-3::Muddu::(MD5Sum: f614af0a286319181dbfb207f35b5)
-- Setup a new permission for document type KFST
INSERT INTO `KRIM_ROLE_PERM_T` (`VER_NBR`, `OBJ_ID`, `ACTV_IND`, `PERM_ID`, `ROLE_PERM_ID`, `ROLE_ID`) VALUES (1, uuid(), 'Y', '1100', '1106', '62');

-- Changeset updates/2010-03-22-5480-PermissionForAutoDisapprovalStepJob.xml::5480-1-4::Muddu::(MD5Sum: 6d729a28275fb79c27192be4767868)
-- Setup a new permission for document type KFST
INSERT INTO `KRIM_ROLE_PERM_T` (`VER_NBR`, `OBJ_ID`, `ACTV_IND`, `PERM_ID`, `ROLE_PERM_ID`, `ROLE_ID`) VALUES (1, uuid(), 'Y', '1100', '1106', '55');

-- Changeset updates/2010-03-22-5480-PermissionForAutoDisapprovalStepJob.xml::5480-1-5::Muddu::(MD5Sum: b8e36db46b85956b9ebde781aa8299)
-- Setup a new permission for document type KFST
DELETE FROM `KRIM_ROLE_PERM_T`  WHERE ROLE_ID IN ('62', '55') AND PERM_ID IN ('68', '203', '204');

-- Changeset updates/2010-03-22-5480-PermissionForAutoDisapprovalStepJob.xml::5480-1-6::Muddu::(MD5Sum: c341b543877d22342d35f8fa0ea9b)
-- Setup a new permission for document type KFST
DELETE FROM `KRIM_PERM_T`  WHERE NM = 'Administer Routing for Document' AND PERM_ID IN ('68', '203', '204');


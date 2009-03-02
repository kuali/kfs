-- make org hierarchy nodes run in parallel
UPDATE KREW_RTE_NODE_T
    SET ACTVN_TYP = 'P'
    WHERE nm LIKE '%OrganizationHierarchy'
/
-- fix document type names in the role member table
UPDATE KRIM_ROLE_MBR_ATTR_DATA_T
    SET attr_val = 'KBD'
    WHERE attr_val = 'BudgetDocument'
      AND KIM_ATTR_DEFN_ID = (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T WHERE nm = 'documentTypeName')
/
UPDATE KRIM_ROLE_MBR_ATTR_DATA_T
    SET attr_val = 'ACCT'
    WHERE attr_val = 'AccountMaintenanceDocument'
      AND KIM_ATTR_DEFN_ID = (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T WHERE nm = 'documentTypeName')
/
UPDATE KRIM_ROLE_MBR_ATTR_DATA_T
    SET attr_val = 'DV'
    WHERE attr_val = 'DisbursementVoucherDocument'
      AND KIM_ATTR_DEFN_ID = (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T WHERE nm = 'documentTypeName')
/
UPDATE KRIM_ROLE_MBR_ATTR_DATA_T
    SET attr_val = 'TF'
    WHERE attr_val = 'TransferOfFundsDocument'
      AND KIM_ATTR_DEFN_ID = (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T WHERE nm = 'documentTypeName')
/
UPDATE KRIM_ROLE_MBR_ATTR_DATA_T
    SET attr_val = 'BA'
    WHERE attr_val = 'BudgetAdjustmentDocument'
      AND KIM_ATTR_DEFN_ID = (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T WHERE nm = 'documentTypeName')
/
UPDATE KRIM_ROLE_MBR_ATTR_DATA_T
    SET attr_val = 'REQS'
    WHERE attr_val = 'RequisitionDocument'
      AND KIM_ATTR_DEFN_ID = (select KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T WHERE nm = 'documentTypeName')
/

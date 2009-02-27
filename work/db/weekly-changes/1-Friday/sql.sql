alter table AR_INV_DOC_T add NXT_INV_ITEM_NBR NUMBER(7)
/
UPDATE KRNS_PARM_T
SET PARM_DESC_TXT = 'The Labor Object table pay type code for hourly/biweekly pay.'
WHERE NMSPC_CD = 'KFS-BC'
 AND PARM_DTL_TYP_CD = 'BudgetConstruction'
 AND PARM_NM = 'BIWEEKLY_PAY_TYPE_CODES'
/

UPDATE KRNS_PARM_T
SET PARM_DESC_TXT = 'Fund groups that only allow accounting lines with objects found in the Labor Object table having Detail Position Required.'
WHERE NMSPC_CD = 'KFS-BC'
 AND PARM_DTL_TYP_CD = 'BudgetConstruction'
 AND PARM_NM = 'SALARY_SETTING_FUND_GROUPS'
/

commit
/
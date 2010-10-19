INSERT INTO KRNS_PARM_T 
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD) 
VALUES ('KFS-FP', 'BudgetAdjustment', 'RESEARCH_ADMIN_BA_DOCUMENT_ROUTE_ACTION', SYS_GUID(), 1, 'CONFG', 
'save', 'If KFS is integrated with a Research Admin System that has the ability to generate KFS budget adjustment documents, this is the routing action that will take place on the generated document.', 'A', 'KFS');

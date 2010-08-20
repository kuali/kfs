INSERT INTO KRNS_PARM_T(nmspc_cd, parm_dtl_typ_cd, parm_nm, obj_id, ver_nbr, parm_typ_cd, txt, parm_desc_txt, cons_cd, appl_nmspc_cd)
VALUES (
'KFS-ENDOW',
'EndowmentTransactionCode',
'TRANSACTION_ARCHIVE_DOCUMENT_TYPE_NAMES',
sys_guid(),
1,
'CONFG',
'EAD;EAI;ECDD;ECI;ECT;ECA;EUSA;ELD;ELI;EST;EHA;EGLT;GLET',
'The values of this parameter represent a complete list of document type names for the endowment transactional documents that can be posted to the endowment transaction archive table. The application maps this list to the document types that have been registered with the Workflow Engine.',
'A',
'KFS'); 
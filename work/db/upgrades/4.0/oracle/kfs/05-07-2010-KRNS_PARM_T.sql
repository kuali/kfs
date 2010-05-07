insert into KRNS_PARM_T
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD)
values
('KFS-LD', 'LaborScrubberStep', 'DEMERGE_DOCUMENT_TYPES', sys_guid(), 1, 'CONFG', 'BT;ST', 'Document type(s) that will demerge (remove entire document from processing) all entries with the same document number when an error is detected on one entry with that document number. Document types not listed here will only have error entries removed from processing; no other records will be demerged.', 'A', 'KFS'); 

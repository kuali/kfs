-- Capitalization Offset Code
insert into KRNS_PARM_T
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD)
values
('KFS-GL','ScrubberStep','CAPITALIZATION_OFFSET_CODE',sys_guid(),1,'CONFG','','This overrides the Chart Fund Balance object code for Capitalization.','A','KFS');

-- Liability Offset Code
insert into KRNS_PARM_T
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD)
values
('KFS-GL','ScrubberStep','LIABILITY_OFFSET_CODE',sys_guid(),1,'CONFG','','This overrides the Chart Fund Balance object code for Liability.','A','KFS');


-- Plant Indebtedness Offset Code
insert into KRNS_PARM_T
(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD,APPL_NMSPC_CD)
values
('KFS-GL','ScrubberStep','PLANT_INDEBTEDNESS_OFFSET_CODE',sys_guid(),1,'CONFG','','This overrides the Chart Fund Balance object code for Plant Indebtedness.','A','KFS'); 

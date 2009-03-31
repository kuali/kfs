update krim_entity_emp_info_t set emp_stat_cd = 'A' where emp_stat_cd not in ('A', 'L', 'P') and entity_id in (select entity_id from krim_prncpl_t where prncpl_id in (select mbr_id from krim_role_mbr_t where mbr_typ_cd = 'P'))
/

ALTER TABLE KRIM_DLGN_MBR_T
	ADD ROLE_MBR_ID VARCHAR2(40)
/
ALTER TABLE KRIM_DLGN_MBR_T
	ADD CONSTRAINT KRIM_DLGN_MBR_TR1
	FOREIGN KEY (ROLE_MBR_ID)
	REFERENCES KRIM_ROLE_MBR_T(ROLE_MBR_ID)
/

ALTER TABLE KRIM_DLGN_MBR_T
	ADD CONSTRAINT KRIM_DLGN_MBR_TR2
	FOREIGN KEY (DLGN_ID)
	REFERENCES KRIM_DLGN_T(DLGN_ID)
/

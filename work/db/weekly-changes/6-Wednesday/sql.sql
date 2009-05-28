UPDATE KRIM_PERM_ATTR_DATA_T SET PERM_ID=375 WHERE ATTR_DATA_ID =572
/
UPDATE KRIM_ROLE_PERM_T SET PERM_ID=375 WHERE ROLE_PERM_ID=684
/
UPDATE KRIM_PERM_ATTR_DATA_T SET PERM_ID=376 WHERE ATTR_DATA_ID =573
/
UPDATE KRIM_ROLE_PERM_T SET PERM_ID=376 WHERE ROLE_PERM_ID=685
/
UPDATE KRIM_ROLE_PERM_T SET PERM_ID=377 WHERE ROLE_PERM_ID IN (686,687)
/

update krns_parm_t set TXT = 'F=A,C,S;G=A,C,S;N=A,C,S;P=A,C,S;S=A,C,S;T=A,C,S;A=N' where parm_nm = 'VALID_ASSET_STATUSES_BY_ACQUISITION_TYPE'
/


--original dept code - BL-ARSD
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4003', '5B0A1BA85D7F27E4E0404F8189D83938', '1', '1', '36', '22', 'NW')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4011', '5B0A1BA85D8027E4E0404F8189D83938', '1', '1', '36', '24', 'FORN')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4021', '5B0A1BA85DBC27E4E0404F8189D83938', '1', '1', '36', '4', 'KFS-SYS')
/
--original dept code - BL-ARSD
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4000', '5B0A1BA85D8B27E4E0404F8189D83938', '1', '4', '36', '22', 'BL')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4001', '5B0A1BA85D8C27E4E0404F8189D83938', '1', '4', '36', '24', 'ACTG')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4022', '5B0A1BA85DBF27E4E0404F8189D83938', '1', '4', '36', '4', 'KFS-SYS')
/
--original dept code - BL-ARSD
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4002', '5B0A1BA85D9027E4E0404F8189D83938', '1', '5', '36', '22', 'BL')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4032', '5B0A1BA85DDB27E4E0404F8189D83938', '1', '5', '36', '24', 'ADMS')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4023', '5B0A1BA85DD827E4E0404F8189D83938', '1', '5', '36', '4', 'KFS-SYS')
/
--original dept code - BL-BL!
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4004', '5B0A1BA85D9727E4E0404F8189D83938', '1', '7', '36', '22', 'BL')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4005', '5B0A1BA85D9827E4E0404F8189D83938', '1', '7', '36', '24', 'BOND')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4024', '5B0A1BA85DC327E4E0404F8189D83938', '1', '7', '36', '4', 'KFS-SYS')
/
--original dept code - BL-PSY
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4006', '5B0A1BA85D9B27E4E0404F8189D83938', '1', '8', '36', '22', 'BL')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4007', '5B0A1BA85D9C27E4E0404F8189D83938', '1', '8', '36', '24', 'AIND')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4025', '5B0A1BA85DC427E4E0404F8189D83938', '1', '8', '36', '4', 'KFS-SYS')
/
--original dept code - BL-RUAD
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4008', '5B0A1BA85D9F27E4E0404F8189D83938', '1', '9', '36', '22', 'NW')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4009', '5B0A1BA85DA027E4E0404F8189D83938', '1', '9', '36', '24', 'NURS')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4026', '5B0A1BA85DC727E4E0404F8189D83938', '1', '9', '36', '4', 'KFS-SYS')
/
--original dept code - BL-PSY
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4010', '5B0A1BA85DA327E4E0404F8189D83938', '1', '10', '36', '22', 'BL')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4012', '5B0A1BA85DA427E4E0404F8189D83938', '1', '10', '36', '24', 'BUS')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4027', '5B0A1BA85DC827E4E0404F8189D83938', '1', '10', '36', '4', 'KFS-SYS')
/
--original dept code - BL-PSY
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4013', '5B0A1BA85DA727E4E0404F8189D83938', '1', '11', '36', '22', 'IN')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4014', '5B0A1BA85DA827E4E0404F8189D83938', '1', '11', '36', '24', 'AFFA')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4028', '5B0A1BA85DCB27E4E0404F8189D83938', '1', '11', '36', '4', 'KFS-SYS')
/
--original dept code - NW-SOC
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4015', '5B0A1BA85DAB27E4E0404F8189D83938', '1', '12', '36', '22', 'NW')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4016', '5B0A1BA85DAC27E4E0404F8189D83938', '1', '12', '36', '24', 'BOOK')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4029', '5B0A1BA85DCC27E4E0404F8189D83938', '1', '12', '36', '4', 'KFS-SYS')
/
--original dept code - BL-PSY
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4017', '5B0A1BA85DAF27E4E0404F8189D83938', '1', '13', '36', '22', 'IN')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4018', '5B0A1BA85DB027E4E0404F8189D83938', '1', '13', '36', '24', 'ALUM')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4030', '5B0A1BA85DCF27E4E0404F8189D83938', '1', '13', '36', '4', 'KFS-SYS')
/
--original dept code - IN_PD
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4019', '5B0A1BA85DB327E4E0404F8189D83938', '1', '14', '36', '22', 'NW')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4020', '5B0A1BA85DB427E4E0404F8189D83938', '1', '14', '36', '24', 'CACA')
/
insert into krim_role_mbr_attr_data_t(attr_data_id, obj_id,ver_nbr, role_mbr_id, kim_typ_id,kim_attr_defn_id, attr_val)
values('4031', '5B0A1BA85DD027E4E0404F8189D83938', '1', '14', '36', '4', 'KFS-SYS')
/

DECLARE
	toDisplay VARCHAR(40);
	counter NUMBER;
	dept_code VARCHAR(20);
	role_mbr_id_to_delete VARCHAR(20);
	nmspc_cd VARCHAR(20);
	CURSOR CurRoleMbr IS select * from KRIM_ROLE_MBR_T where role_id in (select role_id from krim_role_t where KIM_TYP_ID=(select kim_typ_id from krim_typ_t where NM like 'Financial System User')) order by to_number(role_mbr_id);
	role_mbr_rec CurRoleMbr%ROWTYPE;
	CURSOR CurEntityEmpData IS select PRMRY_DEPT_CD from krim_entity_emp_info_t where entity_id = (select entity_id from krim_prncpl_t where prncpl_id = (select mbr_id from krim_role_mbr_t where role_mbr_id = role_mbr_rec.ROLE_MBR_ID));
	entity_emp_data CurEntityEmpData%ROWTYPE;
BEGIN
	counter := 0;
	nmspc_cd := null;
	dbms_output.enable;
	OPEN CurRoleMbr;
	LOOP
		FETCH CurRoleMbr INTO role_mbr_rec;
		EXIT WHEN CurRoleMbr%NOTFOUND;
		OPEN CurEntityEmpData;
		LOOP
			FETCH CurEntityEmpData INTO entity_emp_data;
			EXIT WHEN CurEntityEmpData%NOTFOUND;
			
			select ((select attr_val from krim_role_mbr_attr_data_t where kim_attr_defn_id = 22 and role_mbr_id=role_mbr_rec.ROLE_MBR_ID) || '-' || (select attr_val from krim_role_mbr_attr_data_t where kim_attr_defn_id = 24 and role_mbr_id=role_mbr_rec.ROLE_MBR_ID)) into dept_code from dual;
		
			select nvl((select attr_val from krim_role_mbr_attr_data_t where kim_attr_defn_id = 4 and role_mbr_id=role_mbr_rec.ROLE_MBR_ID),'null') into nmspc_cd from dual;

			if nmspc_cd = 'null' and dept_code = entity_emp_data.PRMRY_DEPT_CD  THEN
				role_mbr_id_to_delete := role_mbr_rec.ROLE_MBR_ID;
				delete from krim_role_mbr_attr_data_t where ROLE_MBR_ID=role_mbr_id_to_delete;
				counter := counter+1;
			END if;
		END LOOP;
		CLOSE CurEntityEmpData;
	END LOOP;
	CLOSE CurRoleMbr;
	DBMS_OUTPUT.PUT_LINE('Number of role members whose attr data was deleted: ');
	DBMS_OUTPUT.PUT_LINE(counter);
END;

--original dept code - UA-VPIT
update krim_role_mbr_attr_data_t set attr_val='NW' where ROLE_MBR_ID=654 and KIM_ATTR_DEFN_ID=22
/
update krim_role_mbr_attr_data_t set attr_val='CHEM' where ROLE_MBR_ID=654 and KIM_ATTR_DEFN_ID=24
/

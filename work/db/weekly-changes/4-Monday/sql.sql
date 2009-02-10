INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT, LAST_UPDT_DT) 
    VALUES('1640', 1, sys_guid(), '20', '19', 'R', null, null, SYSDATE)
/
delete from krim_role_mbr_t where role_mbr_id = '1353'
/
Insert into krns_parm_t
SELECT 'KFS-CG', 'CfdaBatchStep', 'RESULT_SUMMARY_TO_EMAIL_ADDRESSES' , sys_guid(),1,
       'CONFG', 'kbatch-l@indiana.edu', 'Listserv for recipients of CFDA batch process results.',
       'A'
  FROM dual
/
alter table KRIM_ENTITY_ADDR_T
drop constraint KRIM_ENTITY_ADDR_TR2
/
-- Start of DDL Script for Table KULDEV.KRIM_ADDR_TYP_T
-- Generated 2/9/2009 1:51:02 PM from KULDEV@
create table krim_addr_typ_t_bak as select * from krim_addr_typ_t
/ 
drop table krim_addr_typ_t cascade constraints purge
/
CREATE TABLE krim_addr_typ_t
    (addr_typ_cd                    VARCHAR2(40) NOT NULL,
    obj_id                         VARCHAR2(36) NOT NULL,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    nm                             VARCHAR2(40),
    actv_ind                       VARCHAR2(1) DEFAULT 'Y',
    display_sort_cd                VARCHAR2(2),
    last_updt_dt                   DATE DEFAULT SYSDATE)
/
insert into krim_addr_typ_t select * from krim_addr_typ_t_bak
/
commit
/
-- Constraints for KRIM_ADDR_TYP_T

ALTER TABLE krim_addr_typ_t
ADD CONSTRAINT krim_addr_typ_tc0 UNIQUE (obj_id)
USING INDEX
/

ALTER TABLE krim_addr_typ_t
ADD CONSTRAINT krim_addr_typ_tc1 UNIQUE (nm)
USING INDEX
/

ALTER TABLE krim_addr_typ_t
ADD CONSTRAINT krim_addr_typ_tp1 PRIMARY KEY (addr_typ_cd)
USING INDEX
/


-- End of DDL Script for Table KULDEV.KRIM_ADDR_TYP_T
alter table KRIM_ENTITY_ADDR_T
add constraint KRIM_ENTITY_ADDR_TR2
foreign key (addr_typ_cd) references krim_addr_typ_t(addr_typ_cd)
/
drop table krim_addr_typ_t_bak
/
-- Start of DDL Script for Table KULDEV.KRIM_DLGN_ATTR_DATA_T
-- Generated 2/9/2009 3:28:47 PM from KULDEV@
create table krim_dlgn_attr_data_t_bak as select * from krim_dlgn_attr_data_t
/
drop table krim_dlgn_attr_data_t cascade constraints purge
/

CREATE TABLE krim_dlgn_attr_data_t
    (attr_data_id                   VARCHAR2(40),
    obj_id                         VARCHAR2(36) NOT NULL,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    target_primary_key             VARCHAR2(40),
    kim_typ_id                     VARCHAR2(40),
    kim_attr_defn_id               VARCHAR2(40),
    attr_val                       VARCHAR2(400))
/

insert into krim_dlgn_attr_data_t select * from krim_dlgn_attr_data_t_bak
/
commit
/
-- Constraints for KRIM_DLGN_ATTR_DATA_T

ALTER TABLE krim_dlgn_attr_data_t
ADD CONSTRAINT krim_dlgn_attr_data_tc0 UNIQUE (obj_id)
/

ALTER TABLE krim_dlgn_attr_data_t
ADD CONSTRAINT krim_dlgt_attr_data_tp1 
Primary key (attr_data_id)
/





-- End of DDL Script for Table KULDEV.KRIM_DLGN_ATTR_DATA_T

-- Foreign Key
ALTER TABLE krim_dlgn_attr_data_t
ADD CONSTRAINT krim_dlgn_attr_data_tr2 FOREIGN KEY (kim_attr_defn_id)
REFERENCES krim_attr_defn_t (kim_attr_defn_id)
/
ALTER TABLE krim_dlgn_attr_data_t
ADD CONSTRAINT krim_dlgn_attr_data_tr3 FOREIGN KEY (target_primary_key)
REFERENCES krim_dlgn_t (dlgn_id) ON DELETE CASCADE
/
ALTER TABLE krim_dlgn_attr_data_t
ADD CONSTRAINT krim_dlgn_attr_data_tr1 FOREIGN KEY (kim_typ_id)
REFERENCES krim_typ_t (kim_typ_id)
/
-- End of DDL script for Foreign Key(s)
drop table krim_dlgn_attr_data_t_bak
/
-- Start of DDL Script for Table KULDEV.KRIM_DLGN_T
-- Generated 2/9/2009 3:54:42 PM from KULDEV@
create table krim_dlgn_t_bak as select * from krim_dlgn_t
/
drop table krim_dlgn_t cascade constraints purge
/

CREATE TABLE krim_dlgn_t
    (dlgn_id                        VARCHAR2(40),
    ver_nbr                        NUMBER(8,0) NOT NULL,
    obj_id                         VARCHAR2(36) NOT NULL,
    role_id                        VARCHAR2(40),
    actv_ind                       VARCHAR2(1) DEFAULT 'Y',
    kim_typ_id                     VARCHAR2(40),
    dlgn_typ_cd                    VARCHAR2(1))
/

insert into KRIM_DLGN_T select * from KRIM_DLGN_T_bak
/
commit
/
-- Constraints for KRIM_DLGN_T

ALTER TABLE krim_dlgn_t
ADD CONSTRAINT krim_dlgn_tc0 UNIQUE (obj_id)
/

ALTER TABLE krim_dlgn_t
ADD CONSTRAINT krim_dlgt_tp1
primary key (dlgn_id)
/




-- End of DDL Script for Table KULDEV.KRIM_DLGN_T

-- Foreign Key
ALTER TABLE krim_dlgn_t
ADD CONSTRAINT krim_dlgn_tr2 FOREIGN KEY (kim_typ_id)
REFERENCES krim_typ_t (kim_typ_id)
/
ALTER TABLE krim_dlgn_t
ADD CONSTRAINT krim_dlgn_tr1 FOREIGN KEY (role_id)
REFERENCES krim_role_t (role_id)
/
-- End of DDL script for Foreign Key(s)
drop table KRIM_DLGN_T_bak
/
-- Start of DDL Script for Table KULDEV.KRIM_DLGN_MBR_ATTR_DATA_T
-- Generated 2/9/2009 4:04:03 PM from KULDEV@
create table KRIM_DLGN_MBR_ATTR_DATA_T_bak as select * from KRIM_DLGN_MBR_ATTR_DATA_T 
/
drop table KRIM_DLGN_MBR_ATTR_DATA_T cascade constraints purge
/

CREATE TABLE krim_dlgn_mbr_attr_data_t
    (attr_data_id                   VARCHAR2(40),
    obj_id                         VARCHAR2(36) NOT NULL,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    target_primary_key             VARCHAR2(40),
    kim_typ_id                     VARCHAR2(40),
    kim_attr_defn_id               VARCHAR2(40),
    attr_val                       VARCHAR2(400))
/

insert into KRIM_DLGN_MBR_ATTR_DATA_T select * from KRIM_DLGN_MBR_ATTR_DATA_T_bak
/
commit
/
-- Constraints for KRIM_DLGN_MBR_ATTR_DATA_T

ALTER TABLE krim_dlgn_mbr_attr_data_t
ADD CONSTRAINT krim_dlgn_mbr_attr_data_tc0 UNIQUE (obj_id)
/

ALTER TABLE krim_dlgn_mbr_attr_data_t
ADD CONSTRAINT krim_dlgt_mbr_attr_data_tp1 
primary key (attr_data_id)
/




-- End of DDL Script for Table KULDEV.KRIM_DLGN_MBR_ATTR_DATA_T

-- Foreign Key
ALTER TABLE krim_dlgn_mbr_attr_data_t
ADD CONSTRAINT krim_dlgn_mbr_attr_data_tr2 FOREIGN KEY (kim_attr_defn_id)
REFERENCES krim_attr_defn_t (kim_attr_defn_id)
/
ALTER TABLE krim_dlgn_mbr_attr_data_t
ADD CONSTRAINT krim_dlgn_mbr_attr_data_tr1 FOREIGN KEY (kim_typ_id)
REFERENCES krim_typ_t (kim_typ_id)
/
-- End of DDL script for Foreign Key(s)
drop table KRIM_DLGN_MBR_ATTR_DATA_T_bak
/
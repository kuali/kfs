DROP TABLE krim_dlgn_attr_data_t
/

CREATE TABLE KRIM_ROLE_DOCUMENT_T
(
    FDOC_NBR          VARCHAR2(14)    NOT NULL,
    ROLE_ID           VARCHAR2(40)    NOT NULL,
    OBJ_ID            VARCHAR2(36)    DEFAULT SYS_GUID() NOT NULL,
    VER_NBR           NUMBER(8,0)     DEFAULT 1 NOT NULL,
    ROLE_TYP_ID       VARCHAR2(40)    NOT NULL,
    ROLE_NMSPC        VARCHAR2(100)  NOT NULL,
    ROLE_NM           VARCHAR2(400),
    ACTV_IND          VARCHAR2(1)     DEFAULT 'Y',
    CONSTRAINT krim_role_document_tp1 PRIMARY KEY (FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_ROLE_PERM_T ( 
    FDOC_NBR          VARCHAR2(14)    NOT NULL,
    ROLE_PERM_ID      VARCHAR2(40)    NOT NULL,
    OBJ_ID           VARCHAR2(36)    DEFAULT SYS_GUID() NOT NULL,
    VER_NBR          NUMBER(8,0)     DEFAULT 1 NOT NULL,
    ROLE_ID           VARCHAR2(40)    NOT NULL,    
    PERM_ID           VARCHAR2(40)    NOT NULL,
    ACTV_IND         VARCHAR2(1)     DEFAULT 'Y' NULL,
    PRIMARY KEY(ROLE_PERM_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_ROLE_RSP_T ( 
    FDOC_NBR          VARCHAR2(14)    NOT NULL,
    ROLE_RSP_ID       VARCHAR2(40)    NOT NULL,
    OBJ_ID           VARCHAR2(36)    DEFAULT SYS_GUID() NOT NULL,
    VER_NBR          NUMBER(8,0)     DEFAULT 1 NOT NULL,
    ROLE_ID           VARCHAR2(40)    NOT NULL,    
    RSP_ID            VARCHAR2(40)    NOT NULL,
    ACTV_IND         VARCHAR2(1)     DEFAULT 'Y' NULL,
    PRIMARY KEY(ROLE_RSP_ID,FDOC_NBR)
)
/
ALTER TABLE KRIM_PND_ROLE_RSP_ACTN_MT 
ADD (IGNORE_PREV_IND VARCHAR(1))
/

-- Start of DDL Script for Table KULDEV.KRIM_ENTITY_NM_T
-- Generated 10-Feb-2009 14:51:59 from KULDEV@
create table KRIM_ENTITY_NM_T_bak as select * from KRIM_ENTITY_NM_T 
/
drop table KRIM_ENTITY_NM_T  cascade constraints purge
/

CREATE TABLE krim_entity_nm_t
    (entity_nm_id                   VARCHAR2(40),
    obj_id                         VARCHAR2(36) NOT NULL,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    entity_id                      VARCHAR2(40),
    nm_typ_cd                      VARCHAR2(40),
    first_nm                       VARCHAR2(40),
    middle_nm                      VARCHAR2(40),
    last_nm                        VARCHAR2(80),
    suffix_nm                      VARCHAR2(20),
    title_nm                       VARCHAR2(20),
    dflt_ind                       VARCHAR2(1) DEFAULT 'N',
    actv_ind                       VARCHAR2(1) DEFAULT 'Y',
    last_updt_dt                   DATE DEFAULT SYSDATE)
/

insert into KRIM_ENTITY_NM_T select * from KRIM_ENTITY_NM_T_bak
/
commit
/
-- Constraints for KRIM_ENTITY_NM_T

ALTER TABLE krim_entity_nm_t
ADD CONSTRAINT krim_entity_name_tp1 
primary key (entity_nm_id)
/

ALTER TABLE krim_entity_nm_t
ADD CONSTRAINT krim_entity_nm_tc0 UNIQUE (obj_id)
/




-- End of DDL Script for Table KULDEV.KRIM_ENTITY_NM_T

-- Foreign Key
ALTER TABLE krim_entity_nm_t
ADD CONSTRAINT krim_entity_nm_tr2 FOREIGN KEY (nm_typ_cd)
REFERENCES krim_ent_nm_typ_t (ent_nm_typ_cd)
/
ALTER TABLE krim_entity_nm_t
ADD CONSTRAINT krim_entity_nm_tr1 FOREIGN KEY (entity_id)
REFERENCES krim_entity_t (entity_id) ON DELETE CASCADE
/
-- End of DDL script for Foreign Key(s)
drop table KRIM_ENTITY_NM_T_bak
/
-- Start of DDL Script for Table KULDEV.KRIM_ROLE_RSP_ACTN_T
-- Generated 10-Feb-2009 15:21:37 from KULDEV@
create table KRIM_ROLE_RSP_ACTN_T_bak as select * from KRIM_ROLE_RSP_ACTN_T 
/
drop table KRIM_ROLE_RSP_ACTN_T cascade constraints purge
/

CREATE TABLE krim_role_rsp_actn_t
    (role_rsp_actn_id               VARCHAR2(40),
    obj_id                         VARCHAR2(36) NOT NULL,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    actn_typ_cd                    VARCHAR2(40),
    priority_nbr                   NUMBER(3,0),
    actn_plcy_cd                   VARCHAR2(40),
    role_mbr_id                    VARCHAR2(40),
    role_rsp_id                    VARCHAR2(40),
    ignore_prev_ind                VARCHAR2(1) DEFAULT 'N')
/


-- Constraints for KRIM_ROLE_RSP_ACTN_T


ALTER TABLE krim_role_rsp_actn_t
ADD CONSTRAINT krim_role_resp_actn_tp1 
primary key (role_rsp_actn_id)
/

ALTER TABLE krim_role_rsp_actn_t
ADD CONSTRAINT krim_role_rsp_actn_tc0 UNIQUE (obj_id)
/

ALTER TABLE krim_role_rsp_actn_t
ADD CONSTRAINT krim_role_rsp_actn_tc1 UNIQUE (role_rsp_id, role_mbr_id)
/


-- End of DDL Script for Table KULDEV.KRIM_ROLE_RSP_ACTN_T

-- Foreign Key
ALTER TABLE krim_role_rsp_actn_t
ADD CONSTRAINT krim_role_rsp_actn_tr2 FOREIGN KEY (role_rsp_id)
REFERENCES krim_role_rsp_t (role_rsp_id)
/
-- End of DDL script for Foreign Key(s)
insert into KRIM_ROLE_RSP_ACTN_T select * from KRIM_ROLE_RSP_ACTN_T_bak
/
commit
/
drop table KRIM_ROLE_RSP_ACTN_T_bak
/
ALTER TABLE krim_role_rsp_actn_t
Drop CONSTRAINT krim_role_rsp_actn_tr2
/
-- Start of DDL Script for Table KULDEV.KRIM_ROLE_RSP_T
-- Generated 2/10/2009 3:50:00 PM from KULDEV@
create table KRIM_ROLE_RSP_T_bak as select * from KRIM_ROLE_RSP_T
/

drop table KRIM_ROLE_RSP_T cascade constraints purge
/

CREATE TABLE krim_role_rsp_t
    (role_rsp_id                    VARCHAR2(40),
    obj_id                         VARCHAR2(36) NOT NULL,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    role_id                        VARCHAR2(40),
    rsp_id                         VARCHAR2(40),
    actv_ind                       VARCHAR2(1) DEFAULT 'Y')
/





-- Constraints for KRIM_ROLE_RSP_T


ALTER TABLE krim_role_rsp_t
ADD CONSTRAINT krim_role_resp_tp1 primary key (role_rsp_id)
/

ALTER TABLE krim_role_rsp_t
ADD CONSTRAINT krim_role_rsp_tc0 UNIQUE (obj_id)
/


-- End of DDL Script for Table KULDEV.KRIM_ROLE_RSP_T

-- Foreign Key
ALTER TABLE krim_role_rsp_t
ADD CONSTRAINT krim_role_rsp_tr1 FOREIGN KEY (rsp_id)
REFERENCES krim_rsp_t (rsp_id)
/
-- End of DDL script for Foreign Key(s)
insert into KRIM_ROLE_RSP_T select * from KRIM_ROLE_RSP_T_bak
/
commit
/

drop table KRIM_ROLE_RSP_T_bak
/

ALTER TABLE krim_role_rsp_actn_t
ADD CONSTRAINT krim_role_rsp_actn_tr2 FOREIGN KEY (role_rsp_id)
REFERENCES krim_role_rsp_t (role_rsp_id)
/
-- Start of DDL Script for Table KULDEV.KRIM_RSP_ATTR_DATA_T
-- Generated 2/10/2009 3:59:21 PM from KULDEV@
create table KRIM_RSP_ATTR_DATA_T_bak as select * from KRIM_RSP_ATTR_DATA_T
/

drop table KRIM_RSP_ATTR_DATA_T cascade constraints purge
/

CREATE TABLE krim_rsp_attr_data_t
    (attr_data_id                   VARCHAR2(40),
    obj_id                         VARCHAR2(36) NOT NULL,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    target_primary_key             VARCHAR2(40),
    kim_typ_id                     VARCHAR2(40),
    kim_attr_defn_id               VARCHAR2(40),
    attr_val                       VARCHAR2(400))
/





-- Constraints for KRIM_RSP_ATTR_DATA_T

ALTER TABLE krim_rsp_attr_data_t
ADD CONSTRAINT krim_resp_attr_data_tc0 UNIQUE (obj_id)
/

ALTER TABLE krim_rsp_attr_data_t
ADD CONSTRAINT krim_resp_attr_data_tp1 primary key(attr_data_id)
/





-- End of DDL Script for Table KULDEV.KRIM_RSP_ATTR_DATA_T

-- Foreign Key
ALTER TABLE krim_rsp_attr_data_t
ADD CONSTRAINT krim_rsp_attr_data_tr1 FOREIGN KEY (kim_typ_id)
REFERENCES krim_typ_t (kim_typ_id)
/
ALTER TABLE krim_rsp_attr_data_t
ADD CONSTRAINT krim_rsp_attr_data_tr2 FOREIGN KEY (kim_attr_defn_id)
REFERENCES krim_attr_defn_t (kim_attr_defn_id)
/
ALTER TABLE krim_rsp_attr_data_t
ADD CONSTRAINT krim_rsp_attr_data_tr3 FOREIGN KEY (target_primary_key)
REFERENCES krim_rsp_t (rsp_id) ON DELETE CASCADE
/
-- End of DDL script for Foreign Key(s)
insert into KRIM_RSP_ATTR_DATA_T select * from KRIM_RSP_ATTR_DATA_T_bak
/
commit
/

drop table KRIM_RSP_ATTR_DATA_T_bak
/
 alter table KRIM_ROLE_RSP_T
 drop  CONSTRAINT KRIM_ROLE_RSP_TR1
/
 alter table KRIM_RSP_ATTR_DATA_T
 drop CONSTRAINT KRIM_RSP_ATTR_DATA_TR3 
/
 alter table KRIM_RSP_RQRD_ATTR_T
 drop CONSTRAINT KRIM_RSP_RQRD_ATTR_TR1
/
-- Start of DDL Script for Table KULDEV.KRIM_RSP_T
-- Generated 2/10/2009 4:05:18 PM from KULDEV@

create table KRIM_RSP_T_bak as select * from KRIM_RSP_T
/

drop table KRIM_RSP_T cascade constraints purge
/

CREATE TABLE krim_rsp_t
    (rsp_id                         VARCHAR2(40),
    obj_id                         VARCHAR2(36) NOT NULL,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    rsp_tmpl_id                    VARCHAR2(40),
    nm                             VARCHAR2(100),
    desc_txt                       VARCHAR2(400),
    actv_ind                       VARCHAR2(1) DEFAULT 'Y',
    nmspc_cd                       VARCHAR2(40))
/





-- Constraints for KRIM_RSP_T

ALTER TABLE krim_rsp_t
ADD CONSTRAINT krim_resp_tp1 primary key (rsp_id)  
/

ALTER TABLE krim_rsp_t
ADD CONSTRAINT krim_rsp_tc0 UNIQUE (obj_id)
/



-- End of DDL Script for Table KULDEV.KRIM_RSP_T

-- Foreign Key
ALTER TABLE krim_rsp_t
ADD CONSTRAINT krim_rsp_tr1 FOREIGN KEY (rsp_tmpl_id)
REFERENCES krim_rsp_tmpl_t (rsp_tmpl_id)
/
-- End of DDL script for Foreign Key(s)
insert into KRIM_RSP_T select * from KRIM_RSP_T_bak
/
commit
/

drop table KRIM_RSP_T_bak
/

 alter table KRIM_ROLE_RSP_T
 ADD CONSTRAINT KRIM_ROLE_RSP_TR1 FOREIGN KEY (RSP_ID)
 REFERENCES KRIM_RSP_T (RSP_ID)
/
 alter table KRIM_RSP_ATTR_DATA_T
 ADD CONSTRAINT KRIM_RSP_ATTR_DATA_TR3 FOREIGN KEY (TARGET_PRIMARY_KEY)
 REFERENCES KRIM_RSP_T (RSP_ID)
/
 alter table KRIM_RSP_RQRD_ATTR_T
 ADD CONSTRAINT KRIM_RSP_RQRD_ATTR_TR1 FOREIGN KEY (RSP_ID)
 REFERENCES KRIM_RSP_T (RSP_ID)
/
alter table KRIM_RSP_T
drop CONSTRAINT KRIM_RSP_TR1
/
-- Start of DDL Script for Table KULDEV.KRIM_RSP_TMPL_T
-- Generated 2/10/2009 4:19:34 PM from KULDEV@
create table KRIM_RSP_TMPL_T_bak as select * from KRIM_RSP_TMPL_T
/

drop table KRIM_RSP_TMPL_T cascade constraints purge
/

CREATE TABLE krim_rsp_tmpl_t
    (rsp_tmpl_id                    VARCHAR2(40),
    obj_id                         VARCHAR2(36) NOT NULL,
    ver_nbr                        NUMBER(8,0) DEFAULT 1 NOT NULL,
    nm                             VARCHAR2(100),
    kim_typ_id                     VARCHAR2(40),
    desc_txt                       VARCHAR2(400),
    actv_ind                       VARCHAR2(1) DEFAULT 'Y',
    nmspc_cd                       VARCHAR2(40))
/





-- Constraints for KRIM_RSP_TMPL_T

ALTER TABLE krim_rsp_tmpl_t
ADD CONSTRAINT krim_resp_tmpl_tp1 primary key (rsp_tmpl_id)
/

ALTER TABLE krim_rsp_tmpl_t
ADD CONSTRAINT krim_rsp_tmpl_tc0 UNIQUE (obj_id)
/



-- End of DDL Script for Table KULDEV.KRIM_RSP_TMPL_T

-- Foreign Key
ALTER TABLE krim_rsp_tmpl_t
ADD CONSTRAINT krim_rsp_tmpl_tr1 FOREIGN KEY (kim_typ_id)
REFERENCES krim_typ_t (kim_typ_id)
/
-- End of DDL script for Foreign Key(s)
insert into KRIM_RSP_TMPL_T select * from KRIM_RSP_TMPL_T_bak
/
commit
/

drop table KRIM_RSP_TMPL_T_bak
/

alter table KRIM_RSP_T
ADD CONSTRAINT KRIM_RSP_TR1 FOREIGN KEY (RSP_TMPL_ID)
REFERENCES KRIM_RSP_TMPL_T (RSP_TMPL_ID)
/

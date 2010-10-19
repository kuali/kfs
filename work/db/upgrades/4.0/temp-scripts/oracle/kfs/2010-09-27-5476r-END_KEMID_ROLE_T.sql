DROP SEQUENCE XFR_NBR_SEQ;

CREATE SEQUENCE END_REC_CSH_XFR_SEQ
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    ORDER NOCYCLE;

create table END_KEMID_ROLE_T(
        KEMID VARCHAR2(10) not null,
        ROLE_SEQ_NBR VARCHAR2(4) not null,
        ROLE_ID VARCHAR2(40) not null,
        ROLE_PRNCPL_ID VARCHAR2(40) not null,
        ROLE_TERM_DT date,
        ROW_ACTV_IND VARCHAR2(1) default 'Y',
        OBJ_ID VARCHAR2(36) not null unique,
        VER_NBR NUMBER(8) default '1' not null,
        primary key (KEMID,ROLE_SEQ_NBR,ROLE_ID,ROLE_PRNCPL_ID)
    );
    
    alter table END_KEMID_ROLE_T
        add constraint END_KEMID_ROLE_TR1
        foreign key (KEMID)
        references END_KEMID_T(KEMID);
         
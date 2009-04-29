CREATE SEQUENCE KRNS_MAINT_LOCK_S START WITH 2000 INCREMENT BY 1
/
ALTER TABLE KRNS_MAINT_LOCK_T ADD MAINT_LOCK_ID VARCHAR2(14)
/
ALTER TABLE KRNS_MAINT_LOCK_T DROP PRIMARY KEY
/
DECLARE
CURSOR cursor1 IS
SELECT MAINT_LOCK_REP_TXT FROM KRNS_MAINT_LOCK_T;
BEGIN
FOR r IN cursor1 LOOP
        execute immediate 'UPDATE KRNS_MAINT_LOCK_T SET MAINT_LOCK_ID=KRNS_MAINT_LOCK_S.nextval';
    END LOOP;
END;
/
ALTER TABLE KRNS_MAINT_LOCK_T ADD PRIMARY KEY (MAINT_LOCK_ID)
/
ALTER TABLE KRNS_MAINT_LOCK_T MODIFY MAINT_LOCK_REP_TXT VARCHAR2(500)
/

update krim_role_perm_t set role_id = '83' where perm_id = '262'
/

update krim_entity_emp_info_t set EMP_REC_ID = 1
/
delete krns_parm_t
where parm_nm = 'KFS-FP'
;
commit;
insert into krns_parm_t
(SELECT 'KFS-FP', 'DisbursementVoucher',
'VALID_VENDOR_OWNERSHIP_TYPES_BY_PAYMENT_REASON', sys_guid(),1,
'VALID', 'M=ID',
'Defines an valid relationship between the vendor ownership types and the payment reasons on the Disbursement Voucher. Format of list is payment reason 1=ownership type1, ownership type2;payment reason 2=ownership type3, ownership type4, ownership type5.',
'A'
FROM dual)
;
commit;
delete krns_parm_t
where nmspc_cd = 'KFS-FP'
and parm_dtl_typ_cd = 'IndirectCostAdjustment'
and parm_nm = 'OBJECT_TYPES' ;
commit;
insert into krns_parm_t
(SELECT 'KFS-FP', 'DisbursementVoucher',
'DECEDENT_COMPENSATION_PAYMENT_REASONS', sys_guid(),1,
'CONFG', 'D',
'Payment reason(s) used for the compensation in respect to decedent.',
'A'
FROM dual);
commit;
create table CM_CPTLAST_LOCK_T                    
(                    
  DOC_HDR_ID         VARCHAR2(14) not null,                    
  CPTLAST_NBR        NUMBER(12) not null,                    
  LOCK_INFO_TXT      VARCHAR2(20) not null,                
  OBJ_ID             VARCHAR2(36) not null,                    
  VER_NBR            NUMBER(8) default 1 not null,
  DOC_TYP_NM         VARCHAR2(10),                    
  CONSTRAINT CM_CPTLAST_LOCK_TC0 UNIQUE (OBJ_ID)                    
)                    
;                               
ALTER TABLE CM_CPTLAST_LOCK_T                    
    ADD CONSTRAINT CM_CPTLAST_LOCK_TP1                    
PRIMARY KEY (DOC_HDR_ID, CPTLAST_NBR,LOCK_INFO_TXT)                    
;

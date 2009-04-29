delete krns_parm_t
where nmspc_cd = 'KFS-FP'
and parm_dtl_typ_cd = 'IndirectCostAdjustment'
and parm_nm = 'OBJECT_TYPES.'
;
insert into krns_parm_t 
(SELECT 'KFS-GL', 'PosterIndirectCostRecoveryEntriesStep',
'INDIRECT_COST_FISCAL_PERIODS', sys_guid(),1,
'CONFG', 'AB;BB;CB',
'Excludes transactions associated with certain fiscal period(s) from consideration for posting of indirect cost entries.',
'D'
FROM dual)
;
insert into krns_parm_t 
(SELECT 'KFS-GL', 'PosterIndirectCostRecoveryEntriesStep',
'INDIRECT_COST_TYPES', sys_guid(),1,
'CONFG', '10',
'Excludes transactions associated with certain indirect cost type(s) from consideration for posting of indirect cost entries.',
'D'
FROM dual)
;
commit;
\p done
insert into krns_parm_t 
(SELECT 'KFS-FP', 'DisbursementVoucher',
'KFS-FP', sys_guid(),1,
'VALID', 'M=ID',
'Defines an valid relationship between the vendor ownership types and the payment reasons on the Disbursement Voucher. Format of list is payment reason 1=ownership type1, ownership type2;payment reason 2=ownership type3, ownership type4, ownership type5.',
'A'
FROM dual)
;
commit;
\p done
\p delete KREW_RULE_ATTR_T
delete KREW_RULE_ATTR_T
where rule_attr_typ_cd like '%Rule%'
;
\p delete  KREW_RULE_TMPL_ATTR_T T
delete  KREW_RULE_TMPL_ATTR_T T
where not exists (select *
from krew_rule_attr_t 
where RULE_ATTR_ID= T.RULE_ATTR_ID)
;
\p delete KREW_RULE_T T
delete KREW_RULE_T T
where not exists (select *
from KREW_RULE_TMPL_ATTR_T
where RULE_TMPL_ID= T.RULE_TMPL_ID)
;
\p delete KREW_RULE_TMPL_T T
delete KREW_RULE_TMPL_T T
where not exists (select *
from KREW_RULE_TMPL_ATTR_T
where RULE_TMPL_ID= T.RULE_TMPL_ID)
;
\p delete KREW_RULE_TMPL_OPTN_T T
delete KREW_RULE_TMPL_OPTN_T T
where not exists (select *
from KREW_RULE_TMPL_ATTR_T
where RULE_TMPL_ID= T.RULE_TMPL_ID)
;
\p delete KREW_RULE_RSP_T T
delete KREW_RULE_RSP_T T
where not exists (select *
from KREW_RULE_T
where RULE_ID= T.RULE_ID)
;
\p delete KREW_RULE_EXT_T T
delete KREW_RULE_EXT_T T
where not exists (select *
from KREW_RULE_T
where RULE_ID= T.RULE_ID)
;
\p delete KREW_RULE_EXT_VAL_T T
delete KREW_RULE_EXT_VAL_T T
where not exists (select *
from KREW_RULE_EXT_T
where RULE_EXT_ID= T.RULE_EXT_ID)
;
commit;
\p done
-- scripts/upgrades/0.9.3 to 0.9.4/db-updates-during-qa/03-21-2009.sql 

DECLARE
CURSOR cursor1 IS
    SELECT RULE_ID FROM KREW_RULE_T WHERE NM IS NULL AND CUR_IND=1;
BEGIN
    FOR r IN cursor1 LOOP
        execute immediate 'UPDATE KREW_RULE_T SET NM=SYS_GUID() WHERE RULE_ID='||r.RULE_ID;
    END LOOP;
END;
/

DECLARE
CURSOR cursor1 IS
    SELECT PREV.RULE_ID, RULE.NM FROM KREW_RULE_T PREV, KREW_RULE_T RULE
    WHERE PREV.RULE_ID=RULE.PREV_RULE_VER_NBR AND RULE.NM IS NOT NULL;
cnt NUMBER := 0;
BEGIN
    LOOP
        FOR r IN cursor1 LOOP
            UPDATE KREW_RULE_T SET NM=r.NM WHERE RULE_ID=r.RULE_ID;
        END LOOP;
        cnt := cnt + 1;
        IF cnt > 1000 THEN EXIT; END IF;
    END LOOP;
END;
/

UPDATE KREW_RULE_T RULE
SET (NM) =
(
  SELECT PREV.NM
  FROM KREW_RULE_T PREV
  WHERE PREV.RULE_ID=RULE.PREV_RULE_VER_NBR
  AND PREV.NM IS NOT NULL
)
WHERE RULE.NM IS NULL

ALTER TABLE KREW_RULE_T MODIFY NM NOT NULL
/
commit;
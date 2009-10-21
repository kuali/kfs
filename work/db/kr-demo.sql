/* Disable constraints for the duration of this script */
DECLARE 
   CURSOR constraint_cursor IS 
      SELECT table_name, constraint_name 
         FROM user_constraints 
         WHERE constraint_type = 'R'
           AND status = 'ENABLED';
BEGIN 
   FOR r IN constraint_cursor LOOP
      execute immediate 'ALTER TABLE '||r.table_name||' DISABLE CONSTRAINT '||r.constraint_name; 
   END LOOP; 
END;
/

-- clear all locks

TRUNCATE TABLE krns_maint_lock_t
/

-- check parm table for IU hosts/emails
UPDATE krns_parm_t
	SET txt = 'knoreceipt-l@indiana.edu'
 WHERE appl_nmspc_cd = 'KFS' AND parm_nm LIKE '%ADDRESSES'
/

-- delete old version document types
DELETE FROM KREW_DOC_TYP_T
    WHERE cur_ind = 0
/

COMMIT
/

/* Re-enable constraints */
DECLARE 
   CURSOR constraint_cursor IS 
      SELECT table_name, constraint_name 
         FROM user_constraints 
         WHERE constraint_type = 'R'
           AND status <> 'ENABLED';
BEGIN 
   FOR r IN constraint_cursor LOOP
      execute immediate 'ALTER TABLE '||r.table_name||' ENABLE CONSTRAINT '||r.constraint_name; 
   END LOOP; 
END;
/
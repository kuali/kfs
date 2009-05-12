DECLARE
	cursor c1 is select distinct emplid from ld_ldgr_entr_t
	where emplid not in (select emp_id from krim_entity_emp_info_t);
	cursor c2 is select distinct emp_id from krim_entity_emp_info_t a
	where not exists (select * from ld_ldgr_entr_t
	where emplid = a.emp_id);
	new_emp c2%ROWTYPE;
	loopcount NUMBER;
BEGIN
	OPEN c2;
	FOR emp in c1
	LOOP
		FETCH c2 INTO new_emp;
		EXIT WHEN c2%NOTFOUND;
		update ld_ldgr_entr_t set emplid = new_emp.emp_id where emplid = emp.emplid;
		update ld_ldgr_bal_t set emplid = new_emp.emp_id where emplid = emp.emplid;
	END LOOP;
	CLOSE c2;
	commit;
END;
delete from ld_ldgr_entr_t
where emplid not in (select emp_id from krim_entity_emp_info_t);
commit;
delete from ld_ldgr_bal_t
where emplid not in (select emp_id from krim_entity_emp_info_t);
commit;

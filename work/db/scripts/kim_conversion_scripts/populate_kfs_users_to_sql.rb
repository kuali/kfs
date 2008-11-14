require 'kfsdb'
require 'ojdbc14.jar'
include_class 'oracle.jdbc.OracleDriver'

file = File.new("kfs_user_populate.sql", "w")


role_mbr_id = 1
db_connect("sample_db") do |con|
  con.query("select max(role_mbr_id) from krim_role_prncpl_t") do |row|
            role_mbr_id = row.getInt("max(role_mbr_id)")
  end
end

attr_data_id = 1
db_connect("sample_db") do |con|
  con.query("select max(attr_data_id) from KRIM_ROLE_MBR_ATTR_DATA_T") do |row|
            attr_data_id = row.getInt("max(attr_data_id)")
  end
end


db_connect("sample_db") do |con|
  con.query("select b.prncpl_id, a.fin_coa_cd, a.org_cd from fs_usr_attrib_t a, krim_prncpl_t b where a.row_actv_ind = 'Y' and a.person_unvl_id = b.prncpl_id") do |row|		
			role_mbr_id += 1
			file.puts "INSERT INTO KRIM_ROLE_PRNCPL_T VALUES ('#{role_mbr_id}', sys_guid(), 1, '54', '#{row.getString("prncpl_id")}', sysdate, 'Y')\n/\n"
		
            attr_data_id += 1
   		    file.puts "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T VALUES ('#{attr_data_id}', sys_guid(), 1, '#{role_mbr_id}', '36', '4', '')\n/\n"
           
            attr_data_id += 1
 		    file.puts "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T VALUES ('#{attr_data_id}', sys_guid(), 1, '#{role_mbr_id}', '36', '22', '#{row.getString("fin_coa_cd")}')\n/\n"
          
            attr_data_id += 1
 		    file.puts "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T VALUES ('#{attr_data_id}', sys_guid(), 1, '#{role_mbr_id}', '36', '24', '#{row.getString("org_cd")}')\n/\n"			
  end
end
   

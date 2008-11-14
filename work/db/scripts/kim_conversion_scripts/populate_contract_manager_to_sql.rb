require 'kfsdb'
require 'ojdbc14.jar'
include_class 'oracle.jdbc.OracleDriver'

file = File.new("contract_manager.sql", "w")

role_mbr_id = 1198
attr_data_id = 2807
db_connect("sample_db") do |con|
  con.query("select a.contr_mgr_cd, b.prncpl_id from pur_contr_mgr_t a, krim_prncpl_t b where lower(a.contr_mgr_usr_id) = b.prncpl_nm") do |row|		
			role_mbr_id += 1
			file.puts "INSERT INTO KRIM_ROLE_PRNCPL_T VALUES ('#{role_mbr_id}', sys_guid(), 1, '25', '#{row.getString("prncpl_id")}', sysdate, 'Y')\n/\n"			
         
  		    attr_data_id += 1
   		    file.puts "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T VALUES ('#{attr_data_id}', sys_guid(), 1, '#{role_mbr_id}', '34', '32', '#{row.getString("contr_mgr_cd")}')\n/\n"
 
  end
end
   

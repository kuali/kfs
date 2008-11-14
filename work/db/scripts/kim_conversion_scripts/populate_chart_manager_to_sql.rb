require 'kfsdb'
require 'ojdbc14.jar'
include_class 'oracle.jdbc.OracleDriver'

file = File.new("chart_manager.sql", "w")

role_mbr_id = 1240
attr_data_id = 2849
db_connect("sample_db") do |con|
  con.query("select fin_coa_cd, fin_coa_mgrunvl_id from ca_chart_t") do |row|		
			role_mbr_id += 1
			file.puts "INSERT INTO KRIM_ROLE_PRNCPL_T VALUES ('#{role_mbr_id}', sys_guid(), 1, '37', '#{row.getString("fin_coa_mgrunvl_id")}', sysdate, 'Y')\n/\n"			
         
  		    attr_data_id += 1
   		    file.puts "INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T VALUES ('#{attr_data_id}', sys_guid(), 1, '#{role_mbr_id}', '25', '22', '#{row.getString("fin_coa_cd")}')\n/\n"
 
  end
end
   

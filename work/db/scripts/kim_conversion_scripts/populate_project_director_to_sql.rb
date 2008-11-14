require 'kfsdb'
require 'ojdbc14.jar'
include_class 'oracle.jdbc.OracleDriver'

file = File.new("project_director.sql", "w")

role_mbr_id = 1045
db_connect("sample_db") do |con|
  con.query("select b.prncpl_id from CG_PRJDR_T a, krim_prncpl_t b where a.row_actv_ind = 'Y' and a.person_unvl_id = b.prncpl_id") do |row|		
			role_mbr_id += 1
			file.puts "INSERT INTO KRIM_ROLE_PRNCPL_T VALUES ('#{role_mbr_id}', sys_guid(), 1, '40', '#{row.getString("prncpl_id")}', sysdate, 'Y')\n/\n"			
  end
end
   

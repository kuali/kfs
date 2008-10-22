require 'kfsdb'

def initial_or_request(node_type)
	node_type.strip == "org.kuali.rice.kew.engine.node.InitialNode" ? "Initial" : "Request"
end

db_connect("local_dev") do |con|
	con.query("select wrkgrp_id, wrkgrp_nm from en_wrkgrp_t where wrkgrp_actv_ind = 1 and wrkgrp_cur_ind = 1 order by wrkgrp_nm") do |row|
		puts "#{row.getString("wrkgrp_nm")}"
		workgroup_id = row.getString("wrkgrp_id")
		
		con.query("select fs_universal_usr_t.person_user_id from fs_universal_usr_t join en_wrkgrp_mbr_t on fs_universal_usr_t.person_unvl_id = en_wrkgrp_mbr_t.wrkgrp_mbr_prsn_en_id where en_wrkgrp_mbr_t.wrkgrp_id = ? and en_wrkgrp_mbr_t.wrkgrp_mbr_typ = 'U'", workgroup_id) do |row2|
			puts "\t#{row2.getString("person_user_id")}"
		end
	end
end
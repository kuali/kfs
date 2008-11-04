require 'kfsdb'

def initial_or_request(node_type)
	node_type.strip == "org.kuali.rice.kew.engine.node.InitialNode" ? "Initial" : "Request"
end

DB="sample_db"
db_connect(DB) do |con|
	con.query("select WRKGRP_ID, WRKGRP_NM from EN_WRKGRP_T where WRKGRP_ACTV_IND = 1 and WRKGRP_CUR_IND = 1 order by WRKGRP_NM") do |row|
		puts "#{row.getString("WRKGRP_NM")}"
		workgroup_id = row.getString("WRKGRP_ID")
		
		con.query("select FS_UNIVERSAL_USR_T.PERSON_USER_ID from FS_UNIVERSAL_USR_T join EN_WRKGRP_MBR_T on FS_UNIVERSAL_USR_T.PERSON_UNVL_ID = EN_WRKGRP_MBR_T.WRKGRP_MBR_PRSN_EN_ID where EN_WRKGRP_MBR_T.WRKGRP_ID = ? and EN_WRKGRP_MBR_T.WRKGRP_MBR_TYP = 'U'", workgroup_id) do |row2|
			puts "\t#{row2.getString("PERSON_USER_ID")}"
		end
	end
end
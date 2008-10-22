require 'kfsdb'

def initial_or_request(node_type)
	node_type.strip == "org.kuali.rice.kew.engine.node.InitialNode" ? "Initial" : "Request"
end

db_connect("local_dev") do |con|
	con.query("select doc_typ_id, doc_typ_nm from en_doc_typ_t where DOC_TYP_ACTV_IND = 1 and DOC_TYP_CUR_IND = 1 order by doc_typ_nm") do |row|
		puts "#{row.getString("doc_typ_nm")}"
		doc_type_id = row.getString("doc_typ_id")
		
		con.query("select rte_node_id, rte_node_nm, rte_node_typ, doc_rte_mthd_nm from en_rte_node_t where doc_typ_id = ?", doc_type_id) do |row2|
			# JAMES!! don't forget ordering of nodes here!!
			puts "\t#{row2.getString("rte_node_nm")}; Type: #{initial_or_request(row2.getString("rte_node_typ"))}; Method: #{row2.getString("doc_rte_mthd_nm")}"
		end
	end
end
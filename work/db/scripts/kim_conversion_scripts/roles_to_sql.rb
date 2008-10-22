require 'digest/sha1'

INPUT_FILE = "roles.txt"

def generate_obj_id(table_name, unique_name)
	Digest::SHA1.hexdigest("#{table_name}:#{unique_name}")[0..32]
end

def format_count(count)
	return "#{"%02d" % count}"
end

def generate_role_id(count)
	"KFS-ROLE#{format_count(count)}"
end

def generate_role_sql(role_id, role_namespace, role_name, role_description)
	table_name = "kr_kim_role_t"
	obj_id = generate_obj_id(table_name, "#{role_namespace} #{role_name}")
	"insert into #{table_name} (role_id, obj_id, ver_nbr, role_nm, nmspc_cd, role_desc, typ_id, actv_ind)\n\tvalues ('#{role_id}', '#{obj_id}', 0, '#{role_name.gsub("'", "''")}', '#{role_namespace}', '#{role_description.gsub("'", "''")}', '', 'Y')\n/\n"
end

File.open(INPUT_FILE, "r") do |fin|
	count = 1
	fin.each_line do |line|
		full_role_name, role_description = line.split(/::/)
		role_description = "" if role_description.nil?
		role_namespace = full_role_name.match(/^[A-Z\-]+/)[0]
		role_name = full_role_name.gsub(/^#{role_namespace}/, "").strip
		role_id = generate_role_id(count)
		puts generate_role_sql(role_id, role_namespace, role_name, role_description)
		count += 1
	end
end
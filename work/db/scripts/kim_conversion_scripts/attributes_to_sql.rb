require 'digest/sha1'

INPUT_FILE = "attribute-definition.txt"

def generate_obj_id(table_name, unique_name)
	Digest::SHA1.hexdigest("#{table_name}:#{unique_name}")[0..32]
end

def format_count(count)
	return "#{"%02d" % count}"
end

def generate_attribute_id(count)
	"KFS-ATTRIB#{format_count(count)}"
end

def generate_attribute_sql(attribute_id, namespace, name, label)
	table_name = "KRIM_ATTR_DEFN_T"
	obj_id = generate_obj_id(table_name, "#{namespace} #{name}")
	"insert into #{table_name} (KIM_ATTR_DEFN_ID, NM, LBL, NMSPC_CD, OBJ_ID, VER_NBR, ACTV_IND)\n\tvalues ('#{attribute_id}', '#{name}', '#{label}', '#{namespace}', '#{obj_id}', 0, 'Y')\n/\n"
end

File.open(INPUT_FILE, "r") do |fin|
	count = 1
	fin.each_line do |line|
		name, label, namespace = line.split(/::/)
		attribute_id = generate_attribute_id(count)
		puts generate_attribute_sql(attribute_id, namespace, name, label)
		count += 1
	end
end
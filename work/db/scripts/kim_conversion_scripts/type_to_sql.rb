require 'kfsdb'
require 'digest/sha1'

input_file = ARGV[0].nil? ? "permission_types.txt" : ARGV[0]
DB="sample_db"

def create_attribute_map()
	attributes = {}
	db_connect(DB) do |con|
		con.query("select KIM_ATTR_DEFN_ID, LBL from KRIM_ATTR_DEFN_T") do |row|
			attributes[row.getString("LBL")] = row.getString("KIM_ATTR_DEFN_ID")
		end
	end
	attributes
end

def generate_obj_id(table_name, unique_name)
	Digest::SHA1.hexdigest("#{table_name}:#{unique_name}")[0..32]
end

def format_count(count)
	return "#{"%02d" % count}"
end

def generate_type_id(template_count)
	"KFS-TYPE#{format_count(template_count)}"
end

def generate_type_attribute_id(template_id, template_attribute_count)
	"#{template_id}-ATTR#{format_count(template_attribute_count)}"
end

def parse_template_name(template_name)
    namespace = template_name.strip.match(/^([A-Z\-]+)/)[1]
    namespace = namespace.strip if !namespace.nil?
    true_template_name = !namespace.nil? ? template_name.strip.gsub(/^#{namespace}/, "").strip : template_name.strip
    [namespace, true_template_name]
end 

def generate_type_sql(template_name, template_id)
	table_name = "KRIM_TYP_T"
	obj_id = generate_obj_id(table_name, template_name)
	namespace, true_template_name = parse_template_name(template_name) 
	"insert into #{table_name} (KIM_TYP_ID, OBJ_ID, VER_NBR, NMSPC_CD, NM, ACTV_IND)\n\tvalues ('#{template_id}', '#{obj_id}', 0, '#{namespace}', '#{true_template_name}', 'Y')\n/\n" 
end

def generate_type_attribute_sql(template_id, attribute_id, template_attribute_id)
	table_name = "KRIM_TYP_ATTR_T"
	obj_id = generate_obj_id(table_name, template_attribute_id)
	"insert into #{table_name} (KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND)\n\tvalues ('#{template_attribute_id}', '#{obj_id}', 0, '#{template_id}', '#{attribute_id}', 'Y')\n/\n"
end

attributes = create_attribute_map
File.open(input_file, "r") do |fin|
	last_type = nil
	last_type_id = nil
	template_count = 0
	template_attribute_count = 0
	fin.each_line do |line|
		if line.strip =~ /^- /
			attribute_name = line.strip.gsub(/^- /, "")
			attribute_id = attributes[attribute_name]
			template_attribute_count += 1
			template_attribute_id = generate_type_attribute_id(last_type_id, template_attribute_count)
			puts generate_type_attribute_sql(last_type_id, attribute_id, template_attribute_id)
		else
			last_type = line.strip
			template_attribute_count = 0
			template_count += 1
			last_type_id = generate_type_id(template_count)
			puts generate_type_sql(last_type, last_type_id)
		end
	end
end
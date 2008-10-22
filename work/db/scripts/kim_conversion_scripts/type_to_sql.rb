require 'kfsdb'
require 'local_dev_connect'
require 'digest/sha1'

input_file = ARGV[0].nil? ? "permission_types.txt" : ARGV[0]

def create_attribute_map()
	attributes = {}
	db_connect do |con|
		con.query("select kim_attrib_id, attrib_lbl from kr_kim_attribute_t") do |row|
			attributes[row.getString("attrib_lbl")] = row.getString("kim_attrib_id")
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

def generate_type_sql(template_name, template_id)
	table_name = "kr_kim_type_t"
	obj_id = generate_obj_id(table_name, template_name)
	"insert into #{table_name} (kim_type_id, obj_id, ver_nbr, type_nm, actv_ind)\n\tvalues ('#{template_id}', '#{obj_id}', 0, '#{template_name}', 'Y')\n/\n"
end

def generate_type_attribute_sql(template_id, attribute_id, template_attribute_id)
	table_name = "kr_kim_type_attribute_t"
	obj_id = generate_obj_id(table_name, template_attribute_id)
	"insert into #{table_name} (kim_type_attrib_id, obj_id, ver_nbr, kim_type_id, kim_attrib_id, actv_ind)\n\tvalues ('#{template_attribute_id}', '#{obj_id}', 0, '#{template_id}', '#{attribute_id}', 'Y')\n/\n"
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
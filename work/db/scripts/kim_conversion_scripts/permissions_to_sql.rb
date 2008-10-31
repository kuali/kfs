require 'kfsdb'
require 'digest/sha1'
require 'mysql-connector-java-5.0.5-bin.jar'
include_class 'com.mysql.jdbc.Driver'

INPUT_FILE = "permissions.txt"

def generate_obj_id(table_name, unique_name)
	Digest::SHA1.hexdigest("#{table_name}:#{unique_name}")[0..35]
end

def format_count(count)
	return "#{"%02d" % count}"
end

class PermissionAttribute
	attr_reader :attribute_type_id
	attr_reader :attribute_value
	attr_reader :attribute_permission_id
	attr_reader :permission_id
	attr_reader :type_id
	
	def initialize(attribute_type_id, attribute_value, count, permission_id, type_id)
		@attribute_type_id = attribute_type_id
		@attribute_value = attribute_value
		@permission_id = permission_id
		@attribute_permission_id = generate_id(count)
		@type_id = type_id
	end
	
	def generate_id(count)
		"#{@permission_id}-ATTR#{format_count(count)}"
	end
	
	def generate_sql()
		table_name = "KRIM_PERM_ATTR_DATA_T"
		obj_id = generate_obj_id(table_name, @attribute_permission_id)
		"insert into #{table_name} (ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)\n\tvalues ('#{@attribute_permission_id}', '#{obj_id}', 0, '#{@permission_id}', '#{@type_id}','#{@attribute_type_id}', '#{@attribute_value.gsub(/'/,"''")}')\n/\n"
	end
	
end

class Permission
	attr_reader :permission_id
	attr_reader :name
	attr_reader :template_id
	attr_reader :template_type_id
	attr_reader :roles
	attr_reader :attributes
	attr_reader :namespace
	attr_accessor :description
	
	def initialize(count, name, template_id, template_type_id, namespace)
		@permission_id = generate_id(count)
		@name = name.nil? ? "" : name
		@description = ""
		@template_id = template_id
		@template_type_id = template_type_id
		@roles = []
		@attributes = []
		@namespace = namespace
	end
	
	def add_attribute(attribute)
		@attributes << attribute
	end
	
	def add_role(role_id)
		@roles << role_id
	end
	
	def add_roles(role_ids)
		role_ids.each do |role_id|
			@roles << role_id
		end
	end
	
	def generate_id(count)
		"KFS-PERM#{format_count(count)}"
	end
	
	def to_s()
		"Permission #{@name} (#{@permission_id}) based on template #{@template_id}; roles assigned = #{@roles.join(",")}"
	end
	
	def generate_role_permission_id(role_count)
		permission_num = @permission_id.gsub(/^KFS-PERM/,"")
		"KFS-ROlE#{format_count(role_count)}-PERM#{permission_num}"
	end
	
	def generate_sql()
		table_name = "KRIM_PERM_T"
		obj_id = generate_obj_id(table_name, @name)
		permission_insert = "insert into #{table_name} (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)\n\tvalues ('#{@permission_id}', '#{obj_id}', 0, '#{@template_id}', '#{@name.gsub(/'/,"''")}', '#{@description.gsub(/'/,"''")}', 'Y', #{@namespace})\n/\n"
		role_count = 0
		role_sql = @roles.collect {|role_id| role_count += 1; generate_role_sql(role_id, role_count)}.join()
		"#{permission_insert}#{role_sql}#{@attributes.collect{|attribute|attribute.generate_sql()}.join()}"
	end
	
	def generate_role_sql(role_id, count)
		table_name = "KRIM_ROLE_PERM_T"
		obj_id = generate_obj_id(table_name, "#{@permission_id} #{role_id}")
		role_perm_id = generate_role_permission_id(count)
		"insert into #{table_name} (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)\n\tvalues('#{role_perm_id}', '#{obj_id}', 0, '#{role_id}', '#{@permission_id}', 'Y')\n/\n"
	end
end

def parse_permission_line(line)
  namespace = line.strip.split(/ /, 2)
	matches = namespace[1].strip.match(/([^\<]+)\<([^\(]+)\(([^\)]+)\)/)
	return [namespace[0],matches[1],matches[2],matches[3]]
end

def parse_roles(roles_line)
	roles_line.split(/,/).collect {|role_name| role_name.strip }
end

def templates_map()
	templates = nil
	db_connect("sample_db") do |con|
		templates = con.table_to_map("KRIM_PERM_TMPL_T","NM","PERM_TMPL_ID")
	end
	templates
end

def roles_map()
	roles = {}
	db_connect("sample_db") do |con|
		con.query("select NMSPC_CD, ROLE_NM, ROLE_ID from KRIM_ROLE_T") do |row|
			roles["#{row.getString("NMSPC_CD")} #{row.getString("ROLE_NM")}"] = row.getString("ROLE_ID")
		end
	end
	roles
end

def attributes_map()
	attributes = {}
	db_connect("sample_db") do |con|
		attributes = con.table_to_map("KRIM_ATTR_DEFN","NM","KIM_ATTR_DEFN_ID")
	end
	attributes
end

def template_types_map()
	template_types = {}
	db_connect("sample_db") do |con|
		template_types = con.table_to_map("KRIM_PERM_TMPL_T","PERM_TMPL_ID","KIM_TYP_ID")
	end
	template_types
end

def parse_attribute(line, attribute_count, permission_id, template_type_id, permission_attributes)
	attribute_line = line.strip.gsub(/^\- /, "")
	#puts attribute_line
	attribute_type, attribute_value = attribute_line.split(/:/)
	if !permission_attributes.keys.include?(attribute_type.strip)
		puts "There is no attribute named #{attribute_type}"
		exit
	end
	attribute_type_id = permission_attributes[attribute_type.strip]
	PermissionAttribute.new(attribute_type_id, attribute_value.strip, attribute_count, permission_id, template_type_id)
end

def check_roles(roles_to_check, roles_map)
	role_names = roles_map.keys
	roles_to_check.each do |role|
		if !role_names.include?(role)
			puts "No role #{role} exists"
			exit
		end
	end
end

def parse_permission(line, permission_count, templates, template_types, roles)
	namespace, permission_name, permission_template_name, assigned_roles_line = parse_permission_line(line)
	assigned_roles = parse_roles(assigned_roles_line)
	if !templates.keys.include?(permission_template_name.strip)
		puts "There is no permission template named #{permission_template_name}"
		exit
	end
	template_id = templates[permission_template_name.strip]
	last_permission = Permission.new(permission_count, permission_name.strip, template_id, template_types[template_id], namespace)
	check_roles(assigned_roles, roles)
	last_permission.add_roles(assigned_roles.collect{|role_name| roles[role_name]})
	last_permission
end

def parse_description(line, permission)
	description = line.strip.match(/\"\"(.+)\"\"/)[1]
	permission.description = description
end

templates = templates_map()
template_types = template_types_map()
roles = roles_map()
permission_attributes = attributes_map()

permissions = []
File.open(INPUT_FILE, "r") do |fin|
	permission_count = 1
	attribute_count = 1
	last_permission = nil
	fin.each_line do |line|
		if line.strip.length == 0 || line.strip =~ /^#/ || line.strip =~ /^\/\//
			# don't do nothin'
		elsif (line.strip) =~ /^\-/
			last_permission.add_attribute(parse_attribute(line, attribute_count, last_permission.permission_id, last_permission.template_type_id, permission_attributes))
			attribute_count += 1
		elsif (line.strip) =~ /^""/
			# deal with description for permission
			parse_description(line, last_permission)
		else
			permissions << last_permission if !last_permission.nil?
		
			last_permission = parse_permission(line, permission_count, templates, template_types, roles)
			
			attribute_count = 1
			permission_count += 1
		end
	end
end
permissions.each do |permission|
	puts permission.generate_sql()
end
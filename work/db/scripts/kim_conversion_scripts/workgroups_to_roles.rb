require 'kfsdb'
require 'digest/sha1'
require 'set'

INPUT_FILE = "workgroups_to_roles.txt"
DB = "local_dev"

def generate_obj_id(table_name, unique_name)
	Digest::SHA1.hexdigest("#{table_name}:#{unique_name}")[0..32]
end

def format_count(count)
	return "#{"%02d" % count}"
end

class Member
	attr_reader :user_id
	attr_reader :username
	attr_reader :campus
	attr_reader :department
	
	@@entities_map = nil
	
	def initialize(user_id, username, campus, department)
		@user_id = user_id
		@username = username
		@campus = campus
		@department = department
		@role_member_id = nil
	end
	
	def role_member_id(role_id)
		entities_map = Member.entities_map()
		if @role_member_id.nil?
			@role_member_id = "#{role_id}-MBR#{entities_map[@user_id]}"
		end
		@role_member_id
	end

	def generate_role_princial_sql(role_id)
		table_name = "kr_kim_role_principal_t"
		obj_id = generate_obj_id(table_name, "#{role_id} #{@user_id}")
		"insert into #{table_name} (role_member_id, obj_id, ver_nbr, role_id, prncpl_id)\n\tvalues('#{self.role_member_id(role_id)}','#{obj_id}', 0, '#{role_id}', '#{@user_id}')\n/\n"
	end
	
	def generate_role_princial_attributed_sql(role_member_id, role_id)
		table_name = "kr_kim_role_principal_t"
		obj_id = generate_obj_id(table_name, "#{role_member_id}")
		"insert into #{table_name} (role_member_id, obj_id, ver_nbr, role_id, prncpl_id)\n\tvalues('#{role_member_id}','#{obj_id}', 0, '#{role_id}', '#{@user_id}')\n/\n"
	end
	
	class << self
		def entities_map()
			if @@entities_map.nil?
				@@entities_map = {}
				db_connect(DB) do |con|
					con.query("select prncpl_id, entity_id from kr_kim_principal_t") do |row|
						@@entities_map[row.getString("prncpl_id")] = row.getString("entity_id")
					end
				end
			end
			@@entities_map
		end
	end
	
end

class QualificationAttribute
	attr_reader :name
	attr_reader :value
	
	@@attributes_map = nil
	
	def initialize(name, value)
		@name = name.strip
		@value = value.strip
	end
	
	def generate_id(role_member_id, count)
		"#{role_member_id}-ATTR#{format_count(count)}"
	end
	
	def generate_sql(role_member_id, type_id, count, member)
		attributes = QualificationAttribute.attributes_map()
		table_name = "kr_kim_role_mbr_attr_data_t"
		obj_id = generate_obj_id(table_name, "#{role_member_id} #{type_id} #{@name} #{count}")
		attrib_id = attributes[@name]
		if attrib_id.nil?
			raise Exception.new, "Could not find attribute named #{@name}"
		end
		
		"insert into #{table_name}(attrib_data_id, obj_id, ver_nbr, target_primary_key, kim_type_id, kim_attrib_id, attrib_val)\n\tvalues('#{generate_id(role_member_id, count)}', '#{obj_id}', 0, '#{role_member_id}', '#{type_id}', '#{attrib_id}', '#{reify_value(member)}')\n/\n"
	end
	
	def reify_value(member)
		if @value == "self.campus"
			member.campus.gsub("'","''")
		else
			@value.gsub("'","''")
		end
	end
	
	class << self
		def attributes_map()
			if @@attributes_map.nil?
				@@attributes_map = {}
				db_connect(DB) do |con|
					con.query("select attrib_lbl, kim_attrib_id from kr_kim_attribute_t") do |row|
						@@attributes_map[row.getString("attrib_lbl")] = row.getString("kim_attrib_id")
					end
				end
			end
			@@attributes_map
		end
	end
end

class Qualification
	attr_reader :type_name
	attr_reader :attributes
	
	@@types_map = nil
	
	def initialize(type_name)
		@type_name = type_name
		@attributes = []
	end
	
	def add_attribute(attribute)
		@attributes << attribute
	end
	
	def generate_sql(member, role_id)
		types_map = Qualification.types_map()
		type_id = types_map[@type_name]
		if type_id.nil?
			raise Exception.new, "Could not find type: #{@type_name}"
		end
		statements = ""
		count = 1
		
		base_role_member_id = member.role_member_id(role_id)
		
		@attributes.each do |qualification_attribute| 
			statements << member.generate_role_princial_attributed_sql(generate_role_member_id(base_role_member_id, count), role_id)
			statements << qualification_attribute.generate_sql(generate_role_member_id(base_role_member_id, count), type_id, count, member)
			count += 1
		end
		statements
	end
	
	def generate_role_member_id(base_role_member_id, count)
		"#{base_role_member_id}-#{format_count(count)}"
	end
	
	class << self
		def parse_qualification(qualification_line)
			qual_pieces = qualification_line.split(/>>/)
			if qual_pieces.length >= 2
				q = Qualification.new(qual_pieces[0].strip)
				attributes_lines = qual_pieces[1].strip.split(/;/)
				attributes_lines.each do |attributes_line|
					attribute_lines = attributes_line.split(/,/)
					attribute_lines.each do |attribute_line|
						attribute_name, attribute_value = attribute_line.split(/:/)
						qual_attr = QualificationAttribute.new(attribute_name, attribute_value)
						q.add_attribute(qual_attr)
					end
				end
				q
			else
				raise Exception.new, "Couldn't parse line: #{qualification_line}"
			end
		end
		
		def types_map()
			if @@types_map.nil?
				@@types_map = {}
				db_connect(DB) do |con|
					con.query("select type_nm, kim_type_id from kr_kim_type_t") do |row|
						@@types_map[row.getString("type_nm")] = row.getString("kim_type_id")
					end
				end
			end
			
			@@types_map
		end
	end
end

def workgroup_map(con)
	workgroups = {}
	con.query("select wrkgrp_id, wrkgrp_nm from en_wrkgrp_t where wrkgrp_actv_ind = 1 and wrkgrp_cur_ind = 1") do |row|
		workgroups[row.getString("wrkgrp_nm")] = row.getString("wrkgrp_id")
	end
	workgroups
end

def role_map(con)
	roles = {}
	con.query("select role_id, nmspc_cd, role_nm from kr_kim_role_t") do |row|
		roles["#{row.getString("nmspc_cd")} #{row.getString("role_nm")}"] = row.getString("role_id")
	end
	roles
end

def principals_set(con)
	principals = Set.new
	con.query("select prncpl_id from kr_kim_principal_t") do |row|
		principals << row.getString("prncpl_id")
	end
	principals
end

def parse_line(line)
	return [nil, nil, nil] if line =~ /^(\s*)#/
	
	matches = line.match(/^([^\>]+)(>(\s*)([^\(]+)\(?([^\)]+)\)?(\s*))?$/)
	workgroup_name = matches[1].nil? ? nil : matches[1].strip
	role_name = matches[4].nil? ? nil : matches[4].strip
	qualifications_section = matches[5].nil? ? nil : matches[5].strip
	return [workgroup_name, role_name, qualifications_section]
end

def each_user_member(workgroup_id, con, &block)
	con.query("select distinct fs_universal_usr_t.person_unvl_id, fs_universal_usr_t.person_user_id, fs_universal_usr_t.campus_cd, fs_universal_usr_t.emp_prm_dept_cd from fs_universal_usr_t join en_wrkgrp_mbr_t on fs_universal_usr_t.person_unvl_id = en_wrkgrp_mbr_t.wrkgrp_mbr_prsn_en_id where en_wrkgrp_mbr_t.wrkgrp_id = ? and en_wrkgrp_mbr_t.wrkgrp_mbr_typ = 'U'",workgroup_id) do |row|
		yield Member.new(row.getString("person_unvl_id"), row.getString("person_user_id"), row.getString("campus_cd"), row.getString("emp_prm_dept_cd"))
	end
end

db_connect(DB) do |con|
	workgroups = workgroup_map(con)
	roles = role_map(con)
	principals = principals_set(con)

	File.open(INPUT_FILE, "r") do |fin|
		role_member_keys = Set.new
	
		fin.each_line do |line|
			workgroup_name, role_name, qualifications = parse_line(line)
			if !role_name.nil? and role_name.strip.length > 0
				workgroup_id = workgroups[workgroup_name]
				if workgroup_id.nil?
					raise Exception.new, "No workgroup defined: #{workgroup_name}"
				end
				role_id = roles[role_name]
				if role_id.nil?
					raise Exception.new, "No role defined: #{role_name}, workgroup: #{workgroup_name}"
				end

				each_user_member(workgroup_id, con) do |member|
					if !principals.include?(member.user_id)
						raise Exception.new, "Universal User #{member.username} (#{member.user_id}) is not a principal"
					else
						role_member_id = member.role_member_id(role_id)
						
						if !role_member_keys.include?(member.role_member_id(role_id))
							if !qualifications.nil? && qualifications.size > 0
								q = Qualification.parse_qualification(qualifications)
								puts q.generate_sql(member, role_id)
							else
								# we won't need to add principal once per qualification value - so let's just add them
								puts member.generate_role_princial_sql(role_id)
							end

							role_member_keys << role_member_id # don't add the same principal to the same role twice
						end
					end
				end
			end
		end
	end
end
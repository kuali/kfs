require 'kfsdb'
require 'digest/sha1'
require 'set'

INPUT_FILE = "workgroups_to_roles.txt"
DB = "sample_db"

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
		table_name = "KRIM_ROLE_PRNCPL_T"
		obj_id = generate_obj_id(table_name, "#{role_id} #{@user_id}")
		"insert into #{table_name} (ROLE_MEMBER_ID, OBJ_ID, VER_NBR, ROLE_ID, PRNCPL_ID)\n\tvalues('#{self.role_member_id(role_id)}','#{obj_id}', 0, '#{role_id}', '#{@user_id}')\n/\n"
	end
	
	def generate_role_princial_attributed_sql(role_member_id, role_id)
		table_name = "KRIM_ROLE_PRNCPL_T"
		obj_id = generate_obj_id(table_name, "#{role_member_id}")
		"insert into #{table_name} (ROLE_MEMBER_ID, OBJ_ID, VER_NBR, ROLE_ID, PRNCPL_ID)\n\tvalues('#{role_member_id}','#{obj_id}', 0, '#{role_id}', '#{@user_id}')\n/\n"
	end
	
	class << self
		def entities_map()
			if @@entities_map.nil?
				@@entities_map = {}
				db_connect(DB) do |con|
					con.query("select PRNCPL_ID, ENTITY_ID from KRIM_PRNCPL_T") do |row|
						@@entities_map[row.getString("PRNCPL_ID")] = row.getString("ENTITY_ID")
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
		table_name = "KRIM_ROLE_MBR_ATTR_DATA_T"
		obj_id = generate_obj_id(table_name, "#{role_member_id} #{type_id} #{@name} #{count}")
		attrib_id = attributes[@name]
		if attrib_id.nil?
			raise Exception.new, "Could not find attribute named #{@name}"
		end
		
		"insert into #{table_name}(ATTRIB_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYPE_ID, KIM_ATTRIB_ID, ATTRIB_VAL)\n\tvalues('#{generate_id(role_member_id, count)}', '#{obj_id}', 0, '#{role_member_id}', '#{type_id}', '#{attrib_id}', '#{reify_value(member)}')\n/\n"
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
					con.query("select LBL, KIM_ATTR_DEFN_ID from KRIM_ATTR_DEFN_T") do |row|
						@@attributes_map[row.getString("LBL")] = row.getString("KIM_ATTR_DEFN_ID")
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
					con.query("select NM, KIM_TYP_ID from KRIM_TYP_T") do |row|
						@@types_map[row.getString("NM")] = row.getString("KIM_TYP_ID")
					end
				end
			end
			
			@@types_map
		end
	end
end

def workgroup_map(con)
	workgroups = {}
	con.query("select WRKGRP_ID, WRKGRP_NM from EN_WRKGRP_T where WRKGRP_ACTV_IND = 1 and WRKGRP_CUR_IND = 1") do |row|
		workgroups[row.getString("WRKGRP_NM")] = row.getString("WRKGRP_ID")
	end
	workgroups
end

def role_map(con)
	roles = {}
	con.query("select ROLE_ID, NMSPC_CD, ROLE_NM from KRIM_ROLE_T") do |row|
		roles["#{row.getString("NMSPC_CD")} #{row.getString("ROLE_NM")}"] = row.getString("ROLE_ID")
	end
	roles
end

def principals_set(con)
	principals = Set.new
	con.query("select PRNCPL_ID from KRIM_PRNCPL_T") do |row|
		principals << row.getString("PRNCPL_ID")
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
	con.query("select distinct FS_UNIVERSAL_USR_T.PERSON_UNVL_ID, FS_UNIVERSAL_USR_T.PERSON_USER_ID, FS_UNIVERSAL_USR_T.CAMPUS_CD, FS_UNIVERSAL_USR_T.EMP_PRM_DEPT_CD from FS_UNIVERSAL_USR_T join EN_WRKGRP_MBR_T on FS_UNIVERSAL_USR_T.PERSON_UNVL_ID = EN_WRKGRP_MBR_T.WRKGRP_MBR_PRSN_EN_ID where EN_WRKGRP_MBR_T.WRKGRP_ID = ? and EN_WRKGRP_MBR_T.WRKGRP_MBR_TYP = 'U'",workgroup_id) do |row|
		yield Member.new(row.getString("PERSON_UNVL_ID"), row.getString("PERSON_USER_ID"), row.getString("CAMPUS_CD"), row.getString("EMP_PRM_DEPT_CD"))
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
include Java
require 'yaml'
#require 'mysql-connector-java-5.0.5-bin.jar'

class JdbcConnection

	class << self
		def db_connect(url, user, pass, driver, &block)
			con = nil
			begin
			  	java.lang.Class.forName(driver).newInstance
				con = java.sql.DriverManager.getConnection(url, user, pass)
				yield JdbcConnection.new(con)
			ensure
				begin
					con.close unless con.nil?
				ensure
					con = nil
				end
			end
		end
	end
	
	def query(query, *args, &block)
		stmt = @con.prepareStatement(query)
		add_args_to_stmt!(stmt, args)
		rs = stmt.executeQuery
		while rs.next
			yield rs
		end
		rs.close
		stmt.close
	end
	
	def table_to_map(table_name, key_field, value_field)
		m = {}
		query("select #{key_field}, #{value_field} from #{table_name}") do |row|
			m[row.getString(key_field)] = row.getString(value_field)
		end
		m
	end
	
	private
	
	def initialize(con)
		@con = con
	end
	
	def add_args_to_stmt!(stmt, args)
		count = 1
		args.each do |arg|
			if arg.nil?
				stmt.setNull(count)
			elsif arg.class == String
				stmt.setString(count, arg)
			elsif arg.class == Fixnum || arg.class == Integer
				stmt.setLong(count, arg)
			end
			count += 1
		end
	end
end

class MysqlJdbcConnection < JdbcConnection
	def current_datetime_function()
		"now()"
	end
	
	def generate_update_sequence_sql(sequence_name)
		
	end
	
	def generate_sequence_nextval_placeholder(sequence_name)
		
	end
end

class OracleJdbcConnection < JdbcConnection
	def current_datetime_function()
		
	end
	
	def generate_update_sequence_sql()
		
	end
	
	def generate_sequence_nextval_placeholder()
		
	end
end

def find_workgroup_id(workgroup_name, con)
	workgroup_id = nil
	con.complex_query("select wrkgrp_id from en_wrkgrp_t where wrkgrp_nm = ? and wrkgrp_actv_ind = 1 and wrkgrp_cur_ind = 1", workgroup_name) do |rs|
		workgroup_id = rs.getLong("wrkgrp_id")
	end
	if workgroup_id.nil?
		$stderr.write("Cannot find workgroup record for #{workgroup_name}\n")
	end
	workgroup_id
end

def escape_string(str)
	str = str.gsub("'", "''") unless str.nil?
	if str.nil? || str.length == 0
		"null"
	else
		"'#{str}'"
	end
end

def db_connect(connection_values, &block)
	def read_connection_values(connection_values)
		values_yaml = nil
		File.open("#{connection_values}.yml", "r") do |fin|
			values_yaml = YAML::load(fin)
		end
		values_yaml
	end
	
	def determine_driver(db_url)
		if db_url =~ /^jdbc:mysql:/ 
			"com.mysql.jdbc.Driver"
		elsif db_url =~ /^jdbc:oracle:thin:/ 
			"oracle.jdbc.OracleDriver"
		else
			nil
		end
	end
	
	db_connection_values = read_connection_values(connection_values)
	puts db_connection_values.values.join(",")
	JdbcConnection.db_connect(db_connection_values["url"], db_connection_values["user"], db_connection_values["pass"], determine_driver(db_connection_values["url"])) do |con|
		yield con
	end
end
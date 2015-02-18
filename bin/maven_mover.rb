# The Kuali Financial System, a comprehensive financial management system for higher education.
# 
# Copyright 2005-2014 The Kuali Foundation
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

require 'fileutils'
require 'optparse'

module MavenMover
    def self.correct_path()
        if Dir.pwd =~ /bin$/
            Dir.chdir("..")
        end
    end

	def self.move_code_files(src_dir, target_dir, options)
	     processor = lambda do |file_name|
            fn = MavenMoverHelper.break_file_name(src_dir, MavenMoverHelper.file_delim_correct(file_name))
            j_or_r = (fn.file =~ /\.java$/) ? "java" : "resources"
            true_target_dir = "#{target_dir}/#{j_or_r}#{fn.directory}"
            if options[:verbose]
                STDOUT.puts "target for #{file_name} is #{true_target_dir}"
            end
            MavenMoverHelper.create_dir_as_needed(true_target_dir, options[:verbose])
            MavenMoverHelper.move_file(file_name,"#{true_target_dir}#{fn.file}",(!options[:real] || options[:verbose]))
        end
        MavenMoverHelper.dir_stepper(src_dir, processor)
	end
	
	def self.create_dir(dir, options)
		if !Dir.exists?(dir)
			if !options[:verbose]
				Dir.mkdir(dir)
			else
				STDOUT.puts "creating directory #{dir}`"
			end
		end
	end
		
	module MavenMoverHelper
		FileName = Struct.new(:directory, :file)

		def self.file_delim_correct(fn)
			fn.gsub(/\\/,"/")
		end

		def self.break_file_name(src_dir, file_name)
			fn_match = file_name.match(/^#{src_dir}(.+?)([^\/]+)$/)
			just_dir = fn_match[1]
			just_file = fn_match[2]
			FileName.new(just_dir, just_file)
		end
		
		def self.remove_last_sub_dir(dir)
			dir.gsub(/\/[^\/]+\/?$/,"")
		end

		def self.create_dir_as_needed(dir, verbose_mode)
			if !dir.nil? && dir.strip.length > 0
				if !Dir.exists?(dir)
					if verbose_mode
						STDOUT.puts "dir #{dir} does not exist; we need to create it"
					end
					MavenMoverHelper.create_dir_as_needed(MavenMoverHelper.remove_last_sub_dir(dir), verbose_mode)
					if verbose_mode
						STDOUT.puts "creating directory #{dir}`"
					else
						Dir.mkdir(dir)
					end
				end
			end
		end

		def self.move_file(src, target, mime_mode)
			if mime_mode
				STDOUT.puts "`git mv #{file_delim_correct(src)} #{target}`"
			else
				`git mv #{file_delim_correct(src)} #{target}`
				`git add #{target}`
				`git rm #{src}`
				
				
				#`git mv #{file_delim_correct(src)} #{target}`
			end
		end

		def self.dir_stepper(dir, processor)
        	Dir.foreach(dir) do |dir_child|
        		if dir_child != "." && dir_child != ".."
        			dir_child_name = "#{dir}/#{dir_child}"
        			if File.directory?(dir_child_name)
        				dir_stepper(dir_child_name, processor)
        			else
        				if File.file?(dir_child_name) && File.readable?(dir_child_name)
        				    processor.call(dir_child_name)
        				end
        			end
        		end
        	end
        end
	end
end

options = {}
OptionParser.new do |opts|
	opts.banner = "Usage: maven_mover.rb [options]"
	
	opts.on("-v","--[no-]verbose","Run verbose report - no directories will be created, no files will be moved, and heavy duty reporting will be generated") do |v|
		options[:verbose] = v
	end
	
	opts.on("-r","--[no-]real","Run real mode - ie, the script will actually perform all real actions, creating directories and moving files") do |r|
		options[:real] = r
	end
end.parse!

MavenMover.correct_path()
MavenMover.create_dir("src", options)
MavenMover.move_code_files("work/src","src/main", options)
MavenMover.move_code_files("test/unit/src","src/test", options)
MavenMover.move_code_files("test/integration/src","src/test", options)
MavenMover.move_code_files("test/infrastructure/src","src/test", options)

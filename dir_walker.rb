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

def dir_walker(dir, files_to_read_predicate, read_file_block, dirs_not_to_traverse=[])
	Dir.foreach(dir) do |dir_child|
		if dir_child != "." && dir_child != ".." && !dirs_not_to_traverse.include?(dir_child)
			dir_child_name = "#{dir}/#{dir_child}"
			if File.directory?(dir_child_name)
				dir_walker(dir_child_name, files_to_read_predicate, read_file_block)
			else
				if File.file?(dir_child_name) && File.readable?(dir_child_name) && files_to_read_predicate.call(dir_child_name)
					File.open(dir_child_name,"r") do |fin|
						read_file_block.call(fin, dir_child_name)
					end
				end
			end
		end
	end
end
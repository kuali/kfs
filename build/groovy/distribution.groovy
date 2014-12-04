/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
inputFile = new File( "${properties['all.temp.directory']}/kfs/build-foundation.xml" );
outputFile = new File( "${properties['all.temp.directory']}/kfs/build-foundation.tmp" );
skip = false

outputFile.withWriter { writer ->

	inputFile.eachLine { line ->
		if ( !skip && line =~ "REMOVE FROM DISTRIBUTION" ) {
			println "Starting Exclusion: " + line
			skip = true;
		} else if ( skip && line =~ "END REMOVE FROM DISTRIBUTION" ) {
			println "Ending Exclusion: " + line
			skip = false;
		} else {
			if ( !skip ) {
				writer.writeLine( line );
			}
		}
	}
}

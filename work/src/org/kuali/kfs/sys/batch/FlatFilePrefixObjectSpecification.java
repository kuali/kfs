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
package org.kuali.kfs.sys.batch;

/**
 * The specification for a business object which should be parsed into during the parsing of a flat file
 */
public class FlatFilePrefixObjectSpecification extends AbstractFlatFileObjectSpecification {

	protected String linePrefix;
    /**
     * @return the prefix of the line which determines if the given line should be associated with this object specification
     */
    public String getLinePrefix() {
		return linePrefix;
	}

    /**
     * Sets the prefix that configures which lines this object specification will be associated with
     * @param linePrefix the prefix
     */
	public void setLinePrefix(String linePrefix) {
		this.linePrefix = linePrefix;
	}
}

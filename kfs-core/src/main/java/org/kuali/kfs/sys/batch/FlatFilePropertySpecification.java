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
 * A contract of methods which must be implemented by configuration elements which associate setting a substring
 * of a parsed line as a property on a business object
 */
public interface FlatFilePropertySpecification {
    /**
     * Sets the property on the business object
     * @param value the substring of the parsed line to set
     * @param businessObject the business object to set the parsed line on
     * @param lineNumber the current line number
     */
	public void setProperty(String value, Object businessObject, int lineNumber);

	/**
	 * @return the name of the property that should be set
	 */
	public String getPropertyName();
}

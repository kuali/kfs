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

import java.util.List;

/**
 * Contract of methods for the configuration element which specifies how to parse a flat file
 */
public interface FlatFileSpecification {
    /**
     * @return a List of the specifications for all objects which will be parsed into during the course of
     * this flat file parse
     */
	public List<FlatFileObjectSpecification> getObjectSpecifications();

	/**
	 * Retrieves the FlatFilePrefixObjectSpecification specifically associated with a given class
	 * @param businessObjectClass the class of a business object which will be parsed into
	 * @return the corresponding FlatFilePrefixObjectSpecification configuration object
	 */
	public FlatFileObjectSpecification getObjectSpecification(Class<?> businessObjectClass);

	/**
	 * Determines the Class of the business object that the given line should be parsed into
	 * @param line the current line of the flat file parser being parsed
	 * @return the Class of the business object that the given line will be parsed into
	 */
	public Class<?> determineClassForLine(String line);

	/**
	 * Parses the current line of the flat file into a business object
	 * @param parseSpecification the specification explaining how to parse the line into the business object
	 * @param lineToParse the current line being parsed
	 * @param parseIntoObject the target object to parse into
	 * @param lineNumber the current line number
	 */
	public void parseLineIntoObject(
			FlatFileObjectSpecification parseSpecification, String lineToParse,
			Object parseIntoObject, int lineNumber);
}

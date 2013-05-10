/*
 * Copyright 2012 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

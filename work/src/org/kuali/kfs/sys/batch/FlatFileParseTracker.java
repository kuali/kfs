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
 * Contract of methods required by the flat file parser to track the parent/child tree while parsing
 */
public interface FlatFileParseTracker {
	/**
	 * Initializes a new FlatFileParseTracker
	 * @param flatFileSpecification the FlatFileSpecificationBase instance which will determine which object should be instantiated for a given line
	 * @param specifications the specifications for all objects that will be parsed into, to build a parent/child map out of
	 */
	public void initialize(FlatFileSpecification flatFileClassIdentifier);

	/**
	 * Determines which class should be parsed into and returns an instance of that
	 * @param lineToParse the line which is going to be parsed
	 * @return the object to parse into
	 */
	public abstract Object getObjectToParseInto(String lineToParse);

	/**
	 * Called when a line has completed parsing. Throws an exception if a proper parent
	 * is not found for the line being parsed
	 */
	public abstract void completeLineParse();

	/**
	 * @return the List of parsed parent objects
	 */
	public abstract List<Object> getParsedObjects();
}

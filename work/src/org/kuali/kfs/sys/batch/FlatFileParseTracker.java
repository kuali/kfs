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

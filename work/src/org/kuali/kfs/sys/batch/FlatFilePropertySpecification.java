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

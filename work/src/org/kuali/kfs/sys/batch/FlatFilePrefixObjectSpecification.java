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

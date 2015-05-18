/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.validation.charlevel;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.exporter.ExportMap;
import org.kuali.rice.krad.datadictionary.validation.CharacterLevelValidationPattern;

import java.util.regex.Pattern;

/**
 * Pattern for matching any character in the given list (String)
 * 
 * 
 */
public class CharsetValidationPattern extends CharacterLevelValidationPattern {
    protected String validChars;

    /**
     * @return String containing all valid chars for this charset
     */
    public String getValidChars() {
        return validChars;
    }

    /**
     * @param validChars for this charset
     */
    public void setValidChars(String validChars) {
        if (StringUtils.isEmpty(validChars)) {
            throw new IllegalArgumentException("invalid (empty) validChars");
        }

        this.validChars = validChars;
    }


    /**
     * Escapes every special character I could think of, to limit potential misuse of this pattern.
     * 
     * @see org.kuali.rice.krad.datadictionary.validation.ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        if (StringUtils.isEmpty(validChars)) {
            throw new IllegalStateException("validChars is empty");
        }

        // filter out and escape chars which would confuse the pattern-matcher
        Pattern filteringChars = Pattern.compile("([\\-\\[\\]\\{\\}\\$\\.\\^\\(\\)\\*\\&\\|])");
        String filteredChars = filteringChars.matcher(validChars).replaceAll("\\\\$1");

        StringBuffer regexString = new StringBuffer("[");
        regexString.append(filteredChars);
        if (filteredChars.endsWith("\\")) {
            regexString.append("\\");
        }
        regexString.append("]");

        return regexString.toString();
    }


    /**
     * @see CharacterLevelValidationPattern#extendExportMap(org.kuali.bo.datadictionary.exporter.ExportMap)
     */
    public void extendExportMap(ExportMap exportMap) {
        exportMap.set("type", "charset");

        exportMap.set("validChars", getValidChars());
    }

	/**
	 * This overridden method ...
	 * 
	 * @see CharacterLevelValidationPattern#getValidationErrorMessageParameters(String, String)
	 */
	@Override
	public String[] getValidationErrorMessageParameters(String attributeLabel) {
		// build character list
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < validChars.length(); i++) {
			buf.append(validChars.charAt(i));
			if (i != validChars.length() - 1) {
				buf.append(", ");
			}
		}
		String characterList = buf.toString();
		
		if (getMaxLength() != -1) {
			return new String[] {attributeLabel, String.valueOf(getMaxLength()), characterList};
		}
		if (getExactLength() != -1) {
			return new String[] {attributeLabel, String.valueOf(getExactLength()), characterList};
		}
		return new String[] {attributeLabel, "0", characterList};
	}
}

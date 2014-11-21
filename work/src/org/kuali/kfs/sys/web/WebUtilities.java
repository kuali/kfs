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
package org.kuali.kfs.sys.web;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * A utility class which holds functions that can be used as JSP functions.
 */
public class WebUtilities {
    /**
     * Converts a property name so that it is correct for the purposes of populating a business object
     * in the maintenance framework - basically by changing "document.oldMaintainableObject.businessObject" to 
     * "document.oldMaintainableObject" and doing a similar operation for "document.newMaintainableObject.businessObject"
     * @param propertyName the property name to fix
     * @return the corrected version of the property name
     */
    public static String renamePropertyForMaintenanceFramework(String propertyName) {
        if (propertyName == null) {
            return null;
        }
        Pattern oldMaintainablePattern = Pattern.compile("^document\\.oldMaintainableObject\\.businessObject\\.");
        if (oldMaintainablePattern.matcher(propertyName).find()) {
            return propertyName.replaceFirst("^document\\.oldMaintainableObject\\.businessObject\\.", "document.oldMaintainableObject.");
        }
        Pattern newMaintainablePattern = Pattern.compile("^document\\.newMaintainableObject\\.businessObject\\.");
        if (newMaintainablePattern.matcher(propertyName).find()) {
            return propertyName.replaceFirst("^document\\.newMaintainableObject\\.businessObject\\.", "document.newMaintainableObject.");
        }
        return propertyName;
    }
    
    /**
     * Determines if the given value matches the given pattern
     * @param value the value which is matching the pattern
     * @param pattern the Java regular expression pattern to match against
     * @return true if the value matches; false otherwise
     * @see java.util.regex.Pattern
     */
    public static boolean matchesPattern(String value, String pattern) {
        return (StringUtils.isBlank(value)) ? false : value.matches(pattern);
    }
}

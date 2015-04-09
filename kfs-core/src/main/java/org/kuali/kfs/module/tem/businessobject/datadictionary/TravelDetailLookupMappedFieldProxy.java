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

package org.kuali.kfs.module.tem.businessobject.datadictionary;

import java.util.Map;

import org.kuali.rice.kns.datadictionary.FieldDefinition;

/**
 * Contains field-related information for DataDictionary entries.  Used by lookups and inquiries. Maps to
 * fields in other classes for indirect lookup of information
 *
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 */
public class TravelDetailLookupMappedFieldProxy extends FieldDefinition {
    private static final long serialVersionUID = -3426603523049661524L;
    private Map<String,String> attributeMap;

    public void setAttributeMap(final Map<String, String> attributeMap) {
        this.attributeMap = attributeMap;
    }

    public Map<String, String> getAttributeMap() {
        return attributeMap;
    }
}

/*
 * Copyright 2005-2007 The Kuali Foundation
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

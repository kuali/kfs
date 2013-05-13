/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject.datadictionary;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sec.datadictionary.AccessSecurityAttributeRestrictionEntry;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;

/**
 * Overrides KNS BusinessObjectEntry to add access security configuration properties
 */
public class FinancialSystemBusinessObjectEntry extends BusinessObjectEntry {

    protected List<AccessSecurityAttributeRestrictionEntry> accessRestrictedAttributes;

    public FinancialSystemBusinessObjectEntry() {
        accessRestrictedAttributes = new ArrayList<AccessSecurityAttributeRestrictionEntry>();
    }

    /**
     * Gets the accessRestrictedAttributes attribute.
     * 
     * @return Returns the accessRestrictedAttributes.
     */
    public List<AccessSecurityAttributeRestrictionEntry> getAccessRestrictedAttributes() {
        return accessRestrictedAttributes;
    }

    /**
     * Sets the accessRestrictedAttributes attribute value.
     * 
     * @param accessRestrictedAttributes The accessRestrictedAttributes to set.
     */
    public void setAccessRestrictedAttributes(List<AccessSecurityAttributeRestrictionEntry> accessRestrictedAttributes) {
        this.accessRestrictedAttributes = accessRestrictedAttributes;
    }

}

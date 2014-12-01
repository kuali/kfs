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

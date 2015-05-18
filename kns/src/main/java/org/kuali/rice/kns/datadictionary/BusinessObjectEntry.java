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
package org.kuali.rice.kns.datadictionary;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public class BusinessObjectEntry extends org.kuali.rice.krad.datadictionary.BusinessObjectEntry {

    protected InquiryDefinition inquiryDefinition;
    protected LookupDefinition lookupDefinition;

    @Override
    public void completeValidation() {

        super.completeValidation();

        if (hasInquiryDefinition()) {
            inquiryDefinition.completeValidation(getDataObjectClass(), null);
        }

        if (hasLookupDefinition()) {
            lookupDefinition.completeValidation(getDataObjectClass(), null);
        }
    }

    /**
     * @return true if this instance has an inquiryDefinition
     */
    public boolean hasInquiryDefinition() {
        return (inquiryDefinition != null);
    }

    /**
     * @return current inquiryDefinition for this BusinessObjectEntry, or null if there is none
     */
    public InquiryDefinition getInquiryDefinition() {
        return inquiryDefinition;
    }

    /**
     * The inquiry element is used to specify the fields that will be displayed on the
     * inquiry screen for this business object and the order in which they will appear.
     *
     * DD: See InquiryDefinition.java
     *
     * JSTL: The inquiry element is a Map which is accessed using
     * a key of "inquiry".  This map contains the following keys:
     * title (String)
     * inquiryFields (Map)
     *
     * See InquiryMapBuilder.java
     */
    public void setInquiryDefinition(InquiryDefinition inquiryDefinition) {
        this.inquiryDefinition = inquiryDefinition;
    }

    /**
     * @return true if this instance has a lookupDefinition
     */
    public boolean hasLookupDefinition() {
        return (lookupDefinition != null);
    }

    /**
     * @return current lookupDefinition for this BusinessObjectEntry, or null if there is none
     */
    public LookupDefinition getLookupDefinition() {
        return lookupDefinition;
    }

    /**
     * The lookup element is used to specify the rules for "looking up"
     * a business object.  These specifications define the following:
     * How to specify the search criteria used to locate a set of business objects
     * How to display the search results
     *
     * DD: See LookupDefinition.java
     *
     * JSTL: The lookup element is a Map which is accessed using
     * a key of "lookup".  This map contains the following keys:
     * lookupableID (String, optional)
     * title (String)
     * menubar (String, optional)
     * defaultSort (Map, optional)
     * lookupFields (Map)
     * resultFields (Map)
     * resultSetLimit (String, optional)
     *
     * See LookupMapBuilder.java
     */
    public void setLookupDefinition(LookupDefinition lookupDefinition) {
        this.lookupDefinition = lookupDefinition;
    }
}

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
                  The inquirySubSectionHeader allows a separator containing text to
                  separate groups of fields.  The name attribute is the displayed text.

                  DD:   See InquirySubSectionHeaderDefinition.
                  JSTL: inquirySubSectionHeader appears in the inquiryFields map as:
                      * key = "attributeName"
                      * value = name of inquirySubSectionHeader
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Deprecated
public class InquirySubSectionHeaderDefinition extends FieldDefinition implements SubSectionHeaderDefinitionI {
    private static final long serialVersionUID = -4979343188029630857L;

	public String getName() {
        return getAttributeName();
    }

    @Override
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        //no-op
    }    
}

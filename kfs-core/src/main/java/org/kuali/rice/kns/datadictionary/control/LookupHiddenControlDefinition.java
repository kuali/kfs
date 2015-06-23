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
package org.kuali.rice.kns.datadictionary.control;


/**
 *  The lookupHidden control element creates a field with a magnifying
 glass, but no value showing.  This can be used to do a lookup to
 return a value which will appear in another field.
 */
public class LookupHiddenControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = -2145156789968831921L;

    public LookupHiddenControlDefinition() {
        this.type = ControlDefinitionType.LOOKUP_HIDDEN;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isLookupHidden()
     */
    public boolean isLookupHidden() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "LookupHiddenControlDefinition";
    }
}

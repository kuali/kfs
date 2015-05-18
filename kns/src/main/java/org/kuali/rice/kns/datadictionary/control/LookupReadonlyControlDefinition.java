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
    The lookupReadonly control element creates a field with a magnifying
    glass and a read-only value.  This forces the user to change the value
    of the field only by use of the magnifying glass.
 */
@Deprecated
public class LookupReadonlyControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = -5036539644716405540L;

	public LookupReadonlyControlDefinition() {
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isLookupReadonly()
     */
    public boolean isLookupReadonly() {
        return true;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return "LookupReadonlyControlDefinition";
    }
}

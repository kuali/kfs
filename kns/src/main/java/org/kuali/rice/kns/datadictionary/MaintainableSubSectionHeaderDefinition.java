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
    The subSectionHeader allows the section to be separated
    into sub-sections, each with its own name.
 */
@Deprecated
public class MaintainableSubSectionHeaderDefinition extends MaintainableItemDefinition implements SubSectionHeaderDefinitionI {
    private static final long serialVersionUID = 3752757590555028866L;

	public MaintainableSubSectionHeaderDefinition() {
    }

    /**
     * Directly validate simple fields.
     * 
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(Class, Object)
     */    
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        //do nothing ? 
    }
    
    public String toString() {
        return "MaintainableSubSectionHeaderDefinition '" + getName() + "'";
    }
}

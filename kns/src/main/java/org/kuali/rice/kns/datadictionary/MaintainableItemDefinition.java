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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;

/**
 * Abstract superclass for all maintainable fields and collections.  Never used directly.
 */
@Deprecated
public abstract class MaintainableItemDefinition extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = 4564613758722159747L;
    
	private String name;

    public MaintainableItemDefinition() {
    }


    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name to the given value.
     * 
     * @param name
     * @throws IllegalArgumentException if the given name is blank
     */
    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("invalid (blank) name");
        }
        this.name = name;
    }


    /**
     * @see Object#toString()
     */
    public String toString() {
        return "MaintainableItemDefinition for item " + getName();
    }
}

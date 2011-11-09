/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.datadictionary;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.document.web.NestedFieldTotaling;
import org.kuali.kfs.sys.document.web.renderers.Renderer;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;

/**
 * Metadata about something that will be responsible for rendering some total of some accounting line group sometime, or something
 */
public abstract class TotalDefinition extends DataDictionaryDefinitionBase implements NestedFieldTotaling {

    /**
     * Returns a renderer which will render the total for this total definition
     * 
     * @return a Renderer which will render a total
     */
    public abstract Renderer getTotalRenderer();

    /**
     * get the actual property name if the property is nested; otherwise, return the given property name
     * 
     * @param containingPropertyName the given containing property name
     * @param propertyName the given peropety name
     * @return the actual property name if the property is nested; otherwise, return the given property name
     */
    public String getActualPropertyName(String containingPropertyName, String propertyName) {
        if (this.isNestedProperty() && StringUtils.isNotBlank(containingPropertyName)) {
            return containingPropertyName + PropertyUtils.NESTED_DELIM + propertyName;
        }

        return propertyName;
    }
}

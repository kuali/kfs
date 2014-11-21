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

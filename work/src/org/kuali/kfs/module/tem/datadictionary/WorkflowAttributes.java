/*
 * Copyright 2005-2007 The Kuali Foundation
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

package org.kuali.kfs.module.tem.datadictionary;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.krad.datadictionary.SearchingTypeDefinition;

/**
 * Generic WorkflowAttributes that are aware of the business object. This makes it so that we can specify
 * attributes per business object if we need to and reduce the amount of repitious code.
 *
 */
public class WorkflowAttributes extends org.kuali.rice.krad.datadictionary.WorkflowAttributes {
    private static final long serialVersionUID = -3426603523049661524L;
    private String businessObjectClassName;

    /**
     * @return Returns the businessObjectClassName.
     */
    public String getBusinessObjectClassName() {
        return businessObjectClassName;
    }

    /**
     * @param businessObjectClassName The businessObjectClassName to set.
     */
    public void setBusinessObjectClassName(final String businessObjectClassName) {
        this.businessObjectClassName = businessObjectClassName;
        if (getSearchingTypeDefinitions() != null) {
            ((BoAwareList) getSearchingTypeDefinitions()).setBusinessObjectClassName(getBusinessObjectClassName());
        }
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.WorkflowAttributes#setSearchingTypeDefinitions(java.util.List)
     */
    @Override
    public void setSearchingTypeDefinitions(final List<SearchingTypeDefinition> searchingTypeDefinitions) {
        super.setSearchingTypeDefinitions(new BoAwareList(searchingTypeDefinitions));
        ((BoAwareList) getSearchingTypeDefinitions()).setBusinessObjectClassName(getBusinessObjectClassName());
    }

    private static class BoAwareList extends ArrayList<SearchingTypeDefinition> {
        private String businessObjectClassName;

        public BoAwareList(final List<SearchingTypeDefinition> data) {
            super(data);
        }

        /**
         * Gets the businessObjectClassName attribute.
         * @return Returns the businessObjectClassName.
         */
        public String getBusinessObjectClassName() {
            return businessObjectClassName;
        }

        /**
         * Sets the businessObjectClassName attribute value.
         * @param businessObjectClassName The businessObjectClassName to set.
         */
        public void setBusinessObjectClassName(final String businessObjectClassName) {
            this.businessObjectClassName = businessObjectClassName;
            for (final SearchingTypeDefinition type : this) {
                if (isBlank(type.getSearchingAttribute().getBusinessObjectClassName())) {
                    type.getSearchingAttribute().setBusinessObjectClassName(businessObjectClassName);
                }
            }
        }
    }
}

/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject.inquiry;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;

public class OrganizationReversionInquirable extends KfsInquirableImpl {
    private OrganizationReversionService organizationReversionService;

    /**
     * Overridden to take out details with inactive categories
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.krad.bo.BusinessObject)
     * 
     * KRAD Conversion: Inquirable performs conditional display/hiding of the fields/sections on the inquiry
     * But all field/section definitions are in data dictionary for bo Organization.
     */
    @Override
    public List<Section> getSections(BusinessObject bo) {
        List<Section> sections = super.getSections(bo);
        if (organizationReversionService == null) {
            organizationReversionService = SpringContext.getBean(OrganizationReversionService.class);
        }
        for (Section section : sections) {
            for (Row row : section.getRows()) {
                List<Field> updatedFields = new ArrayList<Field>();
                for (Field field : row.getFields()) {
                    if (shouldIncludeField(field)) {
                        updatedFields.add(field);
                    }
                }
                row.setFields(updatedFields);
            }
        }
        return sections;
    }

    /**
     * Determines if the given field should be included in the updated row, once we take out inactive categories
     * @param field the field to check
     * @return true if the field should be included (ie, it doesn't describe an organization reversion with an inactive category); false otherwise
     *
     * KRAD Conversion: Determines if fields should be included in the section.
     * But all field/section definitions are in data dictionary.
     */
    protected boolean shouldIncludeField(Field field) {
        boolean includeField = true;
        if (field.getContainerRows() != null) {
            for (Row containerRow : field.getContainerRows()) {
                for (Field containedField : containerRow.getFields()) {
                    if (containedField.getPropertyName().matches("organizationReversionDetail\\[\\d+\\]\\.organizationReversionCategoryCode")) {
                        final String categoryValue = containedField.getPropertyValue();
                        includeField = organizationReversionService.isCategoryActive(categoryValue);
                    }
                }
            }
        }
        return includeField;
    }
}

/*
 * Copyright 2006 The Kuali Foundation
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

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

/**
 * This class adds in some new sections for {@link ObjectCode} inquiries, specifically Research object code fields
 */
public class ObjectCodeInquirable extends KfsInquirableImpl {

    public void addAdditionalSections(List sections, BusinessObject bo) {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String enableResearchAdminObjectCodeAttributeInd = parameterService.getParameterValue(ObjectCode.class, KFSConstants.ObjectCodeConstants.PARAMETER_KC_ENABLE_RESEARCH_ADMIN_OBJECT_CODE_ATTRIBUTE_IND);

        if ((bo instanceof ObjectCode) && (enableResearchAdminObjectCodeAttributeInd.equals("Y")) ) {
            ObjectCode objectCode = (ObjectCode) bo;
            
            List rows = new ArrayList();

            Field f = new Field();
            f.setPropertyName("Budget Category Code");
            f.setFieldLabel("Budget Category Code");
            f.setPropertyValue(objectCode.getRschBudgetCategoryCode());
            f.setFieldType(Field.TEXT);
            rows.add(new Row(f));
            
            f = new Field();
            f.setPropertyName("On Campus?");
            f.setFieldLabel("On Campus?");
            f.setPropertyValue(objectCode.isRschOnCampusIndicator());
            f.setFieldType(Field.CHECKBOX);
            rows.add(new Row(f));
            
            f = new Field();
            f.setPropertyName("Object Code Description");
            f.setFieldLabel("Object Code Description");
            f.setPropertyValue(objectCode.getRschObjectCodeDescription());
            f.setFieldType(Field.TEXT);
            rows.add(new Row(f));
           
            Section section = new Section();
            section.setRows(rows);
            section.setSectionTitle("Research Admin Attributes");
            sections.add(section);
        }
    }


}

/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.chart.maintenance;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.core.web.ui.Section;
import org.kuali.kfs.inquiry.KfsInquirableImpl;
import org.kuali.module.chart.bo.Org;

/**
 * 
 * This class adds in some new sections for {@link Org} inquiries, specifically
 * Org Hierarchy
 * Org Review Hierarchy
 */
public class OrgInquirable extends KfsInquirableImpl {

    public void addAdditionalSections(List sections, BusinessObject bo) {
        if (bo instanceof Org) {
            Org org = (Org) bo;
            
            List rows = new ArrayList();
            
            Field f = new Field();
            f.setPropertyName("Organization Hierarchy");
            f.setFieldLabel("Organization Hierarchy");
            f.setPropertyValue(org.getOrganizationHierarchy());
            f.setFieldType(Field.HIDDEN);
            rows.add(new Row(f));

            f = new Field();
            f.setPropertyName("Organization Review Hierarchy");
            f.setFieldLabel("Organization Review Hierarchy");
            f.setPropertyValue("run search");
            f.setFieldType(Field.HIDDEN);
            f.setInquiryURL(org.getOrganizationReviewHierarchy());
            rows.add(new Row(f));
            
            Section section = new Section();
            section.setRows(rows);
            section.setSectionTitle("Organization Hierarchy");
            sections.add(section);
        }
    }


}
    

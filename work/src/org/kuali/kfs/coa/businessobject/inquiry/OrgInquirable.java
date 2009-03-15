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
package org.kuali.kfs.coa.businessobject.inquiry;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

/**
 * This class adds in some new sections for {@link Org} inquiries, specifically Org Hierarchy Org Review Hierarchy
 */
public class OrgInquirable extends KfsInquirableImpl {

    public void addAdditionalSections(List sections, BusinessObject bo) {
        if (bo instanceof Organization) {
            Organization org = (Organization) bo;

            List rows = new ArrayList();

            Field f = new Field();
            f.setPropertyName("Organization Hierarchy");
            f.setFieldLabel("Organization Hierarchy");
            f.setPropertyValue(org.getOrganizationHierarchy());
            f.setFieldType(Field.TEXT);
            rows.add(new Row(f));

            f = new Field();
            f.setPropertyName("Organization Review Hierarchy");
            f.setFieldLabel("Organization Review Hierarchy");
            f.setPropertyValue("run search");
            f.setFieldType(Field.TEXT);
            HtmlData hRef = new AnchorHtmlData(org.getOrganizationReviewHierarchy(), KNSConstants.EMPTY_STRING);
            f.setInquiryURL(hRef);
            rows.add(new Row(f));

            Section section = new Section();
            section.setRows(rows);
            section.setSectionTitle("Organization Hierarchy");
            sections.add(section);
        }
    }


}

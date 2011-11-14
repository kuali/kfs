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
import java.util.Properties;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This class adds in some new sections for {@link Org} inquiries, specifically Org Hierarchy Org Review Hierarchy
 */
public class OrgInquirable extends KfsInquirableImpl {

    /**
     * KRAD Conversion: Inquirable adds new fields to sections and then adds new sections.
     * But all field/section definitions are built here for the new section.
     */
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
            Properties parameters = new Properties();
            parameters.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, org.getChartOfAccountsCode());
            parameters.put(KfsKimAttributes.ORGANIZATION_CODE, org.getOrganizationCode());
            parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, OrgReviewRole.class.getName());
            parameters.put(KRADConstants.RETURN_LOCATION_PARAMETER, KRADConstants.PORTAL_ACTION);
            parameters.put(KRADConstants.DOC_FORM_KEY, KimConstants.KimUIConstants.KIM_ROLE_DOCUMENT_SHORT_KEY);
            String hrefStr = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, parameters);
            HtmlData hRef = new AnchorHtmlData(hrefStr, KRADConstants.EMPTY_STRING);
            f.setInquiryURL(hRef);
            rows.add(new Row(f));

            Section section = new Section();
            section.setRows(rows);
            section.setSectionTitle("Organization Hierarchy");
            sections.add(section);
        }
    }


}

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

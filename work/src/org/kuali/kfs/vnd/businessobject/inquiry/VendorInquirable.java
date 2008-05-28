/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.vendor.maintenance;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.inquiry.KfsInquirableImpl;
import org.kuali.module.vendor.bo.VendorDetail;

/**
 * This class adds in some new sections for {@link Org} inquiries, specifically Org Hierarchy Org Review Hierarchy
 */
public class VendorInquirable extends KfsInquirableImpl {
    
    /**
     * Overrides the helper method to build an inquiry url for a result field. 
     * For the Vendor URL field, returns the url address as the inquiry URL, so that Vendor URL functions as a hyperlink. 
     * 
     * @param bo the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    public String getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (businessObject instanceof VendorDetail && attributeName.equalsIgnoreCase("vendorUrlAddress")) {
            Object objFieldValue = ObjectUtils.getPropertyValue(businessObject, attributeName);
            String fieldValue = objFieldValue == null ? KFSConstants.EMPTY_STRING : objFieldValue.toString();
            return fieldValue;
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }
    
    /*
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
    */
}

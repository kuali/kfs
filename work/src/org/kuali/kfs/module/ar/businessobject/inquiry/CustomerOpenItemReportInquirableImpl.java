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
package org.kuali.kfs.module.ar.businessobject.inquiry;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

public class CustomerOpenItemReportInquirableImpl extends KfsInquirableImpl {

    /**
     * Show the Customer Open Item Report tab. This is Customer History Report.
     *
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#addAdditionalSections(java.util.List, org.kuali.rice.krad.bo.BusinessObject)
     *
     * KRAD Conversion: Inquirable performs adding a new field and adding a new section to the sections.
     *
     * Fields are in data dictionary for Customer.
     */
    @Override
    public void addAdditionalSections(List sections, BusinessObject bo) {
        if (bo instanceof Customer) {
            Customer customer = (Customer) bo;

            List rows = new ArrayList();

            Field f = new Field();
            f.setBusinessObjectClassName(bo.getClass().getName());
            f.setPropertyName(KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME);
            f.setFieldLabel("History Report");
            f.setPropertyValue("Click here to view the history report for this customer.");
            f.setFieldType(Field.TEXT);

            AnchorHtmlData inquiryHref = new AnchorHtmlData(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING, "view open item report");
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, CustomerOpenItemReportDetail.class.getName());
            parameters.put(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, ArConstants.CUSTOMER_OPEN_ITEM_REPORT_LOOKUPABLE_IMPL);
            parameters.put(KFSConstants.DOC_FORM_KEY, "88888888");
            parameters.put(KFSConstants.RETURN_LOCATION_PARAMETER, StringUtils.EMPTY);
            parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            parameters.put(KFSConstants.CustomerOpenItemReport.REPORT_NAME, KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME);
            parameters.put(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, customer.getCustomerNumber());
            parameters.put(ArPropertyConstants.CustomerFields.CUSTOMER_NAME, customer.getCustomerName());

            inquiryHref.setHref(UrlFactory.parameterizeUrl(getKualiConfigurationService().getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + ArConstants.UrlActions.CUSTOMER_OPEN_ITEM_REPORT_LOOKUP, parameters));

            f.setInquiryURL(inquiryHref);
            rows.add(new Row(f));

            Section section = new Section();
            section.setRows(rows);
            section.setSectionTitle(KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME);
            sections.add(section);
        }
    }
}

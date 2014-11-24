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
package org.kuali.kfs.module.ar.businessobject.inquiry;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;

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
            HtmlData hRef = new AnchorHtmlData("../arCustomerOpenItemReportLookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail&lookupableImplementaionServiceName=arCustomerOpenItemReportLookupable&docFormKey=88888888&returnLocation=&hideReturnLink=true&reportName=" + KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME + "&customerNumber=" + customer.getCustomerNumber() + "&customerName="+customer.getCustomerName(),KRADConstants.EMPTY_STRING, "view open item report");
            f.setInquiryURL(hRef);
            rows.add(new Row(f));

            Section section = new Section();
            section.setRows(rows);
            section.setSectionTitle(KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME);
            sections.add(section);
        }
    }
}

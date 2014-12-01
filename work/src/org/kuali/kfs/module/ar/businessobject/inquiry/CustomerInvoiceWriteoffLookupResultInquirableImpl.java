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

import java.util.Properties;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class CustomerInvoiceWriteoffLookupResultInquirableImpl extends KfsInquirableImpl {

    /**
     * Helper method to build an inquiry URLs for Customer Invoice Writeoff Lookup Results
     * 
     * @param businessObject the business object instance to build the urls for
     * @param attributeName the attribute name which links to an inquirable
     * @return String url to inquiry
     */
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName) {

        AnchorHtmlData inquiryHref = new AnchorHtmlData(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING);
        if (ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER.equals(attributeName)) {
            String baseUrl = KFSConstants.INQUIRY_ACTION;
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Customer.class.getName());
            parameters.put(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, ObjectUtils.getPropertyValue((CustomerInvoiceWriteoffLookupResult) businessObject, attributeName));

            inquiryHref.setHref(UrlFactory.parameterizeUrl(baseUrl, parameters));
        } else if (ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER.equals(attributeName) ){
            
            String documentNumber = ObjectUtils.getPropertyValue((CustomerInvoiceDocument)businessObject, attributeName).toString();
            inquiryHref.setHref(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + documentNumber + "&command=displayDocSearchView");
        }
        return inquiryHref;
    }
}

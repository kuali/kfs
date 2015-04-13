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
package org.kuali.kfs.module.tem.businessobject.inquiry;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Corrects the vendor number
 */
public class CreditCardAgencyInquirable extends KualiInquirableImpl {

    @Override
    @Deprecated
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (StringUtils.equals(attributeName, KFSPropertyConstants.VENDOR_NUMBER)) {
            CreditCardAgency agency = (CreditCardAgency)businessObject;
            if (agency.getVendorHeaderGeneratedIdentifier() != null && agency.getVendorDetailAssignedIdentifier() != null) {
                Properties params = new Properties();
                params.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, agency.getVendorHeaderGeneratedIdentifier().toString());
                params.put(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, agency.getVendorDetailAssignedIdentifier().toString());
                params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
                params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, VendorDetail.class.getName());

                String url = UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, params);

                Map<String, String> fieldList = new HashMap<String, String>();
                fieldList.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, agency.getVendorHeaderGeneratedIdentifier().toString());
                fieldList.put(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, agency.getVendorDetailAssignedIdentifier().toString());

                return getHyperLink(VendorDetail.class, fieldList, url);
            }
        }
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}

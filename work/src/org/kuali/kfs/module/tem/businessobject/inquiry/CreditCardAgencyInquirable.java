/*
 * Copyright 2014 The Kuali Foundation.
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

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
package org.kuali.kfs.fp.businessobject.inquirable;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

public class DisbursementPayeeInquirableImpl extends KualiInquirableImpl {

    /**
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String,
     *      boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (businessObject instanceof DisbursementPayee && KFSPropertyConstants.PAYEE_NAME.equals(attributeName)) {
            DisbursementPayee payee = (DisbursementPayee) businessObject;

            boolean isVendor = SpringContext.getBean(DisbursementVoucherPayeeService.class).isVendor(payee);
            if (isVendor) {
                return this.getVendorInquiryUrl(payee);
            }

            boolean isEmployee = SpringContext.getBean(DisbursementVoucherPayeeService.class).isEmployee(payee);
            if (isEmployee) {
                return this.getPersonInquiryUrl(payee);
            }
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

    // get inquiry url to a person if the payee is a non-vendor employee
    private HtmlData getPersonInquiryUrl(DisbursementPayee payee) {
        Properties params = new Properties();
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Person.class.getName());
        params.put(KFSPropertyConstants.PRINCIPAL_ID, payee.getPrincipalId());

        String url = UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, params);

        Map<String, String> fieldList = new HashMap<String, String>();
        fieldList.put(KFSPropertyConstants.PRINCIPAL_ID, payee.getPrincipalId());

        return this.getHyperLink(Person.class, fieldList, url);
    }

    // get inquiry url to a vendor if the payee is a vendor
    private HtmlData getVendorInquiryUrl(DisbursementPayee payee) {
        String payeeIdNumber = payee.getPayeeIdNumber();
        String vendorHeaderGeneratedIdentifier = StringUtils.substringBefore(payeeIdNumber, "-");
        String vendorDetailAssignedIdentifier = StringUtils.substringAfter(payeeIdNumber, "-");

        Properties params = new Properties();
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, VendorDetail.class.getName());
        params.put(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorHeaderGeneratedIdentifier);
        params.put(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorDetailAssignedIdentifier);

        String url = UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, params);

        Map<String, String> fieldList = new HashMap<String, String>();
        fieldList.put(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorHeaderGeneratedIdentifier);
        fieldList.put(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorDetailAssignedIdentifier);

        return this.getHyperLink(VendorDetail.class, fieldList, url);
    }

}

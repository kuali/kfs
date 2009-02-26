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
package org.kuali.kfs.pdp.businessobject.inquiry;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * Sets inquiry on payee name
 */
public class PayeeAchAccountInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (PdpPropertyConstants.PAYEE_NAME.equals(attributeName)) {
            Properties parameters = new Properties();

            parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, PayeeACHAccount.class.getName());

            KualiInteger generatedIdentifier = (KualiInteger) ObjectUtils.getPropertyValue(businessObject, PdpPropertyConstants.ACH_ACCOUNT_GENERATED_IDENTIFIER);
            parameters.put(PdpPropertyConstants.ACH_ACCOUNT_GENERATED_IDENTIFIER, generatedIdentifier.toString());

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(PdpPropertyConstants.ACH_ACCOUNT_GENERATED_IDENTIFIER, generatedIdentifier.toString());
            fieldList.put(KFSPropertyConstants.PRINCIPAL_ID, ( (PayeeACHAccount)businessObject).getPrincipalId());
            
            return getHyperLink(PayeeACHAccount.class, fieldList, UrlFactory.parameterizeUrl(KNSConstants.INQUIRY_ACTION, parameters));
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}

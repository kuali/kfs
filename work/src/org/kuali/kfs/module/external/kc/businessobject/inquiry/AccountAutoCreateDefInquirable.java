/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.businessobject.inquiry;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Inquirable class for {@link Account}
 */
public class AccountAutoCreateDefInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (KcConstants.ACCOUNT_CREATE_DEFAULT_IDENTIFIER.equals(attributeName)) {
            String baseUrl = KRADConstants.INQUIRY_ACTION;

            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AccountAutoCreateDefaults.class.getName());
            parameters.put(KFSConstants.DOC_FORM_KEY, "88888888");

            Map<String, String> inquiryFields = new HashMap<String, String>();
            String acctIdentifier =  ObjectUtils.getPropertyValue(businessObject, attributeName).toString();
            if (StringUtils.isBlank(acctIdentifier)) {
                return new AnchorHtmlData();
            }
            inquiryFields.put(KcConstants.ACCOUNT_CREATE_DEFAULT_IDENTIFIER, acctIdentifier);
            parameters.put(KcConstants.ACCOUNT_CREATE_DEFAULT_IDENTIFIER, acctIdentifier);

            return getHyperLink(AccountAutoCreateDefaults.class, inquiryFields, UrlFactory.parameterizeUrl(baseUrl, parameters));
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}

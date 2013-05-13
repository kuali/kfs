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
package org.kuali.kfs.coa.businessobject.inquiry;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRate;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Inquirable class for {@link Account}
 */
public class AccountInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER.equals(attributeName)) {
            String baseUrl = KRADConstants.INQUIRY_ACTION;

            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, IndirectCostRecoveryRate.class.getName());
            parameters.put(KFSConstants.DOC_FORM_KEY, "88888888");

            Map<String, String> inquiryFields = new HashMap<String, String>();
            String icrIdentifier = (String) ObjectUtils.getPropertyValue(businessObject, attributeName);
            if (StringUtils.isBlank(icrIdentifier)) {
                return new AnchorHtmlData();
            }
            inquiryFields.put(KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, icrIdentifier);
            parameters.put(KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, icrIdentifier);

            Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
            parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear.toString());

            return getHyperLink(IndirectCostRecoveryRate.class, inquiryFields, UrlFactory.parameterizeUrl(baseUrl, parameters));
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}

/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.inquiry;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentBalance;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentBalanceDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;

public class KEMIDCurrentBalanceInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        KEMIDCurrentBalance currentBalance = (KEMIDCurrentBalance) businessObject;
        if (EndowPropertyConstants.CURRENT_BAL_TOTAL_MARKET_VALUE.equals(attributeName) && ObjectUtils.isNotNull(currentBalance.getTotalMarketValue())) {

            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, KEMIDCurrentBalanceDetail.class.getName());
            params.put(KNSConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KNSConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            params.put(KFSConstants.LOOKUP_READ_ONLY_FIELDS, EndowPropertyConstants.KEMID + "," + EndowPropertyConstants.CURRENT_BAL_PURPOSE_CD + "," + EndowPropertyConstants.CURRENT_BAL_KEMID_BALANCE_DATE + "," + EndowPropertyConstants.CURRENT_BAL_CLOSED_INDICATOR + "," + EndowPropertyConstants.KEMID_CRNT_BAL_KEMID_SHORT_TTL + "," + EndowPropertyConstants.KEMID_CRNT_BAL_PURPOSE_DESC);
            params.put(EndowPropertyConstants.KEMID, UrlFactory.encode(String.valueOf(currentBalance.getKemid())));
            params.put(EndowPropertyConstants.CURRENT_BAL_PURPOSE_CD, UrlFactory.encode(currentBalance.getKemidObj().getPurposeCode()));
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            params.put(EndowPropertyConstants.CURRENT_BAL_KEMID_BALANCE_DATE, dateTimeService.toDateString(currentBalance.getBalanceDate()));
            params.put(EndowPropertyConstants.CURRENT_BAL_CLOSED_INDICATOR, currentBalance.getKemidObj().isClose() ? "Yes" : "No");
            params.put(EndowPropertyConstants.KEMID_CRNT_BAL_KEMID_SHORT_TTL, currentBalance.getKemidObj().getShortTitle());

            if (ObjectUtils.isNotNull(currentBalance.getKemidObj().getPurpose())) {
                params.put(EndowPropertyConstants.KEMID_CRNT_BAL_PURPOSE_DESC, currentBalance.getKemidObj().getPurpose().getName());
            }

            String url = UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(EndowPropertyConstants.KEMID, currentBalance.getKemid().toString());
            fieldList.put(EndowPropertyConstants.CURRENT_BAL_PURPOSE_CD, currentBalance.getKemidObj().getPurposeCode());
            fieldList.put(EndowPropertyConstants.CURRENT_BAL_KEMID_BALANCE_DATE, dateTimeService.toDateString(currentBalance.getBalanceDate()));
            fieldList.put(EndowPropertyConstants.CURRENT_BAL_CLOSED_INDICATOR, currentBalance.getKemidObj().isClose() ? "Yes" : "No");
            fieldList.put(EndowPropertyConstants.KEMID_CRNT_BAL_KEMID_SHORT_TTL, currentBalance.getKemidObj().getShortTitle());
            
            if (ObjectUtils.isNotNull(currentBalance.getKemidObj().getPurpose())) {
                fieldList.put(EndowPropertyConstants.KEMID_CRNT_BAL_PURPOSE_DESC, currentBalance.getKemidObj().getPurpose().getName());
            }

            return getHyperLink(KEMIDCurrentBalanceDetail.class, fieldList, url);
        }
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}

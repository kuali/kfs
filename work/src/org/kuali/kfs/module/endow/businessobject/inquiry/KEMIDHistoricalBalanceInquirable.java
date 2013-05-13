/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.inquiry;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMIDHistoricalBalance;
import org.kuali.kfs.module.endow.businessobject.KEMIDHistoricalBalanceDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class KEMIDHistoricalBalanceInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        KEMIDHistoricalBalance historicalBalance = (KEMIDHistoricalBalance) businessObject;
        if (EndowPropertyConstants.KEMID_HIST_BAL_TOTAL_MARKET_VAL.equals(attributeName) && ObjectUtils.isNotNull(historicalBalance.getTotalMarketValue())) {

            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, KEMIDHistoricalBalanceDetail.class.getName());
            params.put(KRADConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            params.put(KFSConstants.LOOKUP_READ_ONLY_FIELDS, EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID + "," + EndowPropertyConstants.KEMID_HIST_BAL_DET_PURPOSE_CODE + "," + EndowPropertyConstants.KEMID_HIST_BAL_DET_DATE_ID + "," + EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID_CLOSED_INDICATOR + "," + EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID_SHORT_TTL + "," + EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID_PURPOSE_DESC);
            params.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID, UrlFactory.encode(String.valueOf(historicalBalance.getKemid())));
            params.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_PURPOSE_CODE, UrlFactory.encode(historicalBalance.getKemidObj().getPurposeCode()));
            params.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_DATE_ID, UrlFactory.encode(String.valueOf(historicalBalance.getHistoryBalanceDateId())));
            params.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID_CLOSED_INDICATOR, historicalBalance.getKemidObj().isClose() ? "Yes" : "No");
            params.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID_SHORT_TTL, historicalBalance.getKemidObj().getShortTitle());
            params.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID_PURPOSE_DESC, historicalBalance.getKemidObj().getPurpose().getName());

            String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID, historicalBalance.getKemid().toString());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_PURPOSE_CODE, historicalBalance.getKemidObj().getPurposeCode());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_DATE_ID, String.valueOf(historicalBalance.getHistoryBalanceDateId()));
            fieldList.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID_CLOSED_INDICATOR, historicalBalance.getKemidObj().isClose() ? "Yes" : "No");
            fieldList.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID_SHORT_TTL, historicalBalance.getKemidObj().getShortTitle());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID_PURPOSE_DESC, historicalBalance.getKemidObj().getPurpose().getName());

            return getHyperLink(KEMIDHistoricalBalanceDetail.class, fieldList, url);
        }
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }


}

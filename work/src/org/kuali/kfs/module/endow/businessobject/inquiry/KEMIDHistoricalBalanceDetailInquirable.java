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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMIDHistoricalBalanceDetail;
import org.kuali.kfs.module.endow.businessobject.KEMIDHistoricalReportingGroup;
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

public class KEMIDHistoricalBalanceDetailInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        KEMIDHistoricalBalanceDetail historicalBalanceDetail = (KEMIDHistoricalBalanceDetail) businessObject;

        boolean isIncome = EndowPropertyConstants.KEMID_HIST_BAL_DET_INC_AT_MARKET.equals(attributeName);
        boolean isIncomeNotNullOrZero = ObjectUtils.isNotNull(historicalBalanceDetail.getIncomeAtMarket()) && (historicalBalanceDetail.getIncomeAtMarket().compareTo(BigDecimal.ZERO) != 0);
        boolean isPrincipal = EndowPropertyConstants.KEMID_HIST_BAL_DET_PRINC_AT_MARKET.equals(attributeName);
        boolean isPrincipalNotNullOrZero = ObjectUtils.isNotNull(historicalBalanceDetail.getPrincipalAtMarket()) && (historicalBalanceDetail.getPrincipalAtMarket().compareTo(BigDecimal.ZERO) != 0);

        if ((isIncome && isIncomeNotNullOrZero) || (isPrincipal && isPrincipalNotNullOrZero)) {

            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, KEMIDHistoricalReportingGroup.class.getName());
            params.put(KRADConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            params.put(KFSConstants.LOOKUP_READ_ONLY_FIELDS, EndowPropertyConstants.KEMID + "," + EndowPropertyConstants.KEMID_HIST_BAL_DET_PURPOSE_CODE + "," + EndowPropertyConstants.KEMID_HIST_REP_GRP_CD + "," + EndowPropertyConstants.KEMID_HIST_REP_GRP_IP_IND + "," + EndowPropertyConstants.KEMID_HIST_BAL_DET_DATE_ID + "," + EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID_CLOSED_INDICATOR + "," + EndowPropertyConstants.KEMID_HIST_REP_GRP_KEMID_SHORT_TTL + "," + EndowPropertyConstants.KEMID_HIST_REP_GRP_PURPOSE_DESC + "," + EndowPropertyConstants.KEMID_HIST_REP_GRP_DESC + "," + EndowPropertyConstants.KEMID_HIST_REP_GRP_IP_IND_DESC);

            params.put(EndowPropertyConstants.KEMID, UrlFactory.encode(String.valueOf(historicalBalanceDetail.getKemid())));
            params.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_PURPOSE_CODE, UrlFactory.encode(historicalBalanceDetail.getKemidObj().getPurposeCode()));
            params.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_DATE_ID, UrlFactory.encode(String.valueOf(historicalBalanceDetail.getHistoryBalanceDateId())));
            params.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID_CLOSED_INDICATOR, historicalBalanceDetail.getKemidObj().isClose() ? "Yes" : "No");
            params.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_IP_IND, historicalBalanceDetail.getIncomePrincipalIndicator());
            params.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_CD, historicalBalanceDetail.getReportingGroupCode());
            params.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_KEMID_SHORT_TTL, historicalBalanceDetail.getKemidObj().getShortTitle());
            params.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_PURPOSE_DESC, historicalBalanceDetail.getKemidObj().getPurpose().getName());
            params.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_DESC, historicalBalanceDetail.getReportingGroup().getName());

            String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(EndowPropertyConstants.KEMID, historicalBalanceDetail.getKemid().toString());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_PURPOSE_CODE, historicalBalanceDetail.getKemidObj().getPurposeCode());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_DATE_ID, String.valueOf(historicalBalanceDetail.getHistoryBalanceDateId()));
            fieldList.put(EndowPropertyConstants.KEMID_HIST_BAL_DET_KEMID_CLOSED_INDICATOR, historicalBalanceDetail.getKemidObj().isClose() ? "Yes" : "No");
            fieldList.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_IP_IND, historicalBalanceDetail.getIncomePrincipalIndicator());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_CD, historicalBalanceDetail.getReportingGroupCode());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_KEMID_SHORT_TTL, historicalBalanceDetail.getKemidObj().getShortTitle());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_PURPOSE_DESC, historicalBalanceDetail.getKemidObj().getPurpose().getName());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_DESC, historicalBalanceDetail.getReportingGroup().getName());


            return getHyperLink(KEMIDHistoricalReportingGroup.class, fieldList, url);
        }
        else if ((isIncome && !isIncomeNotNullOrZero) || (isPrincipal && !isPrincipalNotNullOrZero)) {
            return new AnchorHtmlData(" ", " ", " ");
        }
        else

            return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}

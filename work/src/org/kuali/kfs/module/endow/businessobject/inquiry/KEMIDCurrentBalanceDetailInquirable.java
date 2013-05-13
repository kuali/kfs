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
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentBalanceDetail;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentReportingGroup;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class KEMIDCurrentBalanceDetailInquirable extends KfsInquirableImpl {


    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        KEMIDCurrentBalanceDetail currentBalanceDetail = (KEMIDCurrentBalanceDetail) businessObject;
        boolean isIncome = EndowPropertyConstants.KEMID_CRNT_BAL_DET_INC_AT_MARKET.equals(attributeName);
        boolean isIncomeNotNullOrZero = ObjectUtils.isNotNull(currentBalanceDetail.getIncomeAtMarket()) && (currentBalanceDetail.getIncomeAtMarket().compareTo(BigDecimal.ZERO) != 0);
        boolean isPrincipal = EndowPropertyConstants.KEMID_CRNT_BAL_DET_PRIN_AT_MARKET.equals(attributeName);
        boolean isPrincipalNotNullOrZero = ObjectUtils.isNotNull(currentBalanceDetail.getPrincipalAtMarket()) && (currentBalanceDetail.getPrincipalAtMarket().compareTo(BigDecimal.ZERO) != 0);

        if (!currentBalanceDetail.isNoDrillDownOnMarketVal() && ((isIncome && isIncomeNotNullOrZero) || (isPrincipal && isPrincipalNotNullOrZero))) {

            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, KEMIDCurrentReportingGroup.class.getName());
            params.put(KRADConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            params.put(KFSConstants.LOOKUP_READ_ONLY_FIELDS, EndowPropertyConstants.KEMID + "," + EndowPropertyConstants.CURRENT_BAL_PURPOSE_CD + "," + EndowPropertyConstants.KEMID_CRNT_REP_GRP_CD + "," + EndowPropertyConstants.KEMID_CRNT_REP_GRP_IP_IND + "," + EndowPropertyConstants.CURRENT_BAL_KEMID_BALANCE_DATE + "," + EndowPropertyConstants.CURRENT_BAL_CLOSED_INDICATOR + "," + EndowPropertyConstants.KEMID_CRNT_REP_GRP_KEMID_SHORT_TTL + "," + EndowPropertyConstants.KEMID_CRNT_REP_GRP_PURPOSE_DESC + "," + EndowPropertyConstants.KEMID_CRNT_REP_GRP_DESC + "," + EndowPropertyConstants.KEMID_CRNT_REP_GRP_IP_IND_DESC);
            params.put(EndowPropertyConstants.KEMID, UrlFactory.encode(String.valueOf(currentBalanceDetail.getKemid())));
            params.put(EndowPropertyConstants.CURRENT_BAL_PURPOSE_CD, UrlFactory.encode(currentBalanceDetail.getKemidObj().getPurposeCode()));
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_DATE, dateTimeService.toDateString(currentBalanceDetail.getBalanceDate()));
            params.put(EndowPropertyConstants.CURRENT_BAL_CLOSED_INDICATOR, currentBalanceDetail.getKemidObj().isClose() ? "Yes" : "No");
            params.put(EndowPropertyConstants.KEMID_CRNT_REP_GRP_IP_IND, currentBalanceDetail.getIncomePrincipalIndicator());
            params.put(EndowPropertyConstants.KEMID_CRNT_REP_GRP_CD, currentBalanceDetail.getReportingGroupCode());
            params.put(EndowPropertyConstants.KEMID_CRNT_REP_GRP_KEMID_SHORT_TTL, currentBalanceDetail.getKemidObj().getShortTitle());
            params.put(EndowPropertyConstants.KEMID_CRNT_REP_GRP_PURPOSE_DESC, currentBalanceDetail.getKemidObj().getPurpose().getName());
            params.put(EndowPropertyConstants.KEMID_CRNT_REP_GRP_DESC, currentBalanceDetail.getReportingGroup().getName());
            params.put(EndowPropertyConstants.KEMID_CRNT_REP_GRP_IP_IND_DESC, currentBalanceDetail.getIpIndicator().getName());
            String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(EndowPropertyConstants.KEMID, currentBalanceDetail.getKemid().toString());
            fieldList.put(EndowPropertyConstants.CURRENT_BAL_PURPOSE_CD, currentBalanceDetail.getKemidObj().getPurposeCode());
            fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_DATE, dateTimeService.toDateString(currentBalanceDetail.getBalanceDate()));
            fieldList.put(EndowPropertyConstants.CURRENT_BAL_CLOSED_INDICATOR, currentBalanceDetail.getKemidObj().isClose() ? "Yes" : "No");
            fieldList.put(EndowPropertyConstants.KEMID_CRNT_REP_GRP_IP_IND, currentBalanceDetail.getIncomePrincipalIndicator());
            fieldList.put(EndowPropertyConstants.KEMID_CRNT_REP_GRP_CD, currentBalanceDetail.getReportingGroupCode());
            fieldList.put(EndowPropertyConstants.KEMID_CRNT_REP_GRP_KEMID_SHORT_TTL, currentBalanceDetail.getKemidObj().getShortTitle());
            fieldList.put(EndowPropertyConstants.KEMID_CRNT_REP_GRP_PURPOSE_DESC, currentBalanceDetail.getKemidObj().getPurpose().getName());
            fieldList.put(EndowPropertyConstants.KEMID_CRNT_REP_GRP_DESC, currentBalanceDetail.getReportingGroup().getName());
            fieldList.put(EndowPropertyConstants.KEMID_CRNT_REP_GRP_IP_IND_DESC, currentBalanceDetail.getIpIndicator().getName());

            return getHyperLink(KEMIDCurrentReportingGroup.class, fieldList, url);
        }
        else
            return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }
}

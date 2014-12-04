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

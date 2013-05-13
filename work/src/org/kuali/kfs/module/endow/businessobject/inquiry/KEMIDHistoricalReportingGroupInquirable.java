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
import org.kuali.kfs.module.endow.businessobject.KEMIDHistoricalReportingGroup;
import org.kuali.kfs.module.endow.businessobject.KEMIDHistoricalTaxLot;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class KEMIDHistoricalReportingGroupInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        KEMIDHistoricalReportingGroup historicalReportingGroup = (KEMIDHistoricalReportingGroup) businessObject;
        if (EndowPropertyConstants.KEMID_CRNT_REP_GRP_UNITS.equals(attributeName) && ObjectUtils.isNotNull(historicalReportingGroup.getUnits())) {

            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, KEMIDHistoricalTaxLot.class.getName());
            params.put(KRADConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            params.put(KFSConstants.LOOKUP_READ_ONLY_FIELDS, EndowPropertyConstants.KEMID + "," + EndowPropertyConstants.KEMID_HIST_TAX_LOT_KEMID_PURPOSE_CD + "," + EndowPropertyConstants.KEMID_HIST_TAX_LOT_REP_GRP + "," + EndowPropertyConstants.KEMID_HIST_TAX_LOT_IP_IND + "," + EndowPropertyConstants.KEMID_HIST_TAX_LOT_SECURITY_ID + "," + EndowPropertyConstants.KEMID_HIST_TAX_LOT_REGIS_CD + "," + EndowPropertyConstants.KEMID_HIST_TAX_LOT_BALANCE_DATE + "," + EndowPropertyConstants.KEMID_HIST_TAX_LOT_KEMID_CLOSED_IND + "," + EndowPropertyConstants.KEMID_HIST_TAX_LOT_REGIS_DESC + "," + EndowPropertyConstants.KEMID_HIST_TAX_LOT_SEC_DESC + "," + EndowPropertyConstants.KEMID_HIST_TAX_LOT_KEMID_SHORT_TTL + "," + EndowPropertyConstants.KEMID_HIST_TAX_LOT_PURPOSE_DESC + "," + EndowPropertyConstants.KEMID_HIST_TAX_LOT_INC_PRIN_DESC + "," + EndowPropertyConstants.MONTH_END_DATE_ID);

            params.put(EndowPropertyConstants.KEMID, UrlFactory.encode(String.valueOf(historicalReportingGroup.getKemid())));
            params.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_KEMID_PURPOSE_CD, UrlFactory.encode(historicalReportingGroup.getKemidObj().getPurposeCode()));
            params.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_REP_GRP, UrlFactory.encode(historicalReportingGroup.getReportingGroupCode()));
            params.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_IP_IND, UrlFactory.encode(historicalReportingGroup.getIpIndicator()));
            params.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_SEC_ID, UrlFactory.encode(historicalReportingGroup.getSecurityId()));
            params.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_REGIS_CD, UrlFactory.encode(historicalReportingGroup.getRegistrationCode()));
            params.put(EndowPropertyConstants.MONTH_END_DATE_ID, UrlFactory.encode(String.valueOf(historicalReportingGroup.getHistoryBalanceDateId())));
            params.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_KEMID_CLOSED_IND, historicalReportingGroup.getKemidObj().isClose() ? "Yes" : "No");
            params.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_REGIS_DESC, historicalReportingGroup.getRegistration().getName());
            params.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_SEC_DESC, historicalReportingGroup.getSecurity().getDescription());
            params.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_KEMID_SHORT_TTL, historicalReportingGroup.getKemidObj().getShortTitle());
            params.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_PURPOSE_DESC, historicalReportingGroup.getKemidObj().getPurpose().getName());
            params.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_INC_PRIN_DESC, historicalReportingGroup.getIncomePrincipalIndicator().getName());

            String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(EndowPropertyConstants.KEMID, historicalReportingGroup.getKemid().toString());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_KEMID_PURPOSE_CD, historicalReportingGroup.getKemidObj().getPurposeCode());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_REP_GRP, historicalReportingGroup.getReportingGroupCode());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_IP_IND, historicalReportingGroup.getIpIndicator());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_SEC_ID, historicalReportingGroup.getSecurityId());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_REGIS_CD, historicalReportingGroup.getRegistrationCode());
            fieldList.put(EndowPropertyConstants.MONTH_END_DATE_ID, String.valueOf(historicalReportingGroup.getHistoryBalanceDateId()));
            fieldList.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_KEMID_CLOSED_IND, historicalReportingGroup.getKemidObj().isClose() ? "Yes" : "No");
            fieldList.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_REGIS_DESC, historicalReportingGroup.getRegistration().getName());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_SEC_DESC, historicalReportingGroup.getSecurity().getDescription());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_KEMID_SHORT_TTL, historicalReportingGroup.getKemidObj().getShortTitle());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_PURPOSE_DESC, historicalReportingGroup.getKemidObj().getPurpose().getName());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_TAX_LOT_INC_PRIN_DESC, historicalReportingGroup.getIncomePrincipalIndicator().getName());

            return getHyperLink(KEMIDHistoricalTaxLot.class, fieldList, url);
        }
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}

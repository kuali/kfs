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
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
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

public class KEMIDCurrentReportingGroupInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        KEMIDCurrentReportingGroup currentReportingGroup = (KEMIDCurrentReportingGroup) businessObject;
        if (EndowPropertyConstants.KEMID_CRNT_REP_GRP_UNITS.equals(attributeName) && ObjectUtils.isNotNull(currentReportingGroup.getUnits())) {

            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, CurrentTaxLotBalance.class.getName());
            params.put(KRADConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            params.put(KFSConstants.LOOKUP_READ_ONLY_FIELDS, EndowPropertyConstants.KEMID + "," + EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_PURPOSE_CD + "," + EndowPropertyConstants.CURRENT_TAX_LOT_REP_GRP + "," + EndowPropertyConstants.CURRENT_TAX_LOT_IP_IND + "," + EndowPropertyConstants.CURRENT_TAX_LOT_SECURITY_ID + "," + EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_CD + "," + EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_DATE + "," + EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_CLOSED_IND + "," + EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_DESC + "," + EndowPropertyConstants.CURRENT_TAX_LOT_SEC_DESC + "," + EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_SHORT_TTL + "," + EndowPropertyConstants.CURRENT_TAX_LOT_PURPOSE_DESC + "," + EndowPropertyConstants.CURRENT_TAX_LOT_INC_PRIN_DESC);
            params.put(EndowPropertyConstants.KEMID, UrlFactory.encode(String.valueOf(currentReportingGroup.getKemid())));
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_PURPOSE_CD, UrlFactory.encode(currentReportingGroup.getKemidObj().getPurposeCode()));
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_REP_GRP, UrlFactory.encode(currentReportingGroup.getReportingGroupCode()));
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_IP_IND, UrlFactory.encode(currentReportingGroup.getIpIndicator()));
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_SECURITY_ID, UrlFactory.encode(currentReportingGroup.getSecurityId()));
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_CD, UrlFactory.encode(currentReportingGroup.getRegistrationCode()));
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_DATE, dateTimeService.toDateString(currentReportingGroup.getBalanceDate()));
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_CLOSED_IND, currentReportingGroup.getKemidObj().isClose() ? "Yes" : "No");
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_DESC, currentReportingGroup.getRegistration().getName());
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_SEC_DESC, currentReportingGroup.getSecurity().getDescription());
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_SHORT_TTL, currentReportingGroup.getKemidObj().getShortTitle());
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_PURPOSE_DESC, currentReportingGroup.getKemidObj().getPurpose().getName());
            params.put(EndowPropertyConstants.CURRENT_TAX_LOT_INC_PRIN_DESC, currentReportingGroup.getIncomePrincipalIndicator().getName());

            String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(EndowPropertyConstants.KEMID, currentReportingGroup.getKemid().toString());
            fieldList.put(EndowPropertyConstants.CURRENT_BAL_PURPOSE_CD, currentReportingGroup.getKemidObj().getPurposeCode());
            fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_REP_GRP, currentReportingGroup.getReportingGroupCode());
            fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_IP_IND, currentReportingGroup.getIpIndicator());
            fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_SECURITY_ID, currentReportingGroup.getSecurityId());
            fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_CD, currentReportingGroup.getRegistrationCode());
            fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_DATE, dateTimeService.toDateString(currentReportingGroup.getBalanceDate()));
            fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_CLOSED_IND, currentReportingGroup.getKemidObj().isClose() ? "Yes" : "No");
            fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_DESC, currentReportingGroup.getRegistration().getName());
            fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_SEC_DESC, currentReportingGroup.getSecurity().getDescription());
            fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_SHORT_TTL, currentReportingGroup.getKemidObj().getShortTitle());
            fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_PURPOSE_DESC, currentReportingGroup.getKemidObj().getPurpose().getName());
            fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_INC_PRIN_DESC, currentReportingGroup.getIncomePrincipalIndicator().getName());

            return getHyperLink(CurrentTaxLotBalance.class, fieldList, url);
        }
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }
}

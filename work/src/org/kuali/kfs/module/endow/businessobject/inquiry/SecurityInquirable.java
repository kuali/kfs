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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

public class SecurityInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        Security security = (Security) businessObject;

        // if the attribute is currentHolders or holdersInHistory then we build the lookup links for Current Holders and Holders in
        // History
        if (EndowPropertyConstants.SECURITY_CURRENT_HOLDERS.equals(attributeName) || EndowPropertyConstants.SECURITY_HOLDERS_IN_HISTORY.equals(attributeName)) {

            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);

            // the only difference between the two links is the BO class
            // if currentHolders set the BO class to be HoldingTaxLot
            if (EndowPropertyConstants.SECURITY_CURRENT_HOLDERS.equals(attributeName)) {
                params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, HoldingTaxLot.class.getName());
            }
            // if holders in history set the BO to be HoldingHistory
            if (EndowPropertyConstants.SECURITY_HOLDERS_IN_HISTORY.equals(attributeName)) {
                params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, HoldingHistory.class.getName());

                // set month end date to be by default the most recent month end date
                BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
                Collection<MonthEndDate> monthEndDates = businessObjectService.findMatchingOrderBy(MonthEndDate.class, new HashMap(), EndowPropertyConstants.MONTH_END_DATE, false);
                Iterator<MonthEndDate> iterator = monthEndDates.iterator();
                if (iterator.hasNext()) {
                    MonthEndDate monthEndDate = iterator.next();
                    params.put(EndowPropertyConstants.HOLDING_HISTORY_MONTH_END_DATE_ID, UrlFactory.encode(String.valueOf(monthEndDate.getMonthEndDateId())));
                }
            }

            params.put(KRADConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            params.put(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, UrlFactory.encode(String.valueOf(security.getId())));
            params.put(KFSConstants.SUPPRESS_ACTIONS, "true");
            String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(EndowPropertyConstants.HOLDING_TAX_LOT_SECURITY_ID, security.getId().toString());

            return getHyperLink(Security.class, fieldList, url);
        }


        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }
}

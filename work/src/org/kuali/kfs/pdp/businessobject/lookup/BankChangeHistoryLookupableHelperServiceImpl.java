/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.BankChangeHistory;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;

/**
 * Performs search on PaymentGroupHistory for changed bank records and constructs BankChangeHistory report objects.
 */
public class BankChangeHistoryLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        // retrieve payment group history records for bank code changes
        fieldValues.put(PdpPropertyConstants.PAYMENT_CHANGE_CODE, PdpConstants.PaymentChangeCodes.BANK_CHNG_CD);
        List<PaymentGroupHistory> resultsForPaymentGroupHistory = (List<PaymentGroupHistory>) getLookupService().findCollectionBySearchHelper(PaymentGroupHistory.class, fieldValues, false);

        List<BankChangeHistory> bankHistoryResults = new ArrayList<BankChangeHistory>();
        for (PaymentGroupHistory paymentGroupHistory : resultsForPaymentGroupHistory) {
            BankChangeHistory bankChangeHistory = new BankChangeHistory();


        }

        return new CollectionIncomplete(bankHistoryResults, new Long(0));
    }

}

/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.lookup;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.IncomePrincipalIndicator;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentBalanceDetail;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.document.service.impl.KemidCurrentCashServiceImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

public class KEMIDCurrentBalanceDetailLookupableHelperService extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<KEMIDCurrentBalanceDetail> results = (List<KEMIDCurrentBalanceDetail>) super.getSearchResults(fieldValues);

        // check if there are entries with a CSHEQ reporting group in the results list
        boolean incCashEq = false;
        boolean princCashEq = false;

        for (KEMIDCurrentBalanceDetail balanceDetail : results) {
            if (EndowConstants.SecurityReportingGroups.CASH_EQUIVALENTS.equalsIgnoreCase(balanceDetail.getReportingGroupCode())) {

                if (EndowConstants.IncomePrincipalIndicator.INCOME.equalsIgnoreCase(balanceDetail.getIncomePrincipalIndicator())) {
                    incCashEq = true;
                }
                else if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(balanceDetail.getIncomePrincipalIndicator())) {
                    princCashEq = true;
                }

                if (incCashEq && princCashEq) {
                    break;
                }
            }
        }

        if (!incCashEq || !princCashEq) {
            String kemid = fieldValues.get(EndowPropertyConstants.KEMID);
            // check if there is current income/principal cash
            KemidCurrentCashServiceImpl kemidCurrentCashService = SpringContext.getBean(KemidCurrentCashServiceImpl.class);
            KemidCurrentCash currentCash = kemidCurrentCashService.getByPrimaryKey(kemid);

            if (ObjectUtils.isNotNull(currentCash)) {
                // if there is an entry in the current cash for the kemid, and there is income current cash but there were no
                // holdings for this kemid for CSHEQ reporting group add a new entry in the results list to show the current income
                // cash
                if (!incCashEq && currentCash.getCurrentIncomeCash().isNonZero()) {
                    KEMIDCurrentBalanceDetail balanceDetail = createCurrentBalanceDetailForCSHEQ(kemid, EndowConstants.IncomePrincipalIndicator.INCOME, currentCash);

                    results.add(balanceDetail);
                }

                // if there is an entry in the current cash for the kemid, and there is principal current cash but there were no
                // holdings for this kemid for CSHEQ reporting group add a new entry in the results list to show the current
                // principal cash
                if (!princCashEq && currentCash.getCurrentPrincipalCash().isNonZero()) {
                    KEMIDCurrentBalanceDetail balanceDetail = createCurrentBalanceDetailForCSHEQ(kemid, EndowConstants.IncomePrincipalIndicator.PRINCIPAL, currentCash);

                    results.add(balanceDetail);
                }
            }
        }

        return results;
    }

    /**
     * Creates a new KEMIDCurrentBalanceDetail for CSHEQ reporting group. This method is used when there is an entry in the current
     * cash for the kemid but there are no holdings.
     * 
     * @param kemid
     * @param incomeOrPrincipal
     * @param currentCash
     * @return a new KEMIDCurrentBalanceDetail
     */
    private KEMIDCurrentBalanceDetail createCurrentBalanceDetailForCSHEQ(String kemid, String incomeOrPrincipal, KemidCurrentCash currentCash) {

        KEMIDCurrentBalanceDetail balanceDetail = new KEMIDCurrentBalanceDetail();

        balanceDetail.setKemid(kemid);
        balanceDetail.setReportingGroupCode(EndowConstants.SecurityReportingGroups.CASH_EQUIVALENTS);
        balanceDetail.setIncomePrincipalIndicator(incomeOrPrincipal);


        if (EndowConstants.IncomePrincipalIndicator.INCOME.equalsIgnoreCase(incomeOrPrincipal)) {
            balanceDetail.setValueAtMarket(currentCash.getCurrentIncomeCash().bigDecimalValue());
        }
        else {
            balanceDetail.setValueAtMarket(currentCash.getCurrentPrincipalCash().bigDecimalValue());
        }

        Map<String, String> keys = new HashMap<String, String>();
        keys.put(EndowPropertyConstants.KUALICODEBASE_CODE, incomeOrPrincipal);
        IncomePrincipalIndicator incomePrincipalIndicator = (IncomePrincipalIndicator) businessObjectService.findByPrimaryKey(IncomePrincipalIndicator.class, keys);
        balanceDetail.setIpIndicator(incomePrincipalIndicator);

        balanceDetail.setAnnualEstimatedIncome(BigDecimal.ZERO.setScale(2));
        balanceDetail.setNextFYEstimatedIncome(BigDecimal.ZERO.setScale(2));
        balanceDetail.setRemainderOfFYEstimatedIncome(BigDecimal.ZERO.setScale(2));

        balanceDetail.setNoDrillDownOnMarketVal(true);

        return balanceDetail;
    }
}

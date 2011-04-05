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
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;

public class KEMIDCurrentBalanceDetailLookupableHelperService extends KualiLookupableHelperServiceImpl {

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<KEMIDCurrentBalanceDetail> results = (List<KEMIDCurrentBalanceDetail>) super.getSearchResults(fieldValues);

        // check if there are cash equivalents in the result
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
            // check if there is current income cash
            KemidCurrentCashServiceImpl kemidCurrentCashService = SpringContext.getBean(KemidCurrentCashServiceImpl.class);
            KemidCurrentCash currentCash = kemidCurrentCashService.getByPrimaryKey(kemid);

            if (!incCashEq) {
                KEMIDCurrentBalanceDetail balanceDetail = new KEMIDCurrentBalanceDetail();
                balanceDetail.setKemid(fieldValues.get(kemid));
                balanceDetail.setReportingGroupCode(EndowConstants.SecurityReportingGroups.CASH_EQUIVALENTS);
                balanceDetail.setValueAtMarket(currentCash.getCurrentIncomeCash().bigDecimalValue());
                balanceDetail.setIncomePrincipalIndicator(EndowConstants.IncomePrincipalIndicator.INCOME);

                Map<String, String> keys = new HashMap<String, String>();
                keys.put(EndowPropertyConstants.KUALICODEBASE_CODE, EndowConstants.IncomePrincipalIndicator.INCOME);
                IncomePrincipalIndicator incomePrincipalIndicator = (IncomePrincipalIndicator) businessObjectService.findByPrimaryKey(IncomePrincipalIndicator.class, keys);
                balanceDetail.setIpIndicator(incomePrincipalIndicator);

                balanceDetail.setAnnualEstimatedIncome(BigDecimal.ZERO);
                balanceDetail.setNextFYEstimatedIncome(BigDecimal.ZERO);
                balanceDetail.setRemainderOfFYEstimatedIncome(BigDecimal.ZERO);

                balanceDetail.setNoDrillDownOnMarketVal(true);

                results.add(balanceDetail);
            }
            if (!princCashEq) {
                KEMIDCurrentBalanceDetail balanceDetail = new KEMIDCurrentBalanceDetail();
                balanceDetail.setKemid(fieldValues.get(kemid));
                balanceDetail.setReportingGroupCode(EndowConstants.SecurityReportingGroups.CASH_EQUIVALENTS);
                balanceDetail.setValueAtMarket(currentCash.getCurrentPrincipalCash().bigDecimalValue());
                balanceDetail.setIncomePrincipalIndicator(EndowConstants.IncomePrincipalIndicator.PRINCIPAL);

                Map<String, String> keys = new HashMap<String, String>();
                keys.put(EndowPropertyConstants.KUALICODEBASE_CODE, EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
                IncomePrincipalIndicator incomePrincipalIndicator = (IncomePrincipalIndicator) businessObjectService.findByPrimaryKey(IncomePrincipalIndicator.class, keys);
                balanceDetail.setIpIndicator(incomePrincipalIndicator);

                balanceDetail.setAnnualEstimatedIncome(BigDecimal.ZERO);
                balanceDetail.setNextFYEstimatedIncome(BigDecimal.ZERO);
                balanceDetail.setRemainderOfFYEstimatedIncome(BigDecimal.ZERO);

                balanceDetail.setNoDrillDownOnMarketVal(true);

                results.add(balanceDetail);
            }
        }


        return results;
    }
}

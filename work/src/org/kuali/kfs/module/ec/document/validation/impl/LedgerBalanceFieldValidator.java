/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.effort.rules;

import java.util.Map;
import java.util.Set;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.effort.EffortKeyConstants;
import org.kuali.module.effort.util.LaborObjectKeyFieldMap;
import org.kuali.module.gl.util.Message;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.MessageBuilder;

public class LedgerBalanceFieldValidator {

    /**
     * check if the given ledger balance has an account qualified for effort reporting
     * 
     * @param ledgerBalance the given ledger balance
     * @return true if the given ledger balance has an account qualified for effort reporting; otherwise, false
     */
    public static Message checkAccountNumber(LedgerBalance ledgerBalance) {
        if (ledgerBalance.getAccount() == null) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_ACCOUNT_NUMBER_NOT_FOUND, Message.TYPE_FATAL);
        }       
        return null;
    }

    public static Message checkHigherEdFunction(LedgerBalance ledgerBalance) {
        Account account = ledgerBalance.getAccount();
        if (account.getFinancialHigherEdFunction() == null) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_HIGHER_EDUCATION_CODE_NOT_FOUND, Message.TYPE_FATAL);
        }
        return null;
    }
    
    public static Message checkFundGroup(LedgerBalance ledgerBalance, String... validFundGroupCodes) {
        Account account = ledgerBalance.getAccount();
        if (account.getSubFundGroup().getFundGroupCode() == null) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_FUND_GROUP_NOT_FOUND, Message.TYPE_FATAL);
        }
        return null;
    }
        
    public static Message checkSubFundGroup(LedgerBalance ledgerBalance, String... validSubFundGroupCodes) {
        Account account = ledgerBalance.getAccount();
        if (account.getSubFundGroup().getSubFundGroupCode() == null) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_FUND_GROUP_NOT_FOUND, Message.TYPE_FATAL);
        }
        return null;
    }
    
    public static Message checkObjectCode(LedgerBalance ledgerBalance, LaborObjectKeyFieldMap laborObjectKeyFieldMap) {
        Integer fiscalYear = ledgerBalance.getUniversityFiscalYear();
        String chartOfAccountsCode = ledgerBalance.getChartOfAccountsCode();
        String objectCode = ledgerBalance.getFinancialObjectCode();
    
        if (laborObjectKeyFieldMap.contains(objectCode, chartOfAccountsCode, fiscalYear)) {
            return MessageBuilder.buildErrorMessage(EffortKeyConstants.ERROR_FUND_GROUP_NOT_FOUND, Message.TYPE_FATAL);
        }
        return null;
    }
    
    public static boolean isZeroAmountBalanceWithinReportPeriod(LedgerBalance ledgerBalance, Map<Integer, Set<String>> reportPeriods) {
        KualiDecimal totalAmount = calculateTotalAmountWithinReportPeriod(ledgerBalance, reportPeriods);
        return totalAmount.isZero();
    }
    
    public static KualiDecimal calculateTotalAmountWithinReportPeriod(LedgerBalance ledgerBalance, Map<Integer, Set<String>> reportPeriods) {
        Integer fiscalYear = ledgerBalance.getUniversityFiscalYear();
        KualiDecimal totalAmount = KualiDecimal.ZERO;

        Set<String> periodCodes = reportPeriods.get(fiscalYear);
        for (String period : periodCodes) {
            totalAmount.add(ledgerBalance.getAmountByPeriod(period));
        }        
        return totalAmount;
    }
}

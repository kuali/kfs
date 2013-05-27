/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ec.document.validation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortKeyConstants;
import org.kuali.kfs.module.ec.util.LedgerBalanceConsolidationHelper;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * The validator provides a set of facilities to determine whether the given ledger balances meet the specified requirements. As a
 * pattern, null would be returned if the requirements are met; otherwise, return an error message.
 */
public class LedgerBalanceFieldValidator {

    /**
     * check if the given ledger balance has an account qualified for effort reporting
     *
     * @param ledgerBalance the given ledger balance
     * @return null if the given ledger balance has an account qualified for effort reporting; otherwise, a message
     */
    public static Message hasValidAccount(LaborLedgerBalance ledgerBalance) {
        if (ObjectUtils.isNull(ledgerBalance.getAccount())) {
            String account = new StringBuilder(ledgerBalance.getChartOfAccountsCode()).append(EffortConstants.VALUE_SEPARATOR).append(ledgerBalance.getAccountNumber()).toString();
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_ACCOUNT_NUMBER_NOT_FOUND, account);
        }
        return null;
    }

    /**
     * detetermine if the fund group code associated with the given ledger balance is in the given fund group codes
     *
     * @param ledgerBalance the given ledger balance
     * @param fundGroupCodes the given fund group codes
     * @return null if the fund group code associated with the given ledger balance is in the given fund group codes; otherwise, a
     *         message
     */
    public static Message isInFundGroups(LaborLedgerBalance ledgerBalance, List<String> fundGroupCodes) {
        SubFundGroup subFundGroup = getSubFundGroup(ledgerBalance);

        if (ObjectUtils.isNull(subFundGroup) || !fundGroupCodes.contains(subFundGroup.getFundGroupCode())) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_FUND_GROUP_NOT_FOUND, subFundGroup.getFundGroupCode());
        }
        return null;
    }

    /**
     * detetermine if the sub fund group code associated with the given ledger balance is in the given sub fund group codes
     *
     * @param ledgerBalance the given ledger balance
     * @param subFundGroupCodes the given sub fund group codes
     * @return null if the sub fund group code associated with the given ledger balance is in the given sub fund group codes;
     *         otherwise, an error message
     */
    public static Message isInSubFundGroups(LaborLedgerBalance ledgerBalance, List<String> subFundGroupCodes) {
        SubFundGroup subFundGroup = getSubFundGroup(ledgerBalance);

        if (ObjectUtils.isNull(subFundGroup) || !subFundGroupCodes.contains(subFundGroup.getSubFundGroupCode())) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_FUND_GROUP_NOT_FOUND, subFundGroup.getSubFundGroupCode());
        }
        return null;
    }

    /**
     * determine if the total amount within the specified periods of the given ledger balance is ZERO
     *
     * @param ledgerBalance the given ledger balance
     * @param reportPeriods the specified periods
     * @return null the total amount within the specified periods of the given ledger balance is NOT ZERO; otherwise, a message
     *         message
     */
    public static Message isNonZeroAmountBalanceWithinReportPeriod(LaborLedgerBalance ledgerBalance, Map<Integer, Set<String>> reportPeriods) {
        KualiDecimal totalAmount = LedgerBalanceConsolidationHelper.calculateTotalAmountWithinReportPeriod(ledgerBalance, reportPeriods, false);

        if (totalAmount.isZero()) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_ZERO_PAYROLL_AMOUNT, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * determine if the total amount within the specified periods of the given ledger balances is positive
     *
     * @param ledgerBalance the given ledger balance
     * @param reportPeriods the specified periods
     * @return null the total amount within the specified periods of the given ledger balance is positive; otherwise, a message
     *         message
     */
    public static Message isTotalAmountPositive(Collection<LaborLedgerBalance> ledgerBalances, Map<Integer, Set<String>> reportPeriods) {
        KualiDecimal totalAmount = LedgerBalanceConsolidationHelper.calculateTotalAmountWithinReportPeriod(ledgerBalances, reportPeriods, false);

        if (!totalAmount.isPositive()) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_NONPOSITIVE_PAYROLL_AMOUNT, totalAmount.toString());
        }
        return null;
    }

    /**
     * check if there is at least one account of the given ledger balances that has a fund group code or subfund group code that is
     * in the specifed group codes. If fundGroupDenotesCGIndictor is ture, only examine the fund group code associated with the
     * ledger balances; otherwise, the sub fund group code.
     *
     * @param ledgerBalances the given ledger balances
     * @return null if one of the group codes associated with the ledger balances is in the specified codes; otherwise, a message
     *         message
     */
    public static Message hasGrantAccount(Collection<LaborLedgerBalance> ledgerBalances) {
        for (LaborLedgerBalance balance : ledgerBalances) {
            if (balance.getAccount().isForContractsAndGrants()) {
                return null;
            }
        }

        return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_NOT_PAID_BY_GRANT_ACCOUNT, Message.TYPE_FATAL);
    }

    /**
     * determine whether there is at least one account of the given ledger balances that is funded by a federal grant. The award
     * associated with the account must be one of the given federal agency types or have an enabled federal pass through flag.
     *
     * @param the given labor ledger balances
     * @param federalAgencyTypeCodes the given federal agency type codes
     * @return null if there is at least one account with federal funding; otherwise, a message
     */
    public static Message hasFederalFunds(Collection<LaborLedgerBalance> ledgerBalances, Collection<String> federalAgencyTypeCodes) {
        for (LaborLedgerBalance balance : ledgerBalances) {
            Account account = balance.getAccount();
            if (SpringContext.getBean(ContractsAndGrantsModuleService.class).isAwardedByFederalAgency(account.getChartOfAccountsCode(), account.getAccountNumber(),new ArrayList<String>(federalAgencyTypeCodes))) {
                return null;
            }
        }
        return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_NOT_PAID_BY_FEDERAL_FUNDS, Message.TYPE_FATAL);
    }

    /**
     * determine if the given ledger balances have the accounts that belong to multiple organizations
     *
     * @param ledgerBalance the given ledger balance
     * @return null if the given ledger balances have the accounts that belong to a single organization; otherwise, a message
     */
    public static Message isFromSingleOrganization(Collection<LaborLedgerBalance> ledgerBalances) {
        Organization tempOrganization = null;

        boolean isFirstTime = true;
        for (LaborLedgerBalance balance : ledgerBalances) {
            Organization organization = balance.getAccount().getOrganization();

            if (isFirstTime) {
                tempOrganization = organization;
                isFirstTime = false;
            }

            if (!organization.equals(tempOrganization)) {
                return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_MULTIPLE_ORGANIZATIONS_FOUND, Message.TYPE_FATAL);
            }
        }
        return null;
    }

    /**
     * get the sub fund group associated with the given ledger balance
     *
     * @param ledgerBalance the given ledger balance
     * @return the sub fund group associated with the given ledger balance
     */
    public static SubFundGroup getSubFundGroup(LaborLedgerBalance ledgerBalance) {
        SubFundGroup subFundGroup = null;
        try {
            subFundGroup = ledgerBalance.getAccount().getSubFundGroup();
        }
        catch (NullPointerException npe) {
            return null;
        }
        return subFundGroup;
    }
}

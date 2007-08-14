/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.service.impl;

import java.util.Calendar;
import java.util.HashMap;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.financial.exceptions.InvalidFlexibleOffsetException;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.bo.OriginEntry;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements FlexibleOffsetAccountService.
 */
@Transactional
public class FlexibleOffsetAccountServiceImpl implements FlexibleOffsetAccountService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FlexibleOffsetAccountServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private AccountService accountService;
    private ObjectCodeService objectCodeService;
    private DateTimeService dateTimeService;

    /**
     * @see FlexibleOffsetAccountService#getByPrimaryIdIfEnabled
     */
    public OffsetAccount getByPrimaryIdIfEnabled(String chartOfAccountsCode, String accountNumber, String financialOffsetObjectCode) {
        LOG.debug("getByPrimaryIdIfEnabled() started");

        if (!getEnabled()) {
            return null;
        }
        HashMap keys = new HashMap();
        keys.put("chartOfAccountsCode", chartOfAccountsCode);
        keys.put("accountNumber", accountNumber);
        keys.put("financialOffsetObjectCode", financialOffsetObjectCode);
        return (OffsetAccount) businessObjectService.findByPrimaryKey(OffsetAccount.class, keys);
    }

    /**
     * @see FlexibleOffsetAccountService#getEnabled
     */
    public boolean getEnabled() {
        LOG.debug("getEnabled() started");

        // KualiConfigurationService needs to be gotten dynamically here so TransferOfFundsDocumentRuleTest can mock it.
        return SpringContext.getBean(KualiConfigurationService.class).getApplicationParameterIndicator(KFSConstants.ParameterGroups.SYSTEM, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG);
    }

    /**
     * 
     * @see org.kuali.module.financial.service.FlexibleOffsetAccountService#updateOffset(org.kuali.module.gl.bo.OriginEntry)
     */
    public boolean updateOffset(OriginEntry originEntry) {
        LOG.debug("setBusinessObjectService() started");

        if (!getEnabled()) {
            return false;
        }
        String keyOfErrorMessage = "";

        Integer fiscalYear = originEntry.getUniversityFiscalYear();
        String chartOfAccountsCode = originEntry.getChartOfAccountsCode();
        String accountNumber = originEntry.getAccountNumber();

        String balanceTypeCode = originEntry.getFinancialBalanceTypeCode();
        String documentTypeCode = originEntry.getFinancialDocumentTypeCode();

        // do nothing if there is no the offset account with the given chart of accounts code,
        // account number and offset object code in the offset table.
        OffsetAccount flexibleOffsetAccount = getByPrimaryIdIfEnabled(chartOfAccountsCode, accountNumber, originEntry.getFinancialObjectCode());
        if (flexibleOffsetAccount == null) {
            return false;
        }

        String offsetAccountNumber = flexibleOffsetAccount.getFinancialOffsetAccountNumber();
        String offsetChartOfAccountsCode = flexibleOffsetAccount.getFinancialOffsetChartOfAccountCode();

        Account offsetAccount = accountService.getByPrimaryId(offsetChartOfAccountsCode, offsetAccountNumber);
        if (offsetAccount == null) {
            throw new InvalidFlexibleOffsetException("Invalid Flexible Offset Account " + offsetChartOfAccountsCode + "-" + offsetAccountNumber);
        }

        // Can't be closed and can't be expired
        if (offsetAccount.isAccountClosedIndicator()) {
            throw new InvalidFlexibleOffsetException("Closed Flexible Offset Account " + offsetChartOfAccountsCode + "-" + offsetAccountNumber);
        }
        if ((offsetAccount.getAccountExpirationDate() != null) && isExpired(offsetAccount, dateTimeService.getCurrentCalendar())) {
            throw new InvalidFlexibleOffsetException("Expired Flexible Offset Account " + offsetChartOfAccountsCode + "-" + offsetAccountNumber);
        }

        // If the chart changes, make sure the object code is still valid
        if (!chartOfAccountsCode.equals(offsetChartOfAccountsCode)) {
            ObjectCode objectCode = objectCodeService.getByPrimaryId(fiscalYear, offsetChartOfAccountsCode, originEntry.getFinancialObjectCode());
            if (objectCode == null) {
                throw new InvalidFlexibleOffsetException("Invalid Object Code for flexible offset " + fiscalYear + "-" + offsetChartOfAccountsCode + "-" + originEntry.getFinancialObjectCode());
            }
        }

        // replace the chart and account of the given transaction with those of the offset account obtained above
        originEntry.setAccount(offsetAccount);
        originEntry.setAccountNumber(offsetAccountNumber);
        originEntry.setChartOfAccountsCode(offsetChartOfAccountsCode);

        // blank out the sub account and sub object since the account has been replaced
        originEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        originEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        return true;
    }

    private boolean isExpired(Account account, Calendar runCalendar) {

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTimeInMillis(account.getAccountExpirationDate().getTime());

        int expirationYear = expirationDate.get(Calendar.YEAR);
        int runYear = runCalendar.get(Calendar.YEAR);
        int expirationDoy = expirationDate.get(Calendar.DAY_OF_YEAR);
        int runDoy = runCalendar.get(Calendar.DAY_OF_YEAR);

        return (expirationYear < runYear) || (expirationYear == runYear && expirationDoy < runDoy);
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}

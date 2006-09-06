/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service.impl;

import java.util.Calendar;
import java.util.HashMap;

import org.kuali.Constants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.financial.exceptions.InvalidFlexibleOffsetException;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.bo.OriginEntry;

/**
 * This class implements FlexibleOffsetAccountService.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class FlexibleOffsetAccountServiceImpl implements FlexibleOffsetAccountService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FlexibleOffsetAccountServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private KualiConfigurationService kualiConfigurationService;
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

        return kualiConfigurationService.getApplicationParameterIndicator(Constants.ParameterGroups.SYSTEM, Constants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG);
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
        originEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        originEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
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

    // This shouldn't be here
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}

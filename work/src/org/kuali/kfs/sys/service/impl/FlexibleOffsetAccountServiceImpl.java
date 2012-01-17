/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.sys.service.impl;

import java.util.Calendar;
import java.util.HashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.businessobject.OffsetAccount;
import org.kuali.kfs.gl.businessobject.FlexibleAccountUpdateable;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.exception.InvalidFlexibleOffsetException;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This is the default implementation of the FlexibleOffsetAccountService interface.
 */
public class FlexibleOffsetAccountServiceImpl implements FlexibleOffsetAccountService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FlexibleOffsetAccountServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private AccountService accountService;
    private ObjectCodeService objectCodeService;
    private DateTimeService dateTimeService;
    private ParameterService parameterService;

    /**
     * This method uses the parameters provided to retrieve an OffsetAccount instance if the flexible offset account flag is
     * enabled.
     * 
     * @param chartOfAccountsCode The chart code used to retrieve the flexible offset account.
     * @param accountNumber The account number of the flexible offset account being retrieved.
     * @param financialOffsetObjectCode The offset object code used to retrieve the offset account.
     * @return A flexible offset account based on the parameters provided, or null if offsets are not enabled.
     * 
     * @see FlexibleOffsetAccountService#getByPrimaryIdIfEnabled
     */
    public OffsetAccount getByPrimaryIdIfEnabled(String chartOfAccountsCode, String accountNumber, String financialOffsetObjectCode) {
        LOG.debug("getByPrimaryIdIfEnabled() started");

        if (!getEnabled()) {
            return null;
        }
        HashMap<String,Object> keys = new HashMap();
        keys.put("chartOfAccountsCode", chartOfAccountsCode);
        keys.put("accountNumber", accountNumber);
        keys.put("financialOffsetObjectCode", financialOffsetObjectCode);
        return (OffsetAccount) businessObjectService.findByPrimaryKey(OffsetAccount.class, keys);
    }

    /**
     * This method queries the parameter table to retrieve the value of the flexible offset flag and returns the resulting value.
     * 
     * @return True if flexible offsets are enabled, false otherwise. 
     * 
     * @see FlexibleOffsetAccountService#getEnabled
     */
    public boolean getEnabled() {
        LOG.debug("getEnabled() started");
        return parameterService.getParameterValueAsBoolean(OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG);
    }

    /**
     * This method modifies the origin entry provided with values from the associated flexible offset account, which is 
     * retrieved from the database using values provided by the origin entry.
     * 
     * @param originEntry The origin entry to be updated with offset account details.
     * @return False if the flexible offset flag is false, if there is no corresponding flexbile offset account, true otherwise.
     * 
     * @see org.kuali.kfs.sys.service.FlexibleOffsetAccountService#updateOffset(org.kuali.kfs.gl.businessobject.OriginEntryFull)
     */
    public boolean updateOffset(FlexibleAccountUpdateable transaction) {
        LOG.debug("setBusinessObjectService() started");

        if (!getEnabled()) {
            return false;
        }
        String keyOfErrorMessage = "";

        Integer fiscalYear = transaction.getUniversityFiscalYear();
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        String accountNumber = transaction.getAccountNumber();

        String balanceTypeCode = transaction.getFinancialBalanceTypeCode();
        String documentTypeCode = transaction.getFinancialDocumentTypeCode();

        // do nothing if there is no the offset account with the given chart of accounts code,
        // account number and offset object code in the offset table.
        OffsetAccount flexibleOffsetAccount = getByPrimaryIdIfEnabled(chartOfAccountsCode, accountNumber, transaction.getFinancialObjectCode());
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
        if (!offsetAccount.isActive()) {
            throw new InvalidFlexibleOffsetException("Closed Flexible Offset Account " + offsetChartOfAccountsCode + "-" + offsetAccountNumber);
        }
        if ((offsetAccount.getAccountExpirationDate() != null) && isExpired(offsetAccount, dateTimeService.getCurrentCalendar())) {
            throw new InvalidFlexibleOffsetException("Expired Flexible Offset Account " + offsetChartOfAccountsCode + "-" + offsetAccountNumber);
        }

        // If the chart changes, make sure the object code is still valid
        if (!chartOfAccountsCode.equals(offsetChartOfAccountsCode)) {
            ObjectCode objectCode = objectCodeService.getByPrimaryId(fiscalYear, offsetChartOfAccountsCode, transaction.getFinancialObjectCode());
            if (objectCode == null) {
                throw new InvalidFlexibleOffsetException("Invalid Object Code for flexible offset " + fiscalYear + "-" + offsetChartOfAccountsCode + "-" + transaction.getFinancialObjectCode());
            }
        }

        // replace the chart and account of the given transaction with those of the offset account obtained above
        transaction.setAccount(offsetAccount);
        transaction.setAccountNumber(offsetAccountNumber);
        transaction.setChartOfAccountsCode(offsetChartOfAccountsCode);

        // blank out the sub account and sub object since the account has been replaced
        transaction.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        transaction.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        return true;
    }

    /**
     * This method determines if an account has expired.  An account has expired if the expiration year of the account is 
     * less than the run date year or if the date of expiration occurred before the run date provided.
     * 
     * @param account The account to be examined.
     * @param runCalendar The date the expiration date is tested against.
     * @return True if the account has expired, false otherwise.
     */
    protected boolean isExpired(Account account, Calendar runCalendar) {

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTimeInMillis(account.getAccountExpirationDate().getTime());

        int expirationYear = expirationDate.get(Calendar.YEAR);
        int runYear = runCalendar.get(Calendar.YEAR);
        int expirationDay = expirationDate.get(Calendar.DAY_OF_YEAR);
        int runDay = runCalendar.get(Calendar.DAY_OF_YEAR);

        return (expirationYear < runYear) || (expirationYear == runYear && expirationDay < runDay);
    }

    /**
     * Sets the local dateTimeService attribute.
     * @param dateTimeService The DateTimeService instance to be set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the local accountService attribute.
     * @param accountService The AccountService instance to be set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Sets the local objectCodeService attribute.
     * @param objectCodeService The ObjectCodeService instance to be set.
     */
    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    /**
     * Sets the local businessObjectService attribute.
     * @param businessObjectService The BusinessObjectService instance to be set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the local parameterService attribute.
     * @param parameterService The ParameterService instance to be set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}

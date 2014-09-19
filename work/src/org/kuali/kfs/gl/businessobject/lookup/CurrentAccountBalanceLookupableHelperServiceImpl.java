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
package org.kuali.kfs.gl.businessobject.lookup;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.BalanceCalculator;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.CurrentAccountBalance;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Add a current balance inquiry menu item to KFS main menu, an extension of KualiLookupableImpl to support
 * account balance lookups
 */
public class CurrentAccountBalanceLookupableHelperServiceImpl extends AbstractGeneralLedgerLookupableHelperServiceImpl {

    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CurrentAccountBalanceLookupableHelperServiceImpl.class);

    private final static String PRINCIPAL_ID_KEY = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ACCOUNT_FISCAL_OFFICER_SYSTEM_IDENTIFIER;
    private final static String PRINCIPAL_NAME_KEY = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ACCOUNT_FISCAL_OFFICER_USER + "." + KFSPropertyConstants.PERSON_USER_ID;
    private final static String ORGANIZATION_FIELD_KEY = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ORGANIZATION_CODE;
    private final static String ACCOUNT_NUMBER_FIELD_KEY = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ACCOUNT_NUMBER;
    private final static String SUPERVISOR_PRINCIPAL_NAME_KEY = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ACCOUNT_SUPERVISORY_USER + "." + KFSPropertyConstants.PERSON_USER_ID;
    private final static String SUPERVISOR_PRINCIPAL_ID_KEY = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ACCOUNTS_SUPERVISORY_SYSTEMS_IDENTIFIER;
    private final static String MANAGER_PRINCIPAL_NAME_KEY = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ACCOUNT_MANAGER_USER + "." + KFSPropertyConstants.PERSON_USER_ID;
    private final static String MANAGER_PRINCIPAL_ID_KEY = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ACCOUNT_MANAGER_SYSTEM_IDENTIFIER;

    private BalanceCalculator postBalance;
    private BalanceService balanceService;
    private PersonService personService;
    private AccountingPeriodService accountingPeriodService;
    private AccountService accountService;
    private OptionsService optionsService;

    /**
     * This method determines how a column's value should appear on the Lookup
     * results page.
     *
     * For instance, given fiscalYear, the HtmlData would contain the year's value,
     * and also contain a hyperlink for a System Options Inquiry of the type
     * "Fiscal Year". The year would be displayed as a hyperlinked text to the search.
     *
     * @return HtmlData an object encapsulating text to be rendered on the Loookup results page.
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {

        if (StringUtils.equals(propertyName, KFSPropertyConstants.SUB_ACCOUNT_NUMBER)) {
            String subAccountNumber = (String) ObjectUtils.getPropertyValue(bo, propertyName);
            if (StringUtils.equals(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER, subAccountNumber)) {
                return super.getEmptyAnchorHtmlData();
            }
        }

        return super.getInquiryUrl(bo, propertyName);
    }

    /**
     * This method performs a search based on fieldValues and returns a list of
     * matching CurrentAccountBalance objects.
     *
     * Noteworthy, is there is no table for CurrentAccountBalance, rather results
     * are derived from matches against the Account, Balance, and Person tables.
     *
     * @param Map<String, String> the fields and correlated values that are searched against.
     * @return List<CurrentAccountBalance> the list of results that match the input fieldValues.
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get
        // search results
        String pendingEntryOption = this.getSelectedPendingEntryOption(fieldValues);

        // need to get this before getting isConsolidated because this value will be removed.
        String consolidationOption = (String) fieldValues.get(GeneralLedgerConstants.DummyBusinessObject.CONSOLIDATION_OPTION);
        // test if the consolidation option is selected or not
        boolean isConsolidated = isConsolidationSelected(fieldValues);

        // added one more node for consolidationOption
        if (consolidationOption.equals(Constant.EXCLUDE_SUBACCOUNTS)){
            fieldValues.put(Constant.SUB_ACCOUNT_OPTION, KFSConstants.getDashSubAccountNumber());
            isConsolidated = false;
        }

        Map<String, String> localFieldValues = this.getLocalFieldValues(fieldValues);

        Collection<CurrentAccountBalance> searchResultsCollection = this.buildCurrentBalanceCollection(localFieldValues, isConsolidated, pendingEntryOption);
        if(LOG.isDebugEnabled()){
            LOG.debug("searchResultsCollection.size(): " + searchResultsCollection.size());
        }

        return this.buildSearchResultList(searchResultsCollection, Long.valueOf(searchResultsCollection.size()));
    }

    /**
     * Replace principalName with principalId via a call to PersonService,
     * if possible.
     *
     * @param fieldValues key/value pairs which may contain principalName property.
     * @return A map with all the original input entries, but with principalId replacing principalName.
     */
    protected Map<String, String> getLocalFieldValues(Map<String, String> fieldValues) {
        Map<String, String> localFieldValues = new HashMap<String, String>();
        localFieldValues.putAll(fieldValues);

        String principalName = fieldValues.get(PRINCIPAL_NAME_KEY);
        if (StringUtils.isNotBlank(principalName)) {
            localFieldValues.remove(PRINCIPAL_NAME_KEY);

            Person person = personService.getPersonByPrincipalName(principalName);
            if (ObjectUtils.isNotNull(person)) {
                localFieldValues.put(PRINCIPAL_ID_KEY, person.getPrincipalId());
            }
            else {
                localFieldValues.put(PRINCIPAL_ID_KEY, principalName);
            }

        }
        String supervisorPrincipalName = fieldValues.get(SUPERVISOR_PRINCIPAL_NAME_KEY);
        if (StringUtils.isNotBlank(supervisorPrincipalName)) {
            localFieldValues.remove(SUPERVISOR_PRINCIPAL_NAME_KEY);
            Person person = personService.getPersonByPrincipalName(supervisorPrincipalName);
            if (ObjectUtils.isNotNull(person)) {
                localFieldValues.put(SUPERVISOR_PRINCIPAL_ID_KEY, person.getPrincipalId());
            }
            else {
                localFieldValues.put(SUPERVISOR_PRINCIPAL_ID_KEY, supervisorPrincipalName);
            }
        }

        String managerPrincipalName = fieldValues.get(MANAGER_PRINCIPAL_NAME_KEY);
        if (StringUtils.isNotBlank(managerPrincipalName)) {
            localFieldValues.remove(MANAGER_PRINCIPAL_NAME_KEY);
            Person person = personService.getPersonByPrincipalName(managerPrincipalName);
            if (ObjectUtils.isNotNull(person)) {
                localFieldValues.put(MANAGER_PRINCIPAL_ID_KEY, person.getPrincipalId());
            }
            else {
                localFieldValues.put(MANAGER_PRINCIPAL_ID_KEY, managerPrincipalName);
            }
        }

        return localFieldValues;
    }


    /**
     * Build a search result list based on the given criteria.
     *
     * @param fieldValues Map of entries that will be used in the search for matching results.
     * @param isConsolidated A property indicating whether subAccounts should be consolidated into one record or considered individually.
     * @param pendingEntryOption A property that indicates if the General Ledger Pending Entry table should be searched too.
     * @return
     */
    protected Collection<CurrentAccountBalance> buildCurrentBalanceCollection(Map<String, String> fieldValues, boolean isConsolidated, String pendingEntryOption) {
        String fiscalPeriod = fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);

        Map<String, CurrentAccountBalance> balanceMap = new HashMap<String, CurrentAccountBalance>();

        Collection<Balance> balances = this.getQualifiedBalances(fieldValues, pendingEntryOption);

        for (Balance balance : balances) {
            if (StringUtils.isBlank(balance.getSubAccountNumber())) {
                balance.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }

            String key = balance.getAccountNumber();
            if (!isConsolidated) {
                key = key + ":" + balance.getSubAccountNumber();
            }

            if (balanceMap.containsKey(key)) {
                CurrentAccountBalance currentBalance = balanceMap.get(key);
                this.updateCurrentBalance(currentBalance, balance, fiscalPeriod);
            }
            else {
                CurrentAccountBalance currentBalance = new CurrentAccountBalance();
                ObjectUtil.buildObject(currentBalance, balance);
                currentBalance.resetAmounts();

                this.updateCurrentBalance(currentBalance, balance, fiscalPeriod);
                balanceMap.put(key, currentBalance);
            }
        }

        Collection<CurrentAccountBalance> currentBalanceList = balanceMap.values();

        return currentBalanceList;
    }


    /**
     * This method retrieves qualified Balance records. If pending entries are
     * needed, they can be included.
     *
     * @param fieldValues The properties and correlated values that will be used in searching for results.
     * @param pendingEntryOption A property to indicate if the General Ledger Pending Entry table should be part of this search.
     * @return A list of Balance objects that meet the input criteria.
     */
    protected Collection<Balance> getQualifiedBalances(Map<String, String> fieldValues, String pendingEntryOption) {
        boolean isConsolidated = false;

        Collection<Balance> balanceList = this.getLookupService().findCollectionBySearchUnbounded(Balance.class, fieldValues);

        updateByPendingLedgerEntry(balanceList, fieldValues, pendingEntryOption, isConsolidated, false);

        return balanceList;
    }


    /**
     * This method updates the current balance with the given balance for the specified period.
     *
     * @param currentBalance The object to update.
     * @param balance The object that contains the updated information.
     * @param fiscalPeriod The period that the balance will accumulate through, inclusive.
     */
    protected void updateCurrentBalance(CurrentAccountBalance currentBalance, Balance balance, String fiscalPeriod) {
        Collection<String> cashBudgetRecordLevelCodes = this.getParameterService().getParameterValuesAsString(CurrentAccountBalance.class, KFSParameterKeyConstants.GlParameterConstants.CASH_BUDGET_RECORD_LEVEL);
        Collection<String> expenseObjectTypeCodes = this.getParameterService().getParameterValuesAsString(CurrentAccountBalance.class, KFSParameterKeyConstants.GlParameterConstants.EXPENSE_OBJECT_TYPE);
        Collection<String> fundBalanceObjCodes = this.getParameterService().getParameterValuesAsString(CurrentAccountBalance.class, KFSParameterKeyConstants.GlParameterConstants.FUND_BALANCE_OBJECT_CODE);
        Collection<String> currentAssetObjCodes = this.getParameterService().getParameterValuesAsString(CurrentAccountBalance.class, KFSParameterKeyConstants.GlParameterConstants.CURRENT_ASSET_OBJECT_CODE);
        Collection<String> currentLiabilityObjCodes = this.getParameterService().getParameterValuesAsString(CurrentAccountBalance.class, KFSParameterKeyConstants.GlParameterConstants.CURRENT_LIABILITY_OBJECT_CODE);
        Collection<String> incomeObjTypeCodes = this.getParameterService().getParameterValuesAsString(CurrentAccountBalance.class, KFSParameterKeyConstants.GlParameterConstants.INCOME_OBJECT_TYPE);
        Collection<String> encumbranceBalTypes = this.getParameterService().getParameterValuesAsString(CurrentAccountBalance.class, KFSParameterKeyConstants.GlParameterConstants.ENCUMBRANCE_BALANCE_TYPE);
        String balanceTypeCode = balance.getBalanceTypeCode();
        String objectTypeCode = balance.getObjectTypeCode();
        String objectCode = balance.getObjectCode();

        SystemOptions options = optionsService.getCurrentYearOptions();
        Collection<String> assetLiabilityFundBalanceTypeCodes = Arrays.asList(options.getFinancialObjectTypeAssetsCd(),  // AS
                                                                              options.getFinObjectTypeLiabilitiesCode(), // LI
                                                                              options.getFinObjectTypeFundBalanceCd());  // FB

        Account account = balance.getAccount();
        if (ObjectUtils.isNull(account)) {
            account = getAccountService().getByPrimaryId(balance.getChartOfAccountsCode(), balance.getAccountNumber());
            balance.setAccount(account);
            currentBalance.setAccount(account);
        }

        boolean isCashBudgetRecording = cashBudgetRecordLevelCodes.contains(account.getBudgetRecordingLevelCode());
        currentBalance.setUniversityFiscalPeriodCode(fiscalPeriod);
        // Current Budget (A)
        if (isCashBudgetRecording) {
            currentBalance.setCurrentBudget(KualiDecimal.ZERO);
        }
        else {
            if (KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE.equals(balanceTypeCode) && expenseObjectTypeCodes.contains(objectTypeCode)) {
                currentBalance.setCurrentBudget(add(currentBalance.getCurrentBudget(), add(accumulateMonthlyAmounts(balance, fiscalPeriod), accumulateMonthlyAmounts(balance, KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE))));
            }
        }
        // Beginning Fund Balance (B)
        if (isCashBudgetRecording) {
            if (fundBalanceObjCodes.contains(objectCode)) {
                currentBalance.setBeginningFundBalance(add(currentBalance.getBeginningFundBalance(), accumulateMonthlyAmounts(balance, KFSConstants.PERIOD_CODE_BEGINNING_BALANCE)));
            }
        }
        else {
            currentBalance.setBeginningFundBalance(KualiDecimal.ZERO);
        }

        // Beginning Current Assets (C)
        if (isCashBudgetRecording) {
            if (currentAssetObjCodes.contains(objectCode)) {
                currentBalance.setBeginningCurrentAssets(add(currentBalance.getBeginningCurrentAssets(), accumulateMonthlyAmounts(balance, KFSConstants.PERIOD_CODE_BEGINNING_BALANCE)));
            }
        }
        else {
            currentBalance.setBeginningCurrentAssets(KualiDecimal.ZERO);
        }

        // Beginning Current Liabilities (D)
        if (isCashBudgetRecording) {
            if (currentLiabilityObjCodes.contains(objectCode)) {
                currentBalance.setBeginningCurrentLiabilities(add(currentBalance.getBeginningCurrentLiabilities(), accumulateMonthlyAmounts(balance, KFSConstants.PERIOD_CODE_BEGINNING_BALANCE)));
            }
        }
        else {
            currentBalance.setBeginningCurrentLiabilities(KualiDecimal.ZERO);
        }

        // Total Income (E)
        if (isCashBudgetRecording) {
            if (incomeObjTypeCodes.contains(objectTypeCode) && KFSConstants.BALANCE_TYPE_ACTUAL.equals(balanceTypeCode)) {
                currentBalance.setTotalIncome(add(currentBalance.getTotalIncome(), accumulateMonthlyAmounts(balance, fiscalPeriod)));
                currentBalance.setTotalIncome(add(currentBalance.getTotalIncome(), accumulateMonthlyAmounts(balance, KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE)));
            }
        }
        else {
            currentBalance.setTotalIncome(KualiDecimal.ZERO);
        }

        // Total Expense (F)
        if (expenseObjectTypeCodes.contains(objectTypeCode) && KFSConstants.BALANCE_TYPE_ACTUAL.equals(balanceTypeCode)) {
            currentBalance.setTotalExpense(add(currentBalance.getTotalExpense(), accumulateMonthlyAmounts(balance, fiscalPeriod)));
            currentBalance.setTotalExpense(add(currentBalance.getTotalExpense(), accumulateMonthlyAmounts(balance, KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE)));
        }

        // Encumbrances (G)
        if (encumbranceBalTypes.contains(balanceTypeCode) && (expenseObjectTypeCodes.contains(objectTypeCode) || incomeObjTypeCodes.contains(objectTypeCode)) && !assetLiabilityFundBalanceTypeCodes.contains(objectTypeCode)) {
            currentBalance.setEncumbrances(add(currentBalance.getEncumbrances(), accumulateMonthlyAmounts(balance, fiscalPeriod)));
        }

        // Budget Balance Available (H)
        if (isCashBudgetRecording) {
            currentBalance.setBudgetBalanceAvailable(KualiDecimal.ZERO);
        }
        else {
            currentBalance.setBudgetBalanceAvailable(currentBalance.getCurrentBudget().subtract(currentBalance.getTotalExpense()).subtract(currentBalance.getEncumbrances()));
        }

        // Cash Expenditure Authority (I)
        if (isCashBudgetRecording) {
            currentBalance.setCashExpenditureAuthority(currentBalance.getBeginningCurrentAssets().subtract(currentBalance.getBeginningCurrentLiabilities()).add(currentBalance.getTotalIncome()).subtract(currentBalance.getTotalExpense()).subtract(currentBalance.getEncumbrances()));
        }
        else {
            currentBalance.setCashExpenditureAuthority(KualiDecimal.ZERO);
        }
        // Current Fund Balance (J)
        if (isCashBudgetRecording) {
            currentBalance.setCurrentFundBalance(currentBalance.getBeginningFundBalance().add(currentBalance.getTotalIncome()).subtract(currentBalance.getTotalExpense()));
        }
        else {
            currentBalance.setCurrentFundBalance(KualiDecimal.ZERO);
        }

    }


    /**
     * This method ensures that:
     * 1. Both fiscalYear and fiscalPeriod are present and valid
     * 2. That at least one field is present from the set: {accountNumber, organizationCode, fiscalOfficerPrincipalName, supervisorPrincipalName}
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        Integer fiscalYear = 0;
        String valueFiscalYear = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        if (!StringUtils.isEmpty(valueFiscalYear)) {
            try {
                fiscalYear = Integer.parseInt(valueFiscalYear);
            }
            catch (NumberFormatException e) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSKeyConstants.PendingEntryLookupableImpl.FISCAL_YEAR_FOUR_DIGIT);
                throw new ValidationException("errors in search criteria");
            }
        }

        String accountNumber = (String) fieldValues.get(ACCOUNT_NUMBER_FIELD_KEY);
        String organizationCode = (String) fieldValues.get(ORGANIZATION_FIELD_KEY);
        String fiscalOfficerPrincipalName = (String) fieldValues.get(PRINCIPAL_NAME_KEY);
        String supervisorPrincipalName = (String) fieldValues.get(SUPERVISOR_PRINCIPAL_NAME_KEY);
        String managerPrincipalName = (String) fieldValues.get(MANAGER_PRINCIPAL_NAME_KEY);

        if (StringUtils.isBlank(accountNumber)
                && StringUtils.isBlank(organizationCode)
                && StringUtils.isBlank(fiscalOfficerPrincipalName)
                && StringUtils.isBlank(supervisorPrincipalName)
                && StringUtils.isBlank(managerPrincipalName)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_CURRBALANCE_LOOKUP_CRITERIA_REQD);
            throw new ValidationException("errors in search criteria");
        }

        String fiscalPeriod = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        AccountingPeriod accountingPeriod = accountingPeriodService.getByPeriod(fiscalPeriod, fiscalYear);
        if (ObjectUtils.isNull(accountingPeriod)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_FOUND);
            throw new ValidationException("errors in search criteria");
        }
    }


    /**
     * This method accumulates a monthly amount up to the given period.
     *
     * For instance, if one provides "03", then the total from
     * monthOneBalance + monthTwoBalance + monthThreeBalance is returned.
     *
     * @param balance The Balance object from which monthly amounts are pulled from.
     * @param fiscalPeriodCode The ending period to accumulate though to.
     * @return The accumulation of monthly balances up through the input fiscalPeriodCode.
     */
    protected KualiDecimal accumulateMonthlyAmounts(Balance balance, String fiscalPeriodCode) {

        KualiDecimal beginningAmount = balance.getBeginningBalanceLineAmount();
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.PERIOD_CODE_BEGINNING_BALANCE)) {
            return beginningAmount;
        }

        KualiDecimal CGBeginningAmount = balance.getContractsGrantsBeginningBalanceAmount();
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE)) {
            return CGBeginningAmount;
        }

        KualiDecimal month0Amount = beginningAmount;
        KualiDecimal month1Amount = add(balance.getMonth1Amount(), month0Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH1)) {
            return month1Amount;
        }

        KualiDecimal month2Amount = add(balance.getMonth2Amount(), month1Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH2)) {
            return month2Amount;
        }

        KualiDecimal month3Amount = add(balance.getMonth3Amount(), month2Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH3)) {
            return month3Amount;
        }

        KualiDecimal month4Amount = add(balance.getMonth4Amount(), month3Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH4)) {
            return month4Amount;
        }

        KualiDecimal month5Amount = add(balance.getMonth5Amount(), month4Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH5)) {
            return month5Amount;
        }

        KualiDecimal month6Amount = add(balance.getMonth6Amount(), month5Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH6)) {
            return month6Amount;
        }

        KualiDecimal month7Amount = add(balance.getMonth7Amount(), month6Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH7)) {
            return month7Amount;
        }

        KualiDecimal month8Amount = add(balance.getMonth8Amount(), month7Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH8)) {
            return month8Amount;
        }

        KualiDecimal month9Amount = add(balance.getMonth9Amount(), month8Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH9)) {
            return month9Amount;
        }

        KualiDecimal month10Amount = add(balance.getMonth10Amount(), month9Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH10)) {
            return month10Amount;
        }

        KualiDecimal month11Amount = add(balance.getMonth11Amount(), month10Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH11)) {
            return month11Amount;
        }

        KualiDecimal month12Amount = add(balance.getMonth12Amount(), month11Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH12)) {
            return month12Amount;
        }

        KualiDecimal month13Amount = add(balance.getMonth13Amount(), month12Amount);
        if (StringUtils.equals(fiscalPeriodCode, KFSConstants.MONTH13)) {
            return month13Amount;
        }

        return balance.getAccountLineAnnualBalanceAmount();
    }


    /**
     * A null-safe addition helper method.
     *
     * @param augend The first number to add.
     * @param addend The second number to add.
     * @return The sum of the two inputs.
     */
    protected KualiDecimal add(KualiDecimal augend, KualiDecimal addend) {
        KualiDecimal first = augend == null ? KualiDecimal.ZERO : augend;
        KualiDecimal second = addend == null ? KualiDecimal.ZERO : addend;

        return first.add(second);
    }


    /**
     * This method updates each Entry with its corresponding Balance object.
     *
     * @see org.kuali.kfs.gl.businessobject.lookup.AbstractGeneralLedgerLookupableHelperServiceImpl#updateEntryCollection(java.util.Collection, java.util.Map, boolean, boolean, boolean)
     */
    @Override
    protected void updateEntryCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated, boolean isCostShareInclusive) {

        // convert the field names of balance object into corresponding ones of
        // pending entry object
        Map pendingEntryFieldValues = BusinessObjectFieldConverter.convertToTransactionFieldValues(fieldValues);
        pendingEntryFieldValues.remove(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);

        // go through the pending entries to update the balance collection
        Iterator<GeneralLedgerPendingEntry> pendingEntryIterator = getGeneralLedgerPendingEntryService().findPendingLedgerEntriesForBalance(pendingEntryFieldValues, isApproved);
        while (pendingEntryIterator.hasNext()) {
            GeneralLedgerPendingEntry pendingEntry = pendingEntryIterator.next();

            Balance balance = this.getPostBalance().findBalance(entryCollection, pendingEntry);

            this.getPostBalance().updateBalance(pendingEntry, balance);
        }
    }

    /**
     * Sets the balanceService attribute value.
     *
     * @param balanceService The balanceService to set.
     */
    public void setBalanceService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /**
     * Gets the personService attribute.
     *
     * @return Returns the personService.
     */
    public PersonService getPersonService() {
        return personService;
    }

    /**
     * Sets the personService attribute value.
     *
     * @param personService The personService to set.
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Gets the postBalance attribute.
     *
     * @return Returns the postBalance.
     */
    public BalanceCalculator getPostBalance() {
        return postBalance;
    }

    /**
     * Sets the postBalance attribute value.
     *
     * @param postBalance The postBalance to set.
     */
    public void setPostBalance(BalanceCalculator postBalance) {
        this.postBalance = postBalance;
    }

    /**
     * Gets the accountingPeriodService attribute.
     *
     * @return Returns the accountingPeriodService.
     */
    public AccountingPeriodService getAccountingPeriodService() {
        return accountingPeriodService;
    }

    /**
     * Sets the accountingPeriodService attribute value.
     *
     * @param accountingPeriodService The accountingPeriodService to set.
     */
    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }

    /**
     * Gets the accountService attribute.
     *
     * @return Returns the accountService.
     */
    public AccountService getAccountService() {
        return accountService;
    }

    /**
     * Sets the accountService attribute.
     *
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Gets the optionService attribute.
     *
     * @return Returns the optionsService.
     */
    public OptionsService getOptionsService() {
        return optionsService;
    }

    /**
     * Sets the optionService attribute.
     *
     * @param The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
}

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

package org.kuali.kfs.module.ar.businessobject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is used to represent an invoice agency address detail business object.
 */
public class InvoiceAccountDetail extends PersistableBusinessObjectBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceAccountDetail.class);
    private String documentNumber;
    private Long proposalNumber;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String contractControlAccountNumber;
    private KualiDecimal budgetAmount = KualiDecimal.ZERO;
    private KualiDecimal expenditureAmount = KualiDecimal.ZERO;
    private KualiDecimal cumulativeAmount = KualiDecimal.ZERO;
    private KualiDecimal balanceAmount = KualiDecimal.ZERO;
    private KualiDecimal billedAmount = KualiDecimal.ZERO;

    private ContractsGrantsInvoiceDocument invoiceDocument;


    /**
     * Default constructor.
     */
    public InvoiceAccountDetail() {
        // to set fiscal year to current for now. To be updated when period 13 logic comes in.
        universityFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();

    }

    /**
     * Gets the universityFiscalYear attribute.
     *
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     *
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /***
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /***
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /***
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the contractControlAccountNumber attribute.
     *
     * @return Returns the contractControlAccountNumber.
     */
    public String getContractControlAccountNumber() {
        return contractControlAccountNumber;
    }


    /**
     * Sets the contractControlAccountNumber attribute value.
     *
     * @param contractControlAccountNumber The contractControlAccountNumber to set.
     */
    public void setContractControlAccountNumber(String contractControlAccountNumber) {
        this.contractControlAccountNumber = contractControlAccountNumber;
    }

    /**
     * Gets the budgetAmount attribute.
     *
     * @return Returns the budgetAmount.
     */
    public KualiDecimal getBudgetAmount() {
        return budgetAmount;
    }

    /**
     * Sets the budgetAmount attribute value.
     *
     * @param budgetAmount The budgetAmount to set.
     */

    public void setBudgetAmount(KualiDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    /**
     * Gets the expenditureAmount attribute.
     *
     * @return Returns the expenditureAmount.
     */
    public KualiDecimal getExpenditureAmount() {
        return expenditureAmount;
    }

    /**
     * Sets the expenditureAmount attribute value.
     *
     * @param expenditureAmount The expenditureAmount to set.
     */
    public void setExpenditureAmount(KualiDecimal expenditureAmount) {
        this.expenditureAmount = expenditureAmount;
    }

    /**
     * Gets the cumulativeAmount attribute.
     *
     * @return Returns the cumulativeAmount.
     */
    public KualiDecimal getCumulativeAmount() {
        return cumulativeAmount;
    }

    /**
     * Sets the cumulativeAmount attribute value.
     *
     * @param cumulativeAmount The cumulativeAmount to set.
     */

    public void setCumulativeAmount(KualiDecimal cumulativeAmount) {
        this.cumulativeAmount = cumulativeAmount;
    }

    /**
     * Gets the balanceAmount attribute.
     *
     * @return Returns the balanceAmount.
     */
    public KualiDecimal getBalanceAmount() {
        KualiDecimal total = KualiDecimal.ZERO;
        total = getBudgetAmount().subtract(getCumulativeAmount());
        return total;
    }

    /**
     * Sets the balanceAmount attribute value.
     *
     * @param balanceAmount The balanceAmount to set.
     */
    @Deprecated
    public void setBalanceAmount(KualiDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }


    /**
     * Gets the invoiceDocument attribute.
     *
     * @return Returns the invoiceDocument.
     */
    public ContractsGrantsInvoiceDocument getInvoiceDocument() {
        return invoiceDocument;
    }

    /**
     * Sets the invoiceDocument attribute value.
     *
     * @param invoiceDocument The invoiceDocument to set.
     */
    public void setInvoiceDocument(ContractsGrantsInvoiceDocument invoiceDocument) {
        this.invoiceDocument = invoiceDocument;
    }


    public KualiDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(KualiDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }


    /**
     * OJB calls this method as the first operation before this BO is inserted into the database. The field is read-only in the data
     * dictionary and so the value does not persist in the DB. So this method makes sure that the values are stored in the DB.
     *
     * @param persistenceBroker from OJB
     * @throws PersistenceBrokerException Thrown by call to super.prePersist();
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    protected void prePersist() {
        super.prePersist();

        balanceAmount = getBalanceAmount();


    }

    /**
     * OJB calls this method as the first operation before this BO is updated to the database. The field is read-only in the data
     * dictionary and so the value does not persist in the DB. So this method makes sure that the values are stored in the DB.
     *
     * @param persistenceBroker from OJB
     * @throws PersistenceBrokerException Thrown by call to super.preUpdate();
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    protected void preUpdate() {
        super.preUpdate();

        balanceAmount = getBalanceAmount();

    }

    /**
     * This method will set Budgets and Cumulative Expenditure amounts for each invoice account detail.
     *
     * @param lastBilledDate
     */
    public void setBudgetsAndCumulatives(java.sql.Date lastBilledDate, String billingFrequency, java.sql.Date awardBeginningDate) {

        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

        List<Balance> glBalances = new ArrayList<Balance>();
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();


        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getFiscalYear(awardBeginningDate);

        for (Integer i = fiscalYear; i <= currentYear; i++) {
            fiscalYears.add(i);
        }
        List<String> balanceTypeCodeList = new ArrayList<String>();
        balanceTypeCodeList.add(ArPropertyConstants.BUDGET_BALANCE_TYPE);
        balanceTypeCodeList.add(ArPropertyConstants.ACTUAL_BALANCE_TYPE);
        for (Integer eachFiscalYr : fiscalYears) {
            Map<String, Object> balanceKeys = new HashMap<String, Object>();
            balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getChartOfAccountsCode());
            balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, getAccountNumber());
            balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
            balanceKeys.put("objectTypeCode", ArPropertyConstants.EXPENSE_OBJECT_TYPE);
            balanceKeys.put(KFSPropertyConstants.BALANCE_TYPE_CODE,balanceTypeCodeList);
            glBalances.addAll(SpringContext.getBean(BusinessObjectService.class).findMatching(Balance.class, balanceKeys));
        }
        KualiDecimal budAmt = KualiDecimal.ZERO;
        KualiDecimal balAmt = KualiDecimal.ZERO;
        KualiDecimal cumAmt = KualiDecimal.ZERO;

        for (Balance bal : glBalances) {
            if (ObjectUtils.isNull(bal.getSubAccount()) || ObjectUtils.isNull(bal.getSubAccount().getA21SubAccount()) || !StringUtils.equalsIgnoreCase(bal.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
                if (bal.getBalanceTypeCode().equalsIgnoreCase(ArPropertyConstants.BUDGET_BALANCE_TYPE)) {
                    balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
                    budAmt = budAmt.add(balAmt);
                }
                else if (bal.getBalanceTypeCode().equalsIgnoreCase(ArPropertyConstants.ACTUAL_BALANCE_TYPE)) {
                    if (billingFrequency.equalsIgnoreCase(ArPropertyConstants.MONTHLY_BILLING_SCHEDULE_CODE) || billingFrequency.equalsIgnoreCase(ArPropertyConstants.QUATERLY_BILLING_SCHEDULE_CODE) || billingFrequency.equalsIgnoreCase(ArPropertyConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE) || billingFrequency.equalsIgnoreCase(ArPropertyConstants.ANNUALLY_BILLING_SCHEDULE_CODE)) {

                        cumAmt = cumAmt.add(contractsGrantsInvoiceDocumentService.retrieveAccurateBalanceAmount(lastBilledDate, bal));
                    }
                    else if (billingFrequency.equalsIgnoreCase(ArPropertyConstants.BILLED_AT_TERM)) {
                        balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
                        cumAmt = cumAmt.add(balAmt);
                    }
                    else {// For other billing frequencies
                        balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
                        cumAmt = cumAmt.add(balAmt);
                    }
                }

            }
        }
        // To set Budgets and cumulative amounts
        setBudgetAmount(budAmt);
        setCumulativeAmount(cumAmt);

    }

    /**
     * This method corrects the Current Expenditure field in case of correction process
     */
    public void correctInvoiceAccountDetailsCurrentExpenditureAmount() {
        this.setExpenditureAmount(getExpenditureAmount().negated());
        this.setInvoiceDocument(null);
    }
}

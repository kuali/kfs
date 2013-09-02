/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.gl.businessobject.Balance;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class represents a invoice detail on the customer invoice document.
 */
public class InvoiceDetail extends PersistableBusinessObjectBase {

    private Long invoiceDetailIdentifier;
    private String documentNumber;
    private String categoryCode;
    private String category;
    private KualiDecimal budget = KualiDecimal.ZERO;
    private KualiDecimal expenditures = KualiDecimal.ZERO;
    private KualiDecimal cumulative = KualiDecimal.ZERO;
    private KualiDecimal balance = KualiDecimal.ZERO;
    private KualiDecimal billed = KualiDecimal.ZERO;
    private KualiDecimal adjustedCumExpenditures = KualiDecimal.ZERO;
    private KualiDecimal adjustedBalance = KualiDecimal.ZERO;
    private boolean indirectCostIndicator;

    private ContractsGrantsInvoiceDocument invoiceDocument;


    /**
     * Gets the categoryCode attribute.
     * 
     * @return Returns the categoryCode.
     */
    public String getCategoryCode() {
        return categoryCode;
    }


    /**
     * Sets the categoryCode attribute value.
     * 
     * @param categoryCode The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }


    /**
     * Gets the invoiceDetailIdentifier attribute.
     * 
     * @return Returns the invoiceDetailIdentifier.
     */
    public Long getInvoiceDetailIdentifier() {
        return invoiceDetailIdentifier;
    }


    /**
     * Sets the invoiceDetailIdentifier attribute value.
     * 
     * @param invoiceDetailIdentifier The invoiceDetailIdentifier to set.
     */
    public void setInvoiceDetailIdentifier(Long invoiceDetailIdentifier) {
        this.invoiceDetailIdentifier = invoiceDetailIdentifier;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the category attribute.
     * 
     * @return Returns the category.
     */
    public String getCategory() {
        return category;
    }


    /**
     * Sets the category attribute value.
     * 
     * @param category The category to set.
     */
    public void setCategory(String category) {
        this.category = category;
    }


    /**
     * Gets the budget attribute.
     * 
     * @return Returns the budget.
     */
    public KualiDecimal getBudget() {
        return budget;
    }

    /**
     * Sets the budget attribute value.
     * 
     * @param budget The budget to set.
     */
    public void setBudget(KualiDecimal budget) {
        this.budget = budget;
    }

    /**
     * Gets the expenditures attribute.
     * 
     * @return Returns the expenditures.
     */
    public KualiDecimal getExpenditures() {
        return expenditures;
    }

    /**
     * Sets the expenditures attribute value.
     * 
     * @param expenditures The expenditures to set.
     */
    public void setExpenditures(KualiDecimal expenditures) {
        this.expenditures = expenditures;
    }

    /**
     * Gets the cumulative attribute.
     * 
     * @return Returns the cumulative.
     */
    public KualiDecimal getCumulative() {
        return cumulative;
    }

    /**
     * Sets the cumulative attribute value.
     * 
     * @param cumulative The cumulative to set.
     */
    public void setCumulative(KualiDecimal cumulative) {
        this.cumulative = cumulative;
    }

    /**
     * Gets the balance attribute.
     * 
     * @return Returns the balance.
     */
    public KualiDecimal getBalance() {
        // Balance = Budget-Cumulative
        KualiDecimal total = KualiDecimal.ZERO;
        total = budget.subtract(cumulative);
        return total;
    }

    /**
     * Sets the balance attribute value.
     * 
     * @param balance The balance to set.
     */
    public void setBalance(KualiDecimal balance) {
        this.balance = balance;
    }

    /**
     * Gets the billed attribute.
     * 
     * @return Returns the billed.
     */
    public KualiDecimal getBilled() {
        return billed;
    }

    /**
     * Sets the billed attribute value.
     * 
     * @param billed The billed to set.
     */
    public void setBilled(KualiDecimal billed) {
        this.billed = billed;
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

    /**
     * Gets the indirectCostIndicator attribute.
     * 
     * @return Returns the indirectCostIndicator.
     */
    public boolean isIndirectCostIndicator() {
        return indirectCostIndicator;
    }


    /**
     * Sets the indirectCostIndicator attribute value.
     * 
     * @param indirectCostIndicator The indirectCostIndicator to set.
     */
    public void setIndirectCostIndicator(boolean indirectCostIndicator) {
        this.indirectCostIndicator = indirectCostIndicator;
    }


    /**
     * Gets the adjustedCumExpenditures attribute.
     * 
     * @return Returns the adjustedCumExpenditures.
     */
    public KualiDecimal getAdjustedCumExpenditures() {
        KualiDecimal total = KualiDecimal.ZERO;
        total = billed.add(expenditures);
        return total;
    }

    /**
     * Sets the adjustedCumExpenditures attribute value.
     * 
     * @param adjustedCumExpenditures The adjustedCumExpenditures to set.
     */
    public void setAdjustedCumExpenditures(KualiDecimal adjustedCumExpenditures) {
        this.adjustedCumExpenditures = adjustedCumExpenditures;
    }


    /**
     * Gets the adjustedBalance attribute.
     * 
     * @return Returns the adjustedBalance.
     */
    public KualiDecimal getAdjustedBalance() {
        KualiDecimal total = KualiDecimal.ZERO;
        total = budget.subtract(this.getAdjustedCumExpenditures());
        return total;
    }

    /**
     * Sets the adjustedBalance attribute value.
     * 
     * @param adjustedBalance The adjustedBalance to set.
     */
    public void setAdjustedBalance(KualiDecimal adjustedBalance) {
        this.adjustedBalance = adjustedBalance;
    }


    /**
     * The budget calculations for individual Invoice Detail object are calculated here. Values are retrieved from GL Balance table
     * and manipulated.
     * 
     * @param awardAccounts - accounts for a particular award.
     * @param objectCodes - set of object codes pertaining to a single category
     */
    public void performBudgetCalculations(List<ContractsAndGrantsCGBAwardAccount> awardAccounts, Set<String> completeObjectCodeArrayForSingleCategory, Date awardBeginningDate) {
        KualiDecimal budAmt = KualiDecimal.ZERO;
        KualiDecimal balAmt = KualiDecimal.ZERO;
        for (ContractsAndGrantsCGBAwardAccount awardAccount : awardAccounts) {
            // To retrieve the complete set of object codes and then categorize them based on object codes and BalanceType
            List<Balance> glBalances = new ArrayList<Balance>();
            Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
            List<Integer> fiscalYears = new ArrayList<Integer>();
            Calendar c = Calendar.getInstance();


            Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getFiscalYear(awardBeginningDate);

            for (Integer i = fiscalYear; i <= currentYear; i++) {
                fiscalYears.add(i);
            }

            for (Integer eachFiscalYr : fiscalYears) {
                Map<String, Object> balanceKeys = new HashMap<String, Object>();
                balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
                balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
                balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
                balanceKeys.put("balanceTypeCode", ArPropertyConstants.BUDGET_BALANCE_TYPE);
                balanceKeys.put("objectTypeCode", ArPropertyConstants.EXPENSE_OBJECT_TYPE);
                glBalances.addAll(SpringContext.getBean(BusinessObjectService.class).findMatching(Balance.class, balanceKeys));
            }
            Iterator<String> it = completeObjectCodeArrayForSingleCategory.iterator();
            while (it.hasNext()) {
                String obCode = it.next();

                for (Balance glBalance : glBalances) {
                    if (ObjectUtils.isNotNull(glBalance.getSubAccount()) && ObjectUtils.isNotNull(glBalance.getSubAccount().getA21SubAccount()) && ObjectUtils.isNotNull(glBalance.getSubAccount().getA21SubAccount().getSubAccountTypeCode()) &&  !StringUtils.equalsIgnoreCase(glBalance.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
                        if (glBalance.getObjectCode().equals(obCode)) {
                            balAmt = glBalance.getContractsGrantsBeginningBalanceAmount().add(glBalance.getAccountLineAnnualBalanceAmount());
                            budAmt = budAmt.add(balAmt);
                        }
                    }
                }
            }
        }


        setBudget(budAmt);// Setting current budget value here

    }

    // get the total from the total table, and put them into buckets
    public void performTotalBilledCalculation(List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilleds, Set<String> objectCodesForCategory) {
        for (AwardAccountObjectCodeTotalBilled accountObjectCodeTotalBilled : awardAccountObjectCodeTotalBilleds) {
            if (objectCodesForCategory.contains(accountObjectCodeTotalBilled.getFinancialObjectCode())) {
                setBilled(getBilled().add(accountObjectCodeTotalBilled.getTotalBilled())); // this adds up all the total billed
                                                                                           // based on object code into categories;
                                                                                           // sum for this category.
            }
        }
    }

    public void performCumulativeExpenditureCalculation(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes, Set<String> objectCodesForCategory) {
        setCumulative(KualiDecimal.ZERO); // clear
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            if (objectCodesForCategory.contains(invoiceDetailAccountObjectCode.getFinancialObjectCode())) {
                setCumulative(getCumulative().add(invoiceDetailAccountObjectCode.getCumulativeExpenditures()));
            }
        }
    }

    public void performCurrentExpenditureCalculation(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes, Set<String> objectCodesForCategory) {
        setExpenditures(KualiDecimal.ZERO); // clear
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            if (objectCodesForCategory.contains(invoiceDetailAccountObjectCode.getFinancialObjectCode())) {
                setExpenditures(getExpenditures().add(invoiceDetailAccountObjectCode.getCurrentExpenditures()));
            }
        }
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
        balance = getBalance();
        adjustedCumExpenditures = this.getAdjustedCumExpenditures();
        adjustedBalance = this.getAdjustedBalance();

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
        balance = getBalance();

    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, this.documentNumber);
        m.put("categoryCode", this.categoryCode);
        m.put("category", this.category);
        if (ObjectUtils.isNotNull(this.invoiceDetailIdentifier)) {
            m.put("invoiceDetailIdentifier", this.invoiceDetailIdentifier.toString());
        }
        if (ObjectUtils.isNotNull(this.budget)) {
            m.put("budget", this.budget.toString());
        }
        if (ObjectUtils.isNotNull(this.expenditures)) {
            m.put("expenditures", this.expenditures.toString());
        }
        if (ObjectUtils.isNotNull(this.cumulative)) {
            m.put("cumulative", this.cumulative.toString());
        }
        if (ObjectUtils.isNotNull(this.balance)) {
            m.put("balance", this.balance.toString());
        }
        if (ObjectUtils.isNotNull(this.billed)) {
            m.put("billed", this.billed.toString());
        }
        if (ObjectUtils.isNotNull(this.adjustedCumExpenditures)) {
            m.put("adjustedCumExpenditures", this.adjustedCumExpenditures.toString());
        }
        if (ObjectUtils.isNotNull(this.adjustedBalance)) {
            m.put("adjustedBalance", this.adjustedBalance.toString());
        }
        return m;
    }

    public void correctInvoiceDetailsCurrentExpenditure() {
        this.setExpenditures(getExpenditures().negated());
        this.setInvoiceDocument(null);
    }
}

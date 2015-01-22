/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent an invoice agency address detail business object.
 */
public class InvoiceAccountDetail extends PersistableBusinessObjectBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceAccountDetail.class);
    private String documentNumber;
    private Long proposalNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String contractControlAccountNumber;
    private KualiDecimal totalBudget = KualiDecimal.ZERO;
    private KualiDecimal invoiceAmount = KualiDecimal.ZERO;
    private KualiDecimal cumulativeExpenditures = KualiDecimal.ZERO;
    private KualiDecimal totalAmountBilledToDate = KualiDecimal.ZERO;

    private ContractsGrantsInvoiceDocument invoiceDocument;

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
     * Gets the totalBudget attribute.
     *
     * @return Returns the totalBudget.
     */
    public KualiDecimal getTotalBudget() {
        return totalBudget;
    }

    /**
     * Sets the totalBudget attribute value.
     *
     * @param totalBudget The totalBudget to set.
     */

    public void setTotalBudget(KualiDecimal budgetAmount) {
        this.totalBudget = budgetAmount;
    }

    /**
     * Gets the invoiceAmount attribute.
     *
     * @return Returns the invoiceAmount.
     */
    public KualiDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * Sets the invoiceAmount attribute value.
     *
     * @param invoiceAmount The invoiceAmount to set.
     */
    public void setInvoiceAmount(KualiDecimal expenditureAmount) {
        this.invoiceAmount = expenditureAmount;
    }

    /**
     * Gets the cumulativeExpenditures attribute.
     *
     * @return Returns the cumulativeExpenditures.
     */
    public KualiDecimal getCumulativeExpenditures() {
        return cumulativeExpenditures;
    }

    /**
     * Sets the cumulativeExpenditures attribute value.
     *
     * @param cumulativeExpenditures The cumulativeExpenditures to set.
     */

    public void setCumulativeExpenditures(KualiDecimal cumulativeAmount) {
        this.cumulativeExpenditures = cumulativeAmount;
    }

    /**
     * Gets the balanceAmount attribute.
     *
     * @return Returns the balanceAmount.
     */
    public KualiDecimal getBudgetRemaining() {
        KualiDecimal total = KualiDecimal.ZERO;
        total = getTotalBudget().subtract(getCumulativeExpenditures());
        return total;
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

    public KualiDecimal getTotalAmountBilledToDate() {
        return totalAmountBilledToDate;
    }

    public void setTotalAmountBilledToDate(KualiDecimal billedAmount) {
        this.totalAmountBilledToDate = billedAmount;
    }

    /**
     * Gets the adjustedCumExpenditures attribute.
     *
     * @return Returns the adjustedCumExpenditures.
     */
    public KualiDecimal getAdjustedCumExpenditures() {
        KualiDecimal total = KualiDecimal.ZERO;
        total = totalAmountBilledToDate.add(invoiceAmount);
        return total;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
}

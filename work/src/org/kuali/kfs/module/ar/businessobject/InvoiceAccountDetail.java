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

import java.util.LinkedHashMap;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

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
     * This method corrects the Current Expenditure field in case of correction process
     */
    public void correctInvoiceAccountDetailsCurrentExpenditureAmount() {
        this.setExpenditureAmount(getExpenditureAmount().negated());
        this.setInvoiceDocument(null);
    }
}

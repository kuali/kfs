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

import java.util.LinkedHashMap;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class represents a invoice detail on the customer invoice document.
 */
public class ContractsGrantsInvoiceDetail extends PersistableBusinessObjectBase {

    private Long invoiceDetailIdentifier;
    private String documentNumber;
    private String categoryCode;
    private String categoryName;
    private KualiDecimal budget = KualiDecimal.ZERO;
    private KualiDecimal expenditures = KualiDecimal.ZERO;
    private KualiDecimal cumulative = KualiDecimal.ZERO;
    private KualiDecimal billed = KualiDecimal.ZERO;
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


    public String getCategoryName() {
        return categoryName;
    }


    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, this.documentNumber);
        m.put("categoryCode", this.categoryCode);
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
        m.put("balance", getBalance().toString());
        if (ObjectUtils.isNotNull(this.billed)) {
            m.put("billed", this.billed.toString());
        }
        m.put("adjustedCumExpenditures", getAdjustedCumExpenditures().toString());
        m.put("adjustedBalance", getAdjustedBalance().toString());
        return m;
    }

    public void correctInvoiceDetailsCurrentExpenditure() {
        this.setExpenditures(getExpenditures().negated());
        this.setInvoiceDocument(null);
    }

}

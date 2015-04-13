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
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class represents a invoice detail on the customer invoice document.
 */
public class ContractsGrantsInvoiceDetail extends PersistableBusinessObjectBase {

    private Long invoiceDetailIdentifier;
    private String documentNumber;
    private String categoryCode;
    private KualiDecimal totalBudget = KualiDecimal.ZERO;
    private KualiDecimal invoiceAmount = KualiDecimal.ZERO;
    private KualiDecimal cumulativeExpenditures = KualiDecimal.ZERO;
    private KualiDecimal totalPreviouslyBilled = KualiDecimal.ZERO;
    private boolean indirectCostIndicator;

    private ContractsGrantsInvoiceDocument invoiceDocument;
    private CostCategory costCategory;

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
    public void setTotalBudget(KualiDecimal totalBudget) {
        this.totalBudget = totalBudget;
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
    public void setInvoiceAmount(KualiDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
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
    public void setCumulativeExpenditures(KualiDecimal cumulativeExpenditures) {
        this.cumulativeExpenditures = cumulativeExpenditures;
    }

    /**
     * @return Returns the budget remaining.
     */
    public KualiDecimal getBudgetRemaining() {
        // Balance = Budget-Cumulative
        KualiDecimal total = KualiDecimal.ZERO;
        total = totalBudget.subtract(cumulativeExpenditures);
        return total;
    }

    /**
     * Gets the totalPreviouslyBilled attribute.
     *
     * @return Returns the totalPreviouslyBilled.
     */
    public KualiDecimal getTotalPreviouslyBilled() {
        return totalPreviouslyBilled;
    }

    /**
     * Sets the totalPreviouslyBilled attribute value.
     *
     * @param totalPreviouslyBilled The totalPreviouslyBilled to set.
     */
    public void setTotalPreviouslyBilled(KualiDecimal totalPreviouslyBilled) {
        this.totalPreviouslyBilled = totalPreviouslyBilled;
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

    public CostCategory getCostCategory() {
        return costCategory;
    }

    public void setCostCategory(CostCategory costCategory) {
        this.costCategory = costCategory;
    }

    /**
     * @return the calculated total amount billed to date (the total previously billed minus the invoice amount)
     */
    public KualiDecimal getTotalAmountBilledToDate() {
        KualiDecimal total = KualiDecimal.ZERO;
        total = totalPreviouslyBilled.add(invoiceAmount);
        return total;
    }

    /**
     * @return the calculated amount remaining to bill (the total budget minus the amount billed to date)
     */
    public KualiDecimal getAmountRemainingToBill() {
        KualiDecimal total = KualiDecimal.ZERO;
        total = totalBudget.subtract(getTotalAmountBilledToDate());
        return total;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("categoryCode", this.categoryCode);
        if (ObjectUtils.isNotNull(this.invoiceDetailIdentifier)) {
            m.put("invoiceDetailIdentifier", this.invoiceDetailIdentifier.toString());
        }
        if (ObjectUtils.isNotNull(this.totalBudget)) {
            m.put("totalBudget", this.totalBudget.toString());
        }
        if (ObjectUtils.isNotNull(this.invoiceAmount)) {
            m.put("invoiceAmount", this.invoiceAmount.toString());
        }
        if (ObjectUtils.isNotNull(this.cumulativeExpenditures)) {
            m.put("cumulativeExpenditures", this.cumulativeExpenditures.toString());
        }
        m.put("budgetRemaining", getBudgetRemaining().toString());
        if (ObjectUtils.isNotNull(this.totalPreviouslyBilled)) {
            m.put("totalPreviouslyBilled", this.totalPreviouslyBilled.toString());
        }
        m.put("totalAmountBilledToDate", getTotalAmountBilledToDate().toString());
        m.put("amountRemainingToBill", getAmountRemainingToBill().toString());
        return m;
    }

    /**
     * Adds the values from the given ContractgsGrantsInvoiceDetail onto this one
     * @param contractsGrantsInvoiceDetail the detail to sum into this
     */
    public void sumInvoiceDetail(ContractsGrantsInvoiceDetail contractsGrantsInvoiceDetail) {
        if (null != contractsGrantsInvoiceDetail.getTotalBudget()) {
            totalBudget = totalBudget.add(contractsGrantsInvoiceDetail.getTotalBudget());
        }
        if (null != contractsGrantsInvoiceDetail.getCumulativeExpenditures()) {
            cumulativeExpenditures = cumulativeExpenditures.add(contractsGrantsInvoiceDetail.getCumulativeExpenditures());
        }
        if (null != contractsGrantsInvoiceDetail.getInvoiceAmount()) {
            invoiceAmount = invoiceAmount.add(contractsGrantsInvoiceDetail.getInvoiceAmount());
        }
        if (null != contractsGrantsInvoiceDetail.getTotalPreviouslyBilled()) {
            totalPreviouslyBilled = totalPreviouslyBilled.add(contractsGrantsInvoiceDetail.getTotalPreviouslyBilled());
        }
    }
}

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

package org.kuali.kfs.fp.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a procurement card transaction detail business object.
 */
public class ProcurementCardTransactionDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer financialDocumentTransactionLineNumber;
    private Date transactionDate;
    private String transactionReferenceNumber;
    private Date transactionPostingDate;
    private String transactionOriginalCurrencyCode;
    private String transactionBillingCurrencyCode;
    private KualiDecimal transactionOriginalCurrencyAmount;
    private BigDecimal transactionCurrencyExchangeRate;
    private KualiDecimal transactionSettlementAmount;
    private KualiDecimal transactionSalesTaxAmount;
    private boolean transactionTaxExemptIndicator;
    private boolean transactionPurchaseIdentifierIndicator;
    private String transactionPurchaseIdentifierDescription;
    private String transactionUnitContactName;
    private String transactionTravelAuthorizationCode;
    private String transactionPointOfSaleCode;
    private Date transactionCycleStartDate;
    private Date transactionCycleEndDate;
    private KualiDecimal transactionTotalAmount;

    private ProcurementCardVendor procurementCardVendor;

    private List sourceAccountingLines;
    private List targetAccountingLines;

    protected FormFile targetFile;
    /**
     * Default constructor.
     */
    public ProcurementCardTransactionDetail() {
        sourceAccountingLines = new ArrayList<ProcurementCardSourceAccountingLine>();
        targetAccountingLines = new ArrayList<ProcurementCardTargetAccountingLine>();
    }

    /**
     * @see org.kuali.rice.krad.document.TransactionalDocument#getTargetTotal()
     */
    public KualiDecimal getTargetTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        AccountingLineBase al = null;
        Iterator iter = getTargetAccountingLines().iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();

            KualiDecimal amount = al.getAmount();
            if (amount != null) {
                total = total.add(amount);
            }
        }
        return total;
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


    /**
     * Gets the financialDocumentTransactionLineNumber attribute.
     *
     * @return Returns the financialDocumentTransactionLineNumber
     */
    public Integer getFinancialDocumentTransactionLineNumber() {
        return financialDocumentTransactionLineNumber;
    }

    /**
     * Sets the financialDocumentTransactionLineNumber attribute.
     *
     * @param financialDocumentTransactionLineNumber The financialDocumentTransactionLineNumber to set.
     */
    public void setFinancialDocumentTransactionLineNumber(Integer financialDocumentTransactionLineNumber) {
        this.financialDocumentTransactionLineNumber = financialDocumentTransactionLineNumber;
    }


    /**
     * Gets the transactionDate attribute.
     *
     * @return Returns the transactionDate
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the transactionDate attribute.
     *
     * @param transactionDate The transactionDate to set.
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }


    /**
     * Gets the transactionReferenceNumber attribute.
     *
     * @return Returns the transactionReferenceNumber
     */
    public String getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    /**
     * Sets the transactionReferenceNumber attribute.
     *
     * @param transactionReferenceNumber The transactionReferenceNumber to set.
     */
    public void setTransactionReferenceNumber(String transactionReferenceNumber) {
        this.transactionReferenceNumber = transactionReferenceNumber;
    }


    /**
     * Gets the transactionPostingDate attribute.
     *
     * @return Returns the transactionPostingDate
     */
    public Date getTransactionPostingDate() {
        return transactionPostingDate;
    }

    /**
     * Sets the transactionPostingDate attribute.
     *
     * @param transactionPostingDate The transactionPostingDate to set.
     */
    public void setTransactionPostingDate(Date transactionPostingDate) {
        this.transactionPostingDate = transactionPostingDate;
    }


    /**
     * Gets the transactionOriginalCurrencyCode attribute.
     *
     * @return Returns the transactionOriginalCurrencyCode
     */
    public String getTransactionOriginalCurrencyCode() {
        return transactionOriginalCurrencyCode;
    }

    /**
     * Sets the transactionOriginalCurrencyCode attribute.
     *
     * @param transactionOriginalCurrencyCode The transactionOriginalCurrencyCode to set.
     */
    public void setTransactionOriginalCurrencyCode(String transactionOriginalCurrencyCode) {
        this.transactionOriginalCurrencyCode = transactionOriginalCurrencyCode;
    }


    /**
     * Gets the transactionBillingCurrencyCode attribute.
     *
     * @return Returns the transactionBillingCurrencyCode
     */
    public String getTransactionBillingCurrencyCode() {
        return transactionBillingCurrencyCode;
    }

    /**
     * Sets the transactionBillingCurrencyCode attribute.
     *
     * @param transactionBillingCurrencyCode The transactionBillingCurrencyCode to set.
     */
    public void setTransactionBillingCurrencyCode(String transactionBillingCurrencyCode) {
        this.transactionBillingCurrencyCode = transactionBillingCurrencyCode;
    }


    /**
     * Gets the transactionOriginalCurrencyAmount attribute.
     *
     * @return Returns the transactionOriginalCurrencyAmount
     */
    public KualiDecimal getTransactionOriginalCurrencyAmount() {
        return transactionOriginalCurrencyAmount;
    }

    /**
     * Sets the transactionOriginalCurrencyAmount attribute.
     *
     * @param transactionOriginalCurrencyAmount The transactionOriginalCurrencyAmount to set.
     */
    public void setTransactionOriginalCurrencyAmount(KualiDecimal transactionOriginalCurrencyAmount) {
        this.transactionOriginalCurrencyAmount = transactionOriginalCurrencyAmount;
    }


    /**
     * Gets the transactionCurrencyExchangeRate attribute.
     *
     * @return Returns the transactionCurrencyExchangeRate
     */
    public BigDecimal getTransactionCurrencyExchangeRate() {
        return transactionCurrencyExchangeRate;
    }

    /**
     * Sets the transactionCurrencyExchangeRate attribute.
     *
     * @param transactionCurrencyExchangeRate The transactionCurrencyExchangeRate to set.
     */
    public void setTransactionCurrencyExchangeRate(BigDecimal transactionCurrencyExchangeRate) {
        this.transactionCurrencyExchangeRate = transactionCurrencyExchangeRate;
    }


    /**
     * Gets the transactionSettlementAmount attribute.
     *
     * @return Returns the transactionSettlementAmount
     */
    public KualiDecimal getTransactionSettlementAmount() {
        return transactionSettlementAmount;
    }

    /**
     * Sets the transactionSettlementAmount attribute.
     *
     * @param transactionSettlementAmount The transactionSettlementAmount to set.
     */
    public void setTransactionSettlementAmount(KualiDecimal transactionSettlementAmount) {
        this.transactionSettlementAmount = transactionSettlementAmount;
    }


    /**
     * Gets the transactionSalesTaxAmount attribute.
     *
     * @return Returns the transactionSalesTaxAmount
     */
    public KualiDecimal getTransactionSalesTaxAmount() {
        return transactionSalesTaxAmount;
    }

    /**
     * Sets the transactionSalesTaxAmount attribute.
     *
     * @param transactionSalesTaxAmount The transactionSalesTaxAmount to set.
     */
    public void setTransactionSalesTaxAmount(KualiDecimal transactionSalesTaxAmount) {
        this.transactionSalesTaxAmount = transactionSalesTaxAmount;
    }


    /**
     * Gets the transactionTaxExemptIndicator attribute.
     *
     * @return Returns the transactionTaxExemptIndicator
     */
    public boolean getTransactionTaxExemptIndicator() {
        return transactionTaxExemptIndicator;
    }

    /**
     * Sets the transactionTaxExemptIndicator attribute.
     *
     * @param transactionTaxExemptIndicator The transactionTaxExemptIndicator to set.
     */
    public void setTransactionTaxExemptIndicator(boolean transactionTaxExemptIndicator) {
        this.transactionTaxExemptIndicator = transactionTaxExemptIndicator;
    }


    /**
     * Gets the transactionPurchaseIdentifierIndicator attribute.
     *
     * @return Returns the transactionPurchaseIdentifierIndicator
     */
    public boolean getTransactionPurchaseIdentifierIndicator() {
        return transactionPurchaseIdentifierIndicator;
    }

    /**
     * Sets the transactionPurchaseIdentifierIndicator attribute.
     *
     * @param transactionPurchaseIdentifierIndicator The transactionPurchaseIdentifierIndicator to set.
     */
    public void setTransactionPurchaseIdentifierIndicator(boolean transactionPurchaseIdentifierIndicator) {
        this.transactionPurchaseIdentifierIndicator = transactionPurchaseIdentifierIndicator;
    }


    /**
     * Gets the transactionPurchaseIdentifierDescription attribute.
     *
     * @return Returns the transactionPurchaseIdentifierDescription
     */
    public String getTransactionPurchaseIdentifierDescription() {
        return transactionPurchaseIdentifierDescription;
    }

    /**
     * Sets the transactionPurchaseIdentifierDescription attribute.
     *
     * @param transactionPurchaseIdentifierDescription The transactionPurchaseIdentifierDescription to set.
     */
    public void setTransactionPurchaseIdentifierDescription(String transactionPurchaseIdentifierDescription) {
        this.transactionPurchaseIdentifierDescription = transactionPurchaseIdentifierDescription;
    }


    /**
     * Gets the transactionUnitContactName attribute.
     *
     * @return Returns the transactionUnitContactName
     */
    public String getTransactionUnitContactName() {
        return transactionUnitContactName;
    }

    /**
     * Sets the transactionUnitContactName attribute.
     *
     * @param transactionUnitContactName The transactionUnitContactName to set.
     */
    public void setTransactionUnitContactName(String transactionUnitContactName) {
        this.transactionUnitContactName = transactionUnitContactName;
    }


    /**
     * Gets the transactionTravelAuthorizationCode attribute.
     *
     * @return Returns the transactionTravelAuthorizationCode
     */
    public String getTransactionTravelAuthorizationCode() {
        return transactionTravelAuthorizationCode;
    }

    /**
     * Sets the transactionTravelAuthorizationCode attribute.
     *
     * @param transactionTravelAuthorizationCode The transactionTravelAuthorizationCode to set.
     */
    public void setTransactionTravelAuthorizationCode(String transactionTravelAuthorizationCode) {
        this.transactionTravelAuthorizationCode = transactionTravelAuthorizationCode;
    }


    /**
     * Gets the transactionPointOfSaleCode attribute.
     *
     * @return Returns the transactionPointOfSaleCode
     */
    public String getTransactionPointOfSaleCode() {
        return transactionPointOfSaleCode;
    }

    /**
     * Sets the transactionPointOfSaleCode attribute.
     *
     * @param transactionPointOfSaleCode The transactionPointOfSaleCode to set.
     */
    public void setTransactionPointOfSaleCode(String transactionPointOfSaleCode) {
        this.transactionPointOfSaleCode = transactionPointOfSaleCode;
    }


    /**
     * Gets the sourceAccountingLines attribute.
     *
     * @return Returns the sourceAccountingLines.
     */
    public List getSourceAccountingLines() {
        return sourceAccountingLines;
    }


    /**
     * Sets the sourceAccountingLines attribute value.
     *
     * @param sourceAccountingLines The sourceAccountingLines to set.
     */
    public void setSourceAccountingLines(List sourceAccountingLines) {
        this.sourceAccountingLines = sourceAccountingLines;
    }


    /**
     * Gets the targetAccountingLines attribute.
     *
     * @return Returns the targetAccountingLines.
     */
    public List getTargetAccountingLines() {
        return targetAccountingLines;
    }


    /**
     * Sets the targetAccountingLines attribute value.
     *
     * @param targetAccountingLines The targetAccountingLines to set.
     */
    public void setTargetAccountingLines(List targetAccountingLines) {
        this.targetAccountingLines = targetAccountingLines;
    }


    /**
     * Gets the transactionCycleEndDate attribute.
     *
     * @return Returns the transactionCycleEndDate.
     */
    public Date getTransactionCycleEndDate() {
        return transactionCycleEndDate;
    }

    /**
     * Sets the transactionCycleEndDate attribute value.
     *
     * @param transactionCycleEndDate The transactionCycleEndDate to set.
     */
    public void setTransactionCycleEndDate(Date transactionCycleEndDate) {
        this.transactionCycleEndDate = transactionCycleEndDate;
    }

    /**
     * Gets the transactionCycleStartDate attribute.
     *
     * @return Returns the transactionCycleStartDate.
     */
    public Date getTransactionCycleStartDate() {
        return transactionCycleStartDate;
    }

    /**
     * Sets the transactionCycleStartDate attribute value.
     *
     * @param transactionCycleStartDate The transactionCycleStartDate to set.
     */
    public void setTransactionCycleStartDate(Date transactionCycleStartDate) {
        this.transactionCycleStartDate = transactionCycleStartDate;
    }

    /**
     * Gets the procurementCardVendor attribute.
     *
     * @return Returns the procurementCardVendor.
     */
    public ProcurementCardVendor getProcurementCardVendor() {
        return procurementCardVendor;
    }

    /**
     * Sets the procurementCardVendor attribute value.
     *
     * @param procurementCardVendor The procurementCardVendor to set.
     */
    public void setProcurementCardVendor(ProcurementCardVendor procurementCardVendor) {
        this.procurementCardVendor = procurementCardVendor;
    }

    /**
     * Gets the transactionTotalAmount attribute.
     *
     * @return Returns the transactionTotalAmount.
     */
    public KualiDecimal getTransactionTotalAmount() {
        return transactionTotalAmount;
    }

    /**
     * Sets the transactionTotalAmount attribute value.
     *
     * @param transactionTotalAmount The transactionTotalAmount to set.
     */
    public void setTransactionTotalAmount(KualiDecimal transactionTotalAmount) {
        this.transactionTotalAmount = transactionTotalAmount;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.financialDocumentTransactionLineNumber != null) {
            m.put("financialDocumentTransactionLineNumber", this.financialDocumentTransactionLineNumber.toString());
        }
        return m;
    }


    /**
     *
     * @return
     */
    public FormFile getTargetFile() {
        return targetFile;
    }

    /**
     *
     * @param targetFile
     */
    public void setTargetFile(FormFile targetFile) {
        this.targetFile = targetFile;
    }

}

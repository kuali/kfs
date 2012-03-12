/*
 * Copyright 2008-2009 The Kuali Foundation
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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class CustomerCreditMemoDetail extends PersistableBusinessObjectBase implements GeneralLedgerPendingEntrySourceDetail, AppliedPayment {

    private String documentNumber;
    private Integer referenceInvoiceItemNumber;
    private BigDecimal creditMemoItemQuantity;
    private KualiDecimal creditMemoItemTaxAmount;
    private KualiDecimal creditMemoItemTotalAmount;
    private KualiDecimal duplicateCreditMemoItemTotalAmount; // not in DB
    private KualiDecimal invoiceLineTotalAmount; // not in DB
    private KualiDecimal creditMemoLineTotalAmount; // not in DB
    private KualiDecimal invoiceOpenItemAmount; // not in DB
    private KualiDecimal invoiceOpenItemQuantity; // not in DB
    private CustomerInvoiceDetail customerInvoiceDetail; // not in DB
    private String financialDocumentReferenceInvoiceNumber; // not in DB
    private boolean invoiceOpenItemQuantityZero; // not in DB

    /**
     * Default constructor.
     */
    public CustomerCreditMemoDetail() {

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
     * Gets the referenceInvoiceItemNumber attribute.
     * 
     * @return Returns the referenceInvoiceItemNumber
     */
    public Integer getReferenceInvoiceItemNumber() {
        return referenceInvoiceItemNumber;
    }

    /**
     * Sets the referenceInvoiceItemNumber attribute.
     * 
     * @param referenceInvoiceItemNumber The referenceInvoiceItemNumber to set.
     */
    public void setReferenceInvoiceItemNumber(Integer referenceInvoiceItemNumber) {
        this.referenceInvoiceItemNumber = referenceInvoiceItemNumber;
    }


    /**
     * Gets the creditMemoItemQuantity attribute.
     * 
     * @return Returns the creditMemoItemQuantity
     */
    public BigDecimal getCreditMemoItemQuantity() {
        return creditMemoItemQuantity;
    }

    /**
     * Sets the creditMemoItemQuantity attribute.
     * 
     * @param creditMemoItemQuantity The creditMemoItemQuantity to set.
     */
    public void setCreditMemoItemQuantity(BigDecimal creditMemoItemQuantity) {
        this.creditMemoItemQuantity = creditMemoItemQuantity;
    }


    /**
     * Gets the creditMemoItemTaxAmount attribute.
     * 
     * @return Returns the creditMemoItemTaxAmount
     */
    public KualiDecimal getCreditMemoItemTaxAmount() {
        if (creditMemoItemTaxAmount == null)
            setCreditMemoItemTaxAmount(KualiDecimal.ZERO);
        return creditMemoItemTaxAmount;
    }

    /**
     * Sets the creditMemoItemTaxAmount attribute.
     * 
     * @param creditMemoItemTaxAmount The creditMemoItemTaxAmount to set.
     */
    public void setCreditMemoItemTaxAmount(KualiDecimal creditMemoItemTaxAmount) {
        if (creditMemoItemTaxAmount == null)
            creditMemoItemTaxAmount = KualiDecimal.ZERO;
        this.creditMemoItemTaxAmount = creditMemoItemTaxAmount;
    }


    /**
     * Gets the creditMemoItemTotalAmount attribute.
     * 
     * @return Returns the creditMemoItemTotalAmount
     */
    public KualiDecimal getCreditMemoItemTotalAmount() {
        return creditMemoItemTotalAmount;
    }

    /**
     * Sets the creditMemoItemTotalAmount attribute.
     * 
     * @param creditMemoItemTotalAmount The creditMemoItemTotalAmount to set.
     */
    public void setCreditMemoItemTotalAmount(KualiDecimal creditMemoItemTotalAmount) {
        this.creditMemoItemTotalAmount = creditMemoItemTotalAmount;
    }

    /**
     * Gets the invoiceOpenItemAmount attribute.
     * 
     * @return Returns the invoiceOpenItemAmount.
     */
    public KualiDecimal getInvoiceOpenItemAmount() {
        return invoiceOpenItemAmount;
    }

    /**
     * Sets the invoiceOpenItemAmount attribute value.
     * 
     * @param invoiceOpenItemAmount The invoiceOpenItemAmount to set.
     */
    public void setInvoiceOpenItemAmount(KualiDecimal invoiceOpenItemAmount) {
        this.invoiceOpenItemAmount = invoiceOpenItemAmount;
    }

    /**
     * Gets the invoiceLineTotalAmount attribute.
     * 
     * @return Returns the invoiceLineTotalAmount.
     */
    public KualiDecimal getInvoiceLineTotalAmount() {
        return invoiceLineTotalAmount;
    }

    /**
     * Sets the invoiceLineTotalAmount attribute value.
     * 
     * @param invoiceLineTotalAmount The invoiceLineTotalAmount to set.
     */
    public void setInvoiceLineTotalAmount(KualiDecimal tax, KualiDecimal invItemAmount) {
        if (invItemAmount == null)
            invItemAmount = KualiDecimal.ZERO;
        if (tax == null)
            tax = KualiDecimal.ZERO;

        this.invoiceLineTotalAmount = invItemAmount.add(tax);
    }

    /**
     * Gets the creditMemoLineTotalAmount attribute.
     * 
     * @return Returns the creditMemoLineTotalAmount.
     */
    public KualiDecimal getCreditMemoLineTotalAmount() {
        if (creditMemoLineTotalAmount == null)
            setCreditMemoLineTotalAmount(KualiDecimal.ZERO);
        return creditMemoLineTotalAmount;
    }

    /**
     * Sets the creditMemoLineTotalAmount attribute value.
     * 
     * @param creditMemoLineTotalAmount The creditMemoLineTotalAmount to set.
     */
    public void setCreditMemoLineTotalAmount(KualiDecimal creditMemoLineTotalAmount) {
        this.creditMemoLineTotalAmount = creditMemoLineTotalAmount;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.referenceInvoiceItemNumber != null) {
            m.put("referenceInvoiceItemNumber", this.referenceInvoiceItemNumber.toString());
        }
        return m;
    }

    public void setInvoiceLineTotalAmount(KualiDecimal invoiceLineTotalAmount) {
        this.invoiceLineTotalAmount = invoiceLineTotalAmount;
    }

    public void recalculateBasedOnEnteredItemQty(CustomerCreditMemoDocument customerCreditMemoDocument) {
        BigDecimal invItemUnitPrice = getCustomerInvoiceDetail().getInvoiceItemUnitPrice();

        creditMemoItemTotalAmount = new KualiDecimal(creditMemoItemQuantity.multiply(invItemUnitPrice).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR));

        if (customerCreditMemoDocument.getArTaxService().isCustomerInvoiceDetailTaxable(customerCreditMemoDocument.getInvoice(), getCustomerInvoiceDetail()))
            creditMemoItemTaxAmount = customerCreditMemoDocument.getTaxService().getTotalSalesTaxAmount(customerCreditMemoDocument.getInvoice().getBillingDate(), customerCreditMemoDocument.getPostalCode(), creditMemoItemTotalAmount);
        else
            creditMemoItemTaxAmount = KualiDecimal.ZERO;
        creditMemoLineTotalAmount = creditMemoItemTotalAmount.add(creditMemoItemTaxAmount);
    }

    public void recalculateBasedOnEnteredItemAmount(CustomerCreditMemoDocument customerCreditMemoDocument) {
        BigDecimal invItemUnitPrice = getCustomerInvoiceDetail().getInvoiceItemUnitPrice();

        KualiDecimal kulVal = creditMemoItemTotalAmount.divide(new KualiDecimal(invItemUnitPrice), true);
        if (kulVal.isZero() && creditMemoItemTotalAmount != null && !creditMemoItemTotalAmount.isZero()) {
            MathContext context = new MathContext(1);
            creditMemoItemQuantity = new BigDecimal(0.01d, context);
            creditMemoItemQuantity.setScale(2);
        }
        else {
            creditMemoItemQuantity = kulVal.bigDecimalValue();
        }

        if (customerCreditMemoDocument.getArTaxService().isCustomerInvoiceDetailTaxable(customerCreditMemoDocument.getInvoice(), getCustomerInvoiceDetail()))
            creditMemoItemTaxAmount = customerCreditMemoDocument.getTaxService().getTotalSalesTaxAmount(customerCreditMemoDocument.getInvoice().getBillingDate(), customerCreditMemoDocument.getPostalCode(), creditMemoItemTotalAmount);
        else
            creditMemoItemTaxAmount = KualiDecimal.ZERO;
        creditMemoLineTotalAmount = creditMemoItemTotalAmount.add(creditMemoItemTaxAmount);
    }

    /**
     * Gets the duplicateCreditMemoItemTotalAmount attribute.
     * 
     * @return Returns the duplicateCreditMemoItemTotalAmount.
     */
    public KualiDecimal getDuplicateCreditMemoItemTotalAmount() {
        return duplicateCreditMemoItemTotalAmount;
    }

    /**
     * Sets the duplicateCreditMemoItemTotalAmount attribute value.
     * 
     * @param duplicateCreditMemoItemTotalAmount The duplicateCreditMemoItemTotalAmount to set.
     */
    public void setDuplicateCreditMemoItemTotalAmount(KualiDecimal duplicateCreditMemoItemTotalAmount) {
        this.duplicateCreditMemoItemTotalAmount = duplicateCreditMemoItemTotalAmount;
    }

    /**
     * Gets the invoiceOpenItemQuantity attribute.
     * 
     * @return Returns the invoiceOpenItemQuantity.
     */
    public KualiDecimal getInvoiceOpenItemQuantity() {
        return invoiceOpenItemQuantity;
    }

    /**
     * Sets the invoiceOpenItemQuantity attribute value.
     * 
     * @param invoiceOpenItemQuantity The invoiceOpenItemQuantity to set.
     */
    public void setInvoiceOpenItemQuantity(KualiDecimal invoiceOpenItemQuantity) {
        this.invoiceOpenItemQuantity = invoiceOpenItemQuantity;
    }

    public String getChartOfAccountsCode() {
        return getCustomerInvoiceDetail().getChartOfAccountsCode();
    }

    public String getAccountNumber() {
        return getCustomerInvoiceDetail().getAccountNumber();
    }

    public Account getAccount() {
        getCustomerInvoiceDetail().refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        return getCustomerInvoiceDetail().getAccount();
    }

    public String getFinancialObjectCode() {
        return getCustomerInvoiceDetail().getFinancialObjectCode();
    }

    public ObjectCode getObjectCode() {
        getCustomerInvoiceDetail().refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
        return getCustomerInvoiceDetail().getObjectCode();
    }

    public String getOrganizationReferenceId() {
        return getCustomerInvoiceDetail().getOrganizationReferenceId();
    }

    public String getProjectCode() {
        return getCustomerInvoiceDetail().getProjectCode();
    }

    public String getReferenceNumber() {
        return getCustomerInvoiceDetail().getReferenceNumber();
    }

    public String getReferenceTypeCode() {
        return getCustomerInvoiceDetail().getReferenceTypeCode();
    }

    public String getReferenceOriginCode() {
        return getCustomerInvoiceDetail().getReferenceOriginCode();
    }

    public String getSubAccountNumber() {
        return getCustomerInvoiceDetail().getSubAccountNumber();
    }

    public String getFinancialSubObjectCode() {
        return getCustomerInvoiceDetail().getFinancialSubObjectCode();
    }

    public String getFinancialDocumentLineDescription() {
        return getCustomerInvoiceDetail().getFinancialDocumentLineDescription();
    }

    public KualiDecimal getAmount() {
        return getCustomerInvoiceDetail().getAmount();
    }

    public Integer getPostingYear() {
        return getCustomerInvoiceDetail().getPostingYear();
    }

    public String getBalanceTypeCode() {
        return getCustomerInvoiceDetail().getBalanceTypeCode();
    }

    public ObjectCode getAccountsReceivableObject() {
        return getCustomerInvoiceDetail().getAccountsReceivableObject();
    }

    public String getAccountsReceivableObjectCode() {
        return getCustomerInvoiceDetail().getAccountsReceivableObjectCode();
    }

    public CustomerInvoiceDetail getCustomerInvoiceDetail() {
        
        if (ObjectUtils.isNull(customerInvoiceDetail) && StringUtils.isNotEmpty(financialDocumentReferenceInvoiceNumber) && ObjectUtils.isNotNull(referenceInvoiceItemNumber)) {
            customerInvoiceDetail = SpringContext.getBean(CustomerInvoiceDetailService.class).getCustomerInvoiceDetail(financialDocumentReferenceInvoiceNumber, referenceInvoiceItemNumber);
        }
        return customerInvoiceDetail;
    }

    /**
     * Sets the customerInvoiceDetail attribute value.
     * 
     * @param customerInvoiceDetail The customerInvoiceDetail to set.
     */
    public void setCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail) {
        this.customerInvoiceDetail = customerInvoiceDetail;
    }    
    
    /**
     * Gets the financialDocumentReferenceInvoiceNumber attribute.
     * 
     * @return Returns the financialDocumentReferenceInvoiceNumber.
     */
    public String getFinancialDocumentReferenceInvoiceNumber() {
        return financialDocumentReferenceInvoiceNumber;
    }

    /**
     * Sets the financialDocumentReferenceInvoiceNumber attribute value.
     * 
     * @param financialDocumentReferenceInvoiceNumber The financialDocumentReferenceInvoiceNumber to set.
     */
    public void setFinancialDocumentReferenceInvoiceNumber(String financialDocumentReferenceInvoiceNumber) {
        this.financialDocumentReferenceInvoiceNumber = financialDocumentReferenceInvoiceNumber;
    }

    public Integer getInvoiceItemNumber() {
        return referenceInvoiceItemNumber;
    }

    public String getInvoiceReferenceNumber() {
        return financialDocumentReferenceInvoiceNumber;
    }

    public boolean isInvoiceOpenItemQuantityZero() {
        return KualiDecimal.ZERO.equals(getInvoiceOpenItemQuantity());
    }

    public void setInvoiceOpenItemQuantityZero(boolean invoiceOpenItemQuantityZero) {
        this.invoiceOpenItemQuantityZero = invoiceOpenItemQuantityZero;
    }
}

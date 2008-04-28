package org.kuali.module.ar.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class CustomerCreditMemoDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer referenceInvoiceItemNumber;
    private BigDecimal creditMemoItemQuantity;
    private KualiDecimal creditMemoItemTaxAmount;
    private KualiDecimal creditMemoItemTotalAmount;
    private KualiDecimal invoiceLineTotalAmount;
    private KualiDecimal creditMemoLineTotalAmount;
    private KualiDecimal invoiceOpenItemAmount;

    /**
     * Default constructor.
     */
    public CustomerCreditMemoDetail() {

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() { 
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the referenceInvoiceItemNumber attribute.
     * 
     * @return Returns the referenceInvoiceItemNumber
     * 
     */
    public Integer getReferenceInvoiceItemNumber() { 
        return referenceInvoiceItemNumber;
    }

    /**
     * Sets the referenceInvoiceItemNumber attribute.
     * 
     * @param referenceInvoiceItemNumber The referenceInvoiceItemNumber to set.
     * 
     */
    public void setReferenceInvoiceItemNumber(Integer referenceInvoiceItemNumber) {
        this.referenceInvoiceItemNumber = referenceInvoiceItemNumber;
    }


    /**
     * Gets the creditMemoItemQuantity attribute.
     * 
     * @return Returns the creditMemoItemQuantity
     * 
     */
    public BigDecimal getCreditMemoItemQuantity() { 
        return creditMemoItemQuantity;
    }

    /**
     * Sets the creditMemoItemQuantity attribute.
     * 
     * @param creditMemoItemQuantity The creditMemoItemQuantity to set.
     * 
     */
    public void setCreditMemoItemQuantity(BigDecimal creditMemoItemQuantity) {
        this.creditMemoItemQuantity = creditMemoItemQuantity;
    }


    /**
     * Gets the creditMemoItemTaxAmount attribute.
     * 
     * @return Returns the creditMemoItemTaxAmount
     * 
     */
    public KualiDecimal getCreditMemoItemTaxAmount() { 
        return creditMemoItemTaxAmount;
    }

    /**
     * Sets the creditMemoItemTaxAmount attribute.
     * 
     * @param creditMemoItemTaxAmount The creditMemoItemTaxAmount to set.
     * 
     */
    public void setCreditMemoItemTaxAmount(KualiDecimal creditMemoItemTaxAmount) {
        this.creditMemoItemTaxAmount = creditMemoItemTaxAmount;
    }


    /**
     * Gets the creditMemoItemTotalAmount attribute.
     * 
     * @return Returns the creditMemoItemTotalAmount
     * 
     */
    public KualiDecimal getCreditMemoItemTotalAmount() { 
        return creditMemoItemTotalAmount;
    }

    /**
     * Sets the creditMemoItemTotalAmount attribute.
     * 
     * @param creditMemoItemTotalAmount The creditMemoItemTotalAmount to set.
     * 
     */
    public void setCreditMemoItemTotalAmount(KualiDecimal creditMemoItemTotalAmount) {
        this.creditMemoItemTotalAmount = creditMemoItemTotalAmount;
    }

    /**
     * Gets the invoiceOpenItemAmount attribute. 
     * @return Returns the invoiceOpenItemAmount.
     */
    public KualiDecimal getInvoiceOpenItemAmount() {
        return invoiceOpenItemAmount;
    }

    /**
     * Sets the invoiceOpenItemAmount attribute value.
     * @param invoiceOpenItemAmount The invoiceOpenItemAmount to set.
     */
    public void setInvoiceOpenItemAmount(KualiDecimal invoiceOpenItemAmount) {
        this.invoiceOpenItemAmount = invoiceOpenItemAmount;
    }
    
    /**
     * Gets the invoiceLineTotalAmount attribute. 
     * @return Returns the invoiceLineTotalAmount.
     */
    public KualiDecimal getInvoiceLineTotalAmount() {
        return invoiceLineTotalAmount;
    }

    /**
     * Sets the invoiceLineTotalAmount attribute value.
     * @param invoiceLineTotalAmount The invoiceLineTotalAmount to set.
     */
    public void setInvoiceLineTotalAmount(KualiDecimal invoiceLineTotalAmount) {
        this.invoiceLineTotalAmount = invoiceLineTotalAmount;
    }
    
    /**
     * Gets the creditMemoLineTotalAmount attribute. 
     * @return Returns the creditMemoLineTotalAmount.
     */
    public KualiDecimal getCreditMemoLineTotalAmount() {
        return creditMemoLineTotalAmount;
    }

    /**
     * Sets the creditMemoLineTotalAmount attribute value.
     * @param creditMemoLineTotalAmount The creditMemoLineTotalAmount to set.
     */
    public void setCreditMemoLineTotalAmount(KualiDecimal creditMemoLineTotalAmount) {
        this.creditMemoLineTotalAmount = creditMemoLineTotalAmount;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        if (this.referenceInvoiceItemNumber != null) {
            m.put("referenceInvoiceItemNumber", this.referenceInvoiceItemNumber.toString());
        }
        return m;
    }

}

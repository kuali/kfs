package org.kuali.module.ar.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Over60Invoice extends PersistableBusinessObjectBase {

	private String documentNumber;
//    private Date billingDate;    
    private Date entryDate;    
    private Date invoiceDueDate;
    private String invoiceDescription;    
    private boolean writeoffIndicator;  
    private String customerNumber;
    private String customerName;    
    private KualiDecimal invoiceItemTotalAmount;    
    private KualiDecimal invoiceDueAmount;    

    private Customer customer;
    
    /**
	 * Default constructor.
	 */
	public Over60Invoice() {

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
	 * Gets the invoiceItemTotalAmount attribute.
	 * 
	 * @return Returns the invoiceItemTotalAmount
	 * 
	 */
	public KualiDecimal getInvoiceItemTotalAmount() { 
		return invoiceItemTotalAmount;
	}

	/**
	 * Sets the invoiceItemTotalAmount attribute.
	 * 
	 * @param invoiceItemTotalAmount The invoiceItemTotalAmount to set.
	 * 
	 */
	public void setInvoiceItemTotalAmount(KualiDecimal invoiceItemTotalAmount) {
		this.invoiceItemTotalAmount = invoiceItemTotalAmount;
	}

    /**
     * Gets the billingDate attribute. 
     * @return Returns the billingDate.
     */
//    public Date getBillingDate() {
//        return billingDate;
//    }

    /**
     * Sets the billingDate attribute value.
     * @param billingDate The billingDate to set.
     */
//    public void setBillingDate(Date billingDate) {
//        this.billingDate = billingDate;
//    }

    /**
     * Gets the customerName attribute. 
     * @return Returns the customerName.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customerName attribute value.
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the customerNumber attribute. 
     * @return Returns the customerNumber.
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute value.
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the entryDate attribute. 
     * @return Returns the entryDate.
     */
    public Date getEntryDate() {
        return entryDate;
    }

    /**
     * Sets the entryDate attribute value.
     * @param entryDate The entryDate to set.
     */
    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    /**
     * Gets the invoiceDescription attribute. 
     * @return Returns the invoiceDescription.
     */
    public String getInvoiceDescription() {
        return invoiceDescription;
    }

    /**
     * Sets the invoiceDescription attribute value.
     * @param invoiceDescription The invoiceDescription to set.
     */
    public void setInvoiceDescription(String invoiceDescription) {
        this.invoiceDescription = invoiceDescription;
    }

    /**
     * Gets the invoiceDueDate attribute. 
     * @return Returns the invoiceDueDate.
     */
    public Date getInvoiceDueDate() {
        return invoiceDueDate;
    }

    /**
     * Sets the invoiceDueDate attribute value.
     * @param invoiceDueDate The invoiceDueDate to set.
     */
    public void setInvoiceDueDate(Date invoiceDueDate) {
        this.invoiceDueDate = invoiceDueDate;
    }

    /**
     * Gets the writeoffIndicator attribute. 
     * @return Returns the writeoffIndicator.
     */
    public boolean isWriteoffIndicator() {
        return writeoffIndicator;
    }

    /**
     * Sets the writeoffIndicator attribute value.
     * @param writeoffIndicator The writeoffIndicator to set.
     */
    public void setWriteoffIndicator(boolean writeoffIndicator) {
        this.writeoffIndicator = writeoffIndicator;
    }

    /**
     * Gets the invoiceDueAmount attribute. 
     * @return Returns the invoiceDueAmount.
     */
    public KualiDecimal getInvoiceDueAmount() {
        return invoiceDueAmount;
    }

    /**
     * Sets the invoiceDueAmount attribute value.
     * @param invoiceDueAmount The invoiceDueAmount to set.
     */
    public void setInvoiceDueAmount(KualiDecimal invoiceDueAmount) {
        this.invoiceDueAmount = invoiceDueAmount;
    }

    /**
     * Gets the customer attribute. 
     * @return Returns the customer.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer attribute value.
     * @param customer The customer to set.
     * @deprecated
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }    
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }    
    
}

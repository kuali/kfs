package org.kuali.module.ar.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class DocumentRecurrence extends PersistableBusinessObjectBase implements Inactivateable {

	private String documentNumber;
	private String referenceFinancialDocumentNumber;
	private String customerNumber;
	private Date documentRecurrenceBeginDate;
	private Date documentRecurrenceEndDate;
	private Integer documentTotalRecurrenceNumber;
	private String documentRecurrenceIntervalCode;
	private Long workgroupIdentifier;
	private String documentInitiatorUserIdentifier;
	private Date documentLastCreateDate;
	private boolean active;

    private DocumentHeader documentHeader;
    private DocumentHeader referenceFinancialDocument;
    private Customer customer;
    
    
	/**
	 * Default constructor.
	 */
	public DocumentRecurrence() {

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
	 * Gets the referenceFinancialDocumentNumber attribute.
	 * 
	 * @return Returns the referenceFinancialDocumentNumber
	 * 
	 */
	public String getReferenceFinancialDocumentNumber() { 
		return referenceFinancialDocumentNumber;
	}

	/**
	 * Sets the referenceFinancialDocumentNumber attribute.
	 * 
	 * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
	 * 
	 */
	public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
		this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
	}


	/**
	 * Gets the customerNumber attribute.
	 * 
	 * @return Returns the customerNumber
	 * 
	 */
	public String getCustomerNumber() { 
		return customerNumber;
	}

	/**
	 * Sets the customerNumber attribute.
	 * 
	 * @param customerNumber The customerNumber to set.
	 * 
	 */
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}


	/**
	 * Gets the documentRecurrenceBeginDate attribute.
	 * 
	 * @return Returns the documentRecurrenceBeginDate
	 * 
	 */
	public Date getDocumentRecurrenceBeginDate() { 
		return documentRecurrenceBeginDate;
	}

	/**
	 * Sets the documentRecurrenceBeginDate attribute.
	 * 
	 * @param documentRecurrenceBeginDate The documentRecurrenceBeginDate to set.
	 * 
	 */
	public void setDocumentRecurrenceBeginDate(Date documentRecurrenceBeginDate) {
		this.documentRecurrenceBeginDate = documentRecurrenceBeginDate;
	}


	/**
	 * Gets the documentRecurrenceEndDate attribute.
	 * 
	 * @return Returns the documentRecurrenceEndDate
	 * 
	 */
	public Date getDocumentRecurrenceEndDate() { 
		return documentRecurrenceEndDate;
	}

	/**
	 * Sets the documentRecurrenceEndDate attribute.
	 * 
	 * @param documentRecurrenceEndDate The documentRecurrenceEndDate to set.
	 * 
	 */
	public void setDocumentRecurrenceEndDate(Date documentRecurrenceEndDate) {
		this.documentRecurrenceEndDate = documentRecurrenceEndDate;
	}


	/**
	 * Gets the documentTotalRecurrenceNumber attribute.
	 * 
	 * @return Returns the documentTotalRecurrenceNumber
	 * 
	 */
	public Integer getDocumentTotalRecurrenceNumber() { 
		return documentTotalRecurrenceNumber;
	}

	/**
	 * Sets the documentTotalRecurrenceNumber attribute.
	 * 
	 * @param documentTotalRecurrenceNumber The documentTotalRecurrenceNumber to set.
	 * 
	 */
	public void setDocumentTotalRecurrenceNumber(Integer documentTotalRecurrenceNumber) {
		this.documentTotalRecurrenceNumber = documentTotalRecurrenceNumber;
	}


	/**
	 * Gets the documentRecurrenceIntervalCode attribute.
	 * 
	 * @return Returns the documentRecurrenceIntervalCode
	 * 
	 */
	public String getDocumentRecurrenceIntervalCode() { 
		return documentRecurrenceIntervalCode;
	}

	/**
	 * Sets the documentRecurrenceIntervalCode attribute.
	 * 
	 * @param documentRecurrenceIntervalCode The documentRecurrenceIntervalCode to set.
	 * 
	 */
	public void setDocumentRecurrenceIntervalCode(String documentRecurrenceIntervalCode) {
		this.documentRecurrenceIntervalCode = documentRecurrenceIntervalCode;
	}


	/**
	 * Gets the workgroupIdentifier attribute.
	 * 
	 * @return Returns the workgroupIdentifier
	 * 
	 */
	public Long getWorkgroupIdentifier() { 
		return workgroupIdentifier;
	}

	/**
	 * Sets the workgroupIdentifier attribute.
	 * 
	 * @param workgroupIdentifier The workgroupIdentifier to set.
	 * 
	 */
	public void setWorkgroupIdentifier(Long workgroupIdentifier) {
		this.workgroupIdentifier = workgroupIdentifier;
	}


	/**
	 * Gets the documentInitiatorUserIdentifier attribute.
	 * 
	 * @return Returns the documentInitiatorUserIdentifier
	 * 
	 */
	public String getDocumentInitiatorUserIdentifier() { 
		return documentInitiatorUserIdentifier;
	}

	/**
	 * Sets the documentInitiatorUserIdentifier attribute.
	 * 
	 * @param documentInitiatorUserIdentifier The documentInitiatorUserIdentifier to set.
	 * 
	 */
	public void setDocumentInitiatorUserIdentifier(String documentInitiatorUserIdentifier) {
		this.documentInitiatorUserIdentifier = documentInitiatorUserIdentifier;
	}


	/**
	 * Gets the documentLastCreateDate attribute.
	 * 
	 * @return Returns the documentLastCreateDate
	 * 
	 */
	public Date getDocumentLastCreateDate() { 
		return documentLastCreateDate;
	}

	/**
	 * Sets the documentLastCreateDate attribute.
	 * 
	 * @param documentLastCreateDate The documentLastCreateDate to set.
	 * 
	 */
	public void setDocumentLastCreateDate(Date documentLastCreateDate) {
		this.documentLastCreateDate = documentLastCreateDate;
	}


	/**
	 * Gets the active attribute.
	 * 
	 * @return Returns the active
	 * 
	 */
	public boolean isActive() { 
		return active;
	}

	/**
	 * Sets the active attribute.
	 * 
	 * @param active The active to set.
	 * 
	 */
	public void setActive(boolean active) {
		this.active = active;
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
     * Gets the documentHeader attribute. 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader The documentHeader to set.
     * @deprecated
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    /**
     * Gets the referenceFinancialDocument attribute. 
     * @return Returns the referenceFinancialDocument.
     */
    public DocumentHeader getReferenceFinancialDocument() {
        return referenceFinancialDocument;
    }

    /**
     * Sets the referenceFinancialDocument attribute value.
     * @param referenceFinancialDocument The referenceFinancialDocument to set.
     * @deprecated
     */
    public void setReferenceFinancialDocument(DocumentHeader referenceFinancialDocument) {
        this.referenceFinancialDocument = referenceFinancialDocument;
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

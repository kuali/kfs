package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashControlDetail extends PersistableBusinessObjectBase {

	private String documentNumber;
	private String referenceFinancialDocumentNumber;
	private String customerPaymentMediumIdentifier;
	private KualiDecimal financialDocumentLineAmount;
	private String customerPaymentDescription;
	private String customerNumber;
	private Date customerPaymentDate;

    private transient PaymentApplicationDocument referenceFinancialDocument;
    private transient CashControlDocument cashControlDocument;
	private Customer customer;
    
    private String status;
    
	/**
	 * Default constructor.
	 */
	public CashControlDetail() {
        financialDocumentLineAmount = new KualiDecimal(0);
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
	 */
	public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
		this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
	}


	/**
	 * Gets the customerPaymentMediumIdentifier attribute.
	 * 
	 * @return Returns the customerPaymentMediumIdentifier
	 * 
	 */
	public String getCustomerPaymentMediumIdentifier() { 
		return customerPaymentMediumIdentifier;
	}

	/**
	 * Sets the customerPaymentMediumIdentifier attribute.
	 * 
	 * @param customerPaymentMediumIdentifier The customerPaymentMediumIdentifier to set.
	 * 
	 */
	public void setCustomerPaymentMediumIdentifier(String customerPaymentMediumIdentifier) {
		this.customerPaymentMediumIdentifier = customerPaymentMediumIdentifier;
	}


	/**
	 * Gets the financialDocumentLineAmount attribute.
	 * 
	 * @return Returns the financialDocumentLineAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentLineAmount() { 
		return financialDocumentLineAmount;
	}

	/**
	 * Sets the financialDocumentLineAmount attribute.
	 * 
	 * @param financialDocumentLineAmount The financialDocumentLineAmount to set.
	 * 
	 */
	public void setFinancialDocumentLineAmount(KualiDecimal financialDocumentLineAmount) {
		this.financialDocumentLineAmount = financialDocumentLineAmount;
	}


	/**
	 * Gets the customerPaymentDescription attribute.
	 * 
	 * @return Returns the customerPaymentDescription
	 * 
	 */
	public String getCustomerPaymentDescription() { 
		return customerPaymentDescription;
	}

	/**
	 * Sets the customerPaymentDescription attribute.
	 * 
	 * @param customerPaymentDescription The customerPaymentDescription to set.
	 * 
	 */
	public void setCustomerPaymentDescription(String customerPaymentDescription) {
		this.customerPaymentDescription = customerPaymentDescription;
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
	 * Gets the customerPaymentDate attribute.
	 * 
	 * @return Returns the customerPaymentDate
	 * 
	 */
	public Date getCustomerPaymentDate() { 
		return customerPaymentDate;
	}

	/**
	 * Sets the customerPaymentDate attribute.
	 * 
	 * @param customerPaymentDate The customerPaymentDate to set.
	 * 
	 */
	public void setCustomerPaymentDate(Date customerPaymentDate) {
		this.customerPaymentDate = customerPaymentDate;
	}


	/**
	 * Gets the referenceFinancialDocument attribute.
	 * 
	 * @return Returns the referenceFinancialDocument
	 * 
	 */
	public PaymentApplicationDocument getReferenceFinancialDocument() { 
	    if (referenceFinancialDocument == null) {
	        try {
                referenceFinancialDocument = (PaymentApplicationDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(this.referenceFinancialDocumentNumber);
            }
            catch (WorkflowException e) {
                throw new RuntimeException("WorkflowException caught while trying to load PayApp Document #" + referenceFinancialDocumentNumber + ".", e);
            }
	    }
		return referenceFinancialDocument;
	}

	/**
	 * Sets the referenceFinancialDocument attribute.
	 * 
	 * @param referenceFinancialDocument The referenceFinancialDocument to set.
	 * 
	 */
	public void setReferenceFinancialDocument(PaymentApplicationDocument referenceFinancialDocument) {
		this.referenceFinancialDocument = referenceFinancialDocument;
	}

	/**
	 * Gets the customer attribute.
	 * 
	 * @return Returns the customer
	 * 
	 */
	public Customer getCustomer() { 
		return customer;
	}

	/**
	 * Sets the customer attribute.
	 * 
	 * @param customer The customer to set.
	 * @deprecated
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

    /**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
    @SuppressWarnings("unchecked")
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        m.put("referenceFinancialDocumentNumber", this.referenceFinancialDocumentNumber);
	    return m;
    }

    public CashControlDocument getCashControlDocument() {
        if (cashControlDocument == null) {
            try {
                String n = getDocumentNumber();
                if(null == n) {
                    return null;
                }
                cashControlDocument = (CashControlDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(n);
            }
            //TODO Andrew - this probably shouldnt throw an exception, because sometimes its okay for there not 
            //     to be a document ... but how can a detail ever exist without a parent document?
            catch (WorkflowException e) {
                throw new RuntimeException("WorkflowException caught while trying to load CashControl Document #" + referenceFinancialDocumentNumber + ".", e);
            }
        }
        return cashControlDocument;
    }

    public void setCashControlDocument(CashControlDocument cashControlDocument) {
        this.cashControlDocument = cashControlDocument;
    }

    /**
     * This method gets cash control detail status
     * @return cash control detail status
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method sets cash control detail status
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
}

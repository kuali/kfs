package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class InvoicePaidApplied extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer paidAppliedItemNumber;
	private String financialDocumentReferenceInvoiceNumber;
	private Integer invoiceItemNumber;
	private Integer universityFiscalYear;
	private String universityFiscalPeriodCode;
	private KualiDecimal invoiceItemAppliedAmount;

    private CustomerInvoiceDetail invoiceItem;
	private AccountingPeriod universityFiscalPeriod;
	private FinancialSystemDocumentHeader documentHeader;
	transient private DocumentService documentService;

	/**
	 * Default constructor.
	 */
	public InvoicePaidApplied() {}
	
    public DocumentService getDocumentService() {
        if(null == documentService) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public CustomerInvoiceDocument getCustomerInvoiceDocument() throws WorkflowException {
        CustomerInvoiceDocument _customerInvoiceDocument =
            (CustomerInvoiceDocument) getDocumentService().getByDocumentHeaderId(getFinancialDocumentReferenceInvoiceNumber());
        return _customerInvoiceDocument;
    }

    public PaymentApplicationDocument getPaymentApplicationDocument() throws WorkflowException {
        return (PaymentApplicationDocument) getDocumentService().getByDocumentHeaderId(getDocumentNumber());
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
	 * Gets the paidAppliedItemNumber attribute.
	 * 
	 * @return Returns the paidAppliedItemNumber
	 * 
	 */
	public Integer getPaidAppliedItemNumber() { 
		return paidAppliedItemNumber;
	}

	/**
	 * Sets the paidAppliedItemNumber attribute.
	 * 
	 * @param paidAppliedItemNumber The paidAppliedItemNumber to set.
	 * 
	 */
	public void setPaidAppliedItemNumber(Integer paidAppliedItemNumber) {
		this.paidAppliedItemNumber = paidAppliedItemNumber;
	}


	/**
	 * Gets the financialDocumentReferenceInvoiceNumber attribute.
	 * 
	 * @return Returns the financialDocumentReferenceInvoiceNumber
	 * 
	 */
	public String getFinancialDocumentReferenceInvoiceNumber() { 
		return financialDocumentReferenceInvoiceNumber;
	}

	/**
	 * Sets the financialDocumentReferenceInvoiceNumber attribute.
	 * 
	 * @param financialDocumentReferenceInvoiceNumber The financialDocumentReferenceInvoiceNumber to set.
	 * 
	 */
	public void setFinancialDocumentReferenceInvoiceNumber(String financialDocumentReferenceInvoiceNumber) {
		this.financialDocumentReferenceInvoiceNumber = financialDocumentReferenceInvoiceNumber;
	}

	/**
	 * Gets the invoiceItemNumber attribute.
	 * 
	 * @return Returns the invoiceItemNumber
	 * 
	 */
	public Integer getInvoiceItemNumber() { 
		return invoiceItemNumber;
	}

	/**
	 * Sets the invoiceItemNumber attribute.
	 * 
	 * @param invoiceItemNumber The invoiceItemNumber to set.
	 * 
	 */
	public void setInvoiceItemNumber(Integer invoiceItemNumber) {
		this.invoiceItemNumber = invoiceItemNumber;
	}

	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}

	/**
	 * Gets the universityFiscalPeriodCode attribute.
	 * 
	 * @return Returns the universityFiscalPeriodCode
	 * 
	 */
	public String getUniversityFiscalPeriodCode() { 
		return universityFiscalPeriodCode;
	}

	/**
	 * Sets the universityFiscalPeriodCode attribute.
	 * 
	 * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
	 * 
	 */
	public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
		this.universityFiscalPeriodCode = universityFiscalPeriodCode;
	}

	public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    /**
	 * Gets the invoiceItemAppliedAmount attribute.
	 * 
	 * @return Returns the invoiceItemAppliedAmount
	 * 
	 */
	public KualiDecimal getInvoiceItemAppliedAmount() { 
		return invoiceItemAppliedAmount;
	}

	/**
	 * Sets the invoiceItemAppliedAmount attribute.
	 * 
	 * @param invoiceItemAppliedAmount The invoiceItemAppliedAmount to set.
	 * 
	 */
	public void setInvoiceItemAppliedAmount(KualiDecimal invoiceItemAppliedAmount) {
		this.invoiceItemAppliedAmount = invoiceItemAppliedAmount;
	}

	/**
	 * Gets the invoiceItem attribute.
	 * 
	 * @return Returns the invoiceItem
	 * 
	 */
	public CustomerInvoiceDetail getInvoiceItem() { 
		return invoiceItem;
	}

	/**
	 * Sets the invoiceItem attribute.
	 * 
	 * @param invoiceItem The invoiceItem to set.
	 * @deprecated
	 */
	public void setInvoiceItem(CustomerInvoiceDetail invoiceItem) {
		this.invoiceItem = invoiceItem;
	}

	/**
	 * Gets the universityFiscalPeriod attribute.
	 * 
	 * @return Returns the universityFiscalPeriod
	 * 
	 */
	public AccountingPeriod getUniversityFiscalPeriod() { 
		return universityFiscalPeriod;
	}

	/**
	 * Sets the universityFiscalPeriod attribute.
	 * 
	 * @param universityFiscalPeriod The universityFiscalPeriod to set.
	 * @deprecated
	 */
	public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
		this.universityFiscalPeriod = universityFiscalPeriod;
	}
    
    /**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
    @SuppressWarnings("unchecked")
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        if (this.paidAppliedItemNumber != null) {
            m.put("paidAppliedItemNumber", this.paidAppliedItemNumber.toString());
        }
	    return m;
    }

}

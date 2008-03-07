package org.kuali.module.ar.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CustomerCreditMemoDetail;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.bo.NonAppliedHolding;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerCreditMemoDocument extends AccountingDocumentBase {

	private String documentNumber;

    private CustomerInvoiceDocument invoice;
    private List<CustomerCreditMemoDetail> customerCreditMemoDetails;

   
	/**
	 * Default constructor.
	 */
	public CustomerCreditMemoDocument() {
	    super();
        customerCreditMemoDetails = new ArrayList<CustomerCreditMemoDetail>();
	}
	
	@Override
	public void handleRouteStatusChange() {
	    // TODO Auto-generated method stub
	    super.handleRouteStatusChange();
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
     * Gets the invoice attribute. 
     * @return Returns the invoice.
     */
    public CustomerInvoiceDocument getInvoice() {
        return invoice;
    }

    /**
     * Sets the invoice attribute value.
     * @param invoice The invoice to set.
     */
    public void setInvoice(CustomerInvoiceDocument invoice) {
        this.invoice = invoice;
    }
    
    /**
     * Gets the customerCreditMemoDetails attribute. 
     * @return Returns the customerCreditMemoDetails.
     */
    public List<CustomerCreditMemoDetail> getCustomerCreditMemoDetails() {
        return customerCreditMemoDetails;
    }

    /**
     * Sets the customerCreditMemoDetails attribute value.
     * @param customerCreditMemoDetails The customerCreditMemoDetails to set.
     */
    public void setCustomerCreditMemoDetails(List<CustomerCreditMemoDetail> customerCreditMemoDetails) {
        this.customerCreditMemoDetails = customerCreditMemoDetails;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    /**
     * Determines if the given AccountingLine (as a GeneralLedgerPendingEntrySourceDetail) is a credit or a debit, in terms of GLPE generation
     * @see org.kuali.kfs.document.AccountingDocumentBase#isDebit(org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        // TODO Auto-generated method stub
        return false;
    }

}

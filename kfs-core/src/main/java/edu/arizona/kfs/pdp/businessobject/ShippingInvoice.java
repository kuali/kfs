package edu.arizona.kfs.pdp.businessobject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent the shipping invoice data sent in a file from the Integration Team.
 */
public class ShippingInvoice extends PersistableBusinessObjectBase { 
    /**
	 * 
	 */
    private static final long serialVersionUID = 1422188012300521306L;
    protected String invoiceNumber;
    protected String shippingCompany;
    protected Date creationDate;
    protected String transactionRefNumber;
    protected Date invoiceDate;
    protected KualiDecimal totalInvoiceCharge;
    protected String billToAccountNumber;
    protected String openCustomField;
    protected String invoiceType;
    protected String settlementOption;    
    protected KualiInteger totalInvoiceTransactions;    
    protected String otherAccountNumber;
    
    protected List<ShippingInvoiceTracking> invoiceTrackingElements;
    
    public ShippingInvoice() {
    	invoiceTrackingElements = new ArrayList<ShippingInvoiceTracking>();        
    }
       
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
       
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
        
    public String getShippingCompany() {
        return shippingCompany;
    }
       
    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }
      
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
       
    public String getTransactionRefNumber() {
        return transactionRefNumber;
    }
       
    public void setTransactionRefNumber(String transactionRefNumber) {
        this.transactionRefNumber = transactionRefNumber;
    }       
        
    public Date getInvoiceDate() {
        return invoiceDate;
    }
       
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
        
    public void setInvoiceDate(String invoiceDate) {
        if (StringUtils.isNotBlank(invoiceDate)) {
            this.invoiceDate = (Date) (new SqlDateConverter()).convert(Date.class, invoiceDate);
        } 
    }  
       
    public KualiDecimal getTotalInvoiceCharge() {
        return totalInvoiceCharge;
    }
    
    public void setTotalInvoiceCharge(KualiDecimal totalInvoiceCharge) {
        this.totalInvoiceCharge = totalInvoiceCharge;
    }
       
    public void setTotalInvoiceCharge(String totalInvoiceCharge) {
        if (StringUtils.isNotBlank(totalInvoiceCharge)) {
            this.totalInvoiceCharge = new KualiDecimal(totalInvoiceCharge);
        }
        else {
            this.totalInvoiceCharge = KualiDecimal.ZERO;
        }    
    }
       
    public String getBillToAccountNumber() {
        return billToAccountNumber;
    }
        
    public void setBillToAccountNumber(String billToAccountNumber) {
        this.billToAccountNumber = billToAccountNumber;
    }
       
    public String getOpenCustomField() {
        return openCustomField;
    }

    public void setOpenCustomField(String masterEDINumber) {
        this.openCustomField = masterEDINumber;
    }
   
    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getSettlementOption() {
        return settlementOption;
    }

    public void setSettlementOption(String settlementOption) {
        this.settlementOption = settlementOption;
    }
    
    public KualiInteger getTotalInvoiceTransactions() {
        return totalInvoiceTransactions;
    }

    public void setTotalInvoiceTransactions(KualiInteger totalInvoiceTransactions) {
        this.totalInvoiceTransactions = totalInvoiceTransactions;
    }

    public void setTotalInvoiceTransactions(String totalInvoiceTransactions) {
        if (StringUtils.isNotBlank(totalInvoiceTransactions)) {
            this.totalInvoiceTransactions = new KualiInteger(totalInvoiceTransactions);
        }
        else {
            this.totalInvoiceTransactions = KualiInteger.ZERO;
        }    
    }

    public String getOtherAccountNumber() {
        return otherAccountNumber;
    }

    public void setOtherAccountNumber(String otherAccountNumber) {
        this.otherAccountNumber = otherAccountNumber;
    }
    
    public List<ShippingInvoiceTracking> getInvoiceTrackingElements() {
        return invoiceTrackingElements;
    }

    public void setInvoiceTrackingElements(List<ShippingInvoiceTracking> invoiceTrackingElements) {
        this.invoiceTrackingElements = invoiceTrackingElements;
    }

    /**
     * This is a convenience method that adds a populated ShippingInvoiceTracking object directly 
     * to the contained ArrayList.
     * 
     * It's primarily used by the Shipping Invoice Load batch process, for each of XML batch file 
     * digesting, though it can be used generally.
     * 
     * NOTE that it will attempt to wire the parent/child relationship by setting the 
     * shippingInvoiceTracking.invoiceNumber to the invoiceNumber of 'this', if the number isn't 
     * already set.
     * 
     * @param shippingInvoiceTrackingElement
     */
    public void addShippingInvoiceTracking(ShippingInvoiceTracking shippingInvoiceTrackingElement) {
        //  do nothing if passed-in shippingInvoiceTrackingElement is null
        if (shippingInvoiceTrackingElement == null) {
            return;
        }
        //  wire the invoice tracking element to ensure a valid parent/child relationship
        if (shippingInvoiceTrackingElement.getInvoiceNumber() == null) {
            if (this.invoiceNumber != null) {
                shippingInvoiceTrackingElement.setInvoiceNumber(this.invoiceNumber);
            }
        }
        this.invoiceTrackingElements.add(shippingInvoiceTrackingElement);
    }     
    
    protected LinkedHashMap<String, String> toStringMapper() {
    	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
    	map.put("invoiceNumber", getInvoiceNumber());
    	map.put("shippingCompany", getShippingCompany());
    	map.put("creationDate", getCreationDate().toString());
    	map.put("transactionRefNumber", getTransactionRefNumber());
    	return map;
    }  
    
    protected void copyFields( ShippingInvoice invoiceToCopy ) {
        this.openCustomField = invoiceToCopy.openCustomField;
        this.shippingCompany = invoiceToCopy.shippingCompany;
        this.creationDate = invoiceToCopy.creationDate;
        this.transactionRefNumber = invoiceToCopy.transactionRefNumber;
        this.invoiceNumber = invoiceToCopy.invoiceNumber;
        this.invoiceDate = invoiceToCopy.invoiceDate;
        this.invoiceType = invoiceToCopy.invoiceType;
        this.settlementOption = invoiceToCopy.settlementOption;
        this.totalInvoiceCharge = invoiceToCopy.totalInvoiceCharge;
        this.totalInvoiceTransactions = invoiceToCopy.totalInvoiceTransactions;
        this.billToAccountNumber = invoiceToCopy.billToAccountNumber;
        this.otherAccountNumber = invoiceToCopy.otherAccountNumber;
    }

    protected String masterEDINumber = "UNUSED";
}

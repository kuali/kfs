package edu.arizona.kfs.pdp.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


public class ShippingBatch extends PersistableBusinessObjectBase {
    private ShippingHeader shippingHeader;
    private List<ShippingInvoice> shippingInvoices;
    
    public ShippingBatch() {
    	shippingInvoices = new ArrayList<ShippingInvoice>();           
    }        
        
    public ShippingHeader getShippingHeader() {
        return shippingHeader;
    }
  
    public void setShippingHeader(ShippingHeader shippingHeader) {
        this.shippingHeader = shippingHeader;
    }

    public List<ShippingInvoice> getShippingInvoices() {
        return shippingInvoices;
    }
   
    public void setShippingInvoices(List<ShippingInvoice> shippingInvoices) {
        this.shippingInvoices = shippingInvoices;
    }
    
    protected LinkedHashMap toStringMapper() {
        return shippingHeader.toStringMapper();
    }
        
}

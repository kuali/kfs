package edu.arizona.kfs.pdp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class ShippingHeader extends PersistableBusinessObjectBase {
	protected Date creationDate;
	protected String transactionRefNumber;
	protected String shippingCompany;
    protected String openCustomField;
     
    
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
   
    public void setCreationDate(String creationDate) {
        if (StringUtils.isNotBlank(creationDate)) {
            this.creationDate = (Date) (new SqlDateConverter()).convert(Date.class, creationDate);
        } 
    }
   
    public String getTransactionRefNumber() {
        return transactionRefNumber;
    }
       
    public void setTransactionRefNumber(String transactionRefNumber) {
        this.transactionRefNumber = transactionRefNumber;
    }
   
    public String getShippingCompany() {
        return shippingCompany;
    }
       
    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }
             
    public String getOpenCustomField() {
        return openCustomField;
    }
   
    public void setOpenCustomField(String masterEDINumber) {
        this.openCustomField = masterEDINumber;
    }
       
    protected LinkedHashMap<String, String> toStringMapper() {
    	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
    	map.put("creationDate", getCreationDate().toString());
    	map.put("transactionRefNumber", getTransactionRefNumber());
        return map;
    }
   
    protected String masterEDINumber = "UNUSED";
}

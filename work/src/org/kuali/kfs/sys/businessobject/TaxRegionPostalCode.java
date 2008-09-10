package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class TaxRegionPostalCode extends PersistableBusinessObjectBase implements Inactivateable {
	
	private String postalCode;
	private String taxRegionCode;
	private boolean active;
	private PostalZipCode postalZip;
	private TaxRegion taxRegion;
	
	public PostalZipCode getPostalZip() {
		return postalZip;
	}
	public void setPostalZip(PostalZipCode postalZip) {
		this.postalZip = postalZip;
	}
	public TaxRegion getTaxRegion() {
        return taxRegion;
    }
    public void setTaxRegion(TaxRegion taxRegion) {
        this.taxRegion = taxRegion;
    }
    public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getTaxRegionCode() {
		return taxRegionCode;
	}
	public void setTaxRegionCode(String taxRegionCode) {
		this.taxRegionCode = taxRegionCode;
	}
	
	protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("postalCode", this.postalCode);
        m.put("taxRegionCode", this.taxRegionCode);
        return m;
    }
}

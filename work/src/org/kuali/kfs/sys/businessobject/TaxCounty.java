package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class TaxCounty extends PersistableBusinessObjectBase implements Inactivateable {
	
	private String countyCode;
	private String stateCode;
	private String taxRegionCode;
	private boolean active;
	private County county;
	
	public String getCountyCode() {
		return countyCode;
	}
	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public String getTaxRegionCode() {
		return taxRegionCode;
	}
	public void setTaxRegionCode(String taxRegionCode) {
		this.taxRegionCode = taxRegionCode;
	}
	
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("countyCode", this.countyCode);
        m.put("stateCode", this.stateCode);
        m.put("taxRegionCode", this.taxRegionCode);
        return m;
    }
	public County getCounty() {
		return county;
	}
	public void setCounty(County county) {
		this.county = county;
	}
}

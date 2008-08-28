package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class TaxRegionType extends PersistableBusinessObjectBase {
	
	private String taxRegionTypeCode;
	private String taxRegionTypeName;
	private String active;
	
	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getTaxRegionTypeCode() {
		return taxRegionTypeCode;
	}

	public void setTaxRegionTypeCode(String taxRegionTypeCode) {
		this.taxRegionTypeCode = taxRegionTypeCode;
	}

	public String getTaxRegionTypeName() {
		return taxRegionTypeName;
	}

	public void setTaxRegionTypeName(String taxRegionTypeName) {
		this.taxRegionTypeName = taxRegionTypeName;
	}

	@Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }

}

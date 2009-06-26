package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.CountyService;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.bo.County;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class TaxRegionCounty extends PersistableBusinessObjectBase implements Inactivateable {
	
    private String postalCountryCode;
	private String countyCode;
	private String stateCode;
	private String taxRegionCode;
	private boolean active;
	
	private Country country;
	private County county;
	private TaxRegion taxRegion;
	
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
	public TaxRegion getTaxRegion() {
        return taxRegion;
    }
    public void setTaxRegion(TaxRegion taxRegion) {
        this.taxRegion = taxRegion;
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
	    county = SpringContext.getBean(CountyService.class).getByPrimaryIdIfNecessary(postalCountryCode, stateCode, countyCode, county);
		return county;
	}
	public void setCounty(County county) {
		this.county = county;
	}
    /**
     * Gets the postalCountryCode attribute. 
     * @return Returns the postalCountryCode.
     */
    public String getPostalCountryCode() {
        return postalCountryCode;
    }
    /**
     * Sets the postalCountryCode attribute value.
     * @param postalCountryCode The postalCountryCode to set.
     */
    public void setPostalCountryCode(String postalCountryCode) {
        this.postalCountryCode = postalCountryCode;
    }
    /**
     * Gets the country attribute. 
     * @return Returns the country.
     */
    public Country getCountry() {
        country = SpringContext.getBean(CountryService.class).getByPrimaryIdIfNecessary(postalCountryCode, country);
        return country;
    }
    /**
     * Sets the country attribute value.
     * @param country The country to set.
     */
    public void setCountry(Country country) {
        this.country = country;
    }
}

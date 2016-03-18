package edu.arizona.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
/**
 * FA Cost Subclass...
 */
public class FACostSubCategory extends PersistableBusinessObjectBase implements MutableInactivatable {
    private String faCostSubCatCode;              // Subcategory Code 
    private boolean active;                       // Active Indicator
    private String faCostSubCatDesc;              // Subcategory Description
    
    /**
     * Default constructor.
     */
    public FACostSubCategory() {
        
    }
    
    /**
     * Gets the faCostSubCatCode attribute. 
     * @return Returns the faCostSubCatCode.
     */
    public String getFaCostSubCatCode() {
        return faCostSubCatCode;
    }
    /**
     * Sets the faCostSubCatCode attribute value.
     * @param faCostSubCatCode The faCostSubcatCode to set.
     */
    public void setFaCostSubCatCode(String faCostSubCatCode) {
        this.faCostSubCatCode = faCostSubCatCode;
    }
    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }
    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * Gets the faCostSubCatDesc attribute. 
     * @return Returns the faCostSubCatDesc.
     */
    public String getFaCostSubCatDesc() {
        return faCostSubCatDesc;
    }
    /**
     * Sets the faCostSubcatDesc attribute value.
     * @param faCostSubcatDesc The faCostSubcatDesc to set.
     */
    public void setFaCostSubCatDesc(String faCostSubCatDesc) {
        this.faCostSubCatDesc = faCostSubCatDesc;
    }
    
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("faCostSubCatCode", this.faCostSubCatCode);
        return m;
    }
}


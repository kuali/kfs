package org.kuali.module.cams.bo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetLocationGlobal extends PersistableBusinessObjectBase {

	private String documentNumber;

    private DocumentHeader documentHeader;

    private List<AssetLocationGlobalDetail> assetLocationGlobalDetails;
    
	/**
	 * Default constructor.
	 */
	public AssetLocationGlobal() {
        assetLocationGlobalDetails = new ArrayList<AssetLocationGlobalDetail>();
        
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
     * Gets the documentHeader attribute. 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader The documentHeader to set.
     * @deprecated
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }
    
    /**
     * Gets the assetLocationGlobalDetails attribute. 
     * @return Returns the assetLocationGlobalDetails.
     */
    public List<AssetLocationGlobalDetail> getAssetLocationGlobalDetails() {
        return assetLocationGlobalDetails;
    }

    /**
     * Sets the assetLocationGlobalDetails attribute value.
     * @param assetLocationGlobalDetails The assetLocationGlobalDetails to set.
     */
    public void setAssetLocationGlobalDetails(List<AssetLocationGlobalDetail> assetLocationGlobalDetails) {
        this.assetLocationGlobalDetails = assetLocationGlobalDetails;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
	    return m;
    }
}

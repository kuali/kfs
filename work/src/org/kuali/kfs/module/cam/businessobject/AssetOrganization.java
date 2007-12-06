package org.kuali.module.cams.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetOrganization extends PersistableBusinessObjectBase {

	private Long capitalAssetNumber;
	private String organizationAssetTypeIdentifier;
	private String organizationTagNumber;
	private String organizationDescription;
	private String organizationText;

    private Asset asset;

	/**
	 * Default constructor.
	 */
	public AssetOrganization() {

	}

	/**
	 * Gets the capitalAssetNumber attribute.
	 * 
	 * @return Returns the capitalAssetNumber
	 * 
	 */
	public Long getCapitalAssetNumber() { 
		return capitalAssetNumber;
	}

	/**
	 * Sets the capitalAssetNumber attribute.
	 * 
	 * @param capitalAssetNumber The capitalAssetNumber to set.
	 * 
	 */
	public void setCapitalAssetNumber(Long capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
	}


	/**
	 * Gets the organizationAssetTypeIdentifier attribute.
	 * 
	 * @return Returns the organizationAssetTypeIdentifier
	 * 
	 */
	public String getOrganizationAssetTypeIdentifier() { 
		return organizationAssetTypeIdentifier;
	}

	/**
	 * Sets the organizationAssetTypeIdentifier attribute.
	 * 
	 * @param organizationAssetTypeIdentifier The organizationAssetTypeIdentifier to set.
	 * 
	 */
	public void setOrganizationAssetTypeIdentifier(String organizationAssetTypeIdentifier) {
		this.organizationAssetTypeIdentifier = organizationAssetTypeIdentifier;
	}


	/**
	 * Gets the organizationTagNumber attribute.
	 * 
	 * @return Returns the organizationTagNumber
	 * 
	 */
	public String getOrganizationTagNumber() { 
		return organizationTagNumber;
	}

	/**
	 * Sets the organizationTagNumber attribute.
	 * 
	 * @param organizationTagNumber The organizationTagNumber to set.
	 * 
	 */
	public void setOrganizationTagNumber(String organizationTagNumber) {
		this.organizationTagNumber = organizationTagNumber;
	}


	/**
	 * Gets the organizationDescription attribute.
	 * 
	 * @return Returns the organizationDescription
	 * 
	 */
	public String getOrganizationDescription() { 
		return organizationDescription;
	}

	/**
	 * Sets the organizationDescription attribute.
	 * 
	 * @param organizationDescription The organizationDescription to set.
	 * 
	 */
	public void setOrganizationDescription(String organizationDescription) {
		this.organizationDescription = organizationDescription;
	}


	/**
	 * Gets the organizationText attribute.
	 * 
	 * @return Returns the organizationText
	 * 
	 */
	public String getOrganizationText() { 
		return organizationText;
	}

	/**
	 * Sets the organizationText attribute.
	 * 
	 * @param organizationText The organizationText to set.
	 * 
	 */
	public void setOrganizationText(String organizationText) {
		this.organizationText = organizationText;
	}


	/**
	 * Gets the asset attribute.
	 * 
	 * @return Returns the asset
	 * 
	 */
	public Asset getAsset() { 
		return asset;
	}

	/**
	 * Sets the asset attribute.
	 * 
	 * @param asset The asset to set.
	 * @deprecated
	 */
	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
	    return m;
    }
}

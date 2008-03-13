package org.kuali.module.cams.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 * @deprecated may be retired in the near future. Talk to Philip if need to use this.
 */
public class AssetDisposition extends PersistableBusinessObjectBase {

	private Long capitalAssetNumber;
	private String dispositionCode;
	private String dispositionOriginationCode;
	private Long originalCapitalAssetNumber;
	private Date dispositionDate;

    private Asset asset;

	/**
	 * Default constructor.
	 */
	public AssetDisposition() {

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
	 * Gets the dispositionCode attribute.
	 * 
	 * @return Returns the dispositionCode
	 * 
	 */
	public String getDispositionCode() { 
		return dispositionCode;
	}

	/**
	 * Sets the dispositionCode attribute.
	 * 
	 * @param dispositionCode The dispositionCode to set.
	 * 
	 */
	public void setDispositionCode(String dispositionCode) {
		this.dispositionCode = dispositionCode;
	}


	/**
	 * Gets the dispositionOriginationCode attribute.
	 * 
	 * @return Returns the dispositionOriginationCode
	 * 
	 */
	public String getDispositionOriginationCode() { 
		return dispositionOriginationCode;
	}

	/**
	 * Sets the dispositionOriginationCode attribute.
	 * 
	 * @param dispositionOriginationCode The dispositionOriginationCode to set.
	 * 
	 */
	public void setDispositionOriginationCode(String dispositionOriginationCode) {
		this.dispositionOriginationCode = dispositionOriginationCode;
	}


	/**
	 * Gets the originalCapitalAssetNumber attribute.
	 * 
	 * @return Returns the originalCapitalAssetNumber
	 * 
	 */
	public Long getOriginalCapitalAssetNumber() { 
		return originalCapitalAssetNumber;
	}

	/**
	 * Sets the originalCapitalAssetNumber attribute.
	 * 
	 * @param originalCapitalAssetNumber The originalCapitalAssetNumber to set.
	 * 
	 */
	public void setOriginalCapitalAssetNumber(Long originalCapitalAssetNumber) {
		this.originalCapitalAssetNumber = originalCapitalAssetNumber;
	}


	/**
	 * Gets the dispositionDate attribute.
	 * 
	 * @return Returns the dispositionDate
	 * 
	 */
	public Date getDispositionDate() { 
		return dispositionDate;
	}

	/**
	 * Sets the dispositionDate attribute.
	 * 
	 * @param dispositionDate The dispositionDate to set.
	 * 
	 */
	public void setDispositionDate(Date dispositionDate) {
		this.dispositionDate = dispositionDate;
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
        m.put("dispositionCode", this.dispositionCode);
	    return m;
    }
}

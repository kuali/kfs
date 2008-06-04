package org.kuali.module.cams.bo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class BarcodeInventoryErrorDetail extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Long uploadRowNumber;
	private String errorCorrectionStatusCode;
	private String correctorUniversalIdentifier;
	private Timestamp inventoryCorrectionTimestamp;
	private String assetTagNumber;
	private boolean uploadScanIndicator;
	private Date uploadScanTimestamp;
	private String campusCode;
	private String buildingCode;
	private String buildingRoomNumber;
	private String buildingSubRoomNumber;
	private String assetConditionCode;

	/**
	 * Default constructor.
	 */
	public BarcodeInventoryErrorDetail() {

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
	 * Gets the uploadRowNumber attribute.
	 * 
	 * @return Returns the uploadRowNumber
	 * 
	 */
	public Long getUploadRowNumber() { 
		return uploadRowNumber;
	}

	/**
	 * Sets the uploadRowNumber attribute.
	 * 
	 * @param uploadRowNumber The uploadRowNumber to set.
	 * 
	 */
	public void setUploadRowNumber(Long uploadRowNumber) {
		this.uploadRowNumber = uploadRowNumber;
	}


	/**
	 * Gets the errorCorrectionStatusCode attribute.
	 * 
	 * @return Returns the errorCorrectionStatusCode
	 * 
	 */
	public String getErrorCorrectionStatusCode() { 
		return errorCorrectionStatusCode;
	}

	/**
	 * Sets the errorCorrectionStatusCode attribute.
	 * 
	 * @param errorCorrectionStatusCode The errorCorrectionStatusCode to set.
	 * 
	 */
	public void setErrorCorrectionStatusCode(String errorCorrectionStatusCode) {
		this.errorCorrectionStatusCode = errorCorrectionStatusCode;
	}


	/**
	 * Gets the correctorUniversalIdentifier attribute.
	 * 
	 * @return Returns the correctorUniversalIdentifier
	 * 
	 */
	public String getCorrectorUniversalIdentifier() { 
		return correctorUniversalIdentifier;
	}

	/**
	 * Sets the correctorUniversalIdentifier attribute.
	 * 
	 * @param correctorUniversalIdentifier The correctorUniversalIdentifier to set.
	 * 
	 */
	public void setCorrectorUniversalIdentifier(String correctorUniversalIdentifier) {
		this.correctorUniversalIdentifier = correctorUniversalIdentifier;
	}


	/**
	 * Gets the inventoryCorrectionTimestamp attribute.
	 * 
	 * @return Returns the inventoryCorrectionTimestamp
	 * 
	 */
	public Timestamp getInventoryCorrectionTimestamp() { 
		return inventoryCorrectionTimestamp;
	}

	/**
	 * Sets the inventoryCorrectionTimestamp attribute.
	 * 
	 * @param inventoryCorrectionTimestamp The inventoryCorrectionTimestamp to set.
	 * 
	 */
	public void setInventoryCorrectionTimestamp(Timestamp inventoryCorrectionTimestamp) {
		this.inventoryCorrectionTimestamp = inventoryCorrectionTimestamp;
	}


	/**
	 * Gets the assetTagNumber attribute.
	 * 
	 * @return Returns the assetTagNumber
	 * 
	 */
	public String getAssetTagNumber() { 
		return assetTagNumber;
	}

	/**
	 * Sets the assetTagNumber attribute.
	 * 
	 * @param assetTagNumber The assetTagNumber to set.
	 * 
	 */
	public void setAssetTagNumber(String assetTagNumber) {
		this.assetTagNumber = assetTagNumber;
	}


	/**
	 * Gets the uploadScanIndicator attribute.
	 * 
	 * @return Returns the uploadScanIndicator
	 * 
	 */
	public boolean isUploadScanIndicator() { 
		return uploadScanIndicator;
	}

	/**
	 * Sets the uploadScanIndicator attribute.
	 * 
	 * @param uploadScanIndicator The uploadScanIndicator to set.
	 * 
	 */
	public void setUploadScanIndicator(boolean uploadScanIndicator) {
		this.uploadScanIndicator = uploadScanIndicator;
	}


	/**
	 * Gets the uploadScanTimestamp attribute.
	 * 
	 * @return Returns the uploadScanTimestamp
	 * 
	 */
	public Date getUploadScanTimestamp() { 
		return uploadScanTimestamp;
	}

	/**
	 * Sets the uploadScanTimestamp attribute.
	 * 
	 * @param uploadScanTimestamp The uploadScanTimestamp to set.
	 * 
	 */
	public void setUploadScanTimestamp(Date uploadScanTimestamp) {
		this.uploadScanTimestamp = uploadScanTimestamp;
	}


	/**
	 * Gets the campusCode attribute.
	 * 
	 * @return Returns the campusCode
	 * 
	 */
	public String getCampusCode() { 
		return campusCode;
	}

	/**
	 * Sets the campusCode attribute.
	 * 
	 * @param campusCode The campusCode to set.
	 * 
	 */
	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}


	/**
	 * Gets the buildingCode attribute.
	 * 
	 * @return Returns the buildingCode
	 * 
	 */
	public String getBuildingCode() { 
		return buildingCode;
	}

	/**
	 * Sets the buildingCode attribute.
	 * 
	 * @param buildingCode The buildingCode to set.
	 * 
	 */
	public void setBuildingCode(String buildingCode) {
		this.buildingCode = buildingCode;
	}


	/**
	 * Gets the buildingRoomNumber attribute.
	 * 
	 * @return Returns the buildingRoomNumber
	 * 
	 */
	public String getBuildingRoomNumber() { 
		return buildingRoomNumber;
	}

	/**
	 * Sets the buildingRoomNumber attribute.
	 * 
	 * @param buildingRoomNumber The buildingRoomNumber to set.
	 * 
	 */
	public void setBuildingRoomNumber(String buildingRoomNumber) {
		this.buildingRoomNumber = buildingRoomNumber;
	}


	/**
	 * Gets the buildingSubRoomNumber attribute.
	 * 
	 * @return Returns the buildingSubRoomNumber
	 * 
	 */
	public String getBuildingSubRoomNumber() { 
		return buildingSubRoomNumber;
	}

	/**
	 * Sets the buildingSubRoomNumber attribute.
	 * 
	 * @param buildingSubRoomNumber The buildingSubRoomNumber to set.
	 * 
	 */
	public void setBuildingSubRoomNumber(String buildingSubRoomNumber) {
		this.buildingSubRoomNumber = buildingSubRoomNumber;
	}


	/**
	 * Gets the assetConditionCode attribute.
	 * 
	 * @return Returns the assetConditionCode
	 * 
	 */
	public String getAssetConditionCode() { 
		return assetConditionCode;
	}

	/**
	 * Sets the assetConditionCode attribute.
	 * 
	 * @param assetConditionCode The assetConditionCode to set.
	 * 
	 */
	public void setAssetConditionCode(String assetConditionCode) {
		this.assetConditionCode = assetConditionCode;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        if (this.uploadRowNumber != null) {
            m.put("uploadRowNumber", this.uploadRowNumber.toString());
        }
	    return m;
    }
}

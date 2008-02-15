package org.kuali.module.cams.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetRepairHistory extends PersistableBusinessObjectBase {

	private Long capitalAssetNumber;
	private Date incidentDate;
	private String problemDescription;
	private String repairContactName;
	private String repairNoteText;
	private Date estimatedRepairDate;
	private Date repairDate;
	private KualiDecimal repairAmount;
	private String repairSolutionDescription;

    private Asset asset;

	/**
	 * Default constructor.
	 */
	public AssetRepairHistory() {

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
	 * Gets the incidentDate attribute.
	 * 
	 * @return Returns the incidentDate
	 * 
	 */
	public Date getIncidentDate() { 
		return incidentDate;
	}

	/**
	 * Sets the incidentDate attribute.
	 * 
	 * @param incidentDate The incidentDate to set.
	 * 
	 */
	public void setIncidentDate(Date incidentDate) {
		this.incidentDate = incidentDate;
	}


	/**
	 * Gets the problemDescription attribute.
	 * 
	 * @return Returns the problemDescription
	 * 
	 */
	public String getProblemDescription() { 
		return problemDescription;
	}

	/**
	 * Sets the problemDescription attribute.
	 * 
	 * @param problemDescription The problemDescription to set.
	 * 
	 */
	public void setProblemDescription(String problemDescription) {
		this.problemDescription = problemDescription;
	}


	/**
	 * Gets the repairContactName attribute.
	 * 
	 * @return Returns the repairContactName
	 * 
	 */
	public String getRepairContactName() { 
		return repairContactName;
	}

	/**
	 * Sets the repairContactName attribute.
	 * 
	 * @param repairContactName The repairContactName to set.
	 * 
	 */
	public void setRepairContactName(String repairContactName) {
		this.repairContactName = repairContactName;
	}


	/**
	 * Gets the repairNoteText attribute.
	 * 
	 * @return Returns the repairNoteText
	 * 
	 */
	public String getRepairNoteText() { 
		return repairNoteText;
	}

	/**
	 * Sets the repairNoteText attribute.
	 * 
	 * @param repairNoteText The repairNoteText to set.
	 * 
	 */
	public void setRepairNoteText(String repairNoteText) {
		this.repairNoteText = repairNoteText;
	}


	/**
	 * Gets the estimatedRepairDate attribute.
	 * 
	 * @return Returns the estimatedRepairDate
	 * 
	 */
	public Date getEstimatedRepairDate() { 
		return estimatedRepairDate;
	}

	/**
	 * Sets the estimatedRepairDate attribute.
	 * 
	 * @param estimatedRepairDate The estimatedRepairDate to set.
	 * 
	 */
	public void setEstimatedRepairDate(Date estimatedRepairDate) {
		this.estimatedRepairDate = estimatedRepairDate;
	}


	/**
	 * Gets the repairDate attribute.
	 * 
	 * @return Returns the repairDate
	 * 
	 */
	public Date getRepairDate() { 
		return repairDate;
	}

	/**
	 * Sets the repairDate attribute.
	 * 
	 * @param repairDate The repairDate to set.
	 * 
	 */
	public void setRepairDate(Date repairDate) {
		this.repairDate = repairDate;
	}


	/**
	 * Gets the repairAmount attribute.
	 * 
	 * @return Returns the repairAmount
	 * 
	 */
	public KualiDecimal getRepairAmount() { 
		return repairAmount;
	}

	/**
	 * Sets the repairAmount attribute.
	 * 
	 * @param repairAmount The repairAmount to set.
	 * 
	 */
	public void setRepairAmount(KualiDecimal repairAmount) {
		this.repairAmount = repairAmount;
	}


	/**
	 * Gets the repairSolutionDescription attribute.
	 * 
	 * @return Returns the repairSolutionDescription
	 * 
	 */
	public String getRepairSolutionDescription() { 
		return repairSolutionDescription;
	}

	/**
	 * Sets the repairSolutionDescription attribute.
	 * 
	 * @param repairSolutionDescription The repairSolutionDescription to set.
	 * 
	 */
	public void setRepairSolutionDescription(String repairSolutionDescription) {
		this.repairSolutionDescription = repairSolutionDescription;
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
        if (this.incidentDate != null) {
            m.put("incidentDate", this.incidentDate.toString());
        }
	    return m;
    }
}

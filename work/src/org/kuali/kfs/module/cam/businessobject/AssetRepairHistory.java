/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cam.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetRepairHistory extends PersistableBusinessObjectBase implements MutableInactivatable {

	private Long capitalAssetNumber;
	private Date incidentDate;
	private String problemDescription;
	private String repairContactName;
	private String repairNoteText;
	private Date estimatedRepairDate;
	private Date repairDate;
	private KualiDecimal repairAmount;
	private String repairSolutionDescription;
	private boolean active;

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
    * Gets the active attribute.
    *
    * @return Returns the active
    */
   @Override
public boolean isActive() {
       return active;
   }

   /**
    * Sets the active attribute.
    *
    * @param active The active to set.
    */
   @Override
public void setActive(boolean active) {
       this.active = active;
   }

	/**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
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

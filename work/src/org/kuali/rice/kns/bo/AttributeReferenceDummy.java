/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns.bo;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * Attribute Reference Dummy Business Object
 */
public class AttributeReferenceDummy extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 6582568341825342401L;
    private String oneDigitTextCode;
    private String twoDigitTextCode;
    private String genericSystemId;
    private Date genericDate;
    private Timestamp genericTimestamp;
    private boolean genericBoolean;
    private boolean activeIndicator;
    private KualiDecimal genericAmount;
    private String genericBigText;
    private String emailAddress;
    private KualiPercent percent;
    private boolean newCollectionRecord;
    private String workflowDocumentStatus;
    private Date createDate;
    private String initiatorNetworkId;
    private Date activeFromDate;
    private Date activeToDate;
    private Date activeAsOfDate;
    private boolean current;

    /**
     * 
     * Constructs a AttributeReferenceDummy.java.
     * 
     */
    public AttributeReferenceDummy() {
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return this.createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the initiatorNetworkId
     */
    public String getInitiatorNetworkId() {
        return this.initiatorNetworkId;
    }

    /**
     * @param initiatorNetworkId the initiatorNetworkId to set
     */
    public void setInitiatorNetworkId(String initiatorNetworkId) {
        this.initiatorNetworkId = initiatorNetworkId;
    }

    /**
     * Gets the percent attribute.
     * 
     * @return Returns the percent.
     */
    public KualiPercent getPercent() {
        return percent;
    }

    /**
     * Sets the percent attribute value.
     * 
     * @param percent The percent to set.
     */
    public void setPercent(KualiPercent percent) {
        this.percent = percent;
    }


    /**
     * Gets the genericSystemId attribute.
     * 
     * @return Returns the genericSystemId.
     */
    public String getGenericSystemId() {
        return genericSystemId;
    }

    /**
     * Sets the genericSystemId attribute value.
     * 
     * @param genericSystemId The genericSystemId to set.
     */
    public void setGenericSystemId(String genericSystemId) {
        this.genericSystemId = genericSystemId;
    }

    /**
     * Gets the oneDigitTextCode attribute.
     * 
     * @return Returns the oneDigitTextCode.
     */
    public String getOneDigitTextCode() {
        return oneDigitTextCode;
    }

    public Timestamp getGenericTimestamp() {
        return genericTimestamp;
    }

    public void setGenericTimestamp(Timestamp genericTimestamp) {
        this.genericTimestamp = genericTimestamp;
    }

    /**
     * Sets the oneDigitTextCode attribute value.
     * 
     * @param oneDigitTextCode The oneDigitTextCode to set.
     */
    public void setOneDigitTextCode(String oneDigitTextCode) {
        this.oneDigitTextCode = oneDigitTextCode;
    }

    /**
     * Gets the twoDigitTextCode attribute.
     * 
     * @return Returns the twoDigitTextCode.
     */
    public String getTwoDigitTextCode() {
        return twoDigitTextCode;
    }

    /**
     * Sets the twoDigitTextCode attribute value.
     * 
     * @param twoDigitTextCode The twoDigitTextCode to set.
     */
    public void setTwoDigitTextCode(String twoDigitTextCode) {
        this.twoDigitTextCode = twoDigitTextCode;
    }

    /**
     * Gets the genericDate attribute.
     * 
     * @return Returns the genericDate.
     */
    public Date getGenericDate() {
        return genericDate;
    }

    /**
     * Sets the genericDate attribute value.
     * 
     * @param genericDate The genericDate to set.
     */
    public void setGenericDate(Date genericDate) {
        this.genericDate = genericDate;
    }

    /**
     * Gets the genericBoolean attribute.
     * 
     * @return Returns the genericBoolean.
     */
    public boolean isGenericBoolean() {
        return genericBoolean;
    }

    /**
     * Sets the genericBoolean attribute value.
     * 
     * @param genericBoolean The genericBoolean to set.
     */
    public void setGenericBoolean(boolean genericBoolean) {
        this.genericBoolean = genericBoolean;
    }

    /**
     * Gets the activeIndicator attribute.
     * 
     * @return Returns the activeIndicator.
     */
    public boolean isActiveIndicator() {
        return activeIndicator;
    }

    /**
     * Sets the activeIndicator attribute value.
     * 
     * @param activeIndicator The activeIndicator to set.
     */
    public void setActiveIndicator(boolean activeIndicator) {
        this.activeIndicator = activeIndicator;
    }

    /**
     * Gets the genericAmount attribute.
     * 
     * @return Returns the genericAmount.
     */
    public KualiDecimal getGenericAmount() {
        return genericAmount;
    }

    /**
     * Sets the genericAmount attribute value.
     * 
     * @param genericAmount The genericAmount to set.
     */
    public void setGenericAmount(KualiDecimal genericAmount) {
        this.genericAmount = genericAmount;
    }

    /**
     * Gets the genericBigText attribute.
     * 
     * @return Returns the genericBigText.
     */
    public String getGenericBigText() {
        return genericBigText;
    }

    /**
     * Sets the genericBigText attribute value.
     * 
     * @param genericBigText The genericBigText to set.
     */
    public void setGenericBigText(String genericBigText) {
        this.genericBigText = genericBigText;
    }

    /**
     * Gets the emailAddress attribute.
     * 
     * @return Returns the emailAddress.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the emailAddress attribute value.
     * 
     * @param emailAddress The emailAddress to set.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Gets the newCollectionRecord attribute. 
     * @return Returns the newCollectionRecord.
     */
    public boolean isNewCollectionRecord() {
        return newCollectionRecord;
    }

    /**
     * Sets the newCollectionRecord attribute value.
     * @param newCollectionRecord The newCollectionRecord to set.
     */
    public void setNewCollectionRecord(boolean newCollectionRecord) {
        this.newCollectionRecord = newCollectionRecord;
    }

    /**
     * @return the workflowDocumentStatus
     */
    public String getWorkflowDocumentStatus() {
        return this.workflowDocumentStatus;
    }

    /**
     * @param workflowDocumentStatus the workflowDocumentStatus to set
     */
    public void setWorkflowDocumentStatus(String workflowDocumentStatus) {
        this.workflowDocumentStatus = workflowDocumentStatus;
    }
    
    public Date getActiveFromDate() {
		return this.activeFromDate;
	}

	public void setActiveFromDate(Date activeFromDate) {
		this.activeFromDate = activeFromDate;
	}

	public Date getActiveToDate() {
		return this.activeToDate;
	}

	public void setActiveToDate(Date activeToDate) {
		this.activeToDate = activeToDate;
	}

	public Date getActiveAsOfDate() {
		return this.activeAsOfDate;
	}

	public void setActiveAsOfDate(Date activeAsOfDate) {
		this.activeAsOfDate = activeAsOfDate;
	}
	
	public boolean isCurrent() {
		return this.current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}
}

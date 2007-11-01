/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Class representing a ResearchRiskType.
 */
public class ResearchRiskType extends PersistableBusinessObjectBase {

    /**
     * Constant values for research risk type notification values
     */
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String ALL = "A";
    public static final String NEVER = "X";

    private String researchRiskTypeCode;
    private boolean dataObjectMaintenanceCodeActiveIndicator;
    private String researchRiskTypeDescription;
    private String controlAttributeTypeCode;
    private Integer researchRiskTypeSortNumber;
    private String researchRiskTypeNotificationGroupText;
    private String researchRiskTypeNotificationValue;

    private ControlAttributeType controlAttributeType;
    private Workgroup routeWorkgroup;

    /**
     * Default constructor.
     */
    public ResearchRiskType() {

    }

    /**
     * Gets the researchRiskTypeCode attribute.
     * 
     * @return Returns the researchRiskTypeCode
     */
    public String getResearchRiskTypeCode() {
        return researchRiskTypeCode;
    }

    /**
     * Sets the researchRiskTypeCode attribute.
     * 
     * @param researchRiskTypeCode The researchRiskTypeCode to set.
     */
    public void setResearchRiskTypeCode(String researchRiskTypeCode) {
        this.researchRiskTypeCode = researchRiskTypeCode;
    }


    /**
     * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
     * 
     * @return Returns the dataObjectMaintenanceCodeActiveIndicator
     */
    public boolean isDataObjectMaintenanceCodeActiveIndicator() {
        return dataObjectMaintenanceCodeActiveIndicator;
    }

    /**
     * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
     * 
     * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
     */
    public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
        this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
    }


    /**
     * Gets the researchRiskTypeDescription attribute.
     * 
     * @return Returns the researchRiskTypeDescription
     */
    public String getResearchRiskTypeDescription() {
        return researchRiskTypeDescription;
    }

    /**
     * Sets the researchRiskTypeDescription attribute.
     * 
     * @param researchRiskTypeDescription The researchRiskTypeDescription to set.
     */
    public void setResearchRiskTypeDescription(String researchRiskTypeDescription) {
        this.researchRiskTypeDescription = researchRiskTypeDescription;
    }

    /**
     * Gets the controlAttributeTypeCode attribute.
     * 
     * @return Returns the controlAttributeTypeCode.
     */
    public String getControlAttributeTypeCode() {
        return controlAttributeTypeCode;
    }

    /**
     * Sets the controlAttributeTypeCode attribute value.
     * 
     * @param controlAttributeTypeCode The controlAttributeTypeCode to set.
     */
    public void setControlAttributeTypeCode(String controlAttributeTypeCode) {
        this.controlAttributeTypeCode = controlAttributeTypeCode;
    }

    /**
     * Gets the researchRiskTypeSortNumber attribute.
     * 
     * @return Returns the researchRiskTypeSortNumber.
     */
    public Integer getResearchRiskTypeSortNumber() {
        return researchRiskTypeSortNumber;
    }

    /**
     * Sets the researchRiskTypeSortNumber attribute value.
     * 
     * @param researchRiskTypeSortNumber The researchRiskTypeSortNumber to set.
     */
    public void setResearchRiskTypeSortNumber(Integer researchRiskTypeSortNumber) {
        this.researchRiskTypeSortNumber = researchRiskTypeSortNumber;
    }

    /**
     * Gets the researchRiskTypeNotificationGroupText attribute.
     * 
     * @return Returns the researchRiskTypeNotificationGroupText.
     */
    public String getResearchRiskTypeNotificationGroupText() {
        return researchRiskTypeNotificationGroupText;
    }

    /**
     * Sets the researchRiskTypeNotificationGroupText attribute value.
     * 
     * @param researchRiskTypeNotificationGroupText The researchRiskTypeNotificationGroupText to set.
     */
    public void setResearchRiskTypeNotificationGroupText(String researchRiskTypeNotificationGroupText) {
        this.researchRiskTypeNotificationGroupText = researchRiskTypeNotificationGroupText;
    }

    /**
     * Gets the researchRiskTypeNotificationValue attribute.
     * 
     * @return Returns the researchRiskTypeNotificationValue.
     */
    public String getResearchRiskTypeNotificationValue() {
        return researchRiskTypeNotificationValue;
    }

    /**
     * Sets the researchRiskTypeNotificationValue attribute value.
     * 
     * @param researchRiskTypeNotificationValue The researchRiskTypeNotificationValue to set.
     */
    public void setResearchRiskTypeNotificationValue(String researchRiskTypeNotificationValue) {
        this.researchRiskTypeNotificationValue = researchRiskTypeNotificationValue;
    }

    /**
     * Gets the controlAttributeType attribute.
     * 
     * @return Returns the controlAttributeType.
     */
    public ControlAttributeType getControlAttributeType() {
        return controlAttributeType;
    }

    /**
     * Sets the controlAttributeType attribute value.
     * 
     * @param controlAttributeType The controlAttributeType to set.
     * @deprecated
     */
    public void setControlAttributeType(ControlAttributeType controlAttributeType) {
        this.controlAttributeType = controlAttributeType;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("researchRiskTypeCode", this.researchRiskTypeCode);
        return m;
    }
}

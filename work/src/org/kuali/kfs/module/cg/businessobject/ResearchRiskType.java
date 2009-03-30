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

package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * Class representing a ResearchRiskType.
 */
public class ResearchRiskType extends PersistableBusinessObjectBase implements Inactivateable {

    /**
     * Constant values for research risk type notification values
     */
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String ALL = "A";
    public static final String NEVER = "X";

    private String researchRiskTypeCode;
    private boolean active;
    private String researchRiskTypeDescription;
    private String controlAttributeTypeCode;
    private Integer researchRiskTypeSortNumber;
    private String researchRiskTypeNotificationValue;

    private ControlAttributeType controlAttributeType;

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
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
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
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("researchRiskTypeCode", this.researchRiskTypeCode);
        return m;
    }
}

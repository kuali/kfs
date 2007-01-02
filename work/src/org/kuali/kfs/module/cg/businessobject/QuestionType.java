/*
 * Copyright 2006 The Kuali Foundation.
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

/**
 * 
 */
public class QuestionType extends PersistableBusinessObjectBase {
    
    private String questionTypeCode;
    private boolean dataObjectMaintenanceCodeActiveIndicator;
    private String questionTypeDescription;
    private Integer questionTypeSortNumber;
    private String questionTypeWorkgroupName;
    private boolean questionTypeWorkgroupIndicator;
    
    /**
     * Gets the dataObjectMaintenanceCodeActiveIndicator attribute. 
     * @return Returns the dataObjectMaintenanceCodeActiveIndicator.
     */
    public boolean isDataObjectMaintenanceCodeActiveIndicator() {
        return dataObjectMaintenanceCodeActiveIndicator;
    }

    /**
     * Sets the dataObjectMaintenanceCodeActiveIndicator attribute value.
     * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
     */
    public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
        this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
    }

    /**
     * Gets the questionTypeCode attribute. 
     * @return Returns the questionTypeCode.
     */
    public String getQuestionTypeCode() {
        return questionTypeCode;
    }

    /**
     * Sets the questionTypeCode attribute value.
     * @param questionTypeCode The questionTypeCode to set.
     */
    public void setQuestionTypeCode(String questionTypeCode) {
        this.questionTypeCode = questionTypeCode;
    }

    /**
     * Gets the questionTypeDescription attribute. 
     * @return Returns the questionTypeDescription.
     */
    public String getQuestionTypeDescription() {
        return questionTypeDescription;
    }

    /**
     * Sets the questionTypeDescription attribute value.
     * @param questionTypeDescription The questionTypeDescription to set.
     */
    public void setQuestionTypeDescription(String questionTypeDescription) {
        this.questionTypeDescription = questionTypeDescription;
    }

    /**
     * Gets the questionTypeSortNumber attribute. 
     * @return Returns the questionTypeSortNumber.
     */
    public Integer getQuestionTypeSortNumber() {
        return questionTypeSortNumber;
    }

    /**
     * Sets the questionTypeSortNumber attribute value.
     * @param questionTypeSortNumber The questionTypeSortNumber to set.
     */
    public void setQuestionTypeSortNumber(Integer questionTypeSortNumber) {
        this.questionTypeSortNumber = questionTypeSortNumber;
    }

    /**
     * Gets the questionTypeWorkgroupIndicator attribute. 
     * @return Returns the questionTypeWorkgroupIndicator.
     */
    public boolean isQuestionTypeWorkgroupIndicator() {
        return questionTypeWorkgroupIndicator;
    }

    /**
     * Sets the questionTypeWorkgroupIndicator attribute value.
     * @param questionTypeWorkgroupIndicator The questionTypeWorkgroupIndicator to set.
     */
    public void setQuestionTypeWorkgroupIndicator(boolean questionTypeWorkgroupIndicator) {
        this.questionTypeWorkgroupIndicator = questionTypeWorkgroupIndicator;
    }

    /**
     * Gets the questionTypeWorkgroupName attribute. 
     * @return Returns the questionTypeWorkgroupName.
     */
    public String getQuestionTypeWorkgroupName() {
        return questionTypeWorkgroupName;
    }

    /**
     * Sets the questionTypeWorkgroupName attribute value.
     * @param questionTypeWorkgroupName The questionTypeWorkgroupName to set.
     */
    public void setQuestionTypeWorkgroupName(String questionTypeWorkgroupName) {
        this.questionTypeWorkgroupName = questionTypeWorkgroupName;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("questionTypeCode", this.questionTypeCode);
        return m;
    }
}

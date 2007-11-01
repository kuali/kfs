/*
 * Copyright 2007 The Kuali Foundation.
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
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormResearchTypeCode extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String researchTypeCode;

    private ResearchTypeCode researchType;

    /**
     * Default constructor.
     */
    public RoutingFormResearchTypeCode() {

    }

    /**
     * Constructs a RoutingFormResearchTypeCode.
     * 
     * @param documentNumber
     * @param researchType
     */
    public RoutingFormResearchTypeCode(String documentNumber, ResearchTypeCode researchType) {
        this();
        this.documentNumber = documentNumber;
        this.researchTypeCode = researchType.getResearchTypeCode();
        this.researchType = researchType;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the researchTypeCode attribute.
     * 
     * @return Returns the researchTypeCode
     */
    public String getResearchTypeCode() {
        return researchTypeCode;
    }

    /**
     * Sets the researchTypeCode attribute.
     * 
     * @param researchTypeCode The researchTypeCode to set.
     */
    public void setResearchTypeCode(String researchTypeCode) {
        this.researchTypeCode = researchTypeCode;
    }

    /**
     * Gets the researchType attribute.
     * 
     * @return Returns the researchType
     */
    public ResearchTypeCode getResearchType() {
        return researchType;
    }

    /**
     * Sets the researchType attribute.
     * 
     * @param researchType The researchType to set.
     */
    public void setResearchType(ResearchTypeCode researchType) {
        this.researchType = researchType;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("researchTypeCode", this.researchTypeCode);
        return m;
    }
}

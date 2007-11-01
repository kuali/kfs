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

public class RoutingFormPurpose extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String purposeCode;

    private Purpose purpose;

    /**
     * Default constructor.
     */
    public RoutingFormPurpose() {

    }

    /**
     * Constructs a RoutingFormPurpose.
     * 
     * @param documentNumber
     * @param purpose
     */
    public RoutingFormPurpose(String documentNumber, Purpose purpose) {
        this();
        this.documentNumber = documentNumber;
        this.purposeCode = purpose.getPurposeCode();
        this.purpose = purpose;
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
     * Gets the purposeCode attribute.
     * 
     * @return Returns the purposeCode
     */
    public String getPurposeCode() {
        return purposeCode;
    }

    /**
     * Sets the purposeCode attribute.
     * 
     * @param purposeCode The purposeCode to set.
     */
    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    /**
     * Gets the purpose attribute.
     * 
     * @return Returns the purpose
     */
    public Purpose getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose attribute.
     * 
     * @param purpose The purpose to set.
     */
    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("purposeCode", this.purposeCode);
        return m;
    }
}

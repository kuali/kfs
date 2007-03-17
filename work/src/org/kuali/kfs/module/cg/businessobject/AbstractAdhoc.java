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
package org.kuali.module.kra.bo;

import java.sql.Timestamp;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Abstract class that represents all KRA ad-hoc types.
 * 
 * 
 */
public abstract class AbstractAdhoc extends PersistableBusinessObjectBase {
    
    private String documentNumber;
    private String adhocTypeCode;
    private String permissionCode;
    private String addedByPerson;
    private Timestamp personAddedTimestamp;
    private String actionRequested;
    
    /**
     * Gets the addedByPerson attribute. 
     * @return Returns the addedByPerson.
     */
    public String getAddedByPerson() {
        return addedByPerson;
    }
    /**
     * Sets the addedByPerson attribute value.
     * @param addedByPerson The addedByPerson to set.
     */
    public void setAddedByPerson(String addedByPerson) {
        this.addedByPerson = addedByPerson;
    }
    /**
     * Gets the adhocTypeCode attribute. 
     * @return Returns the adhocTypeCode.
     */
    public String getAdhocTypeCode() {
        return adhocTypeCode;
    }
    /**
     * Sets the adhocTypeCode attribute value.
     * @param adhocTypeCode The adhocTypeCode to set.
     */
    public void setAdhocTypeCode(String adhocTypeCode) {
        this.adhocTypeCode = adhocTypeCode;
    }
    /**
     * Gets the permissionCode attribute. 
     * @return Returns the permissionCode.
     */
    public String getPermissionCode() {
        return permissionCode;
    }
    /**
     * Sets the permissionCode attribute value.
     * @param permissionCode The permissionCode to set.
     */
    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }
    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }
    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    /**
     * Gets the personAddedTimestamp attribute. 
     * @return Returns the personAddedTimestamp.
     */
    public Timestamp getPersonAddedTimestamp() {
        return personAddedTimestamp;
    }
    /**
     * Sets the personAddedTimestamp attribute value.
     * @param personAddedTimestamp The personAddedTimestamp to set.
     */
    public void setPersonAddedTimestamp(Timestamp personAddedTimestamp) {
        this.personAddedTimestamp = personAddedTimestamp;
    }
    public String getActionRequested() {
        return actionRequested;
    }
    public void setActionRequested(String actionRequested) {
        this.actionRequested = actionRequested;
    }
}

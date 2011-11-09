/*
 * Copyright 2006-2009 The Kuali Foundation
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

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Asset Transaction Type Business Object.
 */
public class AssetLock extends PersistableBusinessObjectBase{

    private String documentNumber;
    private Long capitalAssetNumber;
    private String lockingInformation;
    private String documentTypeName;

    /**
     * Default constructor.
     */
    public AssetLock() {

    }

    public AssetLock(String documentNumber, Long capitalAssetNumber, String lockingInformation, String documentType) {
        this.documentNumber = documentNumber;
        this.capitalAssetNumber = capitalAssetNumber;
        this.lockingInformation = lockingInformation;
        this.documentTypeName = documentType;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("capitalAssetNumber", this.capitalAssetNumber);
        m.put("lockingInformation", this.lockingInformation);
        return m;
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
     * Gets the capitalAssetNumber attribute. 
     * @return Returns the capitalAssetNumber.
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute value.
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    /**
     * Gets the lockingInformation attribute. 
     * @return Returns the lockingInformation.
     */
    public String getLockingInformation() {
        return lockingInformation;
    }

    /**
     * Sets the lockingInformation attribute value.
     * @param lockingInformation The lockingInformation to set.
     */
    public void setLockingInformation(String lockingInformation) {
        this.lockingInformation = lockingInformation;
    }

    /**
     * Gets the documentTypeName attribute. 
     * @return Returns the documentTypeName.
     */
    public String getDocumentTypeName() {
        return documentTypeName;
    }

    /**
     * Sets the documentTypeName attribute value.
     * @param documentTypeName The documentTypeName to set.
     */
    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }
    
    
}

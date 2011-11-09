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
package org.kuali.kfs.module.cab.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class GeneralLedgerEntryAsset extends PersistableBusinessObjectBase {

    private Long generalLedgerAccountIdentifier;
    private Integer capitalAssetBuilderLineNumber;
    private String capitalAssetManagementDocumentNumber;
    private GeneralLedgerEntry generalLedgerEntry;

    /**
     * Gets the generalLedgerAccountIdentifier attribute.
     * 
     * @return Returns the generalLedgerAccountIdentifier
     */
    public Long getGeneralLedgerAccountIdentifier() {
        return generalLedgerAccountIdentifier;
    }

    /**
     * Sets the generalLedgerAccountIdentifier attribute.
     * 
     * @param generalLedgerAccountIdentifier The generalLedgerAccountIdentifier to set.
     */
    public void setGeneralLedgerAccountIdentifier(Long generalLedgerAccountIdentifier) {
        this.generalLedgerAccountIdentifier = generalLedgerAccountIdentifier;
    }

    /**
     * Gets the capitalAssetBuilderLineNumber attribute.
     * 
     * @return Returns the capitalAssetBuilderLineNumber
     */
    public Integer getCapitalAssetBuilderLineNumber() {
        return capitalAssetBuilderLineNumber;
    }

    /**
     * Sets the capitalAssetBuilderLineNumber attribute.
     * 
     * @param capitalAssetBuilderLineNumber The capitalAssetBuilderLineNumber to set.
     */
    public void setCapitalAssetBuilderLineNumber(Integer capitalAssetBuilderLineNumber) {
        this.capitalAssetBuilderLineNumber = capitalAssetBuilderLineNumber;
    }


    /**
     * Gets the capitalAssetManagementDocumentNumber attribute.
     * 
     * @return Returns the capitalAssetManagementDocumentNumber
     */
    public String getCapitalAssetManagementDocumentNumber() {
        return capitalAssetManagementDocumentNumber;
    }

    /**
     * Sets the capitalAssetManagementDocumentNumber attribute.
     * 
     * @param capitalAssetManagementDocumentNumber The capitalAssetManagementDocumentNumber to set.
     */
    public void setCapitalAssetManagementDocumentNumber(String capitalAssetManagementDocumentNumber) {
        this.capitalAssetManagementDocumentNumber = capitalAssetManagementDocumentNumber;
    }

    /**
     * Gets the generalLedgerEntry attribute.
     * 
     * @return Returns the generalLedgerEntry.
     */
    public GeneralLedgerEntry getGeneralLedgerEntry() {
        return generalLedgerEntry;
    }

    /**
     * Sets the generalLedgerEntry attribute value.
     * 
     * @param generalLedgerEntry The generalLedgerEntry to set.
     */
    public void setGeneralLedgerEntry(GeneralLedgerEntry generalLedgerEntry) {
        this.generalLedgerEntry = generalLedgerEntry;
    }
}

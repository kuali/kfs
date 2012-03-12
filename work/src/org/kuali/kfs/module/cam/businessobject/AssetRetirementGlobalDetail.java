/*
 * Copyright 2008-2009 The Kuali Foundation
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

import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetailBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetRetirementGlobalDetail extends GlobalBusinessObjectDetailBase {

    private String documentNumber;
    private Long capitalAssetNumber;
    private DocumentHeader documentHeader;
    private Asset asset;
    private AssetRetirementGlobal assetRetirementGlobal;

    /**
     * Default constructor.
     */
    public AssetRetirementGlobalDetail() {
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
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute.
     * 
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    /**
     * Gets the asset attribute.
     * 
     * @return Returns the asset.
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute value.
     * 
     * @param asset The asset to set.
     * @deprecated
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }


    public AssetRetirementGlobal getAssetRetirementGlobal() {
        return assetRetirementGlobal;
    }

    public void setAssetRetirementGlobal(AssetRetirementGlobal assetRetirementGlobal) {
        this.assetRetirementGlobal = assetRetirementGlobal;
    }


    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        return m;
    }
}

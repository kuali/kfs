/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.businessobject;


import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.integration.businessobject.CapitalAssetLocation;
import org.kuali.kfs.integration.businessobject.CapitalAssetSystem;
import org.kuali.kfs.integration.businessobject.ItemCapitalAsset;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.TypedArrayList;

public abstract class PurchasingCapitalAssetSystemBase extends PersistableBusinessObjectBase implements CapitalAssetSystem {

    private Integer capitalAssetSystemIdentifier;
    private String capitalAssetSystemDescription;
    private boolean capitalAssetNotReceivedCurrentFiscalYearIndicator;
    private String capitalAssetTypeCode;
    private String capitalAssetManufacturerName;
    private String capitalAssetModelDescription;
    private String capitalAssetNoteText;
    private List<ItemCapitalAsset> purchasingItemCapitalAssets;
    private List<CapitalAssetLocation> purchasingCapitalAssetLocations;

    public PurchasingCapitalAssetSystemBase() {
        super();
        purchasingItemCapitalAssets = new TypedArrayList(getItemCapitalAssetClass());
        purchasingCapitalAssetLocations = new TypedArrayList(getCapitalAssetLocationClass());
    }

    public String getCapitalAssetSystemDescription() {
        return capitalAssetSystemDescription;
    }

    public void setCapitalAssetSystemDescription(String capitalAssetSystemDescription) {
        this.capitalAssetSystemDescription = capitalAssetSystemDescription;
    }

    public boolean isCapitalAssetNotReceivedCurrentFiscalYearIndicator() {
        return capitalAssetNotReceivedCurrentFiscalYearIndicator;
    }

    public void setCapitalAssetNotReceivedCurrentFiscalYearIndicator(boolean capitalAssetNotReceivedCurrentFiscalYearIndicator) {
        this.capitalAssetNotReceivedCurrentFiscalYearIndicator = capitalAssetNotReceivedCurrentFiscalYearIndicator;
    }

    public String getCapitalAssetTypeCode() {
        return capitalAssetTypeCode;
    }

    public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
        this.capitalAssetTypeCode = capitalAssetTypeCode;
    }

    public String getCapitalAssetManufacturerName() {
        return capitalAssetManufacturerName;
    }

    public void setCapitalAssetManufacturerName(String capitalAssetManufacturerName) {
        this.capitalAssetManufacturerName = capitalAssetManufacturerName;
    }

    public String getCapitalAssetModelDescription() {
        return capitalAssetModelDescription;
    }

    public void setCapitalAssetModelDescription(String capitalAssetModelDescription) {
        this.capitalAssetModelDescription = capitalAssetModelDescription;
    }

    public List<ItemCapitalAsset> getPurchasingItemCapitalAssets() {
        return purchasingItemCapitalAssets;
    }

    public void setPurchasingItemCapitalAssets(List<ItemCapitalAsset> purchasingItemCapitalAssets) {
        this.purchasingItemCapitalAssets = purchasingItemCapitalAssets;
    }

    public List<CapitalAssetLocation> getPurchasingCapitalAssetLocations() {
        return purchasingCapitalAssetLocations;
    }

    public void setPurchasingCapitalAssetLocations(List<CapitalAssetLocation> purchasingCapitalAssetLocations) {
        this.purchasingCapitalAssetLocations = purchasingCapitalAssetLocations;
    }

    public Integer getCapitalAssetSystemIdentifier() {
        return capitalAssetSystemIdentifier;
    }

    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier) {
        this.capitalAssetSystemIdentifier = capitalAssetSystemIdentifier;
    }

    public String getCapitalAssetNoteText() {
        return capitalAssetNoteText;
    }

    public void setCapitalAssetNoteText(String capitalAssetNoteText) {
        this.capitalAssetNoteText = capitalAssetNoteText;
    }

    public abstract Class getItemCapitalAssetClass();
    
    public abstract Class getCapitalAssetLocationClass();
    
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();	            
        if (this.capitalAssetSystemIdentifier != null) {
            m.put("capitalAssetSystemIdentifier", this.capitalAssetSystemIdentifier.toString());
        }
        return m;
    }

}
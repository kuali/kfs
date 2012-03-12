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
package org.kuali.kfs.module.purap.businessobject;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public abstract class PurchasingCapitalAssetSystemBase extends PersistableBusinessObjectBase implements CapitalAssetSystem {

    private Integer capitalAssetSystemIdentifier;
    private String capitalAssetSystemDescription;
    private boolean capitalAssetNotReceivedCurrentFiscalYearIndicator;
    private String capitalAssetTypeCode;
    private String capitalAssetManufacturerName;
    private String capitalAssetModelDescription;
    private String capitalAssetNoteText;
    private List<ItemCapitalAsset> itemCapitalAssets;
    private List<CapitalAssetLocation> capitalAssetLocations;
    private CapitalAssetLocation newPurchasingCapitalAssetLocationLine;
    private Integer capitalAssetCountAssetNumber;
    
    public Integer getCapitalAssetCountAssetNumber() {
		return capitalAssetCountAssetNumber;
	}

	public void setCapitalAssetCountAssetNumber(Integer capitalAssetCountAssetNumber) {
		this.capitalAssetCountAssetNumber = capitalAssetCountAssetNumber;
	}

	public PurchasingCapitalAssetSystemBase() {
        super();
        itemCapitalAssets = new ArrayList();
        capitalAssetLocations = new ArrayList();
        this.setNewPurchasingCapitalAssetLocationLine(this.setupNewPurchasingCapitalAssetLocationLine());
    }

    public String getCapitalAssetSystemDescription() {
        //This is needed because bean:define would throw error if capitalAssetSystemDescription is empty.
        if (StringUtils.isEmpty(capitalAssetSystemDescription)) {
            capitalAssetSystemDescription = new String(" ");
        }
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

    public List<ItemCapitalAsset> getItemCapitalAssets() {
        return itemCapitalAssets;
    }

    public void setItemCapitalAssets(List<ItemCapitalAsset> itemCapitalAssets) {
        this.itemCapitalAssets = itemCapitalAssets;
    }

    public List<CapitalAssetLocation> getCapitalAssetLocations() {
        return capitalAssetLocations;
    }

    public void setCapitalAssetLocations(List<CapitalAssetLocation> capitalAssetLocations) {
        this.capitalAssetLocations = capitalAssetLocations;
    }

    public Integer getCapitalAssetSystemIdentifier() {
        return capitalAssetSystemIdentifier;
    }

    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier) {
        this.capitalAssetSystemIdentifier = capitalAssetSystemIdentifier;
    }

    public String getCapitalAssetNoteText() {
        //This is needed because bean:define would throw error if capitalAssetNoteText is empty.
        if (StringUtils.isEmpty(capitalAssetNoteText)) {
            capitalAssetNoteText = new String(" ");
        }
        return capitalAssetNoteText;
    }

    public void setCapitalAssetNoteText(String capitalAssetNoteText) {
        this.capitalAssetNoteText = capitalAssetNoteText;
    }

    public boolean isEmpty() {
        return !(StringUtils.isNotEmpty(capitalAssetNoteText) || StringUtils.isNotEmpty(capitalAssetSystemDescription) || StringUtils.isNotEmpty(capitalAssetManufacturerName) || StringUtils.isNotEmpty(this.capitalAssetModelDescription) || StringUtils.isNotEmpty(capitalAssetTypeCode));
    }
    
    public abstract Class getItemCapitalAssetClass();
    
    public abstract Class getCapitalAssetLocationClass();
 
    //CAMS LOCATION   
    public CapitalAssetLocation setupNewPurchasingCapitalAssetLocationLine() {
        CapitalAssetLocation location = null; 
        try{
            location = (CapitalAssetLocation)getCapitalAssetLocationClass().newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Unable to get class");
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to get class");
        }
        catch (NullPointerException e) {
            throw new RuntimeException("Can't instantiate Purchasing Account from base");
        }

        return location;
    }

    public void setNewPurchasingCapitalAssetLocationLine(CapitalAssetLocation newCapitalAssetLocationLine) {
        this.newPurchasingCapitalAssetLocationLine = newCapitalAssetLocationLine;
    }

    public CapitalAssetLocation getNewPurchasingCapitalAssetLocationLine() {
        return newPurchasingCapitalAssetLocationLine;
    }

    public CapitalAssetLocation getAndResetNewPurchasingCapitalAssetLocationLine() {
        CapitalAssetLocation asset = getNewPurchasingCapitalAssetLocationLine();
        setNewPurchasingCapitalAssetLocationLine(setupNewPurchasingCapitalAssetLocationLine());
        return asset;
    }
    
    public void resetNewPurchasingCapitalAssetLocationLine(){
        setNewPurchasingCapitalAssetLocationLine(setupNewPurchasingCapitalAssetLocationLine());
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();	            
        if (this.capitalAssetSystemIdentifier != null) {
            m.put("capitalAssetSystemIdentifier", this.capitalAssetSystemIdentifier.toString());
        }
        return m;
    }

}

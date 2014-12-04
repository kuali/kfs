/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

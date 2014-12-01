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
package org.kuali.kfs.integration.purap;

import java.util.List;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;


public interface CapitalAssetSystem  extends ExternalizableBusinessObject{

    public String getCapitalAssetSystemDescription();

    public void setCapitalAssetSystemDescription(String capitalAssetSystemDescription);

    public boolean isCapitalAssetNotReceivedCurrentFiscalYearIndicator();

    public void setCapitalAssetNotReceivedCurrentFiscalYearIndicator(boolean capitalAssetNotReceivedCurrentFiscalYearIndicator);

    public String getCapitalAssetTypeCode();

    public void setCapitalAssetTypeCode(String capitalAssetTypeCode);

    public String getCapitalAssetManufacturerName();

    public void setCapitalAssetManufacturerName(String capitalAssetManufacturerName);

    public String getCapitalAssetModelDescription();

    public void setCapitalAssetModelDescription(String capitalAssetModelDescription);

    public List<ItemCapitalAsset> getItemCapitalAssets();

    public void setItemCapitalAssets(List<ItemCapitalAsset> itemCapitalAssets);

    public List<CapitalAssetLocation> getCapitalAssetLocations();

    public void setCapitalAssetLocations(List<CapitalAssetLocation> capitalAssetLocations);

    public Integer getCapitalAssetSystemIdentifier();

    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier);

    public String getCapitalAssetNoteText();

    public void setCapitalAssetNoteText(String capitalAssetNoteText);

    public boolean isEmpty();
    
    public CapitalAssetLocation setupNewPurchasingCapitalAssetLocationLine();

    public void setNewPurchasingCapitalAssetLocationLine(CapitalAssetLocation newCapitalAssetLocationLine);

    public CapitalAssetLocation getNewPurchasingCapitalAssetLocationLine();

    public CapitalAssetLocation getAndResetNewPurchasingCapitalAssetLocationLine();
    
    public void resetNewPurchasingCapitalAssetLocationLine();
    
    public Integer getCapitalAssetCountAssetNumber();
    
    public void setCapitalAssetCountAssetNumber(Integer capitalAssetCountAssetNumber);

}

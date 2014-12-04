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
package org.kuali.kfs.integration.cam;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * An interface that declares methods to retrieve information about asset data collected by FP documents.
 */
public interface CapitalAssetManagementAsset extends ExternalizableBusinessObject {

    /**
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber
     */
    public Long getCapitalAssetNumber();
    
    /**
     * Gets the campusTagNumber attribute.
     * 
     * @return Returns the campusTagNumber
     */
    public String getCampusTagNumber();
    
    /**
     * Gets the capitalAssetTypeCode attribute.
     * 
     * @return Returns the capitalAssetTypeCode
     */
    public String getCapitalAssetTypeCode();
    
    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName
     */
    public String getVendorName();
    
    /**
     * Gets the manufacturerName attribute.
     * 
     * @return Returns the manufacturerName
     */
    public String getManufacturerName();
    
    /**
     * Gets the capitalAssetDescription attribute.
     * 
     * @return Returns the capitalAssetDescription
     */
    public String getCapitalAssetDescription();
    
    /**
     * Gets the manufacturerModelNumber attribute.
     * 
     * @return Returns the manufacturerModelNumber
     */
    public String getManufacturerModelNumber();
    
    /**
     * Gets the serialNumber attribute.
     * 
     * @return Returns the serialNumber
     */
    public String getSerialNumber();
    
    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode
     */
    public String getCampusCode();

    /**
     * Gets the buildingCode attribute.
     * 
     * @return Returns the buildingCode
     */
    public String getBuildingCode();

    /**
     * Gets the buildingRoomNumber attribute.
     * 
     * @return Returns the buildingRoomNumber
     */
    public String getBuildingRoomNumber();

    /**
     * Gets the buildingSubRoomNumber attribute.
     * 
     * @return Returns the buildingSubRoomNumber
     */
    public String getBuildingSubRoomNumber();
    
    /**
     * Gets the quantity attribute.
     * 
     * @return Returns the quantity
     */
    public Integer getQuantity();
}

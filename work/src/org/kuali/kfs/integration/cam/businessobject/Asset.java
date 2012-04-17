/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.integration.cam.businessobject;

import org.kuali.kfs.integration.cam.CapitalAssetManagementAsset;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Asset extends PersistableBusinessObjectBase implements CapitalAssetManagementAsset {
    protected Long capitalAssetNumber;
    protected String capitalAssetDescription;
    protected String capitalAssetTypeCode;
    protected String vendorName;
    protected String campusCode;
    protected String buildingCode;
    protected String buildingRoomNumber;
    protected String buildingSubRoomNumber;
    protected String campusTagNumber;
    protected String manufacturerName;
    protected String manufacturerModelNumber;
    protected String serialNumber;
    protected Integer quantity;

    /**
     * Default constructor.
     */
    public Asset() {
    }

    /**
     * Gets the capitalAssetNumber attribute.
     *
     * @return Returns the capitalAssetNumber
     */
    @Override
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
     * Gets the capitalAssetDescription attribute
     *
     * @return Returns the capitalAssetDescription
     */
    @Override
    public String getCapitalAssetDescription() {
        return capitalAssetDescription;
    }

    /**
     * Sets the capitalAssetDescription attribute.
     *
     * @param capitalAssetDescription The capitalAssetDescription to set.
     */
    public void setCapitalAssetDescription(String capitalAssetDescription) {
        this.capitalAssetDescription = capitalAssetDescription;
    }


    /**
     * Gets the capitalAssetTypeCode attribute.
     *
     * @return Returns the capitalAssetTypeCode
     */
    @Override
    public String getCapitalAssetTypeCode() {
        return capitalAssetTypeCode;
    }

    /**
     * Sets the capitalAssetTypeCode attribute.
     *
     * @param capitalAssetTypeCode The capitalAssetTypeCode to set.
     */
    public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
        this.capitalAssetTypeCode = capitalAssetTypeCode;
    }

    /**
     * Gets the vendorName attribute.
     *
     * @return Returns the vendorName
     */
    @Override
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendorName attribute.
     *
     * @param vendorName The vendorName to set.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
    /**
     * Gets the campusCode attribute.
     *
     * @return Returns the campusCode
     */
    @Override
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute.
     *
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }


    /**
     * Gets the buildingCode attribute.
     *
     * @return Returns the buildingCode
     */
    @Override
    public String getBuildingCode() {
        return buildingCode;
    }

    /**
     * Sets the buildingCode attribute.
     *
     * @param buildingCode The buildingCode to set.
     */
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }


    /**
     * Gets the buildingRoomNumber attribute.
     *
     * @return Returns the buildingRoomNumber
     */
    @Override
    public String getBuildingRoomNumber() {
        return buildingRoomNumber;
    }

    /**
     * Sets the buildingRoomNumber attribute.
     *
     * @param buildingRoomNumber The buildingRoomNumber to set.
     */
    public void setBuildingRoomNumber(String buildingRoomNumber) {
        this.buildingRoomNumber = buildingRoomNumber;
    }


    /**
     * Gets the buildingSubRoomNumber attribute.
     *
     * @return Returns the buildingSubRoomNumber
     */
    @Override
    public String getBuildingSubRoomNumber() {
        return buildingSubRoomNumber;
    }

    /**
     * Sets the buildingSubRoomNumber attribute.
     *
     * @param buildingSubRoomNumber The buildingSubRoomNumber to set.
     */
    public void setBuildingSubRoomNumber(String buildingSubRoomNumber) {
        this.buildingSubRoomNumber = buildingSubRoomNumber;
    }

    /**
     * Gets the campusTagNumber attribute.
     *
     * @return Returns the campusTagNumber
     */
    @Override
    public String getCampusTagNumber() {
        return campusTagNumber;
    }

    /**
     * Sets the campusTagNumber attribute.
     *
     * @param campusTagNumber The campusTagNumber to set.
     */
    public void setCampusTagNumber(String campusTagNumber) {
        this.campusTagNumber = campusTagNumber;
    }
    /**
     * Gets the manufacturerName attribute.
     *
     * @return Returns the manufacturerName
     */
    @Override
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * Sets the manufacturerName attribute.
     *
     * @param manufacturerName The manufacturerName to set.
     */
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }


    /**
     * Gets the manufacturerModelNumber attribute.
     *
     * @return Returns the manufacturerModelNumber
     */
    @Override
    public String getManufacturerModelNumber() {
        return manufacturerModelNumber;
    }

    /**
     * Sets the manufacturerModelNumber attribute.
     *
     * @param manufacturerModelNumber The manufacturerModelNumber to set.
     */
    public void setManufacturerModelNumber(String manufacturerModelNumber) {
        this.manufacturerModelNumber = manufacturerModelNumber;
    }


    /**
     * Gets the serialNumber attribute.
     *
     * @return Returns the serialNumber
     */
    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serialNumber attribute.
     *
     * @param serialNumber The serialNumber to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public void refresh() {}

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}

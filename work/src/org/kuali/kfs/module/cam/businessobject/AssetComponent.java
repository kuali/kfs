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
package org.kuali.kfs.module.cam.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetComponent extends PersistableBusinessObjectBase implements MutableInactivatable {

    private Long capitalAssetNumber;
    private Integer componentNumber;
    private String componentDescription;
    private String componentContactPhoneNumber;
    private String componentConditionCode;
    private Integer componentEstimatedLifetimeLimit;
    private String componentManufacturerName;
    private String componentManufacturerModelNumber;
    private String componentSerialNumber;
    private String componentOrganizationTagNumber;
    private String componentOrganizationText;
    private KualiDecimal componentReplacementAmount;
    private String componentVendorName;
    private String componentWarrantyNumber;
    private String componentWarrantyPhoneNumber;
    private String componentWarrantyContactName;
    private String componentWarrantyPurchaseOrderNumber;
    private Date componentWarrantyBeginningDate;
    private Date componentWarrantyEndingDate;
    private String componentWarrantyText;
    private String governmentTagNumber;
    private String nationalStockNumber;
    private boolean active;

    private Asset asset;
    private AssetCondition componentCondition;

    /**
     * Default constructor.
     */
    public AssetComponent() {
    }

    /**
     * Gets the capitalAssetNumber attribute.
     *
     * @return Returns the capitalAssetNumber
     *
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute.
     *
     * @param capitalAssetNumber The capitalAssetNumber to set.
     *
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }


    /**
     * Gets the componentNumber attribute.
     *
     * @return Returns the componentNumber
     *
     */
    public Integer getComponentNumber() {
        return componentNumber;
    }

    /**
     * Sets the componentNumber attribute.
     *
     * @param componentNumber The componentNumber to set.
     *
     */
    public void setComponentNumber(Integer componentNumber) {
        this.componentNumber = componentNumber;
    }


    /**
     * Gets the componentDescription attribute.
     *
     * @return Returns the componentDescription
     *
     */
    public String getComponentDescription() {
        return componentDescription;
    }

    /**
     * Sets the componentDescription attribute.
     *
     * @param componentDescription The componentDescription to set.
     *
     */
    public void setComponentDescription(String componentDescription) {
        this.componentDescription = componentDescription;
    }


    /**
     * Gets the componentContactPhoneNumber attribute.
     *
     * @return Returns the componentContactPhoneNumber
     *
     */
    public String getComponentContactPhoneNumber() {
        return componentContactPhoneNumber;
    }

    /**
     * Sets the componentContactPhoneNumber attribute.
     *
     * @param componentContactPhoneNumber The componentContactPhoneNumber to set.
     *
     */
    public void setComponentContactPhoneNumber(String componentContactPhoneNumber) {
        this.componentContactPhoneNumber = componentContactPhoneNumber;
    }


    /**
     * Gets the componentConditionCode attribute.
     *
     * @return Returns the componentConditionCode
     *
     */
    public String getComponentConditionCode() {
        return componentConditionCode;
    }

    /**
     * Sets the componentConditionCode attribute.
     *
     * @param componentConditionCode The componentConditionCode to set.
     *
     */
    public void setComponentConditionCode(String componentConditionCode) {
        this.componentConditionCode = componentConditionCode;
    }


    /**
     * Gets the componentEstimatedLifetimeLimit attribute.
     *
     * @return Returns the componentEstimatedLifetimeLimit
     *
     */
    public Integer getComponentEstimatedLifetimeLimit() {
        return componentEstimatedLifetimeLimit;
    }

    /**
     * Sets the componentEstimatedLifetimeLimit attribute.
     *
     * @param componentEstimatedLifetimeLimit The componentEstimatedLifetimeLimit to set.
     *
     */
    public void setComponentEstimatedLifetimeLimit(Integer componentEstimatedLifetimeLimit) {
        this.componentEstimatedLifetimeLimit = componentEstimatedLifetimeLimit;
    }


    /**
     * Gets the componentManufacturerName attribute.
     *
     * @return Returns the componentManufacturerName
     *
     */
    public String getComponentManufacturerName() {
        return componentManufacturerName;
    }

    /**
     * Sets the componentManufacturerName attribute.
     *
     * @param componentManufacturerName The componentManufacturerName to set.
     *
     */
    public void setComponentManufacturerName(String componentManufacturerName) {
        this.componentManufacturerName = componentManufacturerName;
    }


    /**
     * Gets the componentManufacturerModelNumber attribute.
     *
     * @return Returns the componentManufacturerModelNumber
     *
     */
    public String getComponentManufacturerModelNumber() {
        return componentManufacturerModelNumber;
    }

    /**
     * Sets the componentManufacturerModelNumber attribute.
     *
     * @param componentManufacturerModelNumber The componentManufacturerModelNumber to set.
     *
     */
    public void setComponentManufacturerModelNumber(String componentManufacturerModelNumber) {
        this.componentManufacturerModelNumber = componentManufacturerModelNumber;
    }


    /**
     * Gets the componentSerialNumber attribute.
     *
     * @return Returns the componentSerialNumber
     *
     */
    public String getComponentSerialNumber() {
        return componentSerialNumber;
    }

    /**
     * Sets the componentSerialNumber attribute.
     *
     * @param componentSerialNumber The componentSerialNumber to set.
     *
     */
    public void setComponentSerialNumber(String componentSerialNumber) {
        this.componentSerialNumber = componentSerialNumber;
    }


    /**
     * Gets the componentOrganizationTagNumber attribute.
     *
     * @return Returns the componentOrganizationTagNumber
     *
     */
    public String getComponentOrganizationTagNumber() {
        return componentOrganizationTagNumber;
    }

    /**
     * Sets the componentOrganizationTagNumber attribute.
     *
     * @param componentOrganizationTagNumber The componentOrganizationTagNumber to set.
     *
     */
    public void setComponentOrganizationTagNumber(String componentOrganizationTagNumber) {
        this.componentOrganizationTagNumber = componentOrganizationTagNumber;
    }


    /**
     * Gets the componentOrganizationText attribute.
     *
     * @return Returns the componentOrganizationText
     *
     */
    public String getComponentOrganizationText() {
        return componentOrganizationText;
    }

    /**
     * Sets the componentOrganizationText attribute.
     *
     * @param componentOrganizationText The componentOrganizationText to set.
     *
     */
    public void setComponentOrganizationText(String componentOrganizationText) {
        this.componentOrganizationText = componentOrganizationText;
    }


    /**
     * Gets the componentReplacementAmount attribute.
     *
     * @return Returns the componentReplacementAmount
     *
     */
    public KualiDecimal getComponentReplacementAmount() {
        return componentReplacementAmount;
    }

    /**
     * Sets the componentReplacementAmount attribute.
     *
     * @param componentReplacementAmount The componentReplacementAmount to set.
     *
     */
    public void setComponentReplacementAmount(KualiDecimal componentReplacementAmount) {
        this.componentReplacementAmount = componentReplacementAmount;
    }


    /**
     * Gets the componentVendorName attribute.
     *
     * @return Returns the componentVendorName
     *
     */
    public String getComponentVendorName() {
        return componentVendorName;
    }

    /**
     * Sets the componentVendorName attribute.
     *
     * @param componentVendorName The componentVendorName to set.
     *
     */
    public void setComponentVendorName(String componentVendorName) {
        this.componentVendorName = componentVendorName;
    }


    /**
     * Gets the componentWarrantyNumber attribute.
     *
     * @return Returns the componentWarrantyNumber
     *
     */
    public String getComponentWarrantyNumber() {
        return componentWarrantyNumber;
    }

    /**
     * Sets the componentWarrantyNumber attribute.
     *
     * @param componentWarrantyNumber The componentWarrantyNumber to set.
     *
     */
    public void setComponentWarrantyNumber(String componentWarrantyNumber) {
        this.componentWarrantyNumber = componentWarrantyNumber;
    }


    /**
     * Gets the componentWarrantyPhoneNumber attribute.
     *
     * @return Returns the componentWarrantyPhoneNumber
     *
     */
    public String getComponentWarrantyPhoneNumber() {
        return componentWarrantyPhoneNumber;
    }

    /**
     * Sets the componentWarrantyPhoneNumber attribute.
     *
     * @param componentWarrantyPhoneNumber The componentWarrantyPhoneNumber to set.
     *
     */
    public void setComponentWarrantyPhoneNumber(String componentWarrantyPhoneNumber) {
        this.componentWarrantyPhoneNumber = componentWarrantyPhoneNumber;
    }


    /**
     * Gets the componentWarrantyContactName attribute.
     *
     * @return Returns the componentWarrantyContactName
     *
     */
    public String getComponentWarrantyContactName() {
        return componentWarrantyContactName;
    }

    /**
     * Sets the componentWarrantyContactName attribute.
     *
     * @param componentWarrantyContactName The componentWarrantyContactName to set.
     *
     */
    public void setComponentWarrantyContactName(String componentWarrantyContactName) {
        this.componentWarrantyContactName = componentWarrantyContactName;
    }


    /**
     * Gets the componentWarrantyPurchaseOrderNumber attribute.
     *
     * @return Returns the componentWarrantyPurchaseOrderNumber
     *
     */
    public String getComponentWarrantyPurchaseOrderNumber() {
        return componentWarrantyPurchaseOrderNumber;
    }

    /**
     * Sets the componentWarrantyPurchaseOrderNumber attribute.
     *
     * @param componentWarrantyPurchaseOrderNumber The componentWarrantyPurchaseOrderNumber to set.
     *
     */
    public void setComponentWarrantyPurchaseOrderNumber(String componentWarrantyPurchaseOrderNumber) {
        this.componentWarrantyPurchaseOrderNumber = componentWarrantyPurchaseOrderNumber;
    }


    /**
     * Gets the componentWarrantyBeginningDate attribute.
     *
     * @return Returns the componentWarrantyBeginningDate
     *
     */
    public Date getComponentWarrantyBeginningDate() {
        return componentWarrantyBeginningDate;
    }

    /**
     * Sets the componentWarrantyBeginningDate attribute.
     *
     * @param componentWarrantyBeginningDate The componentWarrantyBeginningDate to set.
     *
     */
    public void setComponentWarrantyBeginningDate(Date componentWarrantyBeginningDate) {
        this.componentWarrantyBeginningDate = componentWarrantyBeginningDate;
    }


    /**
     * Gets the componentWarrantyEndingDate attribute.
     *
     * @return Returns the componentWarrantyEndingDate
     *
     */
    public Date getComponentWarrantyEndingDate() {
        return componentWarrantyEndingDate;
    }

    /**
     * Sets the componentWarrantyEndingDate attribute.
     *
     * @param componentWarrantyEndingDate The componentWarrantyEndingDate to set.
     *
     */
    public void setComponentWarrantyEndingDate(Date componentWarrantyEndingDate) {
        this.componentWarrantyEndingDate = componentWarrantyEndingDate;
    }


    /**
     * Gets the componentWarrantyText attribute.
     *
     * @return Returns the componentWarrantyText
     *
     */
    public String getComponentWarrantyText() {
        return componentWarrantyText;
    }

    /**
     * Sets the componentWarrantyText attribute.
     *
     * @param componentWarrantyText The componentWarrantyText to set.
     *
     */
    public void setComponentWarrantyText(String componentWarrantyText) {
        this.componentWarrantyText = componentWarrantyText;
    }

    /**
     * Gets the governmentTagNumber attribute.
     *
     * @return Returns the governmentTagNumber.
     */
    public String getGovernmentTagNumber() {
        return governmentTagNumber;
    }

    /**
     * Sets the governmentTagNumber attribute value.
     *
     * @param governmentTagNumber The governmentTagNumber to set.
     */
    public void setGovernmentTagNumber(String governmentTagNumber) {
        this.governmentTagNumber = governmentTagNumber;
    }

    /**
     * Gets the nationalStockNumber attribute.
     *
     * @return Returns the nationalStockNumber.
     */
    public String getNationalStockNumber() {
        return nationalStockNumber;
    }

    /**
     * Sets the nationalStockNumber attribute value.
     *
     * @param nationalStockNumber The nationalStockNumber to set.
     */
    public void setNationalStockNumber(String nationalStockNumber) {
        this.nationalStockNumber = nationalStockNumber;
    }

    /**
     * Gets the asset attribute.
     *
     * @return Returns the asset
     *
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute.
     *
     * @param asset The asset to set.
     * @deprecated
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    /**
     * Gets the componentCondition attribute.
     *
     * @return Returns the componentCondition
     *
     */
    public AssetCondition getComponentCondition() {
        return componentCondition;
    }

    /**
     * Sets the componentCondition attribute.
     *
     * @param componentCondition The componentCondition to set.
     * @deprecated
     */
    public void setComponentCondition(AssetCondition componentCondition) {
        this.componentCondition = componentCondition;
    }


    /**
     * Gets the active attribute.
     *
     * @return Returns the active
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }



    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        if (this.componentNumber != null) {
            m.put("componentNumber", this.componentNumber.toString());
        }
        return m;
    }
}

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
package org.kuali.kfs.fp.businessobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cam.CapitalAssetManagementAsset;
import org.kuali.kfs.integration.cam.CapitalAssetManagementAssetType;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;

public class CapitalAssetInformation extends PersistableBusinessObjectBase {

    //primary key fields..
    private String documentNumber;
    private Integer capitalAssetLineNumber;
    
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorName;
    private Long capitalAssetNumber;
    private Integer capitalAssetQuantity;
    private String capitalAssetTypeCode;
    private String capitalAssetManufacturerName;
    private String capitalAssetDescription;
    private String capitalAssetManufacturerModelNumber;
    private KualiDecimal capitalAssetLineAmount;
    private String capitalAssetActionIndicator;
    private boolean capitalAssetProcessedIndicator;
    private String distributionAmountCode;
    
    private CapitalAssetManagementAsset capitalAssetManagementAsset;
    private CapitalAssetManagementAssetType capitalAssetManagementAssetType;
    private List<CapitalAssetInformationDetail> capitalAssetInformationDetails;
    private List<CapitalAssetAccountsGroupDetails> capitalAssetAccountsGroupDetails;
    
    private VendorDetail vendorDetail;

    /**
     * Constructs a CapitalAssetInformation.java.
     */
    public CapitalAssetInformation() {
        super();
        setCapitalAssetLineAmount(KualiDecimal.ZERO);
        capitalAssetInformationDetails = new ArrayList<CapitalAssetInformationDetail>();
        capitalAssetAccountsGroupDetails = new ArrayList<CapitalAssetAccountsGroupDetails>();
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put(KFSPropertyConstants.CAPITAL_ASSET_LINE_NUMBER, this.getCapitalAssetLineNumber());
        
        return m;
    }

    public String getCapitalAssetDescription() {
        return capitalAssetDescription;
    }

    public void setCapitalAssetDescription(String capitalAssetDescription) {
        this.capitalAssetDescription = capitalAssetDescription;
    }

    public String getCapitalAssetManufacturerModelNumber() {
        return capitalAssetManufacturerModelNumber;
    }

    public void setCapitalAssetManufacturerModelNumber(String capitalAssetManufacturerModelNumber) {
        this.capitalAssetManufacturerModelNumber = capitalAssetManufacturerModelNumber;
    }

    public String getCapitalAssetManufacturerName() {
        return capitalAssetManufacturerName;
    }

    public void setCapitalAssetManufacturerName(String capitalAssetManufacturerName) {
        this.capitalAssetManufacturerName = capitalAssetManufacturerName;
    }

    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    public Integer getCapitalAssetQuantity() {
        // Return capitalAssetQuantity first if it already set. Otherwise, return the size of details. If the order is reversed, the
        // user input of quantity may be overridden.
        if (this.capitalAssetQuantity != null) {
            return this.capitalAssetQuantity;
        }

        if (ObjectUtils.isNotNull(capitalAssetInformationDetails) && !capitalAssetInformationDetails.isEmpty()) {
            return capitalAssetInformationDetails.size();
        }
        
        return null;
    }

    public void setCapitalAssetQuantity(Integer capitalAssetQuantity) {
        this.capitalAssetQuantity = capitalAssetQuantity;
    }


    public String getCapitalAssetTypeCode() {
        return capitalAssetTypeCode;
    }

    public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
        this.capitalAssetTypeCode = capitalAssetTypeCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailedAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailedAssignedIdentifier;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    /**
     * Gets the capitalAssetManagementAsset attribute.
     * 
     * @return Returns the capitalAssetManagementAsset.
     */
    public CapitalAssetManagementAsset getCapitalAssetManagementAsset() {
        capitalAssetManagementAsset = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CapitalAssetManagementAsset.class).retrieveExternalizableBusinessObjectIfNecessary(this, capitalAssetManagementAsset, KFSPropertyConstants.CAPITAL_ASSET_MANAGEMENT_ASSET);
        return capitalAssetManagementAsset;
    }

    /**
     * Sets the capitalAssetManagementAsset attribute value.
     * 
     * @param capitalAssetManagementAsset The capitalAssetManagementAsset to set.
     */
    public void setCapitalAssetManagementAsset(CapitalAssetManagementAsset capitalAssetManagementAsset) {
        this.capitalAssetManagementAsset = capitalAssetManagementAsset;
    }

    /**
     * Gets the capitalAssetManagementAssetType attribute.
     * 
     * @return Returns the capitalAssetManagementAssetType.
     */
    public CapitalAssetManagementAssetType getCapitalAssetManagementAssetType() {
        capitalAssetManagementAssetType = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CapitalAssetManagementAssetType.class).retrieveExternalizableBusinessObjectIfNecessary(this, capitalAssetManagementAssetType, KFSPropertyConstants.CAPITAL_ASSET_MANAGEMENT_ASSET_TYPE);
        return capitalAssetManagementAssetType;
    }

    /**
     * Sets the capitalAssetManagementAssetType attribute value.
     * 
     * @param capitalAssetManagementAssetType The capitalAssetManagementAssetType to set.
     */
    @Deprecated
    public void setCapitalAssetManagementAssetType(CapitalAssetManagementAssetType capitalAssetManagementAssetType) {
        this.capitalAssetManagementAssetType = capitalAssetManagementAssetType;
    }

    /**
     * Gets the vendorDetail attribute.
     * 
     * @return Returns the vendorDetail.
     */
    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    /**
     * Sets the vendorDetail attribute value.
     * 
     * @param vendorDetail The vendorDetail to set.
     */
    @Deprecated
    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     * 
     * @return Map a map with the primitive field names as the key and the primitive values as the map value.
     */
    public Map<String, Object> getValuesMap() {
        Map<String, Object> simpleValues = new HashMap<String, Object>();

        simpleValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        simpleValues.put(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, this.getVendorHeaderGeneratedIdentifier());
        simpleValues.put(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, this.getVendorDetailAssignedIdentifier());
        simpleValues.put(KFSPropertyConstants.VENDOR_NAME, this.getVendorName());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, this.getCapitalAssetNumber());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE, this.getCapitalAssetTypeCode());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_LINE_AMOUNT, this.getCapitalAssetLineAmount());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_LINE_NUMBER, this.getCapitalAssetLineNumber());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_ACTION_INDICATOR, this.getCapitalAssetActionIndicator());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_QUANTITY, this.getCapitalAssetQuantity());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_PROCESSED_IND, this.isCapitalAssetProcessedIndicator());
        simpleValues.put(KFSPropertyConstants.CPTLAST_DST_AMT_CD, this.getDistributionAmountCode());
        return simpleValues;
    }

    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName.
     */
    public String getVendorName() {
        if (ObjectUtils.isNotNull(vendorDetail)) {
            vendorName = vendorDetail.getVendorName();
        }
        else if (StringUtils.isNotBlank(vendorName) && vendorName.indexOf(" > ") > 0){
                vendorName = vendorName.substring(vendorName.indexOf(" > ") + 2, vendorName.length());
        }

        return vendorName;
    }

    /**
     * Sets the vendorName attribute value.
     * 
     * @param vendorName The vendorName to set.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * Gets the capitalAssetAccountsGroupDetails attribute.
     * 
     * @return Returns the capitalAssetAccountsGroupDetails
     */
    public List<CapitalAssetAccountsGroupDetails> getCapitalAssetAccountsGroupDetails() {
        return capitalAssetAccountsGroupDetails;
    }

    /**	
     * Sets the capitalAssetAccountsGroupDetails attribute.
     * 
     * @param capitalAssetAccountsGroupDetails The capitalAssetAccountsGroupDetails to set.
     */
    public void setCapitalAssetAccountsGroupDetails(List<CapitalAssetAccountsGroupDetails> capitalAssetAccountsGroupDetails) {
        this.capitalAssetAccountsGroupDetails = capitalAssetAccountsGroupDetails;
    }

    /**
     * Gets the capitalAssetInformationDetails attribute.
     * 
     * @return Returns the capitalAssetInformationDetails.
     */
    public List<CapitalAssetInformationDetail> getCapitalAssetInformationDetails() {
        return capitalAssetInformationDetails;
    }

    /**
     * Sets the capitalAssetInformationDetails attribute value.
     * 
     * @param capitalAssetInformationDetails The capitalAssetInformationDetails to set.
     */
    public void setCapitalAssetInformationDetails(List<CapitalAssetInformationDetail> capitalAssetInformationDetails) {
        this.capitalAssetInformationDetails = capitalAssetInformationDetails;
    }

    /**
     * Gets the capitalAssetLineNumber attribute. 
     * @return Returns the capitalAssetLineNumber.
     */
    public Integer getCapitalAssetLineNumber() {
        return capitalAssetLineNumber;
    }

    /**
     * Sets the capitalAssetLineNumber attribute value.
     * @param capitalAssetLineNumber The capitalAssetLineNumber to set.
     */
    public void setCapitalAssetLineNumber(Integer capitalAssetLineNumber) {
        this.capitalAssetLineNumber = capitalAssetLineNumber;
    }
    
    /**
     * @return Returns the capitalAssetLineAmount.
     */
    public KualiDecimal getCapitalAssetLineAmount() {
        if (ObjectUtils.isNull(capitalAssetLineAmount)) {
            return KualiDecimal.ZERO;
        }
        else {
            return capitalAssetLineAmount;
        }
    }

    /**
     * @param capitalAssetLineAmount The capitalAssetLineAmount to set.
     */
    public void setCapitalAssetLineAmount(KualiDecimal capitalAssetLineAmount) {
        if (ObjectUtils.isNull(capitalAssetLineAmount)) {
            this.capitalAssetLineAmount = KualiDecimal.ZERO;
        }
        else {
            this.capitalAssetLineAmount = capitalAssetLineAmount;
        }

        this.capitalAssetLineAmount = capitalAssetLineAmount;
    }
    
    
    /**
     * Gets the capitalAssetActionIndicator attribute.
     * 
     * @return Returns the capitalAssetActionIndicator
     */
    
    public String getCapitalAssetActionIndicator() {
        return capitalAssetActionIndicator;
    }

    /** 
     * Sets the capitalAssetActionIndicator attribute.
     * 
     * @param capitalAssetActionIndicator The capitalAssetActionIndicator to set.
     */
    public void setCapitalAssetActionIndicator(String capitalAssetActionIndicator) {
        this.capitalAssetActionIndicator = capitalAssetActionIndicator;
    }

    /**
     * Gets the capitalAssetProcessedIndicator attribute.
     * 
     * @return Returns the capitalAssetProcessedIndicator
     */
    
    public boolean isCapitalAssetProcessedIndicator() {
        return capitalAssetProcessedIndicator;
    }

    /** 
     * Sets the capitalAssetProcessedIndicator attribute.
     * 
     * @param capitalAssetProcessedIndicator The capitalAssetProcessedIndicator to set.
     */
    public void setCapitalAssetProcessedIndicator(boolean capitalAssetProcessedIndicator) {
        this.capitalAssetProcessedIndicator = capitalAssetProcessedIndicator;
    }
    
    /**
     * Gets the distributionAmountCode attribute.
     * 
     * @return Returns the distributionAmountCode
     */
    
    public String getDistributionAmountCode() {
        return distributionAmountCode;
    }

    /** 
     * Sets the distributionAmountCode attribute.
     * 
     * @param distributionAmountCode The distributionAmountCode to set.
     */
    public void setDistributionAmountCode(String distributionAmountCode) {
        this.distributionAmountCode = distributionAmountCode;
    }
}

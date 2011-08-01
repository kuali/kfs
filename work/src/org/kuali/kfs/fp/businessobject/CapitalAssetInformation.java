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
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

public class CapitalAssetInformation extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorName;
    private Long capitalAssetNumber;
    private Integer capitalAssetQuantity;
    private String capitalAssetTypeCode;
    private String capitalAssetManufacturerName;
    private String capitalAssetDescription;
    private String capitalAssetManufacturerModelNumber;
    private Integer capitalAssetLineNumber;
    
    //new properties...
    private Integer sequenceNumber; // relative to the grouping of accounting lines
    private String financialDocumentLineTypeCode;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private KualiDecimal amount;
    private String capitalAssetActionIndicator;
    private boolean capitalAssetProcessedIndicator;
    
    private CapitalAssetManagementAsset capitalAssetManagementAsset;
    private CapitalAssetManagementAssetType capitalAssetManagementAssetType;
    private List<CapitalAssetInformationDetail> capitalAssetInformationDetails;

    private VendorDetail vendorDetail;

    /**
     * Constructs a CapitalAssetInformation.java.
     */
    public CapitalAssetInformation() {
        super();
        setAmount(KualiDecimal.ZERO);
        capitalAssetInformationDetails = new TypedArrayList(CapitalAssetInformationDetail.class);
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("documentNumber", getDocumentNumber());
        m.put("sequenceNumber", this.getSequenceNumber());
        m.put("financialDocumentLineTypeCode", this.getFinancialDocumentLineTypeCode());
        m.put("chartOfAccountsCode", this.getChartOfAccountsCode());
        m.put("accountNumber", this.getAccountNumber());
        m.put("financialObjectCode", this.getFinancialObjectCode());
        m.put("capitalAssetLineNumber", this.getCapitalAssetLineNumber());
        
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
     * Gets the sequenceNumber attribute.
     * 
     * @return Returns the sequenceNumber
     */
    
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /** 
     * Sets the sequenceNumber attribute.
     * 
     * @param sequenceNumber The sequenceNumber to set.
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Gets the financialDocumentLineTypeCode attribute.
     * 
     * @return Returns the financialDocumentLineTypeCode
     */
    
    public String getFinancialDocumentLineTypeCode() {
        return financialDocumentLineTypeCode;
    }

    /** 
     * Sets the financialDocumentLineTypeCode attribute.
     * 
     * @param financialDocumentLineTypeCode The financialDocumentLineTypeCode to set.
     */
    public void setFinancialDocumentLineTypeCode(String financialDocumentLineTypeCode) {
        this.financialDocumentLineTypeCode = financialDocumentLineTypeCode;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /** 
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    
    public String getAccountNumber() {
        return accountNumber;
    }

    /** 
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     */
    
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /** 
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     * 
     * @return Map a map with the primitive field names as the key and the primitive values as the map value.
     */
    public Map<String, Object> getValuesMap() {
        Map<String, Object> simpleValues = new HashMap<String, Object>();

        simpleValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        simpleValues.put(KFSPropertyConstants.SEQUENCE_NUMBER, this.getSequenceNumber());
        simpleValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_LINE_TYPE_CODE, this.getFinancialDocumentLineTypeCode());
        simpleValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.getChartOfAccountsCode());
        simpleValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.getAccountNumber());
        simpleValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, this.getFinancialObjectCode());
        simpleValues.put(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, this.getVendorHeaderGeneratedIdentifier());
        simpleValues.put(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, this.getVendorDetailAssignedIdentifier());
        simpleValues.put(KFSPropertyConstants.VENDOR_NAME, this.getVendorName());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_NUMBER, this.getCapitalAssetNumber());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_TYPE_CODE, this.getCapitalAssetTypeCode());
        simpleValues.put(KFSPropertyConstants.AMOUNT, this.getAmount());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_LINE_NUMBER, this.getCapitalAssetLineNumber());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_ACTION_INDICATOR, this.getCapitalAssetActionIndicator());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_QUANTITY, this.getCapitalAssetQuantity());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_PROCESSED_IND, this.isCapitalAssetProcessedIndicator());
        
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
     * @return Returns the amount.
     */
    public KualiDecimal getAmount() {
        if (ObjectUtils.isNull(amount)) {
            return KualiDecimal.ZERO;
        }
        else {
            return amount;
        }
    }

    /**
     * @param amount The amount to set.
     */
    public void setAmount(KualiDecimal amount) {
        if (ObjectUtils.isNull(amount)) {
            this.amount = KualiDecimal.ZERO;
        }
        else {
            this.amount = amount;
        }

        this.amount = amount;
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
}

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
package org.kuali.kfs.module.cam.document.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class AssetServiceImpl implements AssetService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetServiceImpl.class);

    private ParameterService parameterService;
    private PaymentSummaryService paymentSummaryService;
    private BusinessObjectService businessObjectService;

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public boolean isAssetMovableCheckByAsset(Asset asset) {
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE);
        return asset.getCapitalAssetType().isMovingIndicator();
    }


    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isAssetDepreciationStarted(org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public boolean isAssetDepreciationStarted(Asset asset) {
        // check non-persistent field accumulatedDepreciation first since it's the sum of
        // assetPayment.accumulatedPrimaryDepreciationAmount. If it's not set yet, we'll check assetPayment one by one.
        if (ObjectUtils.isNotNull(asset.getAccumulatedDepreciation()) && asset.getAccumulatedDepreciation().isPositive()) {
            return true;
        }
        else {
            for (AssetPayment assetPayment : asset.getAssetPayments()) {
                if (ObjectUtils.isNotNull(assetPayment.getAccumulatedPrimaryDepreciationAmount()) && assetPayment.getAccumulatedPrimaryDepreciationAmount().isPositive()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCapitalAsset(Asset asset) {
        return parameterService.getParameterValuesAsString(Asset.class, CamsConstants.Parameters.CAPITAL_ASSET_STATUS_CODES).contains(asset.getInventoryStatusCode());
    }

    public boolean isAssetRetired(Asset asset) {
        return parameterService.getParameterValuesAsString(Asset.class, CamsConstants.Parameters.RETIRED_STATUS_CODES).contains(asset.getInventoryStatusCode());
    }

    public boolean isInServiceDateChanged(Asset oldAsset, Asset newAsset) {
        return !(ObjectUtils.isNull(oldAsset.getCapitalAssetInServiceDate()) ? ObjectUtils.isNull(newAsset.getCapitalAssetInServiceDate()) : oldAsset.getCapitalAssetInServiceDate().equals(newAsset.getCapitalAssetInServiceDate()));
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isAssetFabrication(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    public boolean isAssetFabrication(MaintenanceDocument maintenanceDocument) {
        return maintenanceDocument.getNewMaintainableObject().getBusinessObject() instanceof Asset && maintenanceDocument.isNew();
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isAssetLoaned(org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public boolean isAssetLoaned(Asset asset) {
        return ObjectUtils.isNotNull(asset.getExpectedReturnDate()) && ObjectUtils.isNull(asset.getLoanReturnDate());
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isAssetTaggedInPriorFiscalYear(org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public boolean isAssetTaggedInPriorFiscalYear(Asset asset) {
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);

        return StringUtils.isNotBlank(asset.getCampusTagNumber()) && ObjectUtils.isNotNull(asset.getFinancialDocumentPostingYear()) && !universityDateService.getCurrentFiscalYear().equals(asset.getFinancialDocumentPostingYear());
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isTagNumberCheckExclude(org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public boolean isTagNumberCheckExclude(Asset asset) {
        String status = asset.getInventoryStatusCode();

        return StringUtils.equalsIgnoreCase(status, CamsConstants.InventoryStatusCode.CAPITAL_ASSET_RETIRED) || StringUtils.equalsIgnoreCase(status, CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_RETIRED) || StringUtils.equalsIgnoreCase(asset.getCampusTagNumber(), CamsConstants.Asset.NON_TAGGABLE_ASSET);
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isOffCampusLocationEntered(org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public boolean isOffCampusLocationEntered(Asset asset) {
        AssetLocation offCampus = asset.getOffCampusLocation();
        return StringUtils.isNotBlank(offCampus.getAssetLocationContactName()) || StringUtils.isNotBlank(offCampus.getAssetLocationStreetAddress()) || StringUtils.isNotBlank(offCampus.getAssetLocationCityName()) || StringUtils.isNotBlank(offCampus.getAssetLocationStateCode()) || StringUtils.isNotBlank(offCampus.getAssetLocationZipCode()) || StringUtils.isNotBlank(offCampus.getAssetLocationCountryCode());
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isFinancialObjectSubTypeCodeChanged(org.kuali.kfs.module.cam.businessobject.Asset,
     *      org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public boolean isFinancialObjectSubTypeCodeChanged(Asset oldAsset, Asset newAsset) {
        return !StringUtils.equalsIgnoreCase(oldAsset.getFinancialObjectSubTypeCode(), newAsset.getFinancialObjectSubTypeCode());
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isAssetTypeCodeChanged(org.kuali.kfs.module.cam.businessobject.Asset,
     *      org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public boolean isAssetTypeCodeChanged(Asset oldAsset, Asset newAsset) {
        return !StringUtils.equalsIgnoreCase(oldAsset.getCapitalAssetTypeCode(), newAsset.getCapitalAssetTypeCode());
    }

    public boolean isAssetDepreciableLifeLimitZero(Asset asset) {
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE);
        AssetType capitalAssetType = asset.getCapitalAssetType();
        if (ObjectUtils.isNotNull(capitalAssetType)) {
            return Integer.valueOf(0).equals(capitalAssetType.getDepreciableLifeLimit());
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isCapitalAssetNumberDuplicate(java.lang.Long, java.lang.Long)
     */
    public boolean isCapitalAssetNumberDuplicate(Long capitalAssetNumber1, Long capitalAssetNumber2) {
        if (capitalAssetNumber1 != null && capitalAssetNumber2 != null && capitalAssetNumber1.compareTo(capitalAssetNumber2) == 0) {
            return true;
        }
        return false;
    }


    /**
     * This method calls the service codes to calculate the summary fields for each asset
     * 
     * @param asset
     */
    public void setAssetSummaryFields(Asset asset) {
        if (ObjectUtils.isNotNull(asset)) {
            asset.setFederalContribution(paymentSummaryService.calculateFederalContribution(asset));
            asset.setAccumulatedDepreciation(paymentSummaryService.calculatePrimaryAccumulatedDepreciation(asset));
            asset.setBookValue(paymentSummaryService.calculatePrimaryBookValue(asset));
        }
    }

    public PaymentSummaryService getPaymentSummaryService() {
        return paymentSummaryService;
    }

    public void setPaymentSummaryService(PaymentSummaryService paymentSummaryService) {
        this.paymentSummaryService = paymentSummaryService;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isAssetMovableCheckByPayment(java.lang.String)
     */
    public boolean isAssetMovableCheckByPayment(String financialObjectSubTypeCode) {
        if (parameterService.getParameterValuesAsString(Asset.class, CamsConstants.Parameters.MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES).contains(financialObjectSubTypeCode)) {
            return true;
        }
        else if (parameterService.getParameterValuesAsString(Asset.class, CamsConstants.Parameters.NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES).contains(financialObjectSubTypeCode)) {
            return false;
        }
        else {
            throw new ValidationException("Cound not determine movable or non-movable for this object sub-type code " + financialObjectSubTypeCode);
        }
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isAssetMovableCheckByPayment(Asset)
     */
    public boolean isAssetMovableCheckByPayment(Asset asset) {
        String financialObjectSubTypeCode = asset.getFinancialObjectSubTypeCode();
        if (ObjectUtils.isNotNull(asset.getAssetPayments()) && !asset.getAssetPayments().isEmpty()) {
            AssetPayment firstAssetPayment = asset.getAssetPayments().get(0);
            firstAssetPayment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
            ObjectCodeService objectCodeService = (ObjectCodeService) SpringContext.getBean(ObjectCodeService.class);
            ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(firstAssetPayment.getChartOfAccountsCode(),firstAssetPayment.getFinancialObjectCode());
            financialObjectSubTypeCode = objectCode.getFinancialObjectSubTypeCode();
        }

        if (ObjectUtils.isNull(financialObjectSubTypeCode)) {
            return true;
        }
        else if (parameterService.getParameterValuesAsString(Asset.class, CamsConstants.Parameters.MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES).contains(financialObjectSubTypeCode)) {
            return true;
        }
        else if (parameterService.getParameterValuesAsString(Asset.class, CamsConstants.Parameters.NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES).contains(financialObjectSubTypeCode)) {
            return false;
        }
        else {
            throw new ValidationException("Cound not determine movable or non-movable for this object sub-type code " + financialObjectSubTypeCode);
        }
    }

    public List<Asset> findActiveAssetsMatchingTagNumber(String campusTagNumber) {
        List<Asset> activeMatches = new ArrayList<Asset>();
        // find all assets matching this tag number
        Map<String, String> params = new HashMap<String, String>();
        params.put(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, campusTagNumber);
        Collection<Asset> tagMatches = SpringContext.getBean(BusinessObjectService.class).findMatching(Asset.class, params);
        if (tagMatches != null && !tagMatches.isEmpty()) {
            for (Asset asset : tagMatches) {
                // if found matching, check if status is not retired
                if (!isAssetRetired(asset)) {
                    activeMatches.add(asset);
                }
            }
        }
        return activeMatches;
    }


    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#findAssetsMatchingTagNumber(java.lang.String)
     */
    public Collection<Asset> findAssetsMatchingTagNumber(String campusTagNumber) {
        // find all assets matching this tag number
        Map<String, String> params = new HashMap<String, String>();
        params.put(CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, campusTagNumber);
        Collection<Asset> tagMatches = SpringContext.getBean(BusinessObjectService.class).findMatching(Asset.class, params);

        return tagMatches;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isObjectSubTypeCompatible(java.util.List)
     */
    public boolean isObjectSubTypeCompatible(List<String> financialObjectSubTypeCode) {
        if (financialObjectSubTypeCode == null || financialObjectSubTypeCode.size() <= 1) {
            return true;
        }

        List<String> subTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(Asset.class, CamsConstants.Parameters.OBJECT_SUB_TYPE_GROUPS) );
        String firstObjectSubType = (String) financialObjectSubTypeCode.get(0);
        List<String> validObjectSubTypes = new ArrayList<String>();

        // Get the set for compatible object sub type code by the first financial object sub type code
        for (String subType : subTypes) {
            if (subType.contains(firstObjectSubType)) {
                validObjectSubTypes = Arrays.asList(StringUtils.split(subType, ","));
                break;
            }
        }

        if (validObjectSubTypes.isEmpty()) {
            validObjectSubTypes.add(firstObjectSubType);
        }

        // Check in the whole list if all object sub type code are compatible.
        for (String subTypeCode : financialObjectSubTypeCode) {
            if (!validObjectSubTypes.contains(subTypeCode)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#setSeparateHistory(org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public void setSeparateHistory(Asset asset) {
        // Find the asset this was separated from. It should only be one, error if more
        Map<String, String> paramsAssetGlobalDetail = new HashMap<String, String>();
        paramsAssetGlobalDetail.put(CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
        Collection<AssetGlobalDetail> assetGlobalDetails = SpringContext.getBean(BusinessObjectService.class).findMatching(AssetGlobalDetail.class, paramsAssetGlobalDetail);

        if (assetGlobalDetails.size() > 1) {
            throw new IllegalStateException("Asset #" + asset.getCapitalAssetNumber().toString() + " was created from more then one asset document.");
        }
        else if (assetGlobalDetails.size() == 1) {
            // Find the document associated to that
            Map<String, String> paramsAssetGlobal = new HashMap<String, String>();
            paramsAssetGlobal.put(CamsPropertyConstants.AssetGlobal.DOCUMENT_NUMBER, assetGlobalDetails.iterator().next().getDocumentNumber());
            AssetGlobal assetGlobal = (AssetGlobal) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(AssetGlobal.class, paramsAssetGlobal);

            // Only set it if it is in status approved
            if (DocumentStatusCodes.APPROVED.equals((assetGlobal.getDocumentHeader().getFinancialDocumentStatusCode()))) {
                asset.setSeparateHistory(assetGlobal);
            }
        }

        // Else no history, just return
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#getDocumentNumberThatSeparatedThisAsset(java.lang.Long)
     */
    public List<String> getDocumentNumbersThatSeparatedThisAsset(Long capitalAssetNumber) {
        Map<String, String> paramsAssetGlobal = new HashMap<String, String>();
        paramsAssetGlobal.put(CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER, capitalAssetNumber.toString());
        Collection<AssetGlobal> assetGlobals = SpringContext.getBean(BusinessObjectService.class).findMatching(AssetGlobal.class, paramsAssetGlobal);

        List<String> separateDocumentNumbers = new ArrayList<String>();
        for (Iterator<AssetGlobal> assetGlobalIter = assetGlobals.iterator(); assetGlobalIter.hasNext();) {
            AssetGlobal assetGlobal = assetGlobalIter.next();

            if (DocumentStatusCodes.APPROVED.equals(assetGlobal.getDocumentHeader().getFinancialDocumentStatusCode())) {
                separateDocumentNumbers.add(assetGlobal.getDocumentNumber());
            }
        }

        return separateDocumentNumbers;
    }

    /**
     * sets the posting year and posting month based on the asset creation date
     * 
     * @param asset
     * @return none
     */
    public void setFiscalPeriod(Asset asset) {
        if (asset.getCreateDate() == null)
            return;

        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_DATE, asset.getCreateDate());
        UniversityDate universityDate = (UniversityDate) businessObjectService.findByPrimaryKey(UniversityDate.class, primaryKeys);
        if (universityDate != null) {
            asset.setFinancialDocumentPostingYear(universityDate.getUniversityFiscalYear());
            asset.setFinancialDocumentPostingPeriodCode(universityDate.getUniversityFiscalAccountingPeriod());
        }
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#getCurrentRouteLevels(org.kuali.rice.kew.api.WorkflowDocument)
     */
    public Set<String> getCurrentRouteLevels(WorkflowDocument workflowDocument) {
        return workflowDocument.getCurrentNodeNames();
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetService#isMaintenanceDocumentEnroute(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    public boolean isDocumentEnrouting(Document document) {
        return document.getDocumentHeader().getWorkflowDocument().isEnroute();
    }

}

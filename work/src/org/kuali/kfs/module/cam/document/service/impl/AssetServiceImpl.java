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
package org.kuali.module.cams.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetLocation;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.DocumentLockingService;
import org.kuali.module.cams.service.PaymentSummaryService;
import org.kuali.module.financial.service.UniversityDateService;

public class AssetServiceImpl implements AssetService {
    private ParameterService parameterService;
    private DocumentLockingService documentLockingService;
    private PaymentSummaryService paymentSummaryService;

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public DocumentLockingService getDocumentLockingService() {
        return documentLockingService;
    }

    public void setDocumentLockingService(DocumentLockingService documentLockingService) {
        this.documentLockingService = documentLockingService;
    }

    public boolean isAssetMovable(Asset asset) {
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE);
        return asset.getCapitalAssetType().isMovingIndicator();
    }

    public boolean isCapitalAsset(Asset asset) {
        return parameterService.getParameterValues(Asset.class, CamsConstants.Parameters.CAPITAL_ASSET_STATUS_CODES).contains(asset.getInventoryStatusCode());
    }

    public boolean isAssetRetired(Asset asset) {
        return parameterService.getParameterValues(Asset.class, CamsConstants.Parameters.RETIRED_STATUS_CODES).contains(asset.getInventoryStatusCode());
    }

    public boolean isInServiceDateChanged(Asset oldAsset, Asset newAsset) {
        return !(ObjectUtils.isNull(oldAsset.getCapitalAssetInServiceDate()) ? ObjectUtils.isNull(newAsset.getCapitalAssetInServiceDate()) : oldAsset.getCapitalAssetInServiceDate().equals(newAsset.getCapitalAssetInServiceDate()));
    }

    /**
     * @see org.kuali.module.cams.service.AssetService#isAssetLoaned(org.kuali.module.cams.bo.Asset)
     */
    public boolean isAssetLoaned(Asset asset) {
        return ObjectUtils.isNotNull(asset.getExpectedReturnDate()) && ObjectUtils.isNull(asset.getLoanReturnDate());
    }

    /**
     * @see org.kuali.module.cams.service.AssetService#isAssetTaggedInPriorFiscalYear(org.kuali.module.cams.bo.Asset)
     */
    public boolean isAssetTaggedInPriorFiscalYear(Asset asset) {
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);

        return StringUtils.isNotBlank(asset.getCampusTagNumber()) && ObjectUtils.isNotNull(asset.getFinancialDocumentPostingYear()) && !universityDateService.getCurrentFiscalYear().equals(asset.getFinancialDocumentPostingYear());
    }

    /**
     * @see org.kuali.module.cams.service.AssetService#isTagNumberCheckExclude(org.kuali.module.cams.bo.Asset)
     */
    public boolean isTagNumberCheckExclude(Asset asset) {
        String status = asset.getInventoryStatusCode();

        return StringUtils.equalsIgnoreCase(status, CamsConstants.InventoryStatusCode.CAPITAL_ASSET_RETIRED) || StringUtils.equalsIgnoreCase(status, CamsConstants.InventoryStatusCode.NON_CAPITAL_ASSET_RETIRED) || StringUtils.equalsIgnoreCase(asset.getCampusTagNumber(), CamsConstants.NON_TAGGABLE_ASSET);
    }

    /**
     * @see org.kuali.module.cams.service.AssetService#isOffCampusLocationEntered(org.kuali.module.cams.bo.Asset)
     */
    public boolean isOffCampusLocationEntered(Asset asset) {
        AssetLocation offCampus = asset.getOffCampusLocation();
        return StringUtils.isNotBlank(offCampus.getAssetLocationContactName()) || StringUtils.isNotBlank(offCampus.getAssetLocationStreetAddress()) || StringUtils.isNotBlank(offCampus.getAssetLocationCityName()) || StringUtils.isNotBlank(offCampus.getAssetLocationStateCode()) || StringUtils.isNotBlank(offCampus.getAssetLocationZipCode()) || StringUtils.isNotBlank(offCampus.getAssetLocationCountryCode());
    }

    /**
     * @see org.kuali.module.cams.service.AssetService#isFinancialObjectSubTypeCodeChanged(org.kuali.module.cams.bo.Asset,
     *      org.kuali.module.cams.bo.Asset)
     */
    public boolean isFinancialObjectSubTypeCodeChanged(Asset oldAsset, Asset newAsset) {
        return !StringUtils.equalsIgnoreCase(oldAsset.getFinancialObjectSubTypeCode(), newAsset.getFinancialObjectSubTypeCode());
    }

    /**
     * @see org.kuali.module.cams.service.AssetService#isAssetTypeCodeChanged(org.kuali.module.cams.bo.Asset,
     *      org.kuali.module.cams.bo.Asset)
     */
    public boolean isAssetTypeCodeChanged(Asset oldAsset, Asset newAsset) {
        return !StringUtils.equalsIgnoreCase(oldAsset.getCapitalAssetTypeCode(), newAsset.getCapitalAssetTypeCode());
    }

    public boolean isAssetDepreciableLifeLimitZero(Asset asset) {
        return asset.getCapitalAssetType().getDepreciableLifeLimit().intValue() == 0;
    }

    /**
     * @see org.kuali.module.cams.service.AssetService#isCapitalAssetNumberDuplicate(java.lang.Long, java.lang.Long)
     */
    public boolean isCapitalAssetNumberDuplicate(Long capitalAssetNumber1, Long capitalAssetNumber2) {
        if (capitalAssetNumber1 != null && capitalAssetNumber2 != null && capitalAssetNumber1.compareTo(capitalAssetNumber2) == 0) {
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.module.cams.service.AssetService#generateAssetLock(java.lang.String, java.lang.Long)
     */
    public MaintenanceLock generateAssetLock(String documentNumber, Long capitalAssetNumber) {
        MaintenanceLock maintenanceLock = new MaintenanceLock();
        StringBuffer lockrep = new StringBuffer();

        lockrep.append(Asset.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
        lockrep.append("capitalAssetNumber" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
        lockrep.append(capitalAssetNumber);

        maintenanceLock.setDocumentNumber(documentNumber);
        maintenanceLock.setLockingRepresentation(lockrep.toString());

        return maintenanceLock;
    }

    /**
     * @see org.kuali.module.cams.service.AssetService#isAssetLocked(java.lang.String, java.lang.Long)
     */
    public boolean isAssetLocked(String documentNumber, Long capitalAssetNumber) {
        List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
        maintenanceLocks.add(this.generateAssetLock(documentNumber, capitalAssetNumber));
        String lockingDocumentId = getDocumentLockingService().getLockingDocumentId(documentNumber, maintenanceLocks);
        if (StringUtils.isNotEmpty(lockingDocumentId)) {
            documentLockingService.checkForLockingDocument(lockingDocumentId);

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
     * @see org.kuali.module.cams.service.AssetService#isMovableFinancialObjectSubtypeCode(java.lang.String)
     */
    public boolean isMovableFinancialObjectSubtypeCode(String financialObjectSubTypeCode) {
        if (parameterService.getParameterValues(Asset.class, CamsConstants.Parameters.MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES).contains(financialObjectSubTypeCode)) {
            return true;
        }
        else if (parameterService.getParameterValues(Asset.class, CamsConstants.Parameters.NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES).contains(financialObjectSubTypeCode)) {
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


    public boolean isObjectSubTypeCompatible(List<String> financialObjectSubTypeCode) {
        if (financialObjectSubTypeCode == null || financialObjectSubTypeCode.size() <= 1 ) {
            return true;
        }

        List<String> subTypes = SpringContext.getBean(ParameterService.class).getParameterValues(Asset.class, CamsConstants.Parameters.OBJECT_SUB_TYPE_GROUPS);
        String firstObjectSubType = (String) financialObjectSubTypeCode.iterator().next();
        List<String> validObjectSubTypes = new ArrayList<String>();

        for (String subType : subTypes) {
            validObjectSubTypes = Arrays.asList(StringUtils.split(subType, ","));
            if (validObjectSubTypes.contains(firstObjectSubType)) {
                break;
            }
            validObjectSubTypes = new ArrayList<String>();
        }

        if (validObjectSubTypes.isEmpty()) {
            validObjectSubTypes.add(firstObjectSubType);
        }

        for (Iterator iterator = financialObjectSubTypeCode.iterator(); iterator.hasNext();) {
            if (!validObjectSubTypes.contains(iterator.next())) {
                return false;
            }
        }

        return true;
    }
    
}

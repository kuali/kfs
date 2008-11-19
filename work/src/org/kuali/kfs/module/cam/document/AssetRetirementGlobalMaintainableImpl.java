/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.gl.AssetRetirementGeneralLedgerPendingEntrySource;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.workflow.RoutingAssetNumber;
import org.kuali.kfs.module.cam.document.workflow.RoutingAssetTagNumber;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.routing.attribute.KualiAccountAttribute;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingAccount;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.Section;


/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global location
 * assets
 */
public class AssetRetirementGlobalMaintainableImpl extends KualiGlobalMaintainableImpl implements GenericRoutingInfo {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementGlobalMaintainableImpl.class);
    private static AssetRetirementService assetRetirementService = SpringContext.getBean(AssetRetirementService.class);
    private static AssetService assetService = SpringContext.getBean(AssetService.class);

    private static DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

    private static final Map<String, String[]> NON_VIEWABLE_SECTION_MAP = new HashMap<String, String[]>();
    static {
        NON_VIEWABLE_SECTION_MAP.put(CamsConstants.AssetRetirementReasonCode.EXTERNAL_TRANSFER, new String[] { CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD, CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT });
        NON_VIEWABLE_SECTION_MAP.put(CamsConstants.AssetRetirementReasonCode.GIFT, new String[] { CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD, CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT });
        NON_VIEWABLE_SECTION_MAP.put(CamsConstants.AssetRetirementReasonCode.SOLD, new String[] { CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT, CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT });
        NON_VIEWABLE_SECTION_MAP.put(CamsConstants.AssetRetirementReasonCode.AUCTION, new String[] { CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT, CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT });
        NON_VIEWABLE_SECTION_MAP.put(CamsConstants.AssetRetirementReasonCode.THEFT, new String[] { CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD, CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT });
    }

    private Set<RoutingData> routingInfo;


    /**
     * This creates the particular locking representation for this global document.
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        List<MaintenanceLock> maintenanceLocks = new ArrayList();

        // Lock the merge target capital asset if it exists
        if (assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockRep = new StringBuffer();

            lockRep.append(Asset.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockRep.append(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRep.append(assetRetirementGlobal.getMergedTargetCapitalAssetNumber());

            maintenanceLock.setDocumentNumber(assetRetirementGlobal.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockRep.toString());

            maintenanceLocks.add(maintenanceLock);
        }

        // Lock all source assets
        for (AssetRetirementGlobalDetail detail : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockRep = new StringBuffer();

            lockRep.append(Asset.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockRep.append(CamsPropertyConstants.AssetRetirementGlobalDetail.CAPITAL_ASSET_NUMBER + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRep.append(detail.getCapitalAssetNumber());

            maintenanceLock.setDocumentNumber(detail.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockRep.toString());

            maintenanceLocks.add(maintenanceLock);
        }

        return maintenanceLocks;
    }


    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.setupNewFromExisting(document, parameters);

        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        if (assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)){
            if (ObjectUtils.isNotNull(assetRetirementGlobal.getMergedTargetCapitalAssetNumber())) {
                assetRetirementGlobal.setMergedTargetCapitalAssetDescription(assetRetirementGlobal.getMergedTargetCapitalAsset().getCapitalAssetDescription());
            }
        }
    }


    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getCoreSections(org.kuali.rice.kns.maintenance.Maintainable)
     */
    @Override
    public List<Section> getCoreSections(Maintainable oldMaintainable) {
        List<Section> sections = super.getCoreSections(oldMaintainable);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();

        // If retirement reason code is not defined in NON_VIEWABLE_SECTION_MAP, hide all retirement detail sections.
        String[] nonViewableSections = NON_VIEWABLE_SECTION_MAP.get(assetRetirementGlobal.getRetirementReasonCode());

        if (nonViewableSections == null) {
            nonViewableSections = new String[] { CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD, CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT, CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT };
        }

        // Hide retirement detail sections based on the retirement reason code
        for (Section section : sections) {
            if (ArrayUtils.contains(nonViewableSections, section.getSectionId())) {
                section.setHidden(true);
            }

            if (!assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
                if (CamsConstants.AssetRetirementGlobal.SECTION_TARGET_ASSET_RETIREMENT_INFO.equals(section.getSectionId())) {
                    section.setHidden(true);
                }
            }

        }
        return sections;
    }


    /**
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#prepareGlobalsForSave()
     */
    @Override
    protected void prepareGlobalsForSave() {
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();
        AssetRetirementGlobalDetail sharedRetirementInfo = assetRetirementGlobal.getSharedRetirementInfo();

        // Populate shared columns for each assetRetirementGlobalDetail based sharedRetirementInfo
        if (sharedRetirementInfo != null) {
            for (AssetRetirementGlobalDetail assetDetail : assetRetirementGlobalDetails) {
                copyAssetRetirementGlobalDetail(sharedRetirementInfo, assetDetail);
            }
        }

        super.prepareGlobalsForSave();
    }

    /**
     * This method copies each attributes except capitalAssetNumber in AssetRetirementGlobalDetail from source to destination.
     * 
     * @param source
     * @param destination
     */
    private void copyAssetRetirementGlobalDetail(AssetRetirementGlobalDetail source, AssetRetirementGlobalDetail destination) {
        destination.setCashReceiptFinancialDocumentNumber(source.getCashReceiptFinancialDocumentNumber());
        destination.setRetirementAccountNumber(source.getRetirementAccountNumber());
        destination.setRetirementChartOfAccountsCode(source.getRetirementChartOfAccountsCode());
        destination.setDocumentNumber(source.getDocumentNumber());
        destination.setBuyerDescription(source.getBuyerDescription());
        destination.setSalePrice(source.getSalePrice());
        destination.setEstimatedSellingPrice(source.getEstimatedSellingPrice());
        destination.setHandlingFeeAmount(source.getHandlingFeeAmount());
        destination.setPreventiveMaintenanceAmount(source.getPreventiveMaintenanceAmount());
        destination.setPaidCaseNumber(source.getPaidCaseNumber());
        destination.setRetirementCityName(source.getRetirementCityName());
        destination.setRetirementContactName(source.getRetirementContactName());
        destination.setRetirementCountryCode(source.getRetirementCountryCode());
        destination.setRetirementInstitutionName(source.getRetirementInstitutionName());
        destination.setRetirementPhoneNumber(source.getRetirementPhoneNumber());
        destination.setRetirementStateCode(source.getRetirementStateCode());
        destination.setRetirementStreetAddress(source.getRetirementStreetAddress());
        destination.setRetirementZipCode(source.getRetirementZipCode());
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#processGlobalsAfterRetrieve()
     */
    @Override
    protected void processGlobalsAfterRetrieve() {
        super.processGlobalsAfterRetrieve();

        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
            // Restore sharedRetirementInfo by the first assetRetirementGlobalDetail
            if (assetRetirementGlobal.getSharedRetirementInfo() == null) {
                AssetRetirementGlobalDetail sharedRetirementInfo = new AssetRetirementGlobalDetail();
                copyAssetRetirementGlobalDetail(assetRetirementGlobalDetail, sharedRetirementInfo);
                assetRetirementGlobal.setSharedRetirementInfo(sharedRetirementInfo);
            }
            // Set non-persistent values. So the screen can show them after submit.
            assetService.setAssetSummaryFields(assetRetirementGlobalDetail.getAsset());
        }
    }


    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getDocumentBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        if (KFSConstants.MULTIPLE_VALUE.equalsIgnoreCase(refreshCaller)) {
            if (!assetRetirementService.isAllowedRetireMultipleAssets(assetRetirementGlobal.getRetirementReasonCode()) && assetRetirementGlobalDetails.size() > new Integer(1)) {
                String errorPath = KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + KFSConstants.MAINTENANCE_ADD_PREFIX + CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS;
                GlobalVariables.getErrorMap().addToErrorPath(errorPath);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetRetirementGlobalDetail.VERSION_NUMBER, CamsKeyConstants.Retirement.ERROR_MULTIPLE_ASSET_RETIRED);
                GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
            }
            // Set non-persistent values in multiple lookup result collection. So the screen can show them when return from multiple
            // lookup.
            for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
                assetService.setAssetSummaryFields(assetRetirementGlobalDetail.getAsset());
            }
        }
        else if (CamsConstants.ASSET_LOOKUPABLE_ID.equalsIgnoreCase(refreshCaller)) {
            // Set non-persistent values in the result from asset lookup. So the screen can show them when return from sigle asset
            // lookup.
            String referencesToRefresh = (String) fieldValues.get(KNSConstants.REFERENCES_TO_REFRESH);
            if (assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal) && referencesToRefresh.equals(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET)) {
                assetRetirementGlobal.setMergedTargetCapitalAssetDescription(assetRetirementGlobal.getMergedTargetCapitalAsset().getCapitalAssetDescription());
            }
            AssetRetirementGlobalDetail newDetail = (AssetRetirementGlobalDetail) newCollectionLines.get(CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS);
            assetService.setAssetSummaryFields(newDetail.getAsset());
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#handleRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void handleRouteStatusChange(DocumentHeader documentHeader) {
        super.handleRouteStatusChange(documentHeader);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();

        // all approvals have been processed, the retirement date is set to the approval date
        if (documentHeader.getWorkflowDocument().stateIsProcessed()) {
            assetRetirementGlobal.setRetirementDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            SpringContext.getBean(BusinessObjectService.class).save(assetRetirementGlobal);

            if (assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
                assetRetirementGlobal.getMergedTargetCapitalAsset().setCapitalAssetDescription(assetRetirementGlobal.getMergedTargetCapitalAssetDescription());
                SpringContext.getBean(BusinessObjectService.class).save(assetRetirementGlobal.getMergedTargetCapitalAsset());
            }
        }
        new AssetRetirementGeneralLedgerPendingEntrySource((FinancialSystemDocumentHeader) documentHeader).handleRouteStatusChange(assetRetirementGlobal.getGeneralLedgerPendingEntries());

    }

    /**
     * Gets the routingInfo attribute.
     * 
     * @return Returns the routingInfo.
     */
    public Set<RoutingData> getRoutingInfo() {
        return routingInfo;
    }

    /**
     * Sets the routingInfo attribute value.
     * 
     * @param routingInfo The routingInfo to set.
     */
    public void setRoutingInfo(Set<RoutingData> routingInfo) {
        this.routingInfo = routingInfo;
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#populateRoutingInfo()
     */
    public void populateRoutingInfo() {
        routingInfo = new HashSet<RoutingData>();
        Set<OrgReviewRoutingData> organizationRoutingSet = new HashSet<OrgReviewRoutingData>();
        Set<RoutingAccount> accountRoutingSet = new HashSet<RoutingAccount>();
        Set<RoutingAssetNumber> assetNumberRoutingSet = new HashSet<RoutingAssetNumber>();
        Set<RoutingAssetTagNumber> assetTagNumberRoutingSet = new HashSet<RoutingAssetTagNumber>();


        String chartOfAccountsCode;
        String accountNumber;
        String organizationcode;
        Long assetNumber;
        String assetTagNumber;

        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();

        if (assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
            Asset mergedTargetCapitalAsset = assetRetirementGlobal.getMergedTargetCapitalAsset();
            if (ObjectUtils.isNotNull(mergedTargetCapitalAsset)) {
                chartOfAccountsCode = mergedTargetCapitalAsset.getOrganizationOwnerChartOfAccountsCode();
                accountNumber = mergedTargetCapitalAsset.getOrganizationOwnerAccountNumber();
                assetNumber = mergedTargetCapitalAsset.getCapitalAssetNumber();
                assetTagNumber = mergedTargetCapitalAsset.getCampusTagNumber();
                if (ObjectUtils.isNotNull(mergedTargetCapitalAsset.getOrganizationOwnerAccount())) {
                    organizationcode = mergedTargetCapitalAsset.getOrganizationOwnerAccount().getOrganizationCode();
                    organizationRoutingSet.add(new OrgReviewRoutingData(chartOfAccountsCode, organizationcode));
                }
                accountRoutingSet.add(new RoutingAccount(chartOfAccountsCode, accountNumber));
                assetNumberRoutingSet.add(new RoutingAssetNumber(assetNumber.toString()));
                assetTagNumberRoutingSet.add(new RoutingAssetTagNumber(assetTagNumber));
            }
        }

        for (AssetRetirementGlobalDetail detailLine : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            Asset asset = detailLine.getAsset();
            if (ObjectUtils.isNotNull(asset)) {
                assetNumber = asset.getCapitalAssetNumber();
                accountNumber = asset.getOrganizationOwnerAccountNumber();
                chartOfAccountsCode = asset.getOrganizationOwnerChartOfAccountsCode();
                assetTagNumber = asset.getCampusTagNumber();
                if (ObjectUtils.isNotNull(asset.getOrganizationOwnerAccount())) {
                    organizationcode = asset.getOrganizationOwnerAccount().getOrganizationCode();
                    organizationRoutingSet.add(new OrgReviewRoutingData(chartOfAccountsCode, organizationcode));
                }
                accountRoutingSet.add(new RoutingAccount(chartOfAccountsCode, accountNumber));
                assetNumberRoutingSet.add(new RoutingAssetNumber(assetNumber.toString()));
                assetTagNumberRoutingSet.add(new RoutingAssetTagNumber(assetTagNumber));
            }

        }

        // Storing data
        RoutingData organizationRoutingData = new RoutingData();
        organizationRoutingData.setRoutingType(KualiOrgReviewAttribute.class.getSimpleName());
        organizationRoutingData.setRoutingSet(organizationRoutingSet);
        routingInfo.add(organizationRoutingData);

        RoutingData accountRoutingData = new RoutingData();
        accountRoutingData.setRoutingType(KualiAccountAttribute.class.getSimpleName());
        accountRoutingData.setRoutingSet(accountRoutingSet);
        routingInfo.add(accountRoutingData);

        RoutingData assetNumberRoutingData = new RoutingData();
        assetNumberRoutingData.setRoutingType(RoutingAssetNumber.class.getSimpleName());
        assetNumberRoutingData.setRoutingSet(assetNumberRoutingSet);
        routingInfo.add(assetNumberRoutingData);

        RoutingData assetTagNumberRoutingData = new RoutingData();
        assetTagNumberRoutingData.setRoutingType(RoutingAssetTagNumber.class.getSimpleName());
        assetTagNumberRoutingData.setRoutingSet(assetTagNumberRoutingSet);
        routingInfo.add(assetTagNumberRoutingData);
    }

    @Override
    public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
        return Asset.class;
    }
}
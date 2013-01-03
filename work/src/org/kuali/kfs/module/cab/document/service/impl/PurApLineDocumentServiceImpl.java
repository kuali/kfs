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
package org.kuali.kfs.module.cab.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.Pretag;
import org.kuali.kfs.module.cab.businessobject.PretagDetail;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.document.service.PurApInfoService;
import org.kuali.kfs.module.cab.document.service.PurApLineDocumentService;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.PurApLineSession;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderCapitalAssetSystem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusService;


/**
 * This class provides default implementations of {@link PurApLineService}
 */
public class PurApLineDocumentServiceImpl implements PurApLineDocumentService {
    private static final Logger LOG = Logger.getLogger(PurApLineDocumentServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private PurApLineService purApLineService;
    private PurApInfoService purApInfoService;
    private AssetGlobalService assetGlobalService;

    public static final String DOCUMENT_DESC_PREFIX = "CAB created for ";

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineDocumentService#processApplyPayment(PurchasingAccountsPayableItemAsset,
     *      List, PurApLineSession, Integer)
     */
    @Override
    public String processApplyPayment(PurchasingAccountsPayableItemAsset selectedItem, List<PurchasingAccountsPayableDocument> purApDocs, PurApLineSession purApLineSession, Integer requisitionIdentifer) throws WorkflowException {
        AssetPaymentDocument newDocument = (AssetPaymentDocument) documentService.getNewDocument(AssetPaymentDocument.class);
        if (ObjectUtils.isNotNull(selectedItem) && ObjectUtils.isNotNull(selectedItem.getPurchasingAccountsPayableDocument())) {
            newDocument.getDocumentHeader().setDocumentDescription(DOCUMENT_DESC_PREFIX + selectedItem.getPurchasingAccountsPayableDocument().getDocumentTypeCode() + " " + selectedItem.getDocumentNumber());
        }
        // set assetPaymentDetail list
        createAssetPaymentDetails(newDocument.getSourceAccountingLines(), selectedItem, newDocument.getDocumentNumber(), requisitionIdentifer);

        // If PurAp user entered capitalAssetNumbers, include them in the Asset Payment Document.
        if (selectedItem.getPurApItemAssets() != null && !selectedItem.getPurApItemAssets().isEmpty()) {
            createAssetPaymentAssetDetails(newDocument.getAssetPaymentAssetDetail(), selectedItem, newDocument.getDocumentNumber());

        }
        // set origin code in the Asset Payment Document
        newDocument.setCapitalAssetBuilderOriginIndicator(true);
        documentService.saveDocument(newDocument);

        postProcessCreatingDocument(selectedItem, purApDocs, purApLineSession, newDocument.getDocumentNumber());
        return newDocument.getDocumentNumber();
    }


    /**
     * Create AssetPaymentAssetDetail List for assetPaymentDocument.
     *
     * @param assetPaymentAssetDetails
     * @param selectedItem
     * @param documentNumber
     */
    protected void createAssetPaymentAssetDetails(List assetPaymentAssetDetails, PurchasingAccountsPayableItemAsset selectedItem, String documentNumber) {
        for (ItemCapitalAsset capitalAssetNumber : selectedItem.getPurApItemAssets()) {
            // check if capitalAssetNumber is a valid value or not.
            if (isAssetNumberValid(capitalAssetNumber.getCapitalAssetNumber())) {
                AssetPaymentAssetDetail assetDetail = new AssetPaymentAssetDetail();
                assetDetail.setDocumentNumber(documentNumber);

                assetDetail.setCapitalAssetNumber(capitalAssetNumber.getCapitalAssetNumber());
                assetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentAssetDetail.ASSET);

                AssetService assetService = SpringContext.getBean(AssetService.class);
                Asset candidateAsset = assetDetail.getAsset();
                // asset must be an active & not retired. Duplication check is done during feeding asset numbers from PurAp.
                if (ObjectUtils.isNotNull(candidateAsset) && assetService.isCapitalAsset(candidateAsset) && !assetService.isAssetRetired(candidateAsset)) {
                    assetDetail.setPreviousTotalCostAmount(assetDetail.getAsset().getTotalCostAmount());
                    assetPaymentAssetDetails.add(assetDetail);
                }

            }
        }
    }


    /**
     * Check the asset table if given capitalAssetNumber is valid or not.
     *
     * @param capitalAssetNumber
     * @return
     */
    protected boolean isAssetNumberValid(Long capitalAssetNumber) {
        Map pKeys = new HashMap<String, Object>();

        pKeys.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, capitalAssetNumber);

        Asset asset = businessObjectService.findByPrimaryKey(Asset.class, pKeys);

        return ObjectUtils.isNotNull(asset);
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processCreateAsset(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset,
     *      org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    @Override
    public String processCreateAsset(PurchasingAccountsPayableItemAsset selectedItem, List<PurchasingAccountsPayableDocument> purApDocs, PurApLineSession purApLineSession, Integer requisitionIdentifier) throws WorkflowException {
        // Create new CAMS asset global document
        MaintenanceDocument newDocument = (MaintenanceDocument) documentService.getNewDocument(CamsConstants.DocumentTypeName.ASSET_ADD_GLOBAL);
        newDocument.getNewMaintainableObject().setMaintenanceAction(KRADConstants.MAINTENANCE_NEW_ACTION);
        if (ObjectUtils.isNotNull(selectedItem) && ObjectUtils.isNotNull(selectedItem.getPurchasingAccountsPayableDocument())) {
            newDocument.getDocumentHeader().setDocumentDescription(DOCUMENT_DESC_PREFIX + selectedItem.getPurchasingAccountsPayableDocument().getDocumentTypeCode() + " " + selectedItem.getDocumentNumber());
        }

        // populate pre-tagging entry
        Integer poId = selectedItem.getPurchasingAccountsPayableDocument().getPurchaseOrderIdentifier();
        Pretag preTag = purApLineService.getPreTagLineItem(poId, selectedItem.getItemLineNumber());

        // create asset global BO instance
        AssetGlobal assetGlobal = createAssetGlobal(selectedItem, newDocument.getDocumentNumber(), preTag, requisitionIdentifier);

        // save asset global BO to the document
        newDocument.getNewMaintainableObject().setBusinessObject(assetGlobal);

        // save without doing validation - this is just to get us to the asset global maint. screen, the validation will be applied there
        documentService.saveDocument(newDocument, DocumentSystemSaveEvent.class);

        postProcessCreatingDocument(selectedItem, purApDocs, purApLineSession, newDocument.getDocumentNumber());

        // Save for in-active pre-tag detail if it got feed into CAMS
        if (isItemPretagged(preTag)) {
            businessObjectService.save(preTag);
        }
        return newDocument.getDocumentNumber();
    }


    /**
     * Process item line, cab document after creating CAMs document.
     *
     * @param selectedItem
     * @param purApForm
     * @param purApLineSession
     * @param documentNumber
     */
    protected void postProcessCreatingDocument(PurchasingAccountsPayableItemAsset selectedItem, List<PurchasingAccountsPayableDocument> purApDocs, PurApLineSession purApLineSession, String documentNumber) {
        // save CAMS document number in CAB
        selectedItem.setCapitalAssetManagementDocumentNumber(documentNumber);

        // in-activate item, item account and glEntry(conditionally)
        inActivateItem(selectedItem);

        // update submit amount in the associated general ledger entries.
        updateGlEntrySubmitAmount(selectedItem, purApLineSession.getGlEntryUpdateList());

        // in-activate document if all the associated items are inactive.
        if (ObjectUtils.isNotNull(selectedItem.getPurchasingAccountsPayableDocument())) {
            // update document status code as 'Enroute' when all its items are in CAMs. Link reference from item to document should
            // be set in PurApLineAction.getSelectedLineItem().
            conditionalyUpdateDocumentStatusAsEnroute(selectedItem.getPurchasingAccountsPayableDocument());
        }

        // persistent to the table
        purApLineService.processSaveBusinessObjects(purApDocs, purApLineSession);
        // In-activate general ledger afterwards because we don't maintain the non-persistent relationship from GL to account, so
        // account need to persistent changes first.
        List<GeneralLedgerEntry> glEntryUpdatesList = getGlEntryInActivedList(selectedItem);
        if (glEntryUpdatesList != null && !glEntryUpdatesList.isEmpty()) {
            businessObjectService.save(glEntryUpdatesList);
        }
    }


    /**
     * set doc status as enroute when all its items are in CAMs
     *
     * @param selectedDoc
     */
    protected void conditionalyUpdateDocumentStatusAsEnroute(PurchasingAccountsPayableDocument selectedDoc) {
        for (PurchasingAccountsPayableItemAsset item : selectedDoc.getPurchasingAccountsPayableItemAssets()) {
            if (item.isActive()) {
                return;
            }
        }

        selectedDoc.setActivityStatusCode(CabConstants.ActivityStatusCode.ENROUTE);
    }


    /**
     * Update transactionLedgerSubmitAmount in the associated generalLedgerEntry for each item account.
     *
     * @param selectedItem
     */
    protected void updateGlEntrySubmitAmount(PurchasingAccountsPayableItemAsset selectedItem, List glEntryList) {
        GeneralLedgerEntry glEntry = null;
        for (PurchasingAccountsPayableLineAssetAccount account : selectedItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            glEntry = account.getGeneralLedgerEntry();

            if (ObjectUtils.isNotNull(glEntry)) {
                // Add account amount to GL entry submit amount.
                if (glEntry.getTransactionLedgerSubmitAmount() != null) {
                    glEntry.setTransactionLedgerSubmitAmount(glEntry.getTransactionLedgerSubmitAmount().add(account.getItemAccountTotalAmount()));
                }
                else {
                    glEntry.setTransactionLedgerSubmitAmount(new KualiDecimal(account.getItemAccountTotalAmount().toString()));
                }
            }
            // add to the session for persistence
            glEntryList.add(glEntry);
        }
    }


    /**
     * Build asset details/shared details/unique details lists for new asset global document
     *
     * @param selectedItem
     * @param newDocument
     * @param assetGlobal
     */
    protected void setAssetGlobalDetails(PurchasingAccountsPayableItemAsset selectedItem, AssetGlobal assetGlobal, Pretag preTag, PurchaseOrderCapitalAssetSystem capitalAssetSystem) {
        // build assetGlobalDetail list( will be used for creating unique details list at the same time)
        List<AssetGlobalDetail> assetDetailsList = assetGlobal.getAssetGlobalDetails();
        // shared location details list
        List<AssetGlobalDetail> sharedDetails = assetGlobal.getAssetSharedDetails();
        for (int i = 0; i < selectedItem.getAccountsPayableItemQuantity().intValue(); i++) {
            AssetGlobalDetail assetGlobalDetail = new AssetGlobalDetail();
            assetGlobalDetail.setDocumentNumber(assetGlobal.getDocumentNumber());
            assetGlobalDetail.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
            assetDetailsList.add(assetGlobalDetail);
            // build assetSharedDetails and assetGlobalUniqueDetails list. There two lists will be used to rebuild
            // assetGlobalDetails list when AssetGlobalMaintainableImpl.prepareForSave() is called during save the document.
            AssetGlobalDetail sharedDetail = new AssetGlobalDetail();
            // added as unique detail
            sharedDetail.getAssetGlobalUniqueDetails().add(assetGlobalDetail);
            sharedDetails.add(sharedDetail);
        }

        // feeding data from pre-tag details into shared location details list and unique detail list
        if (isItemPretagged(preTag)) {
            setAssetDetailFromPreTag(preTag, sharedDetails, assetDetailsList);
        }

        // feeding location data from PurAp into assetGlobalDetail List.
        if (!isItemFullyPretagged(preTag, assetGlobal) && ObjectUtils.isNotNull(capitalAssetSystem)) {
            setAssetGlobalDetailFromPurAp(capitalAssetSystem, sharedDetails);
        }
    }

    /**
     * Check if the all new assets get pre-tagged by pre-tagging.
     *
     * @param preTag
     * @param assetGlobal
     * @return
     */
    protected boolean isItemFullyPretagged(Pretag preTag, AssetGlobal assetGlobal) {
        if (isItemPretagged(preTag)) {
            List<PretagDetail> pretagDetails = preTag.getPretagDetails();
            int pretagSize = 0;
            for (PretagDetail pretagDetail : pretagDetails) {
                if (pretagDetail.isActive()) {
                    pretagSize++;
                }
            }
            return pretagSize >= assetGlobal.getAssetSharedDetails().size();
        }
        return false;
    }


    /**
     * Set asset global detail location information from PurAp input. In this method, no grouping for shared location because
     * AssetGlobalMaintainableImpl.processAfterRetrieve() will group the shared location anyway...
     *
     * @param capitalAssetSystem
     * @param assetDetailsList
     */
    protected void setAssetGlobalDetailFromPurAp(PurchaseOrderCapitalAssetSystem capitalAssetSystem, List<AssetGlobalDetail> assetSharedDetail) {
        List<CapitalAssetLocation> capitalAssetLocations = capitalAssetSystem.getCapitalAssetLocations();

        if (ObjectUtils.isNotNull(capitalAssetLocations) && !capitalAssetLocations.isEmpty()) {
            Iterator<CapitalAssetLocation> locationIterator = capitalAssetLocations.iterator();
            int locationQuantity = 0;
            CapitalAssetLocation assetLocation = null;
            for (AssetGlobalDetail assetDetail : assetSharedDetail) {
                // if it's already pre-tagged, skip it.
                if (StringUtils.isNotEmpty(assetDetail.getCampusCode())) {
                    continue;
                }

                // Each line item can have multiple locations and each location can have a quantity value with it.
                if (locationQuantity <= 0 && locationIterator.hasNext()) {
                    // when we consume the current location quantity, we need to move to the next PurAp location.
                    assetLocation = locationIterator.next();
                    // initialize location quantity by PurAp setting
                    if (assetLocation.getItemQuantity() != null) {
                        locationQuantity = assetLocation.getItemQuantity().intValue();
                    }
                    else {
                        // if Purap not set item quantity, we set it to 1.
                        locationQuantity = 1;
                    }
                }
                else if (locationQuantity <= 0 && !locationIterator.hasNext()) {
                    // Consume the current location quantity and no more PurAp locations can be used, stop here.
                    break;
                }
                // set PurAp asset location into asset global document
                setNewAssetByPurApLocation(assetLocation, assetDetail);

                locationQuantity--;
            }
        }

    }

    /**
     * Set asset global detail by PurAp asset location.
     *
     * @param assetLocation
     * @param assetDetail
     */
    protected void setNewAssetByPurApLocation(CapitalAssetLocation assetLocation, AssetGlobalDetail assetDetail) {
        String campusCode = assetLocation.getCampusCode();
        // Set campus code only when it is a valid value. Otherwise, when save document, invalid data will violate data integrity
        // and block save.
        if (!StringUtils.isBlank(campusCode) && checkCampusCodeValid(campusCode)) {
            assetDetail.setCampusCode(campusCode);

            // for on-campus
            if (!assetLocation.isOffCampusIndicator()) {
                String buildingCode = assetLocation.getBuildingCode();
                // Set building code only when it is a valid value. Otherwise, when save document, invalid data will violate data
                // integrity and block save.
                if (!StringUtils.isBlank(buildingCode) && checkBuildingCodeValid(campusCode, buildingCode)) {
                    assetDetail.setBuildingCode(buildingCode);

                    String buildingRoomNumber = assetLocation.getBuildingRoomNumber();
                    // Set building room number only when it is a valid value. Otherwise, when save document, invalid data will
                    // violate data integrity and block save.
                    if (!StringUtils.isBlank(buildingRoomNumber) && checkBuildingRoomNumberValid(campusCode, buildingCode, buildingRoomNumber)) {
                        assetDetail.setBuildingRoomNumber(buildingRoomNumber);
                    }
                }
            }
            else {
                // off-campus
                assetDetail.setOffCampusCityName(assetLocation.getCapitalAssetCityName());
                assetDetail.setOffCampusAddress(assetLocation.getCapitalAssetLine1Address());
                assetDetail.setOffCampusCountryCode(assetLocation.getCapitalAssetCountryCode());
                assetDetail.setOffCampusStateCode(assetLocation.getCapitalAssetStateCode());
                assetDetail.setOffCampusZipCode(assetLocation.getCapitalAssetPostalCode());
            }
        }
    }


    /**
     * Check the given buildingCode and campusCode valid.
     *
     * @param campusCode
     * @param buildingCode
     * @param buildingRoomNumber
     * @return
     */
    protected boolean checkBuildingRoomNumberValid(String campusCode, String buildingCode, String buildingRoomNumber) {
        Map<String, Object> pKeys = new HashMap<String, Object>();
        pKeys.put(CabPropertyConstants.AssetGlobalDocumentCreate.CAMPUS_CODE, campusCode);
        pKeys.put(CabPropertyConstants.AssetGlobalDocumentCreate.BUILDING_CODE, buildingCode);
        pKeys.put(CabPropertyConstants.AssetGlobalDocumentCreate.BUILDING_ROOM_NUMBER, buildingRoomNumber);
        Room room = this.getBusinessObjectService().findByPrimaryKey(Room.class, pKeys);
        return ObjectUtils.isNotNull(room) && room.isActive();
    }


    /**
     * Check the given buildingCode and campusCode valid.
     *
     * @param buildingCode
     * @return
     */
    protected boolean checkBuildingCodeValid(String campusCode, String buildingCode) {
        Map<String, Object> pKeys = new HashMap<String, Object>();
        pKeys.put(CabPropertyConstants.AssetGlobalDocumentCreate.CAMPUS_CODE, campusCode);
        pKeys.put(CabPropertyConstants.AssetGlobalDocumentCreate.BUILDING_CODE, buildingCode);
        Building building = this.getBusinessObjectService().findByPrimaryKey(Building.class, pKeys);
        return ObjectUtils.isNotNull(building) && building.isActive();
    }


    /**
     * check the given campus code existing and active status.
     *
     * @param campusCode
     * @return
     */
    protected boolean checkCampusCodeValid(String campusCode) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(CabPropertyConstants.AssetGlobalDocumentCreate.CAMPUS_CODE, campusCode);
        Campus campus = SpringContext.getBean(CampusService.class).getCampus(campusCode/*RICE_20_REFACTORME  criteria */);
        return ObjectUtils.isNotNull(campus) && campus.isActive();
    }

    /**
     * Feeding data into assetGlobalDetail list from preTagDetail
     *
     * @param preTag
     * @param assetDetailsList
     */
    protected void setAssetDetailFromPreTag(Pretag preTag, List<AssetGlobalDetail> assetSharedDetails, List<AssetGlobalDetail> assetUniqueDetails) {
        Iterator<AssetGlobalDetail> sharedDetailsIterator = assetSharedDetails.iterator();
        Iterator<AssetGlobalDetail> uniqueDetailsIterator = assetUniqueDetails.iterator();
        for (PretagDetail preTagDetail : preTag.getPretagDetails()) {
            if (!preTagDetail.isActive()) {
                continue;
            }
            if (sharedDetailsIterator.hasNext()) {
                // set shared location details
                AssetGlobalDetail sharedDetail = sharedDetailsIterator.next();
                sharedDetail.setBuildingCode(preTagDetail.getBuildingCode());
                sharedDetail.setBuildingRoomNumber(preTagDetail.getBuildingRoomNumber());
                sharedDetail.setBuildingSubRoomNumber(preTagDetail.getBuildingSubRoomNumber());
                sharedDetail.setCampusCode(preTagDetail.getCampusCode());
                // In-activate pre-tagging detail, and will be persistent to the DB.
                preTagDetail.setActive(false);
            }
            if (uniqueDetailsIterator.hasNext()) {
                // set asset unique detail
                AssetGlobalDetail uniqueDetail = uniqueDetailsIterator.next();
                uniqueDetail.setGovernmentTagNumber(preTagDetail.getGovernmentTagNumber());
                uniqueDetail.setNationalStockNumber(preTagDetail.getNationalStockNumber());
                uniqueDetail.setCampusTagNumber(preTagDetail.getCampusTagNumber());
                uniqueDetail.setOrganizationInventoryName(preTag.getOrganizationInventoryName());
                uniqueDetail.setSerialNumber(preTagDetail.getSerialNumber());
                uniqueDetail.setRepresentativeUniversalIdentifier(preTag.getRepresentativeUniversalIdentifier());
                // In-activate pre-tagging detail and will be persistent to the DB.
                preTagDetail.setActive(false);
            }
        }
        // In-activate preTag if possible.
        inActivatePreTag(preTag);
    }

    /**
     * In-activate preTag if all its preTagDetail entry are inactive.
     *
     * @param preTag
     */
    protected void inActivatePreTag(Pretag preTag) {
        // get the number of inactive pre-tag detail.
        int inActiveCounter = 0;
        for (PretagDetail preTagDetail : preTag.getPretagDetails()) {
            if (!preTagDetail.isActive()) {
                inActiveCounter++;
            }
        }
        // if the number of inactive preTagDetail is equal or greater than (when quantityInvoiced is decimal) quantityInvoiced,
        // in-activate the preTag active field.
        if (preTag.getQuantityInvoiced().isLessEqual(new KualiDecimal(inActiveCounter))) {
            preTag.setActive(false);
        }
    }

    /**
     * Build asset payment details list for new asset global document.
     *
     * @param selectedItem
     * @param assetGlobal
     * @param requisitionIdentifier
     */
    protected void createAssetPaymentDetails(List<AssetPaymentDetail> assetPaymentList, PurchasingAccountsPayableItemAsset selectedItem, String documentNumber, Integer requisitionIdentifier) {
        int seq = 1;

        for (PurchasingAccountsPayableLineAssetAccount account : selectedItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            GeneralLedgerEntry glEntry = account.getGeneralLedgerEntry();

            if (ObjectUtils.isNotNull(glEntry)) {
                AssetPaymentDetail assetPaymentDetail = new AssetPaymentDetail();
                // initialize payment detail fields
                assetPaymentDetail.setDocumentNumber(documentNumber);
                assetPaymentDetail.setSequenceNumber(Integer.valueOf(seq++));
                assetPaymentDetail.setChartOfAccountsCode(glEntry.getChartOfAccountsCode());
                assetPaymentDetail.setAccountNumber(replaceFiller(glEntry.getAccountNumber()));
                assetPaymentDetail.setSubAccountNumber(replaceFiller(glEntry.getSubAccountNumber()));
                assetPaymentDetail.setFinancialObjectCode(replaceFiller(glEntry.getFinancialObjectCode()));
                assetPaymentDetail.setFinancialSubObjectCode(replaceFiller(glEntry.getFinancialSubObjectCode()));
                assetPaymentDetail.setProjectCode(replaceFiller(glEntry.getProjectCode()));
                assetPaymentDetail.setOrganizationReferenceId(glEntry.getOrganizationReferenceId());
                assetPaymentDetail.setPostingYear(glEntry.getUniversityFiscalYear());
                assetPaymentDetail.setPostingPeriodCode(glEntry.getUniversityFiscalPeriodCode());
                assetPaymentDetail.setExpenditureFinancialSystemOriginationCode(replaceFiller(glEntry.getFinancialSystemOriginationCode()));
                assetPaymentDetail.setExpenditureFinancialDocumentNumber(glEntry.getDocumentNumber());
                assetPaymentDetail.setExpenditureFinancialDocumentTypeCode(replaceFiller(glEntry.getFinancialDocumentTypeCode()));
                assetPaymentDetail.setExpenditureFinancialDocumentPostedDate(glEntry.getTransactionDate());
                assetPaymentDetail.setAmount(account.getItemAccountTotalAmount());
                assetPaymentDetail.setPurchaseOrderNumber(replaceFiller(glEntry.getReferenceFinancialDocumentNumber()));
                assetPaymentDetail.setRequisitionNumber(requisitionIdentifier.toString());
                assetPaymentDetail.setTransferPaymentIndicator(false);

                // add to payment list
                assetPaymentList.add(assetPaymentDetail);
            }
        }
    }


    /**
     * In-activate item, item Account and generalLedgerEntry active indicator.
     *
     * @param selectedItem
     * @param glEntryList
     */
    protected void inActivateItem(PurchasingAccountsPayableItemAsset selectedItem) {
        // in-active each account.
        for (PurchasingAccountsPayableLineAssetAccount selectedAccount : selectedItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            selectedAccount.setActivityStatusCode(CabConstants.ActivityStatusCode.ENROUTE);
        }
        // in-activate selected Item
        selectedItem.setActivityStatusCode(CabConstants.ActivityStatusCode.ENROUTE);
    }

    /**
     * Update GL Entry status as "enroute" if all its amount are consumed by submit CAMs document.Return the general ledger entry
     * changes as a list.
     *
     * @param glEntryList
     * @param selectedAccount
     * @param glEntry
     */
    protected List<GeneralLedgerEntry> getGlEntryInActivedList(PurchasingAccountsPayableItemAsset selectedItem) {
        List<GeneralLedgerEntry> glEntryUpdateList = new ArrayList<GeneralLedgerEntry>();
        for (PurchasingAccountsPayableLineAssetAccount selectedAccount : selectedItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            GeneralLedgerEntry glEntry = selectedAccount.getGeneralLedgerEntry();
            KualiDecimal relatedAccountAmount = KualiDecimal.ZERO;

            // get persistent account list which should be save before hand
            glEntry.refreshReferenceObject(CabPropertyConstants.GeneralLedgerEntry.PURAP_LINE_ASSET_ACCOUNTS);
            // check if all accounts are inactive status
            for (PurchasingAccountsPayableLineAssetAccount account : glEntry.getPurApLineAssetAccounts()) {
                if (!account.isActive()) {
                    relatedAccountAmount = relatedAccountAmount.add(account.getItemAccountTotalAmount());
                }
            }

            // if one account shows active, won't in-activate this general ledger entry.
            if (relatedAccountAmount.compareTo(glEntry.getAmount()) == 0) {
                glEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.ENROUTE);
                glEntryUpdateList.add(glEntry);
            }
        }
        return glEntryUpdateList;
    }


    protected String replaceFiller(String val) {
        return val == null ? "" : val.trim().replaceAll("-", "");
    }

    /**
     * Create AssetGlobal BO and feed data from pre-asset tagging table.
     *
     * @param selectedItem
     * @param newDocument
     * @param preTag
     * @return
     */
    protected AssetGlobal createAssetGlobal(PurchasingAccountsPayableItemAsset selectedItem, String documentNumber, Pretag preTag, Integer requisitionIdentifier) {
        // instantiate AssetGlobal BO
        AssetGlobal assetGlobal = new AssetGlobal();
        assetGlobal.setDocumentNumber(documentNumber);
        assetGlobal.setCapitalAssetDescription(selectedItem.getAccountsPayableLineItemDescription());
        assetGlobal.setConditionCode(CamsConstants.Asset.CONDITION_CODE_E);
        assetGlobal.setAcquisitionTypeCode(getAssetGlobalService().getNewAcquisitionTypeCode());
        assetGlobal.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE);
        // set vendor name from Purchase Order Document
        PurchaseOrderDocument purApdocument = this.getPurApInfoService().getCurrentDocumentForPurchaseOrderIdentifier(selectedItem.getPurchasingAccountsPayableDocument().getPurchaseOrderIdentifier());
        if (purApdocument != null) {
            assetGlobal.setVendorName(purApdocument.getVendorName());
        }
        // set origin code in the Asset Payment Document
        assetGlobal.setCapitalAssetBuilderOriginIndicator(true);

        PurchaseOrderCapitalAssetSystem capitalAssetSystem = null;
        // check and set if purAp has new asset information
        if (selectedItem.getCapitalAssetSystemIdentifier() != null) {
            capitalAssetSystem = findCapitalAssetSystem(selectedItem.getCapitalAssetSystemIdentifier());
            if (ObjectUtils.isNotNull(capitalAssetSystem)) {
                setAssetGlobalFromPurAp(assetGlobal, capitalAssetSystem);
            }
        }
        // feeding data from pre-asset tagging table into assetGlboal. Here, if there are data overlap in pretag and PurAp, we
        // respect data in Pretagging.
        if (isItemPretagged(preTag)) {
            setAssetGlobalFromPreTag(preTag, assetGlobal);
        }

        // set asset global detail list
        setAssetGlobalDetails(selectedItem, assetGlobal, preTag, capitalAssetSystem);

        // Set Organization Inventory Name for each asset unique detail from PO
        setOrgInventoryNameForAssetDetail(assetGlobal.getAssetGlobalDetails(), purApdocument);

        // build payments list for asset global
        createAssetPaymentDetails(assetGlobal.getAssetPaymentDetails(), selectedItem, documentNumber, requisitionIdentifier);

        // set total cost
        setAssetGlobalTotalCost(assetGlobal);
        // Set Asset Global organization owner account, which is the account that contributed the most dollars.
        setAssetGlobalOrgOwnerAccount(assetGlobal);

        return assetGlobal;
    }

    /**
     * Set organization inventory name for each asset detail by PO Contact name or if empty, by Requestor Name.
     *
     * @param assetGlobalDetails
     * @param purApdocument
     */
    protected void setOrgInventoryNameForAssetDetail(List<AssetGlobalDetail> assetGlobalDetails, PurchaseOrderDocument purApdocument) {
        if (ObjectUtils.isNotNull(purApdocument)) {
            String orgInventoryName = purApdocument.getInstitutionContactName();

            if (StringUtils.isBlank(orgInventoryName)) {
                orgInventoryName = purApdocument.getRequestorPersonName();
            }

            for (AssetGlobalDetail assetGlobalDetail : assetGlobalDetails) {
                assetGlobalDetail.setOrganizationInventoryName(orgInventoryName);
            }
        }
    }


    /**
     * check if item is pre-tagged already.
     *
     * @param preTag
     * @return
     */
    protected boolean isItemPretagged(Pretag preTag) {
        return ObjectUtils.isNotNull(preTag) && preTag.isActive() && ObjectUtils.isNotNull(preTag.getPretagDetails()) && !preTag.getPretagDetails().isEmpty();
    }


    /**
     * Set asset information from PurAp PurchaseOrderCapitalAssetSystem.
     *
     * @param assetGlobal
     * @param capitalAssetSystem
     */
    protected void setAssetGlobalFromPurAp(AssetGlobal assetGlobal, PurchaseOrderCapitalAssetSystem capitalAssetSystem) {
        assetGlobal.setManufacturerName(capitalAssetSystem.getCapitalAssetManufacturerName());
        assetGlobal.setManufacturerModelNumber(capitalAssetSystem.getCapitalAssetModelDescription());
        String capitalAssetTypeCode = capitalAssetSystem.getCapitalAssetTypeCode();
        if (!StringUtils.isBlank(capitalAssetTypeCode) && checkCapitalAssetTypeCodeExist(capitalAssetTypeCode)) {
            assetGlobal.setCapitalAssetTypeCode(capitalAssetSystem.getCapitalAssetTypeCode());
        }
    }


    /**
     * check the given capital asset type code exists in CAM
     *
     * @param capitalAssetTypeCode
     * @return
     */
    protected boolean checkCapitalAssetTypeCodeExist(String capitalAssetTypeCode) {
        Map<String, Object> pKeys = new HashMap<String, Object>();
        pKeys.put(CabPropertyConstants.AssetGlobalDocumentCreate.CAPITAL_ASSET_TYPE_CODE, capitalAssetTypeCode);
        AssetType assetType = this.getBusinessObjectService().findByPrimaryKey(AssetType.class, pKeys);
        return ObjectUtils.isNotNull(assetType);
    }


    /**
     * Get PurAp PurchaseOrderCapitalAssetSystem Object if exists.
     *
     * @param capitalAssetSystemIdentifier
     * @return
     */
    protected PurchaseOrderCapitalAssetSystem findCapitalAssetSystem(Integer capitalAssetSystemIdentifier) {
        Map pKeys = new HashMap<String, Object>();

        pKeys.put(PurapPropertyConstants.CAPITAL_ASSET_SYSTEM_IDENTIFIER, capitalAssetSystemIdentifier);
        return businessObjectService.findByPrimaryKey(PurchaseOrderCapitalAssetSystem.class, pKeys);
    }


    /**
     * Set Asset Global org owner account and chart code. It's assigned by selecting the account that contributed the most dollars
     * on the payment request.
     *
     * @param assetGlobal
     */
    protected void setAssetGlobalOrgOwnerAccount(AssetGlobal assetGlobal) {
        AssetPaymentDetail maxCostPayment = null;
        // get the maximum payment cost
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            if (maxCostPayment == null || assetPaymentDetail.getAmount().isGreaterThan(maxCostPayment.getAmount())) {
                maxCostPayment = assetPaymentDetail;
            }
        }

        if (maxCostPayment != null) {
            assetGlobal.setOrganizationOwnerAccountNumber(maxCostPayment.getAccountNumber());
            assetGlobal.setOrganizationOwnerChartOfAccountsCode(maxCostPayment.getChartOfAccountsCode());
        }
    }


    /**
     * Set Asset Global total cost amount.
     *
     * @param assetGlobal
     */
    protected void setAssetGlobalTotalCost(AssetGlobal assetGlobal) {
        KualiDecimal totalCost = KualiDecimal.ZERO;
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            totalCost = totalCost.add(assetPaymentDetail.getAmount());
        }

        assetGlobal.setTotalCostAmount(totalCost);
    }


    /**
     * Feeding data from preTag and set into asset global for shared information. PreTag data may override PurAp asset data since
     * the strategy choose to respect Pretagging
     *
     * @param preTag
     * @param assetGlobal
     */
    protected void setAssetGlobalFromPreTag(Pretag preTag, AssetGlobal assetGlobal) {
        if (StringUtils.isNotBlank(preTag.getManufacturerName())) {
            assetGlobal.setManufacturerName(preTag.getManufacturerName());
        }
        if (StringUtils.isNotBlank(preTag.getManufacturerModelNumber())) {
            assetGlobal.setManufacturerModelNumber(preTag.getManufacturerModelNumber());
        }

        if (StringUtils.isNotBlank(preTag.getCapitalAssetTypeCode())) {
            assetGlobal.setCapitalAssetTypeCode(preTag.getCapitalAssetTypeCode());
        }
        assetGlobal.setOrganizationText(preTag.getOrganizationText());
        assetGlobal.setRepresentativeUniversalIdentifier(preTag.getRepresentativeUniversalIdentifier());
        if (StringUtils.isNotBlank(preTag.getVendorName())) {
            assetGlobal.setVendorName(preTag.getVendorName());
        }
        // acquisition type code is set to "P"(Pre-asset tagging)
        assetGlobal.setAcquisitionTypeCode(CamsConstants.AssetGlobal.PRE_TAGGING_ACQUISITION_TYPE_CODE);
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the documentService attribute.
     *
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }


    /**
     * Sets the documentService attribute value.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the purApLineService attribute.
     *
     * @return Returns the purApLineService.
     */
    public PurApLineService getPurApLineService() {
        return purApLineService;
    }

    /**
     * Sets the purApLineService attribute value.
     *
     * @param purApLineService The purApLineService to set.
     */
    public void setPurApLineService(PurApLineService purApLineService) {
        this.purApLineService = purApLineService;
    }


    /**
     * Gets the purApInfoService attribute.
     *
     * @return Returns the purApInfoService.
     */
    public PurApInfoService getPurApInfoService() {
        return purApInfoService;
    }


    /**
     * Sets the purApInfoService attribute value.
     *
     * @param purApInfoService The purApInfoService to set.
     */
    public void setPurApInfoService(PurApInfoService purApInfoService) {
        this.purApInfoService = purApInfoService;
    }


    private AssetGlobalService getAssetGlobalService() {
        return assetGlobalService;
    }


    public void setAssetGlobalService(AssetGlobalService assetGlobalService) {
        this.assetGlobalService = assetGlobalService;
    }
}

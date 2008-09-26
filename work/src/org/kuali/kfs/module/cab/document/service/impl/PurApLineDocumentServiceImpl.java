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
package org.kuali.kfs.module.cab.document.service.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.Pretag;
import org.kuali.kfs.module.cab.businessobject.PretagDetail;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.document.service.PurApLineDocumentService;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.PurApLineSession;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;


/**
 * This class provides default implementations of {@link PurApLineService}
 */
public class PurApLineDocumentServiceImpl implements PurApLineDocumentService {
    private static final Logger LOG = Logger.getLogger(PurApLineDocumentServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private PurApLineService purApLineService;

    public String processApplyPayment(PurchasingAccountsPayableItemAsset selectedItem, PurApLineForm purApForm, PurApLineSession purApLineSession) throws WorkflowException {
        AssetPaymentDocument newDocument = (AssetPaymentDocument) documentService.getNewDocument(AssetPaymentDocument.class);
        newDocument.getDocumentHeader().setDocumentDescription(CabConstants.NEW_ASSET_DOCUMENT_DESC);
        // set assetPaymentDetail list
        createAssetPaymentDetails(newDocument.getSourceAccountingLines(), selectedItem, newDocument.getDocumentNumber(), purApForm.getRequisitionIdentifier());
        documentService.saveDocument(newDocument);

        postProcessCreatingDocument(selectedItem, purApForm, purApLineSession, newDocument.getDocumentNumber());
        return newDocument.getDocumentNumber();
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processCreateAsset(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset,
     *      org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public String processCreateAsset(PurchasingAccountsPayableItemAsset selectedItem, PurApLineForm purApForm, PurApLineSession purApLineSession) throws WorkflowException {
        // Create new CAMS asset global document
        MaintenanceDocument newDocument = (MaintenanceDocument) documentService.getNewDocument(CabConstants.ASSET_GLOBAL_MAINTENANCE_DOCUMENT);
        newDocument.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);
        newDocument.getDocumentHeader().setDocumentDescription(CabConstants.NEW_ASSET_DOCUMENT_DESC);

        // populate pre-tagging entry
        Pretag preTag = purApLineService.getPreTagLineItem(purApForm.getPurchaseOrderIdentifier(), selectedItem.getItemLineNumber());

        // create asset global BO instance
        AssetGlobal assetGlobal = createAssetGlobal(selectedItem, newDocument.getDocumentNumber(), preTag, purApForm.getRequisitionIdentifier());

        // save asset global BO to the document
        newDocument.getNewMaintainableObject().setBusinessObject(assetGlobal);
        documentService.saveDocument(newDocument);

        postProcessCreatingDocument(selectedItem, purApForm, purApLineSession, newDocument.getDocumentNumber());

        if (preTag != null) {
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
    private void postProcessCreatingDocument(PurchasingAccountsPayableItemAsset selectedItem, PurApLineForm purApForm, PurApLineSession purApLineSession, String documentNumber) {
        // save CAMS document number in CAB
        selectedItem.setCapitalAssetManagementDocumentNumber(documentNumber);
        // in-activate selected Item
        selectedItem.setActive(false);
        // in-active document if all the associated items are inactive.
        if (ObjectUtils.isNotNull(selectedItem.getPurchasingAccountsPayableDocument())) {
            // link reference from item to document should be set in PurApLineAction.getSelectedLineItem().
            purApLineService.inActivateDocument(selectedItem.getPurchasingAccountsPayableDocument());
        }

        setFormActiveItemIndicator(purApForm);
        // persistent to the table
        purApLineService.processSaveBusinessObjects(purApForm, purApLineSession);
    }

    /**
     * In-activate form activeItemExist indicator if all items associating with the form are inactive.
     * 
     * @param purApForm
     */
    private void setFormActiveItemIndicator(PurApLineForm purApForm) {
        for (PurchasingAccountsPayableDocument document : purApForm.getPurApDocs()) {
            if (document.isActive()) {
                purApForm.setActiveItemExist(true);
            }
        }
        purApForm.setActiveItemExist(false);
    }


    /**
     * Build asset details/shared details/unique details lists for new asset global document
     * 
     * @param selectedItem
     * @param newDocument
     * @param assetGlobal
     */
    private void setAssetGlobalDetails(PurchasingAccountsPayableItemAsset selectedItem, AssetGlobal assetGlobal, Pretag preTag) {
        // build assetGlobalDetail list
        List<AssetGlobalDetail> assetDetailsList = assetGlobal.getAssetGlobalDetails();
        List<AssetGlobalDetail> sharedDetails = assetGlobal.getAssetSharedDetails();
        for (int i = 0; i < selectedItem.getAccountsPayableItemQuantity().intValue(); i++) {
            AssetGlobalDetail assetGlobalDetail = new AssetGlobalDetail();
            assetGlobalDetail.setDocumentNumber(assetGlobal.getDocumentNumber());
            assetGlobalDetail.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
            assetDetailsList.add(assetGlobalDetail);
            // build assetSharedDetails and assetGlobalUniqueDetails list. There two lists will be used to rebuild
            // assetGlobalDetails list when AssetGlobalMaintainableImpl.prepareForSave() is called during save the document.
            AssetGlobalDetail sharedDetail = new AssetGlobalDetail();
            sharedDetail.getAssetGlobalUniqueDetails().add(assetGlobalDetail);
            sharedDetails.add(sharedDetail);
        }

        // feeding data from pre-tag details into assetGlobalDetail List
        if (preTag != null) {
            setAssetGlobalDetailFromPreTag(preTag, assetDetailsList);
        }
    }

    /**
     * Feeding data into assetGlobalDetail list from preTagDetail
     * 
     * @param preTag
     * @param assetDetailsList
     */
    private void setAssetGlobalDetailFromPreTag(Pretag preTag, List<AssetGlobalDetail> assetDetailsList) {
        preTag.refreshReferenceObject(CabPropertyConstants.Pretag.PRE_TAG_DETAIS);

        List<PretagDetail> preTagDetails = preTag.getPretagDetails();
        if (ObjectUtils.isNotNull(preTagDetails) && !preTagDetails.isEmpty()) {
            Iterator<PretagDetail> preTagIterator = preTagDetails.iterator();
            for (AssetGlobalDetail assetDetail : assetDetailsList) {
                if (preTagIterator.hasNext()) {
                    PretagDetail preTagDetail = preTagIterator.next();
                    assetDetail.setBuildingCode(preTagDetail.getBuildingCode());
                    assetDetail.setBuildingRoomNumber(preTagDetail.getBuildingRoomNumber());
                    assetDetail.setBuildingSubRoomNumber(preTagDetail.getBuildingSubRoomNumber());
                    assetDetail.setGovernmentTagNumber(preTagDetail.getGovernmentTagNumber());
                    assetDetail.setNationalStockNumber(preTagDetail.getNationalStockNumber());
                    assetDetail.setCampusCode(preTagDetail.getCampusCode());
                    assetDetail.setCampusTagNumber(preTagDetail.getCampusTagNumber());
                    preTagDetail.setActive(false);
                }
                assetDetail.setOrganizationInventoryName(preTag.getOrganizationInventoryName());
            }
            // In-activate preTag if possible.
            inActivatePreTag(preTag);
        }
    }

    /**
     * In-activate preTag if all its preTagDetail entry are inactive.
     * 
     * @param preTag
     */
    private void inActivatePreTag(Pretag preTag) {
        // get the number of inactive pre-tag detail.
        int inActiveCounter = 0;
        for (PretagDetail preTagDetail : preTag.getPretagDetails()) {
            if (!preTagDetail.isActive()) {
                inActiveCounter++;
            }
        }
        // if the number of inactive preTagDetail is equal or greater than (when quantityInvoiced is decimal) quantityInvoiced,
        // in-activate the preTag active field.
        if (preTag.getQuantityInvoiced().isLessThan(new KualiDecimal(inActiveCounter))) {
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
    private void createAssetPaymentDetails(List<AssetPaymentDetail> assetPaymentList, PurchasingAccountsPayableItemAsset selectedItem, String documentNumber, Integer requisitionIdentifier) {
        int seq = 1;

        for (PurchasingAccountsPayableLineAssetAccount account : selectedItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            GeneralLedgerEntry glEntry = account.getGeneralLedgerEntry();
            AssetPaymentDetail assetPaymentDetail = new AssetPaymentDetail();
            // initialize payment detail fields
            assetPaymentDetail.setDocumentNumber(documentNumber);
            assetPaymentDetail.setSequenceNumber(new Integer(seq++));
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
            assetPaymentDetail.setExpenditureFinancialDocumentPostedDate(glEntry.getTransactionPostingDate());
            assetPaymentDetail.setAmount(account.getItemAccountTotalAmount());
            assetPaymentDetail.setPurchaseOrderNumber(replaceFiller(glEntry.getReferenceFinancialDocumentNumber()));
            assetPaymentDetail.setRequisitionNumber(requisitionIdentifier.toString());
            assetPaymentDetail.setTransferPaymentIndicator(false);

            // in-active account.
            account.setActive(false);

            // in-activate generalLedgerEnter if needed
            inActivateGLEntry(glEntry);

            assetPaymentList.add(assetPaymentDetail);
        }
    }


    /**
     * Update GL Entry active indicator to false if all its amount are consumed by submit CAMs document.
     * 
     * @param glEntry
     */
    private void inActivateGLEntry(GeneralLedgerEntry glEntry) {

        for (PurchasingAccountsPayableLineAssetAccount account : glEntry.getPurApLineAssetAccounts()) {
            if (account.isActive()) {
                // if one account shows active, return without modification.
                return;
            }
        }
        glEntry.setActive(false);
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
    private AssetGlobal createAssetGlobal(PurchasingAccountsPayableItemAsset selectedItem, String documentNumber, Pretag preTag, Integer requisitionIdentifier) {
        AssetGlobal assetGlobal = new AssetGlobal();
        assetGlobal.setDocumentNumber(documentNumber);
        assetGlobal.setCapitalAssetDescription(selectedItem.getAccountsPayableLineItemDescription());
        assetGlobal.setConditionCode(CamsConstants.CONDITION_CODE_E);
        assetGlobal.setPrimaryDepreciationMethodCode(CamsConstants.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE);
        assetGlobal.setAcquisitionTypeCode(CamsConstants.AssetGlobal.NEW_ACQUISITION_TYPE_CODE);

        // feeding data from pre-asset tagging table.
        if (preTag != null) {
            setAssetGlobalFromPreTag(preTag, assetGlobal);
        }

        // set asset global detail list
        setAssetGlobalDetails(selectedItem, assetGlobal, preTag);

        // build payments list for asset global
        createAssetPaymentDetails(assetGlobal.getAssetPaymentDetails(), selectedItem, documentNumber, requisitionIdentifier);

        return assetGlobal;
    }

    /**
     * Feeding data from preTag and set into asset global for shared information.
     * 
     * @param preTag
     * @param assetGlobal
     */
    private void setAssetGlobalFromPreTag(Pretag preTag, AssetGlobal assetGlobal) {
        assetGlobal.setManufacturerName(preTag.getManufacturerName());
        assetGlobal.setManufacturerModelNumber(preTag.getManufacturerModelNumber());
        assetGlobal.setCapitalAssetTypeCode(preTag.getCapitalAssetTypeCode());
        assetGlobal.setOrganizationOwnerChartOfAccountsCode(preTag.getChartOfAccountsCode());
        assetGlobal.setOrganizationText(preTag.getOrganizationText());
        assetGlobal.setRepresentativeUniversalIdentifier(preTag.getRepresentativeUniversalIdentifier());
        assetGlobal.setVendorName(preTag.getVendorName());
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


}

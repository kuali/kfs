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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformationDetail;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAsset;
import org.kuali.kfs.module.cab.document.service.GlLineService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.util.ObjectValueUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class GlLineServiceImpl implements GlLineService {
    private static final String CAB_DESC_PREFIX = "CAB created for GL ";
    protected BusinessObjectService businessObjectService;


    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#createAssetGlobalDocument(java.util.List,
     *      org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public Document createAssetGlobalDocument(List<GeneralLedgerEntry> entries, GeneralLedgerEntry primary) throws WorkflowException {
        // initiate a new document
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        MaintenanceDocument document = (MaintenanceDocument) documentService.getNewDocument(CabConstants.ASSET_GLOBAL_MAINTENANCE_DOCUMENT);
        // create asset global
        AssetGlobal assetGlobal = createAssetGlobal(primary, document);
        assetGlobal.setCapitalAssetBuilderOriginIndicator(true);
        assetGlobal.setAcquisitionTypeCode(CamsConstants.AssetGlobal.NEW_ACQUISITION_TYPE_CODE);
        updatePreTagInformation(primary, document, assetGlobal);
        int seq = 0;
        for (GeneralLedgerEntry generalLedgerEntry : entries) {
            assetGlobal.getAssetPaymentDetails().add(createAssetPaymentDetail(generalLedgerEntry, document, ++seq));
        }
        // save the document
        document.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);
        document.getDocumentHeader().setDocumentDescription(CAB_DESC_PREFIX + primary.getGeneralLedgerAccountIdentifier());
        document.getNewMaintainableObject().setBusinessObject(assetGlobal);
        document.getNewMaintainableObject().setBoClass(assetGlobal.getClass());
        documentService.saveDocument(document);
        deactivateGLEntries(entries, document);
        return document;
    }

    /**
     * De-activate the GL Entries
     * 
     * @param entries GL Entries
     * @param document Document
     */
    protected void deactivateGLEntries(List<GeneralLedgerEntry> entries, Document document) {
        for (GeneralLedgerEntry generalLedgerEntry : entries) {
            generalLedgerEntry.setTransactionLedgerSubmitAmount(generalLedgerEntry.getTransactionLedgerEntryAmount());
            generalLedgerEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.ENROUTE);
            createGeneralLedgerEntryAsset(generalLedgerEntry, document);
            getBusinessObjectService().save(generalLedgerEntry);
        }
    }

    /**
     * This method reads the pre-tag information and creates objects for asset global document
     * 
     * @param entry GL Line
     * @param document Asset Global Maintenance Document
     * @param assetGlobal Asset Global Object
     */
    protected void updatePreTagInformation(GeneralLedgerEntry entry, MaintenanceDocument document, AssetGlobal assetGlobal) {
        CapitalAssetInformation assetInformation = findCapitalAssetInformation(entry);
        if (ObjectUtils.isNotNull(assetInformation) && assetInformation.getCapitalAssetNumber() == null) {
            List<CapitalAssetInformationDetail> capitalAssetInformationDetails = assetInformation.getCapitalAssetInformationDetails();
            for (CapitalAssetInformationDetail capitalAssetInformationDetail : capitalAssetInformationDetails) {
                // This is not added to constructor in CAMS to provide module isolation from CAMS
                AssetGlobalDetail assetGlobalDetail = new AssetGlobalDetail();
                assetGlobalDetail.setDocumentNumber(document.getDocumentNumber());
                assetGlobalDetail.setCampusCode(capitalAssetInformationDetail.getCampusCode());
                assetGlobalDetail.setBuildingCode(capitalAssetInformationDetail.getBuildingCode());
                assetGlobalDetail.setBuildingRoomNumber(capitalAssetInformationDetail.getBuildingRoomNumber());
                assetGlobalDetail.setBuildingSubRoomNumber(capitalAssetInformationDetail.getBuildingSubRoomNumber());
                assetGlobalDetail.setSerialNumber(capitalAssetInformationDetail.getCapitalAssetSerialNumber());
                assetGlobalDetail.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
                assetGlobalDetail.setCampusTagNumber(capitalAssetInformationDetail.getCapitalAssetTagNumber());
                AssetGlobalDetail uniqueAsset = new AssetGlobalDetail();
                ObjectValueUtils.copySimpleProperties(assetGlobalDetail, uniqueAsset);
                assetGlobalDetail.getAssetGlobalUniqueDetails().add(uniqueAsset);
                assetGlobal.getAssetSharedDetails().add(assetGlobalDetail);
            }
            assetGlobal.setVendorName(assetInformation.getVendorName());
            assetGlobal.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE);
            assetGlobal.setCapitalAssetTypeCode(assetInformation.getCapitalAssetTypeCode());
            assetGlobal.setManufacturerName(assetInformation.getCapitalAssetManufacturerName());
            assetGlobal.setManufacturerModelNumber(assetInformation.getCapitalAssetManufacturerModelNumber());
            assetGlobal.setCapitalAssetDescription(assetInformation.getCapitalAssetDescription());
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#findCapitalAssetInformation(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public CapitalAssetInformation findCapitalAssetInformation(GeneralLedgerEntry entry) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, entry.getDocumentNumber());
        CapitalAssetInformation assetInformation = (CapitalAssetInformation) businessObjectService.findByPrimaryKey(CapitalAssetInformation.class, primaryKeys);
        return assetInformation;
    }

    /**
     * Creates general ledger entry asset
     * 
     * @param entry GeneralLedgerEntry
     * @param maintDoc Document
     */
    protected void createGeneralLedgerEntryAsset(GeneralLedgerEntry entry, Document document) {
        // store the document number
        GeneralLedgerEntryAsset entryAsset = new GeneralLedgerEntryAsset();
        entryAsset.setGeneralLedgerAccountIdentifier(entry.getGeneralLedgerAccountIdentifier());
        entryAsset.setCapitalAssetBuilderLineNumber(1);
        entryAsset.setCapitalAssetManagementDocumentNumber(document.getDocumentNumber());
        entry.getGeneralLedgerEntryAssets().add(entryAsset);
    }


    /**
     * Creates asset global
     * 
     * @param entry GeneralLedgerEntry
     * @param maintDoc MaintenanceDocument
     * @return AssetGlobal
     */
    protected AssetGlobal createAssetGlobal(GeneralLedgerEntry entry, MaintenanceDocument maintDoc) {
        AssetGlobal assetGlobal = new AssetGlobal();
        assetGlobal.setOrganizationOwnerChartOfAccountsCode(entry.getChartOfAccountsCode());
        assetGlobal.setOrganizationOwnerAccountNumber(entry.getAccountNumber());
        assetGlobal.setDocumentNumber(maintDoc.getDocumentNumber());
        assetGlobal.setConditionCode(CamsConstants.Asset.CONDITION_CODE_E);
        return assetGlobal;
    }


    public Document createAssetPaymentDocument(List<GeneralLedgerEntry> entries, GeneralLedgerEntry primaryGlEntry) throws WorkflowException {
        // Find out the GL Entry
        // initiate a new document
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        AssetPaymentDocument document = (AssetPaymentDocument) documentService.getNewDocument(CabConstants.ASSET_PAYMENT_DOCUMENT);
        document.setCapitalAssetBuilderOriginIndicator(true);
        document.getDocumentHeader().setDocumentDescription(CAB_DESC_PREFIX + primaryGlEntry.getGeneralLedgerAccountIdentifier());
        updatePreTagInformation(primaryGlEntry, document);
        // Asset Payment Detail
        int seq = 0;
        for (GeneralLedgerEntry generalLedgerEntry : entries) {
            AssetPaymentDetail detail = createAssetPaymentDetail(generalLedgerEntry, document, ++seq);
            document.getSourceAccountingLines().add(detail);
        }
        // Asset payment asset detail
        // save the document
        documentService.saveDocument(document);
        deactivateGLEntries(entries, document);
        return document;
    }

    /**
     * Updates pre tag information received from FP document
     * 
     * @param entry GeneralLedgerEntry
     * @param document AssetPaymentDocument
     */
    protected void updatePreTagInformation(GeneralLedgerEntry entry, AssetPaymentDocument document) {
        CapitalAssetInformation assetInformation = findCapitalAssetInformation(entry);
        if (ObjectUtils.isNotNull(assetInformation) && assetInformation.getCapitalAssetNumber() != null) {
            AssetPaymentAssetDetail assetPaymentAssetDetail = new AssetPaymentAssetDetail();
            assetPaymentAssetDetail.setDocumentNumber(document.getDocumentNumber());
            assetPaymentAssetDetail.setCapitalAssetNumber(assetInformation.getCapitalAssetNumber());
            assetPaymentAssetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentAssetDetail.ASSET);
            Asset asset = assetPaymentAssetDetail.getAsset();
            if (ObjectUtils.isNotNull(asset)) {
                assetPaymentAssetDetail.setPreviousTotalCostAmount(asset.getTotalCostAmount() != null ? asset.getTotalCostAmount() : KualiDecimal.ZERO);
                document.getAssetPaymentAssetDetail().add(assetPaymentAssetDetail);
            }
        }
    }

    /**
     * Creates asset payment detail based on GL line. to CAB
     * 
     * @param entry GeneralLedgerEntry
     * @param document Document
     * @return AssetPaymentDetail
     */
    protected AssetPaymentDetail createAssetPaymentDetail(GeneralLedgerEntry entry, Document document, int seqNo) {
        // This is not added to constructor in CAMS to provide module isolation from CAMS
        AssetPaymentDetail detail = new AssetPaymentDetail();
        detail.setDocumentNumber(document.getDocumentNumber());
        detail.setSequenceNumber(seqNo);
        detail.setPostingYear(entry.getUniversityFiscalYear());
        detail.setPostingPeriodCode(entry.getUniversityFiscalPeriodCode());
        detail.setChartOfAccountsCode(entry.getChartOfAccountsCode());
        detail.setAccountNumber(replaceFiller(entry.getAccountNumber()));
        detail.setSubAccountNumber(replaceFiller(entry.getSubAccountNumber()));
        detail.setFinancialObjectCode(replaceFiller(entry.getFinancialObjectCode()));
        detail.setProjectCode(replaceFiller(entry.getProjectCode()));
        detail.setOrganizationReferenceId(replaceFiller(entry.getOrganizationReferenceId()));
        detail.setAmount(KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode()) ? entry.getTransactionLedgerEntryAmount().negated() : entry.getTransactionLedgerEntryAmount());
        detail.setExpenditureFinancialSystemOriginationCode(replaceFiller(entry.getFinancialSystemOriginationCode()));
        detail.setExpenditureFinancialDocumentNumber(entry.getDocumentNumber());
        detail.setExpenditureFinancialDocumentTypeCode(replaceFiller(entry.getFinancialDocumentTypeCode()));
        detail.setExpenditureFinancialDocumentPostedDate(entry.getTransactionDate());
        detail.setPurchaseOrderNumber(replaceFiller(entry.getReferenceFinancialDocumentNumber()));
        detail.setTransferPaymentIndicator(false);
        return detail;
    }

    /**
     * If the value contains only the filler characters, then return blank
     * 
     * @param val Value
     * @return blank if value if a filler
     */
    protected String replaceFiller(String val) {
        if (val == null) {
            return "";
        }
        char[] charArray = val.trim().toCharArray();
        for (char c : charArray) {
            if (c != '-') {
                return val;
            }
        }
        return "";
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
}

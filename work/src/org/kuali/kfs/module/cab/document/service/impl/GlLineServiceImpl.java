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
import org.kuali.kfs.module.cam.CamsConstants.DocumentTypeName;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.util.ObjectValueUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class GlLineServiceImpl implements GlLineService {
    private static final String CAB_DESC_PREFIX = "CAB created for FP ";
    protected BusinessObjectService businessObjectService;
    protected AssetGlobalService assetGlobalService;


    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#createAssetGlobalDocument(java.util.List,
     *      org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public Document createAssetGlobalDocument(GeneralLedgerEntry primary, Integer capitalAssetLineNumber) throws WorkflowException {
        // initiate a new document
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        MaintenanceDocument document = (MaintenanceDocument) documentService.getNewDocument(DocumentTypeName.ASSET_ADD_GLOBAL);
        // create asset global
        AssetGlobal assetGlobal = createAssetGlobal(primary, document);
        assetGlobal.setCapitalAssetBuilderOriginIndicator(true);
        assetGlobal.setAcquisitionTypeCode(getAssetGlobalService().getNewAcquisitionTypeCode());
        updatePreTagInformation(primary, document, assetGlobal, capitalAssetLineNumber);
        assetGlobal.getAssetPaymentDetails().add(createAssetPaymentDetail(primary, document, 0, capitalAssetLineNumber));
        
        // save the document
        document.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);
        document.getDocumentHeader().setDocumentDescription(CAB_DESC_PREFIX + primary.getDocumentNumber());
        document.getNewMaintainableObject().setBusinessObject(assetGlobal);
        document.getNewMaintainableObject().setBoClass(assetGlobal.getClass());
        documentService.saveDocument(document);
        deactivateGLEntries(primary, document);
        return document;
    }

    /**
     * De-activate the GL Entry
     * 
     * @param entries GL Entry
     * @param document Document
     */
    protected void deactivateGLEntries(GeneralLedgerEntry entry, Document document) {
        entry.setTransactionLedgerSubmitAmount(entry.getTransactionLedgerEntryAmount());
        entry.setActivityStatusCode(CabConstants.ActivityStatusCode.ENROUTE);
        createGeneralLedgerEntryAsset(entry, document);
        getBusinessObjectService().save(entry);
    }

    /**
     * This method reads the pre-tag information and creates objects for asset global document
     * 
     * @param entry GL Line
     * @param document Asset Global Maintenance Document
     * @param assetGlobal Asset Global Object
     */
    protected void updatePreTagInformation(GeneralLedgerEntry entry, MaintenanceDocument document, AssetGlobal assetGlobal, Integer capitalAssetLineNumber) {
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry, capitalAssetLineNumber);
        //if it is create asset...
        if (KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR.equals(capitalAssetInformation.getCapitalAssetActionIndicator())) {
            List<CapitalAssetInformationDetail> capitalAssetInformationDetails = capitalAssetInformation.getCapitalAssetInformationDetails();
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
            
            assetGlobal.setVendorName(capitalAssetInformation.getVendorName());
            assetGlobal.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE);
            assetGlobal.setCapitalAssetTypeCode(capitalAssetInformation.getCapitalAssetTypeCode());
            assetGlobal.setManufacturerName(capitalAssetInformation.getCapitalAssetManufacturerName());
            assetGlobal.setManufacturerModelNumber(capitalAssetInformation.getCapitalAssetManufacturerModelNumber());
            assetGlobal.setCapitalAssetDescription(capitalAssetInformation.getCapitalAssetDescription());
        } 
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#findCapitalAssetInformation(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public List<CapitalAssetInformation> findCapitalAssetInformation(GeneralLedgerEntry entry) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, entry.getDocumentNumber());
        List<CapitalAssetInformation> assetInformation = (List<CapitalAssetInformation>) businessObjectService.findMatching(CapitalAssetInformation.class, primaryKeys);
        return assetInformation;
    }
    
    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#findCapitalAssetInformation(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public CapitalAssetInformation findCapitalAssetInformation(GeneralLedgerEntry entry, Integer capitalAssetLineNumber) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, entry.getDocumentNumber());
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.CHART_OF_ACCOUNTS_CODE, entry.getChartOfAccountsCode());
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.ACCOUNT_NUMBER, entry.getAccountNumber());
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.FINANCIAL_OBJECT_CODE, entry.getFinancialObjectCode());
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.ASSET_LINE_NUMBER, capitalAssetLineNumber.toString());

        CapitalAssetInformation assetInformation = (CapitalAssetInformation) businessObjectService.findByPrimaryKey(CapitalAssetInformation.class, primaryKeys);
        return assetInformation;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#findAllCapitalAssetInformation(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public List<CapitalAssetInformation> findAllCapitalAssetInformation(GeneralLedgerEntry entry) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, entry.getDocumentNumber());
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.CHART_OF_ACCOUNTS_CODE, entry.getChartOfAccountsCode());
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.ACCOUNT_NUMBER, entry.getAccountNumber());
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.FINANCIAL_OBJECT_CODE, entry.getFinancialObjectCode());
        
        List<CapitalAssetInformation> assetInformation = (List<CapitalAssetInformation>) businessObjectService.findMatchingOrderBy(CapitalAssetInformation.class, primaryKeys, CabPropertyConstants.CapitalAssetInformation.ACTION_INDICATOR, true);

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


    public Document createAssetPaymentDocument(GeneralLedgerEntry primaryGlEntry, Integer capitalAssetLineNumber) throws WorkflowException {
        // Find out the GL Entry
        // initiate a new document
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        AssetPaymentDocument document = (AssetPaymentDocument) documentService.getNewDocument(DocumentTypeName.ASSET_PAYMENT);
        document.setCapitalAssetBuilderOriginIndicator(true);
        document.setAssetPaymentAllocationTypeCode(CamsPropertyConstants.AssetPaymentAllocation.ASSET_DISTRIBUTION_DEFAULT_CODE);
        document.getDocumentHeader().setDocumentDescription(CAB_DESC_PREFIX + primaryGlEntry.getDocumentNumber());
        updatePreTagInformation(primaryGlEntry, document, capitalAssetLineNumber);
        // Asset Payment Detail
        AssetPaymentDetail detail = createAssetPaymentDetail(primaryGlEntry, document, 0, capitalAssetLineNumber);
        document.getSourceAccountingLines().add(detail);
        // Asset payment asset detail
        // save the document
        documentService.saveDocument(document);
        deactivateGLEntries(primaryGlEntry, document);
        return document;
    }

    /**
     * Updates pre tag information received from FP document
     * 
     * @param entry GeneralLedgerEntry
     * @param document AssetPaymentDocument
     */
    protected void updatePreTagInformation(GeneralLedgerEntry entry, AssetPaymentDocument document, Integer capitalAssetLineNumber) {
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry, capitalAssetLineNumber);
        //if it is modify asset...
        if (KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR.equals(capitalAssetInformation.getCapitalAssetActionIndicator())) {
            AssetPaymentAssetDetail assetPaymentAssetDetail = new AssetPaymentAssetDetail();
            assetPaymentAssetDetail.setDocumentNumber(document.getDocumentNumber());
            assetPaymentAssetDetail.setCapitalAssetNumber(capitalAssetInformation.getCapitalAssetNumber());
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
    protected AssetPaymentDetail createAssetPaymentDetail(GeneralLedgerEntry entry, Document document, int seqNo, Integer capitalAssetLineNumber) {
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
        KualiDecimal capitalAssetAmount = getCapitalAssetAmount(entry, capitalAssetLineNumber);
        detail.setAmount(KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode()) ? capitalAssetAmount.negated() : capitalAssetAmount);
        detail.setExpenditureFinancialSystemOriginationCode(replaceFiller(entry.getFinancialSystemOriginationCode()));
        detail.setExpenditureFinancialDocumentNumber(entry.getDocumentNumber());
        detail.setExpenditureFinancialDocumentTypeCode(replaceFiller(entry.getFinancialDocumentTypeCode()));
        detail.setExpenditureFinancialDocumentPostedDate(entry.getTransactionDate());
        detail.setPurchaseOrderNumber(replaceFiller(entry.getReferenceFinancialDocumentNumber()));
        detail.setTransferPaymentIndicator(false);
        return detail;
    }

    /**
     * retrieves the amount from the capital asset
     * @param entry
     * @param capitalAssetLineNumber
     * @return capital asset amount.
     */
    protected KualiDecimal getCapitalAssetAmount(GeneralLedgerEntry entry, Integer capitalAssetLineNumber) {
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry, capitalAssetLineNumber);
        return capitalAssetInformation.getAmount();
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
    
    private AssetGlobalService getAssetGlobalService() {
        return assetGlobalService;
    }

    public void setAssetGlobalService(AssetGlobalService assetGlobalService) {
        this.assetGlobalService = assetGlobalService;
    }
    
}

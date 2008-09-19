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

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAsset;
import org.kuali.kfs.module.cab.document.service.GlLineService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class GlLineServiceImpl implements GlLineService {

    BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#createAssetGlobalDocument(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public Document createAssetGlobalDocument(GeneralLedgerEntry entry) throws WorkflowException {
        // initiate a new document
        DocumentService documentService = KNSServiceLocator.getDocumentService();
        MaintenanceDocument document = (MaintenanceDocument) documentService.getNewDocument(CabConstants.ASSET_GLOBAL_MAINTENANCE_DOCUMENT);
        // create asset global
        AssetGlobal assetGlobal = createAssetGlobal(entry, document);
        // TODO - Get FP Pre-tag data
        assetGlobal.getAssetPaymentDetails().add(createAssetPaymentDetail(entry, document));
        // save the document
        document.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);
        document.getOldMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);
        document.getDocumentHeader().setDocumentDescription("CAB - Asset Global for GL " + entry.getGeneralLedgerAccountIdentifier());
        document.getOldMaintainableObject().setBusinessObject((PersistableBusinessObject) ObjectUtils.deepCopy(assetGlobal));
        document.getOldMaintainableObject().setBoClass(assetGlobal.getClass());
        document.getNewMaintainableObject().setBusinessObject(assetGlobal);
        document.getNewMaintainableObject().setBoClass(assetGlobal.getClass());
        documentService.saveDocument(document);
        // de-activate the entry
        entry.setActive(false);
        createGeneralLedgerEntryAsset(entry, document);
        getBusinessObjectService().save(entry);
        return document;
    }

    private void createGeneralLedgerEntryAsset(GeneralLedgerEntry entry, Document maintDoc) {
        // store the document number
        GeneralLedgerEntryAsset entryAsset = new GeneralLedgerEntryAsset();
        entryAsset.setGeneralLedgerAccountIdentifier(entry.getGeneralLedgerAccountIdentifier());
        entryAsset.setCapitalAssetBuilderLineNumber(1);
        entryAsset.setCapitalAssetManagementDocumentNumber(maintDoc.getDocumentNumber());
        entry.getGeneralLedgerEntryAssets().add(entryAsset);
    }


    protected AssetGlobal createAssetGlobal(GeneralLedgerEntry entry, MaintenanceDocument maintDoc) {
        AssetGlobal assetGlobal = new AssetGlobal();
        assetGlobal.setOrganizationOwnerChartOfAccountsCode(entry.getChartOfAccountsCode());
        assetGlobal.setOrganizationOwnerAccountNumber(entry.getAccountNumber());
        assetGlobal.setDocumentNumber(maintDoc.getDocumentNumber());
        assetGlobal.setConditionCode(CamsConstants.CONDITION_CODE_E);
        assetGlobal.setPrimaryDepreciationMethodCode(CamsConstants.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE);
        return assetGlobal;
    }

    public Document createAssetPaymentDocument(GeneralLedgerEntry entry) throws WorkflowException {
        // Find out the GL Entry
        // initiate a new document
        DocumentService documentService = KNSServiceLocator.getDocumentService();
        AssetPaymentDocument document = (AssetPaymentDocument) documentService.getNewDocument(CabConstants.ASSET_PAYMENT_DOCUMENT);
        document.getDocumentHeader().setDocumentDescription("CAB - Asset Payment for GL " + entry.getGeneralLedgerAccountIdentifier());
        // Asset Payment Detail
        AssetPaymentDetail detail = createAssetPaymentDetail(entry, document);
        document.getSourceAccountingLines().add(detail);
        // Asset payment asset detail
        // save the document
        documentService.saveDocument(document);
        // de-activate the entry
        entry.setActive(false);
        createGeneralLedgerEntryAsset(entry, document);
        getBusinessObjectService().save(entry);
        return document;
    }

    protected AssetPaymentDetail createAssetPaymentDetail(GeneralLedgerEntry entry, Document document) {
        AssetPaymentDetail detail = new AssetPaymentDetail();
        detail.setDocumentNumber(document.getDocumentNumber());
        detail.setSequenceNumber(1);
        detail.setPaymentApplicationDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        detail.setPostingYear(entry.getUniversityFiscalYear());
        detail.setPostingPeriodCode(entry.getUniversityFiscalPeriodCode());
        detail.setChartOfAccountsCode(entry.getChartOfAccountsCode());
        detail.setAccountNumber(replaceFiller(entry.getAccountNumber()));
        detail.setSubAccountNumber(replaceFiller(entry.getSubAccountNumber()));
        detail.setFinancialObjectCode(replaceFiller(entry.getFinancialObjectCode()));
        detail.setProjectCode(replaceFiller(entry.getProjectCode()));
        detail.setOrganizationReferenceId(replaceFiller(entry.getOrganizationReferenceId()));
        detail.setAmount(KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode()) ? entry.getTransactionLedgerEntryAmount().multiply(new KualiDecimal(-1)) : entry.getTransactionLedgerEntryAmount());
        detail.setExpenditureFinancialSystemOriginationCode(replaceFiller(entry.getFinancialSystemOriginationCode()));
        detail.setExpenditureFinancialDocumentNumber(entry.getDocumentNumber());
        //TODO - There is error when document type code is set
        //detail.setExpenditureFinancialDocumentTypeCode(replaceFiller(entry.getFinancialDocumentTypeCode()));
        detail.setExpenditureFinancialDocumentPostedDate(entry.getTransactionPostingDate());
        detail.setPurchaseOrderNumber(replaceFiller(entry.getReferenceFinancialDocumentNumber()));
        detail.setTransferPaymentIndicator(false);
        return detail;
    }

    protected String replaceFiller(String val) {
        return val == null ? "" : val.trim().replaceAll("-", "");
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

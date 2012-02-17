/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetYearEndDepreciation;
import org.kuali.kfs.module.cam.businessobject.AssetYearEndDepreciationDetail;
import org.kuali.kfs.module.cam.document.gl.AssetRetirementGeneralLedgerPendingEntrySource;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.util.MaintenanceUtils;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * This class overrides the base {KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global location assets
 */
public class AssetYearEndDepreciationMaintainableImpl extends FinancialSystemMaintainable {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetYearEndDepreciationMaintainableImpl.class);
    public static final String DOCUMENT_ERROR_PREFIX = "document.";
    public static final String MAINTAINABLE_ERROR_PATH = DOCUMENT_ERROR_PREFIX + "newMaintainableObject";
    public static final String DETAIL_ERROR_PATH = MAINTAINABLE_ERROR_PATH + ".add.assetYearEndDepreciationDetail";

    /**
     * We are using a substitute mechanism for asset locking which can lock on assets when rule check passed. Return empty list from
     * this method.
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        return new ArrayList<MaintenanceLock>();
    }


    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterEdit(document, parameters);

        AssetYearEndDepreciation assetYearEndDepreciation = (AssetYearEndDepreciation) document.getOldMaintainableObject().getDataObject();

        List<AssetYearEndDepreciationDetail> assetYearEndDepreciationDetails = assetYearEndDepreciation.getAssetYearEndDepreciationDetails();
        for (AssetYearEndDepreciationDetail assetYearEndDepreciationDetail : assetYearEndDepreciationDetails) {
            // Set non-persistent values. So the screen can show them after submit.
            getAssetService().setAssetSummaryFields(assetYearEndDepreciationDetail.getAsset()); // mjmc keep this one
        }
    }

    // I01994/T06314. JWalker. Replace this method which was getting array out of bounds error.
    // Copied from ICR addMultipleValueLookupResults.
    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#addMultipleValueLookupResults(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, java.util.Collection, boolean, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    public void addMultipleValueLookupResults(MaintenanceDocument document, String collectionName, Collection<PersistableBusinessObject> rawValues, boolean needsBlank, PersistableBusinessObject bo) {

        Collection<AssetYearEndDepreciationDetail> maintCollection = (Collection<AssetYearEndDepreciationDetail>) ObjectUtils.getPropertyValue(bo, collectionName);
        String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        List<String> duplicateIdentifierFieldsFromDataDictionary = getDuplicateIdentifierFieldsFromDataDictionary(docTypeName, collectionName);
        List<String> existingIdentifierList = getMultiValueIdentifierList(maintCollection, duplicateIdentifierFieldsFromDataDictionary);
        Class<?> collectionClass = getMaintenanceDocumentDictionaryService().getCollectionBusinessObjectClass(docTypeName, collectionName);

        List<MaintainableSectionDefinition> sections = getMaintenanceDocumentDictionaryService().getMaintainableSections(docTypeName);
        Map<String, String> template = MaintenanceUtils.generateMultipleValueLookupBOTemplate(sections, collectionName);
        try {
            GlobalVariables.getMessageMap().addToErrorPath(DETAIL_ERROR_PATH);
            for (PersistableBusinessObject nextBo : rawValues) {
                AssetYearEndDepreciationDetail templatedBo = (AssetYearEndDepreciationDetail) ObjectUtils.createHybridBusinessObject(collectionClass, nextBo, template);
                templatedBo.setNewCollectionRecord(true);
                templatedBo.setActive(true);
                prepareBusinessObjectForAdditionFromMultipleValueLookup(collectionName, templatedBo);
                if (!hasBusinessObjectExisted(templatedBo, existingIdentifierList, duplicateIdentifierFieldsFromDataDictionary)) {
                    maintCollection.add(templatedBo);
                }
            }
            GlobalVariables.getMessageMap().removeFromErrorPath(DETAIL_ERROR_PATH);
        }
        catch (Exception e) {
            LOG.error("Unable to add multiple value lookup results for assetYearEndDepreciation " + e.getMessage());
            throw new RuntimeException("Unable to add multiple value lookup results for assetYearEndDepreciation " + e.getMessage());
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);

        // I01994/T06314. JAW. When adding new asset, display Accum deprec, Book Value, and Fed Contribution after asset lookup.
        AssetYearEndDepreciationDetail newDetail = (AssetYearEndDepreciationDetail) document.getNewMaintainableObject().getNewCollectionLine("assetYearEndDepreciationDetails");
        getAssetService().setAssetSummaryFields(newDetail.getAsset());

        AssetYearEndDepreciation assetYearEndDepreciation = (AssetYearEndDepreciation) document.getDocumentBusinessObject();
        List<AssetYearEndDepreciationDetail> assetYearEndDepreciationDetails = assetYearEndDepreciation.getAssetYearEndDepreciationDetails();

        if (KFSConstants.MULTIPLE_VALUE.equalsIgnoreCase(refreshCaller)) {
            // Set non-persistent values in multiple lookup result collection. So the screen can show them when return from multiple
            // lookup.
            for (AssetYearEndDepreciationDetail assetYearEndDepreciationDetail : assetYearEndDepreciationDetails) {
                getAssetService().setAssetSummaryFields(assetYearEndDepreciationDetail.getAsset());
            }
        }
        else if (CamsConstants.AssetRetirementGlobal.ASSET_LOOKUPABLE_ID.equalsIgnoreCase(refreshCaller)) {
            // Set non-persistent values in the result from asset lookup. So the screen can show them when return from single asset
            // lookup.
            AssetYearEndDepreciation assetYearEndDepreciationOld = (AssetYearEndDepreciation) document.getOldMaintainableObject().getBusinessObject();

            List<AssetYearEndDepreciationDetail> assetYearEndDepreciationDetailsOld = assetYearEndDepreciationOld.getAssetYearEndDepreciationDetails();
            for (AssetYearEndDepreciationDetail assetYearEndDepreciationDetailOld : assetYearEndDepreciationDetailsOld) {
                // Set non-persistent values. So the screen can show them after submit.
                getAssetService().setAssetSummaryFields(assetYearEndDepreciationDetailOld.getAsset());
            }

        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);
        AssetYearEndDepreciation assetYearEndDepreciation = (AssetYearEndDepreciation) getBusinessObject();
        if (documentHeader.getWorkflowDocument().isEnroute()) {
            // display a message for asset not generating ledger entries when it is federally owned
            boolean allPaymentsFederalOwned = true;
            List<AssetYearEndDepreciationDetail> assetYearEndDepreciationDetails = assetYearEndDepreciation.getAssetYearEndDepreciationDetails();
            for (AssetYearEndDepreciationDetail assetYearEndDepreciationDetail : assetYearEndDepreciationDetails) {
                for (AssetPayment assetPayment : assetYearEndDepreciationDetail.getAsset().getAssetPayments()) {
                    if (!getAssetPaymentService().isPaymentFederalOwned(assetPayment)) {
                        allPaymentsFederalOwned = false;
                    }
                }
            }
            // if (allPaymentsFederalOwned) {
            // GlobalVariables.getMessageList().add(CamsKeyConstants.Retirement.MESSAGE_NO_LEDGER_ENTRY_REQUIRED_RETIREMENT);
            // }
        }
        // all approvals have been processed, the retirement date is set to the approval date
        if (documentHeader.getWorkflowDocument().isProcessed()) {
            SpringContext.getBean(BusinessObjectService.class).save(assetYearEndDepreciation);
        }
        new AssetRetirementGeneralLedgerPendingEntrySource((FinancialSystemDocumentHeader) documentHeader).doRouteStatusChange(assetYearEndDepreciation.getGeneralLedgerPendingEntries());

        // release the lock when document status changed as following...
        WorkflowDocument workflowDoc = documentHeader.getWorkflowDocument();
        if (workflowDoc.isCanceled() || workflowDoc.isDisapproved() || workflowDoc.isProcessed() || workflowDoc.isFinal()) {
            this.getCapitalAssetManagementModuleService().deleteAssetLocks(documentHeader.getDocumentNumber(), null);
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#addNewLineToCollection(String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        AssetYearEndDepreciationDetail newDetail = (AssetYearEndDepreciationDetail) newCollectionLines.get("assetYearEndDepreciationDetails");
        HashMap<String, Object> pKeys = new HashMap<String, Object>();
        // Asset must be valid and capital active 'A','C','S','U'
        Long assetNumber = newDetail.getCapitalAssetNumber();
        pKeys.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, assetNumber);
        Asset asset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, pKeys);
        getAssetService().setAssetSummaryFields(asset);
        AssetYearEndDepreciation assetYearEndDepreciation = (AssetYearEndDepreciation) getBusinessObject();
        List<AssetYearEndDepreciationDetail> assetYearEndDepreciationDetails = assetYearEndDepreciation.getAssetYearEndDepreciationDetails();
        for (AssetYearEndDepreciationDetail assetYearEndDepreciationDetail : assetYearEndDepreciationDetails) {
            getAssetService().setAssetSummaryFields(assetYearEndDepreciationDetail.getAsset());
        }
        super.refreshReferences("add.assetYearEndDepreciationDetails.asset");
        super.addNewLineToCollection(collectionName);
    }


    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#populateNewCollectionLines(java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String)
     */
    @Override
    public Map<String, String> populateNewCollectionLines(Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument, String methodToCall) {
        String capitalAssetNumber = (String) fieldValues.get(CamsPropertyConstants.AssetRetirementGlobal.CAPITAL_ASSET_NUMBER);
        if (StringUtils.isNotBlank(capitalAssetNumber)) {
            fieldValues.remove(CamsPropertyConstants.AssetRetirementGlobal.CAPITAL_ASSET_NUMBER);
            fieldValues.put(CamsPropertyConstants.AssetRetirementGlobal.CAPITAL_ASSET_NUMBER, capitalAssetNumber.trim());
        }
        return super.populateNewCollectionLines(fieldValues, maintenanceDocument, methodToCall);
    }

    /**
     * Convenience method to get {@link AssetRetirementService}
     * @return {@link AssetRetirementService}
     */
    private AssetRetirementService getAssetRetirementService() {
        return SpringContext.getBean(AssetRetirementService.class);
    }

    /**
     * Convenience method to get {@link AssetService}
     * @return {@link AssetService}
     */
    private AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    /**
     * Convenience method to get {@link AssetPayementService}
     * @return
     */
    private AssetPaymentService getAssetPaymentService() {
        return SpringContext.getBean(AssetPaymentService.class);
    }
    
    /**
     * Convenience method to get {@link CapitalAssetManagementModuleService}
     * @return {@link CapitalAssetManagementModuleService}
     */
    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }

}
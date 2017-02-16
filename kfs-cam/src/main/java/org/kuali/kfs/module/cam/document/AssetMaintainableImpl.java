/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cam.document;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetFabrication;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.module.cam.document.service.RetirementInfoService;
import org.kuali.kfs.module.cam.service.AssetLockService;
import org.kuali.kfs.module.cam.util.MaintainableWorkflowUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class implements custom data preparation for displaying asset edit screen.
 */

public class AssetMaintainableImpl extends FinancialSystemMaintainable {

    private static final Logger LOG = Logger.getLogger(AssetMaintainableImpl.class);

    private Asset asset;
    private Asset copyAsset;
    private boolean fabricationOn;
    protected static volatile IdentityService identityService;
    protected static volatile DocumentService documentService;
    
    
    private static final Map<String, String> FINANCIAL_DOC_NAME_MAP = new HashMap<String, String>();
    static {
        FINANCIAL_DOC_NAME_MAP.put(KFSConstants.FinancialDocumentTypeCodes.CASH_RECEIPT, KFSConstants.FinancialDocumentTypeNames.CASH_RECEIPT);
        FINANCIAL_DOC_NAME_MAP.put(KFSConstants.FinancialDocumentTypeCodes.DISTRIBUTION_OF_INCOME_AND_EXPENSE, KFSConstants.FinancialDocumentTypeNames.DISTRIBUTION_OF_INCOME_AND_EXPENSE);
        FINANCIAL_DOC_NAME_MAP.put(KFSConstants.FinancialDocumentTypeCodes.GENERAL_ERROR_CORRECTION, KFSConstants.FinancialDocumentTypeNames.GENERAL_ERROR_CORRECTION);
        FINANCIAL_DOC_NAME_MAP.put(KFSConstants.FinancialDocumentTypeCodes.INTERNAL_BILLING, KFSConstants.FinancialDocumentTypeNames.INTERNAL_BILLING);
        FINANCIAL_DOC_NAME_MAP.put(KFSConstants.FinancialDocumentTypeCodes.SERVICE_BILLING, KFSConstants.FinancialDocumentTypeNames.SERVICE_BILLING);
        FINANCIAL_DOC_NAME_MAP.put(KFSConstants.FinancialDocumentTypeCodes.YEAR_END_DISTRIBUTION_OF_INCOME_AND_EXPENSE, KFSConstants.FinancialDocumentTypeNames.YEAR_END_DISTRIBUTION_OF_INCOME_AND_EXPENSE);
        FINANCIAL_DOC_NAME_MAP.put(KFSConstants.FinancialDocumentTypeCodes.YEAR_END_GENERAL_ERROR_CORRECTION, KFSConstants.FinancialDocumentTypeNames.YEAR_END_GENERAL_ERROR_CORRECTION);
        FINANCIAL_DOC_NAME_MAP.put(KFSConstants.FinancialDocumentTypeCodes.PROCUREMENT_CARD, KFSConstants.FinancialDocumentTypeNames.PROCUREMENT_CARD);
    }

    /**
     * We are using a substitute mechanism for asset locking which can lock on assets when rule check passed. Return empty list from
     * this method.
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        return new ArrayList<MaintenanceLock>();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);
        WorkflowDocument workflowDoc = documentHeader.getWorkflowDocument();
        // release lock for asset edit...
        if ((this.getBusinessObject() instanceof Asset && !(this.getBusinessObject() instanceof AssetFabrication)) && (workflowDoc.isCanceled() || workflowDoc.isDisapproved() || workflowDoc.isProcessed() || workflowDoc.isFinal())) {
            this.getCapitalAssetManagementModuleService().deleteAssetLocks(getDocumentNumber(), null);
        }
    }

    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.Maintainable#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     * @param document Maintenance Document used for editing
     * @param parameters Parameters available
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) { //Changed Map to Map<String, String[]> to fix error in edu.arizona.kfs.module.cam.document.AssetMaintainableImpl "The method processAfterEdit(MaintenanceDocument, Map) is ambiguous for the type AssetMaintainableImpl"
        initializeAttributes(document);
        // Identifies the latest location information
        getAssetLocationService().setOffCampusLocation(copyAsset);
        getAssetLocationService().setOffCampusLocation(asset);

        // Calculates payment summary and depreciation summary based on available payment records
        PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
        paymentSummaryService.calculateAndSetPaymentSummary(copyAsset);
        paymentSummaryService.calculateAndSetPaymentSummary(asset);

        // Identifies the merge history and separation history based on asset disposition records
        getAssetService().setSeparateHistory(copyAsset);
        getAssetService().setSeparateHistory(asset);

        // Finds out the latest retirement info, is asset is currently retired.
        RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
        retirementInfoService.setRetirementInfo(copyAsset);
        retirementInfoService.setRetirementInfo(asset);

        retirementInfoService.setMergeHistory(copyAsset);
        retirementInfoService.setMergeHistory(asset);

        // Finds out the latest equipment loan or return information if available
        EquipmentLoanOrReturnService equipmentLoanOrReturnService = SpringContext.getBean(EquipmentLoanOrReturnService.class);
        equipmentLoanOrReturnService.setEquipmentLoanInfo(copyAsset);
        equipmentLoanOrReturnService.setEquipmentLoanInfo(asset);

        super.processAfterEdit(document, parameters);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     *
     * KRAD Conversion: Performs customization of the core sections.
     *
     * Uses data dictionary for bo Asset to get the core section ids to set section titles.
     */
    @Override
    public List<Section> getCoreSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getCoreSections(document, oldMaintainable);

        Asset asset = (Asset) getBusinessObject();
        if (asset.getAssetPayments().size() == 0) {
            for (Section section : sections) {
                if (CamsConstants.Asset.SECTION_ID_PAYMENT_INFORMATION.equals(section.getSectionId())) {
                    section.setSectionTitle(section.getSectionTitle() + CamsConstants.Asset.SECTION_TITLE_NO_PAYMENT + asset.getCapitalAssetNumber());
                }
            }
        }

        return sections;
    }

    /**
     * This method gets old and new maintainable objects and creates convenience handles to them
     *
     * @param document Asset Edit Document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (asset == null) {
            asset = (Asset) document.getNewMaintainableObject().getBusinessObject();
            asset.setTagged();
        }
        if (copyAsset == null) {
            copyAsset = (Asset) document.getOldMaintainableObject().getBusinessObject();
        }

        setFabricationOn(document.getNewMaintainableObject().getBusinessObject() instanceof AssetFabrication);
    }

    /**
     * KFSMI-5964: added refresh to Asset object after retrieve to prevent updated depreciation data from
     * wiped on existing saved/enrouted maint. doc
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {

        //Asset only
        if (this.getBusinessObject() instanceof Asset && MaintainableWorkflowUtils.isDocumentSavedOrEnroute(getDocumentNumber())) {

            Asset asset = (Asset)getBusinessObject();
            asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);

            PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
            paymentSummaryService.calculateAndSetPaymentSummary(asset);
        }
    }

    /**
    * @see  org.kuali.kfs.sys.document.FinancialSystemMaintainable.processAfterPost
    */
    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        String[] customAction = parameters.get(KRADConstants.CUSTOM_ACTION);
        if (customAction != null && customAction.length > 0 && StringUtils.equals(CamsPropertyConstants.Asset.LAST_INVENTORY_DATE_UPDATE_BUTTON,customAction[0])) {
            WorkflowDocument workflowDoc = document.getDocumentHeader().getWorkflowDocument();
            if(workflowDoc != null && workflowDoc.isInitiated()) {
                asset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));            
                String userPrincipalName= GlobalVariables.getUserSession().getPrincipalName();
                final String noteTextPattern = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(CamsKeyConstants.Asset.LAST_INVENTORY_DATE_UPDATE_NOTE_TEXT);
                Object[] arguments = { userPrincipalName, asset.getCapitalAssetNumber().toString() };
                String noteText = MessageFormat.format(noteTextPattern, arguments);
                Note lastInventoryDateUpdatedNote = getDocumentService().createNoteFromDocument(document, noteText);
                lastInventoryDateUpdatedNote.setAuthorUniversalIdentifier(getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER).getPrincipalId());
                document.addNote(lastInventoryDateUpdatedNote);
                getDocumentService().saveDocumentNotes(document);
            }
        }
        super.processAfterPost(document, parameters);
    }

    @Override
    public void saveBusinessObject() {
        Asset asset = ((Asset) businessObject);
        if (asset.getCapitalAssetNumber() == null) {
            asset.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
        }
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_LOCATIONS);
        // update and save asset location
        if (getAssetLocationService().isOffCampusLocationExists(asset.getOffCampusLocation())) {
            getAssetLocationService().updateOffCampusLocation(asset);
        }
        super.saveBusinessObject();
    }


    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterNew(document, parameters);
        initializeAttributes(document);
        // document.getNewMaintainableObject().setGenerateDefaultValues(false);
        if (asset.getCreateDate() == null) {
            asset.setCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            asset.setAcquisitionTypeCode(CamsConstants.Asset.ACQUISITION_TYPE_CODE_C);
            asset.setVendorName(CamsConstants.Asset.VENDOR_NAME_CONSTRUCTED);
            asset.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_UNDER_CONSTRUCTION);
            asset.setPrimaryDepreciationMethodCode(CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE);
            asset.setCapitalAssetTypeCode(SpringContext.getBean(ParameterService.class).getParameterValueAsString(Asset.class, CamsConstants.Parameters.DEFAULT_FABRICATION_ASSET_TYPE_CODE));
            asset.setManufacturerName(SpringContext.getBean(ParameterService.class).getParameterValueAsString(Asset.class, CamsConstants.Parameters.DEFAULT_FABRICATION_ASSET_MANUFACTURER));
            getAssetService().setFiscalPeriod(asset);
        }
        // setup offCampusLocation
        getAssetLocationService().setOffCampusLocation(asset);
    }

    @Override
    public void setGenerateDefaultValues(String docTypeName) {

    }

    public List<String> getFpLinks() {
        Asset asset = (Asset) getBusinessObject();
        List<Long> assetNumbers = new ArrayList<Long>();
        assetNumbers.add(asset.getCapitalAssetNumber());
        return SpringContext.getBean(AssetLockService.class).getAssetLockingDocuments(assetNumbers, CamsConstants.DocumentTypeName.ASSET_FP_INQUIRY, "");
    }

    public List<String> getPreqLinks() {
        Asset asset = (Asset) getBusinessObject();
        List<Long> assetNumbers = new ArrayList<Long>();
        assetNumbers.add(asset.getCapitalAssetNumber());
        return SpringContext.getBean(AssetLockService.class).getAssetLockingDocuments(assetNumbers, CamsConstants.DocumentTypeName.ASSET_PREQ_INQUIRY, "");
    }

    public List<String> getFpLinkedDocumentInfo() {
        List<String> documentInfo = new ArrayList<String>();
        Iterator<String> fpDocumentNumbers = getFpLinks().iterator();
        while (fpDocumentNumbers.hasNext()) {
            String aDocumentNumber = fpDocumentNumbers.next();
            try {
                String docTypeName = getDocumentService().getByDocumentHeaderId(aDocumentNumber).getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
                documentInfo.add(FINANCIAL_DOC_NAME_MAP.get(docTypeName) + "-" + aDocumentNumber);
            }
            catch (WorkflowException e) {
                throw new RuntimeException("Caught WorkflowException trying to get document type name", e);
            }
        }
        return documentInfo;
    }

    public boolean isFabricationOn() {
        return fabricationOn;
    }

    public void setFabricationOn(boolean fabricationOn) {
        this.fabricationOn = fabricationOn;
    }

    private AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    private AssetLocationService getAssetLocationService() {
        return SpringContext.getBean(AssetLocationService.class);
    }

    /**
     * @return the default implementation of the DocumentService
     */
    protected static DocumentService getDocumentService() {
        if (documentService == null) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }

    public IdentityService getIdentityService() {
        if (identityService == null) {
            identityService = SpringContext.getBean(IdentityService.class);
        }
        return identityService;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }
}

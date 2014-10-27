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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.gl.AssetRetirementGeneralLedgerPendingEntrySource;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.LedgerPostingMaintainable;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global location
 * assets
 */
public class AssetRetirementGlobalMaintainableImpl extends LedgerPostingMaintainable {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementGlobalMaintainableImpl.class);
    protected static final String RETIRED_ASSET_TRANSFERRED_EXTERNALLY = "RetiredAssetTransferredExternally";
    protected static final String RETIRED_ASSET_SOLD_OR_GIFTED = "RetiredAssetSoldOrGifted";


    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemGlobalMaintainable#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        String retirementReason = ((AssetRetirementGlobal) getBusinessObject()).getRetirementReasonCode();
        if (this.RETIRED_ASSET_TRANSFERRED_EXTERNALLY.equals(nodeName)) {
            return CamsConstants.AssetRetirementReasonCode.EXTERNAL_TRANSFER.equalsIgnoreCase(retirementReason);
        }
        if (this.RETIRED_ASSET_SOLD_OR_GIFTED.equals(nodeName)) {
            return CamsConstants.AssetRetirementReasonCode.SOLD.equalsIgnoreCase(retirementReason) || CamsConstants.AssetRetirementReasonCode.GIFT.equalsIgnoreCase(retirementReason);
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

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

    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }


    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.setupNewFromExisting(document, parameters);

        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        if (getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal)) {
            if (ObjectUtils.isNotNull(assetRetirementGlobal.getMergedTargetCapitalAssetNumber())) {
                assetRetirementGlobal.setMergedTargetCapitalAssetDescription(assetRetirementGlobal.getMergedTargetCapitalAsset().getCapitalAssetDescription());
            }
        }

        // add doc header description if retirement reason is "MERGED"
        if (CamsConstants.AssetRetirementReasonCode.MERGED.equals(assetRetirementGlobal.getRetirementReasonCode())) {
            document.getDocumentHeader().setDocumentDescription(CamsConstants.AssetRetirementGlobal.MERGE_AN_ASSET_DESCRIPTION);
        }

        // Fiscal Year End modifications
        String docType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
        ParameterEvaluatorService parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        ParameterEvaluator evaluator = parameterEvaluatorService.getParameterEvaluator(KFSConstants.CoreModuleNamespaces.KFS, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.FISCAL_PERIOD_SELECTION_DOCUMENT_TYPES, docType);
        if (evaluator.evaluationSucceeds() && isPeriod13(assetRetirementGlobal) ) {
            Integer closingYear = new Integer(SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));
            String closingDate = getClosingDate(closingYear);
            try {
                if (ObjectUtils.isNotNull(assetRetirementGlobal.getPostingYear()) ) {
                    assetRetirementGlobal.setAccountingPeriodCompositeString(assetRetirementGlobal.getAccountingPeriod().getUniversityFiscalPeriodCode()+assetRetirementGlobal.getPostingYear());
                }
                updateAssetRetirementGlobalForPeriod13(assetRetirementGlobal, closingYear, closingDate);
                assetRetirementGlobal.refreshNonUpdateableReferences();
            } catch (Exception e) {
                LOG.error(e);
            }
        }

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
            // Set non-persistent values. So the screen can show them after submit.
            getAssetService().setAssetSummaryFields(assetRetirementGlobalDetail.getAsset());
        }
    }

    @Override
    public void addMultipleValueLookupResults(MaintenanceDocument document, String collectionName, Collection<PersistableBusinessObject> rawValues, boolean needsBlank, PersistableBusinessObject bo) {
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getDocumentBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        int nElements = assetRetirementGlobal.getAssetRetirementGlobalDetails().size() + rawValues.size();
        if (!getAssetService().isDocumentEnrouting(document) && !getAssetRetirementService().isAllowedRetireMultipleAssets(document) && nElements > new Integer(1)) {
            GlobalVariables.getMessageMap().putErrorForSectionId(CamsConstants.AssetRetirementGlobal.SECTION_ID_ASSET_DETAIL_INFORMATION, CamsKeyConstants.Retirement.ERROR_MULTIPLE_ASSET_RETIRED);
        }
        else {
            GlobalVariables.getMessageMap().clearErrorMessages();

            // Adding the selected asset.
            super.addMultipleValueLookupResults(document, collectionName, rawValues, needsBlank, bo);
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
            // Set non-persistent values in multiple lookup result collection. So the screen can show them when return from multiple
            // lookup.
            for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
                getAssetService().setAssetSummaryFields(assetRetirementGlobalDetail.getAsset());
            }
        }
        else if (CamsConstants.AssetRetirementGlobal.ASSET_LOOKUPABLE_ID.equalsIgnoreCase(refreshCaller)) {
            // Set non-persistent values in the result from asset lookup. So the screen can show them when return from single asset
            // lookup.
            String referencesToRefresh = (String) fieldValues.get(KRADConstants.REFERENCES_TO_REFRESH);
            if (getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal) && CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET.equals(referencesToRefresh)) {
                assetRetirementGlobal.setMergedTargetCapitalAssetDescription(assetRetirementGlobal.getMergedTargetCapitalAsset().getCapitalAssetDescription());
            }
            AssetRetirementGlobalDetail newDetail = (AssetRetirementGlobalDetail) newCollectionLines.get(CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS);
            getAssetService().setAssetSummaryFields(newDetail.getAsset());
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();

        if (documentHeader.getWorkflowDocument().isEnroute()) {
            // display a message for asset not generating ledger entries when it is federally owned
            boolean allPaymentsFederalOwned = true;
            List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();
            for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
                for (AssetPayment assetPayment : assetRetirementGlobalDetail.getAsset().getAssetPayments()) {
                    if (!getAssetPaymentService().isPaymentFederalOwned(assetPayment)) {
                        allPaymentsFederalOwned = false;
                    }
                }
            }

            if (allPaymentsFederalOwned) {
                KNSGlobalVariables.getMessageList().add(CamsKeyConstants.Retirement.MESSAGE_NO_LEDGER_ENTRY_REQUIRED_RETIREMENT);
            }

        }
        // all approvals have been processed, the retirement date is set to the approval date
        if (documentHeader.getWorkflowDocument().isProcessed()) {
            assetRetirementGlobal.setRetirementDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            SpringContext.getBean(BusinessObjectService.class).save(assetRetirementGlobal);

            if (getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal)) {
                assetRetirementGlobal.getMergedTargetCapitalAsset().setCapitalAssetDescription(assetRetirementGlobal.getMergedTargetCapitalAssetDescription());
                SpringContext.getBean(BusinessObjectService.class).save(assetRetirementGlobal.getMergedTargetCapitalAsset());
            }

        }
        new AssetRetirementGeneralLedgerPendingEntrySource((FinancialSystemDocumentHeader) documentHeader).doRouteStatusChange(assetRetirementGlobal.getGeneralLedgerPendingEntries());

        // release the lock when document status changed as following...
        WorkflowDocument workflowDoc = documentHeader.getWorkflowDocument();
        if (workflowDoc.isCanceled() || workflowDoc.isDisapproved() || workflowDoc.isProcessed() || workflowDoc.isFinal()) {
            this.getCapitalAssetManagementModuleService().deleteAssetLocks(getDocumentNumber(), null);
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        super.addNewLineToCollection(collectionName);

        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        if (StringUtils.isBlank(assetRetirementGlobal.getMergedTargetCapitalAssetDescription()) && ObjectUtils.isNotNull(assetRetirementGlobal.getMergedTargetCapitalAssetNumber())) {
            assetRetirementGlobal.setMergedTargetCapitalAssetDescription(assetRetirementGlobal.getMergedTargetCapitalAsset().getCapitalAssetDescription());
        }
    }

    @Override
    public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
        return Asset.class;
    }

    @Override
    public Map<String, String> populateNewCollectionLines( Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument, String methodToCall ) {
        String capitalAssetNumber = (String)fieldValues.get(CamsPropertyConstants.AssetRetirementGlobal.CAPITAL_ASSET_NUMBER);

        if (StringUtils.isNotBlank(capitalAssetNumber)) {
            fieldValues.remove(CamsPropertyConstants.AssetRetirementGlobal.CAPITAL_ASSET_NUMBER);
            fieldValues.put(CamsPropertyConstants.AssetRetirementGlobal.CAPITAL_ASSET_NUMBER, capitalAssetNumber.trim());
		}
        return super.populateNewCollectionLines(fieldValues, maintenanceDocument, methodToCall);

    }

    private AssetRetirementService getAssetRetirementService() {
        return SpringContext.getBean(AssetRetirementService.class);
    }

    private AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    private AssetPaymentService getAssetPaymentService() {
        return SpringContext.getBean(AssetPaymentService.class);
    }

    /**
     * Checks for Accounting Period 13
     * @param assetRetirementGlobal
     * @return true if the accountingPeriod in assetRetirementGlobal is 13.
     * TODO Remove hardcoding
     */
    private boolean isPeriod13(AssetRetirementGlobal assetRetirementGlobal) {
        if (ObjectUtils.isNull(assetRetirementGlobal.getAccountingPeriod())) {
            return false;
        }
        return "13".equals(assetRetirementGlobal.getAccountingPeriod().getUniversityFiscalPeriodCode());
    }

    /**
     * Return the closing date as mm/dd/yyyy
     * @param closingYear
     * @return the closing date as mm/dd/yyyy

     */
    private String getClosingDate(Integer closingYear) {
        return SpringContext.getBean(AssetGlobalService.class).getFiscalYearEndDayAndMonth() + closingYear.toString();
    }


    /**
     * Return the calendar Date for the closing year
     * @param closingYear
     * @return 01/01/[closing year]
     * TODO Remove hardcoding
     */
    private String getClosingCalendarDate(Integer closingYear) {
        return "01/01/" + closingYear.toString();
    }

    /**
     * Convenience method to reduce clutter
     * @return {@link DateTimeService}
     */
    private DateTimeService getDateTimeService() {
        return SpringContext.getBean(DateTimeService.class);
    }

    /**
     * Perform changes to assetRetirementGlobal on period 13.
     * @param assetRetirementGlobal
     */
    private void doPeriod13Changes(AssetRetirementGlobal assetRetirementGlobal) {
        if (isPeriod13(assetRetirementGlobal)) {
            Integer closingYear = new Integer(SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));
            String closingDate = getClosingDate(closingYear);
            try {
                updateAssetRetirementGlobalForPeriod13(assetRetirementGlobal, closingYear, closingDate);
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }


    /**
     * Update assetRetirementGlobal fields for period 13
     * @param assetRetirementGlobal
     * @param closingYear
     * @param closingDate
     * @throws ParseException
     */
    private void updateAssetRetirementGlobalForPeriod13(AssetRetirementGlobal assetRetirementGlobal, Integer closingYear, String closingDate) throws ParseException {
        // TODO what, if anything needs to be here?
//        assetRetirementGlobal.setCreateDate(getDateTimeService().getCurrentSqlDate());
//        assetRetirementGlobal.setCapitalAssetInServiceDate(getDateTimeService().convertToSqlDate(closingDate));
//        assetRetirementGlobal.setCreateDate(getDateTimeService().convertToSqlDate(closingDate));
//        assetRetirementGlobal.setCapitalAssetDepreciationDate(getDateTimeService().convertToSqlDate(getClosingCalendarDate(closingYear)));
//        assetRetirementGlobal.setLastInventoryDate(getDateTimeService().getCurrentSqlDate());
    }
}

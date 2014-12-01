/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.fp.document.CapitalAssetInformationDocumentBase;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetLock;
import org.kuali.kfs.module.cam.service.AssetLockService;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.ObjectUtils;

public class CapitalAssetManagementModuleServiceImpl implements CapitalAssetManagementModuleService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CapitalAssetManagementModuleServiceImpl.class);

    protected CapitalAssetBuilderModuleService capitalAssetBuilderModuleService;

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#storeAssetLocks(java.util.List, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public boolean storeAssetLocks(List<Long> capitalAssetNumbers, String documentNumber, String documentType, String lockingInformation) {
        /* Asset locking information may take valid information about asset lock from original
         * document. It should not be reset to another value.
         * It's required to fix for PREQ/CM Individual Asset system locking issue.
         */
        List<AssetLock> assetLocks = getAssetLockService().buildAssetLockHelper(capitalAssetNumbers, documentNumber, documentType, StringUtils.isBlank(lockingInformation) ? CamsConstants.defaultLockingInformation : lockingInformation);
        return getAssetLockService().checkAndSetAssetLocks(assetLocks, false);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#deleteAssetLocks(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteAssetLocks(String documentNumber, String lockingInformation) {
        getAssetLockService().deleteAssetLocks(documentNumber, lockingInformation);
    }

    protected AssetLockService getAssetLockService() {
        return SpringContext.getBean(AssetLockService.class);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#isAssetLockedByDocument(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public boolean isAssetLockedByCurrentDocument(String documentNumber, String lockingInformation) {
        //return getAssetLockService().isAssetLockedByCurrentDocument(documentNumber, lockingInformation == null ? CamsConstants.defaultLockingInformation : lockingInformation);
        return getAssetLockService().isAssetLockedByCurrentDocument(documentNumber, lockingInformation);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#isAssetLocked(java.util.List, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public boolean isAssetLocked(List<Long> assetNumbers, String documentTypeName, String excludingDocumentNumber) {
        return getAssetLockService().isAssetLocked(assetNumbers, documentTypeName, excludingDocumentNumber);
    }

    /**
     * Generate asset locks for FP document if it collects capital
     * asset number(s) and has capital asset transactions eligible for CAB
     * batch.
     *
     * Creating asset lock is based on each capital asset line rather
     * than the whole FP document.
     *
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#generateCapitalAssetLock(org.kuali.rice.krad.document.Document)
     */
    @Override
    public void generateCapitalAssetLock(Document document, String documentTypeName) {
        List<AssetLock> assetLocks = new ArrayList<AssetLock>();
        StringBuilder capitalAssetToBeLocked = new StringBuilder();

        if (document instanceof CapitalAssetEditable) {
            List<CapitalAssetInformation> capitalAssets = ((CapitalAssetEditable) document).getCapitalAssetInformation();
            if (capitalAssetBuilderModuleService.isDocumentEligibleForCABBatch(documentTypeName)) {
                List<String> includedObjectSubTypeCodes = capitalAssetBuilderModuleService.getBatchIncludedObjectSubTypes();
                List<String> excludedChartCodes = capitalAssetBuilderModuleService.getBatchExcludedChartCodes();
                List<String> excludedSubFundCodes = capitalAssetBuilderModuleService.getBatchExcludedSubFundCodes();

                for (CapitalAssetInformation assetLine : capitalAssets) {
                    // Can skip asset locking if it's creating new asset instead
                    Long capitalAssetNumber = assetLine.getCapitalAssetNumber();
                    if (capitalAssetNumber != null) {
                        if (capitalAssetBuilderModuleService.isAssetLineEligibleForCABBatch(assetLine, ((CapitalAssetInformationDocumentBase)document).getPostingYear(), includedObjectSubTypeCodes, excludedChartCodes, excludedSubFundCodes)) {
                            AssetLock newLock = new AssetLock(document.getDocumentNumber(), capitalAssetNumber, assetLine.getCapitalAssetLineNumber() == null? CamsConstants.defaultLockingInformation : assetLine.getCapitalAssetLineNumber().toString(), documentTypeName);
                            assetLocks.add(newLock);
                            if (!capitalAssetToBeLocked.toString().isEmpty()) {
                                capitalAssetToBeLocked.append(",");
                            }
                            capitalAssetToBeLocked.append(capitalAssetNumber.toString());
                        }
                    }
                }

                if (!getAssetLockService().checkAndSetAssetLocks(assetLocks, true) ) {
                    throw new ValidationException("Asset " + capitalAssetToBeLocked.toString() + " is being locked by other documents.");
                }
            }
        }
    }

    /**
     * FP document eligible for asset lock when any of its accounting line is taken into CAB during CAB batch.
     *
     * @param accountingDocument
     * @return
     */
    @Override
    public boolean isFpDocumentEligibleForAssetLock(AccountingDocument accountingDocument, String documentType) {
        // get the system parameter values first so we don't need to repeat this step for each accounting line check
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        List<String> excludedDocTypeCodes = new ArrayList<String>( parameterService.getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.DOCUMENT_TYPES) );
        // check with the docTypeCodes system parameter
        if (!excludedDocTypeCodes.isEmpty() && excludedDocTypeCodes.contains(documentType)) {
            return false;
        }

        List<String> includedFinancialObjectSubTypeCodes = new ArrayList<String>( parameterService.getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.OBJECT_SUB_TYPES) );
        List<String> excludedChartCodes = new ArrayList<String>( parameterService.getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.CHARTS) );
        List<String> excludedSubFundCodes = new ArrayList<String>( parameterService.getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.SUB_FUND_GROUPS) );

        List<SourceAccountingLine> sAccountingLines = accountingDocument.getSourceAccountingLines();
        for (SourceAccountingLine sourceAccountingLine : sAccountingLines) {
            if (isAccountLineEligibleForCABBatch(includedFinancialObjectSubTypeCodes, excludedChartCodes, excludedSubFundCodes, sourceAccountingLine)) {
                return true;
            }

        }

        List<TargetAccountingLine> tAccountingLines = accountingDocument.getTargetAccountingLines();
        for (TargetAccountingLine targetAccountingLine : tAccountingLines) {
            if (isAccountLineEligibleForCABBatch(includedFinancialObjectSubTypeCodes, excludedChartCodes, excludedSubFundCodes, targetAccountingLine)) {
                return true;
            }
        }
        // If none of the accounting line eligible for CAB batch, CAB batch won't take the FP document into CAB
        return false;
    }

    /**
     * This method check if accounting line eligible for CAB batch taking into CAB
     *
     * @param includedFinancialBalanceTypeCodes
     * @param includedFinancialObjectSubTypeCodes
     * @param excludedChartCodes
     * @param excludedDocTypeCodes
     * @param excludedSubFundCodes
     * @param accountingLine
     * @return
     */
    protected boolean isAccountLineEligibleForCABBatch(List<String> includedFinancialObjectSubTypeCodes, List<String> excludedChartCodes, List<String> excludedSubFundCodes, AccountingLineBase accountingLine) {
        // check with the financialObjectSubTypeCodes system parameter
        if (!includedFinancialObjectSubTypeCodes.isEmpty() && !includedFinancialObjectSubTypeCodes.contains(accountingLine.getObjectCode().getFinancialObjectSubTypeCode())) {
            return false;
        }

        // check with the charOfAccountCode system parameter
        if (!excludedChartCodes.isEmpty() && excludedChartCodes.contains(accountingLine.getChartOfAccountsCode())) {
            return false;
        }

        // check with the subFundCodes system parameter
        if (!excludedSubFundCodes.isEmpty() && excludedSubFundCodes.contains(accountingLine.getAccount().getSubFundGroupCode())) {
            return false;
        }

        return true;
    }


    /**
     * Remove asset locks if document won't move towards Final status.
     *
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#deleteDocumentAssetLocks(org.kuali.rice.krad.document.Document)
     */
    @Override
    public void deleteDocumentAssetLocks(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        // remove all current locks owned by the document
        if (workflowDocument.isCanceled() || workflowDocument.isDisapproved() || workflowDocument.isRecalled() || workflowDocument.isException()) {
            this.deleteAssetLocks(document.getDocumentNumber(), null);
        }
    }

    /**
     * @return the capitalAssetBuilderModuleService
     */
    public CapitalAssetBuilderModuleService getCapitalAssetBuilderModuleService() {
        return capitalAssetBuilderModuleService;
    }

    /**
     * @param capitalAssetBuilderModuleService the capitalAssetBuilderModuleService to set
     */
    public void setCapitalAssetBuilderModuleService(
            CapitalAssetBuilderModuleService capitalAssetBuilderModuleService) {
        this.capitalAssetBuilderModuleService = capitalAssetBuilderModuleService;
    }
}

/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
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
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class CapitalAssetManagementModuleServiceImpl implements CapitalAssetManagementModuleService {
    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#storeAssetLocks(java.util.List, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public boolean storeAssetLocks(List<Long> capitalAssetNumbers, String documentNumber, String documentType, String lockingInformation) {
        List<AssetLock> assetLocks = getAssetLockService().buildAssetLockHelper(capitalAssetNumbers, documentNumber, documentType, StringUtils.isBlank(lockingInformation) ? CamsConstants.defaultLockingInformation : lockingInformation);
        return getAssetLockService().checkAndSetAssetLocks(assetLocks);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#deleteAssetLocks(java.lang.String, java.lang.String)
     */
    public void deleteAssetLocks(String documentNumber, String lockingInformation) {
        getAssetLockService().deleteAssetLocks(documentNumber, lockingInformation == null ? CamsConstants.defaultLockingInformation : lockingInformation);
    }

    protected AssetLockService getAssetLockService() {
        return SpringContext.getBean(AssetLockService.class);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#isAssetLockedByDocument(java.lang.String,
     *      java.lang.String)
     */
    public boolean isAssetLockedByCurrentDocument(String documentNumber, String lockingInformation) {
        return getAssetLockService().isAssetLockedByCurrentDocument(documentNumber, lockingInformation == null ? CamsConstants.defaultLockingInformation : lockingInformation);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#isAssetLocked(java.util.List, java.lang.String,
     *      java.lang.String)
     */
    public boolean isAssetLocked(List<Long> assetNumbers, String documentTypeName, String excludingDocumentNumber) {
        return getAssetLockService().isAssetLocked(assetNumbers, documentTypeName, excludingDocumentNumber);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#generateCapitalAssetLock(org.kuali.rice.kns.document.Document)
     */
    public void generateCapitalAssetLock(Document document, String documentTypeName) {
        CapitalAssetInformation capitalAssetInformation = ((CapitalAssetEditable) document).getCapitalAssetInformation();

        if (ObjectUtils.isNotNull(capitalAssetInformation) && ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetNumber())) {
            ArrayList<Long> capitalAssetNumbers = new ArrayList<Long>();
            capitalAssetNumbers.add(capitalAssetInformation.getCapitalAssetNumber());

            if (document instanceof AccountingDocument) {
                if (isFpDocumentEligibleForAssetLock((AccountingDocument) document, documentTypeName) && !this.storeAssetLocks(capitalAssetNumbers, document.getDocumentNumber(), documentTypeName, null)) {
                    throw new ValidationException("Asset " + capitalAssetNumbers.toString() + " is being locked by other documents.");
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
    public boolean isFpDocumentEligibleForAssetLock(AccountingDocument accountingDocument, String documentType) {
        // get the system parameter values first so we don't need to repeat this step for each accounting line check
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        List<String> excludedDocTypeCodes = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.DOCUMENT_TYPES);
        // check with the docTypeCodes system parameter
        if (!excludedDocTypeCodes.isEmpty() && excludedDocTypeCodes.contains(documentType)) {
            return false;
        }

        List<String> includedFinancialObjectSubTypeCodes = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.OBJECT_SUB_TYPES);
        List<String> excludedChartCodes = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.CHARTS);
        List<String> excludedSubFundCodes = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.SUB_FUND_GROUPS);

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
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#deleteDocumentAssetLocks(org.kuali.rice.kns.document.Document)
     */
    public void deleteDocumentAssetLocks(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        CapitalAssetInformation capitalAssetInformation = ((CapitalAssetEditable) document).getCapitalAssetInformation();

        // Deleting document lock
        if (workflowDocument.stateIsCanceled() || workflowDocument.stateIsDisapproved()) {
            if (ObjectUtils.isNotNull(capitalAssetInformation) && isAssetLockedByCurrentDocument(document.getDocumentNumber(), null)) {
                this.deleteAssetLocks(document.getDocumentNumber(), null);
            }
        }
    }
}

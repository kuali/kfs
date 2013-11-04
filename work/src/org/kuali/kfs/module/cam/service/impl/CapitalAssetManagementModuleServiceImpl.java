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

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#storeAssetLocks(java.util.List, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public boolean storeAssetLocks(List<Long> capitalAssetNumbers, String documentNumber, String documentType, String lockingInformation) {
        List<AssetLock> assetLocks = getAssetLockService().buildAssetLockHelper(capitalAssetNumbers, documentNumber, documentType, StringUtils.isBlank(lockingInformation) ? CamsConstants.defaultLockingInformation : lockingInformation);
        Integer lockingIndex = 1;
        for (AssetLock assetLock : assetLocks) {
            assetLock.setLockingInformation(lockingIndex.toString());
            lockingIndex++;
        }

        return getAssetLockService().checkAndSetAssetLocks(assetLocks);
    }

    /**
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#deleteAssetLocks(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteAssetLocks(String documentNumber, String lockingInformation) {
        //getAssetLockService().deleteAssetLocks(documentNumber, lockingInformation == null ? CamsConstants.defaultLockingInformation : lockingInformation);
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
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#generateCapitalAssetLock(org.kuali.rice.krad.document.Document)
     */
    @Override
    public void generateCapitalAssetLock(Document document, String documentTypeName) {
        List<CapitalAssetInformation> capitalAssets = ((CapitalAssetEditable) document).getCapitalAssetInformation();

        String capitalAssetToBeLocked = "";
        ArrayList<Long> capitalAssetNumbers = new ArrayList<Long>();

        for (CapitalAssetInformation capitalAssetInformation : capitalAssets) {
            if (ObjectUtils.isNotNull(capitalAssetInformation) && ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetNumber())) {
                capitalAssetNumbers.add(capitalAssetInformation.getCapitalAssetNumber());
                if (capitalAssetToBeLocked.isEmpty()) {
                    capitalAssetToBeLocked = capitalAssetToBeLocked.concat(capitalAssetInformation.getCapitalAssetNumber().toString());
                }
                else {
                    capitalAssetToBeLocked = capitalAssetToBeLocked.concat(",").concat(capitalAssetInformation.getCapitalAssetNumber().toString());
                }

            }
        }

        if (capitalAssetNumbers.size() > 0) {
            if (document instanceof AccountingDocument) {
                if (isFpDocumentEligibleForAssetLock((AccountingDocument) document, documentTypeName) && !this.storeAssetLocks(capitalAssetNumbers, document.getDocumentNumber(), documentTypeName, null)) {
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
     * @see org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService#deleteDocumentAssetLocks(org.kuali.rice.krad.document.Document)
     */
    @Override
    public void deleteDocumentAssetLocks(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        // remove all current locks
        this.deleteAssetLocks(document.getDocumentNumber(), null);

        // if document is not cancelled nor disapproved nor recalled regenerate them all again based on set
        if (!workflowDocument.isCanceled() && !workflowDocument.isDisapproved() && !workflowDocument.isRecalled()) {
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(document.getClass());
            generateCapitalAssetLock(document, documentTypeName);
        }
    }
}

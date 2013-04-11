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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsConstants.DocumentTypeName;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetLock;
import org.kuali.kfs.module.cam.dataaccess.CapitalAssetLockDao;
import org.kuali.kfs.module.cam.service.AssetLockService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AssetLockServiceImpl implements AssetLockService {
    private static Logger LOG = Logger.getLogger(AssetLockService.class);

    private CapitalAssetLockDao capitalAssetLockDao;

    // FP document types includes:
    // CashReceipt,DistributionOfIncomeAndExpense,GeneralErrorCorrection,InternalBilling,ServiceBilling,YearEndDistributionOfIncomeAndExpense,YearEndGeneralErrorCorrection,ProcurementCard
    private static final Map<String, String> FINANCIAL_DOC_TYPE_MAP = new HashMap<String, String>();
    static {
        FINANCIAL_DOC_TYPE_MAP.put(KFSConstants.FinancialDocumentTypeCodes.CASH_RECEIPT, KFSConstants.FinancialDocumentTypeCodes.CASH_RECEIPT);
        FINANCIAL_DOC_TYPE_MAP.put(KFSConstants.FinancialDocumentTypeCodes.ADVANCE_DEPOSIT, KFSConstants.FinancialDocumentTypeCodes.ADVANCE_DEPOSIT);
        FINANCIAL_DOC_TYPE_MAP.put(KFSConstants.FinancialDocumentTypeCodes.CREDIT_CARD_RECEIPT, KFSConstants.FinancialDocumentTypeCodes.CREDIT_CARD_RECEIPT);
        FINANCIAL_DOC_TYPE_MAP.put(KFSConstants.FinancialDocumentTypeCodes.DISTRIBUTION_OF_INCOME_AND_EXPENSE, KFSConstants.FinancialDocumentTypeCodes.DISTRIBUTION_OF_INCOME_AND_EXPENSE);
        FINANCIAL_DOC_TYPE_MAP.put(KFSConstants.FinancialDocumentTypeCodes.GENERAL_ERROR_CORRECTION, KFSConstants.FinancialDocumentTypeCodes.GENERAL_ERROR_CORRECTION);
        FINANCIAL_DOC_TYPE_MAP.put(KFSConstants.FinancialDocumentTypeCodes.INTERNAL_BILLING, KFSConstants.FinancialDocumentTypeCodes.INTERNAL_BILLING);
        FINANCIAL_DOC_TYPE_MAP.put(KFSConstants.FinancialDocumentTypeCodes.SERVICE_BILLING, KFSConstants.FinancialDocumentTypeCodes.SERVICE_BILLING);
        FINANCIAL_DOC_TYPE_MAP.put(KFSConstants.FinancialDocumentTypeCodes.YEAR_END_DISTRIBUTION_OF_INCOME_AND_EXPENSE, KFSConstants.FinancialDocumentTypeCodes.YEAR_END_DISTRIBUTION_OF_INCOME_AND_EXPENSE);
        FINANCIAL_DOC_TYPE_MAP.put(KFSConstants.FinancialDocumentTypeCodes.YEAR_END_GENERAL_ERROR_CORRECTION, KFSConstants.FinancialDocumentTypeCodes.YEAR_END_GENERAL_ERROR_CORRECTION);
        FINANCIAL_DOC_TYPE_MAP.put(KFSConstants.FinancialDocumentTypeCodes.PROCUREMENT_CARD, KFSConstants.FinancialDocumentTypeCodes.PROCUREMENT_CARD);
        FINANCIAL_DOC_TYPE_MAP.put(KFSConstants.FinancialDocumentTypeCodes.INTRA_ACCOUNT_ADJUSTMENT, KFSConstants.FinancialDocumentTypeCodes.INTRA_ACCOUNT_ADJUSTMENT);
    }

    // CAMS document types for maintain asset: AssetMaintenance, AssetFabrication, Asset Global, Asset Location Global,
    // LoanAndReturn
    private static final Map<String, String> ASSET_MAINTAIN_DOC_TYPE_MAP = new HashMap<String, String>();
    static {
        ASSET_MAINTAIN_DOC_TYPE_MAP.put(DocumentTypeName.ASSET_EDIT, DocumentTypeName.ASSET_EDIT);
        ASSET_MAINTAIN_DOC_TYPE_MAP.put(DocumentTypeName.ASSET_LOCATION_GLOBAL, DocumentTypeName.ASSET_LOCATION_GLOBAL);
        ASSET_MAINTAIN_DOC_TYPE_MAP.put(DocumentTypeName.ASSET_EQUIPMENT_LOAN_OR_RETURN, DocumentTypeName.ASSET_EQUIPMENT_LOAN_OR_RETURN);
        ASSET_MAINTAIN_DOC_TYPE_MAP.put(DocumentTypeName.ASSET_BARCODE_INVENTORY_ERROR, DocumentTypeName.ASSET_BARCODE_INVENTORY_ERROR);
        ASSET_MAINTAIN_DOC_TYPE_MAP.put(DocumentTypeName.ASSET_PAYMENT_FROM_CAB, DocumentTypeName.ASSET_PAYMENT_FROM_CAB);
    }

    // CAMS document types relating payment changes: AssetRetirement and Merge, AssetTransfer, AssetPayment, Asset Separate
    private static final Map<String, String> ASSET_PMT_CHG_DOC_TYPE_MAP = new HashMap<String, String>();
    static {
        ASSET_PMT_CHG_DOC_TYPE_MAP.put(DocumentTypeName.ASSET_RETIREMENT_GLOBAL, DocumentTypeName.ASSET_RETIREMENT_GLOBAL);
        ASSET_PMT_CHG_DOC_TYPE_MAP.put(DocumentTypeName.ASSET_TRANSFER, DocumentTypeName.ASSET_TRANSFER);
        ASSET_PMT_CHG_DOC_TYPE_MAP.put(DocumentTypeName.ASSET_PAYMENT, DocumentTypeName.ASSET_PAYMENT);
        ASSET_PMT_CHG_DOC_TYPE_MAP.put(DocumentTypeName.ASSET_SEPARATE, DocumentTypeName.ASSET_SEPARATE);
    }

    protected boolean isPurApDocument(String documentTypeName) {
        return CabConstants.PREQ.equals(documentTypeName) || CabConstants.CM.equals(documentTypeName);
    }

    /**
     * Gets the capitalAssetLockDao attribute.
     *
     * @return Returns the capitalAssetLockDao.
     */
    public CapitalAssetLockDao getCapitalAssetLockDao() {
        return capitalAssetLockDao;
    }


    /**
     * Sets the capitalAssetLockDao attribute value.
     *
     * @param capitalAssetLockDao The capitalAssetLockDao to set.
     */
    public void setCapitalAssetLockDao(CapitalAssetLockDao capitalAssetLockDao) {
        this.capitalAssetLockDao = capitalAssetLockDao;
    }


    /**
     * @param assetLocks must be from the same documentNumber and have the same documentTypeName
     * @return Return false without any of the asset being locked. Return true when all assets can be locked.
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#checkAndLockForDocument(java.util.Collection)
     */
 
    public synchronized boolean checkAndSetAssetLocks(List<AssetLock> assetLocks) {
        if (assetLocks == null || assetLocks.isEmpty() || !assetLocks.iterator().hasNext()) {
            return true;
        }

        AssetLock lock = assetLocks.iterator().next();
        String documentTypeName = lock.getDocumentTypeName();
        String documentNumber = lock.getDocumentNumber();

        // build asset number collection for lock checking.
        List assetNumbers = new ArrayList<Long>();
        for (AssetLock assetLock : assetLocks) {
            assetNumbers.add(assetLock.getCapitalAssetNumber());
        }

        // check each assetNumber is not locked by other document(s). PurAp document will ignore the locks since CAB batch will
        // set the lock anyway.
        if (isAssetLocked(assetNumbers, documentTypeName, documentNumber)) {
            return false;
        }

        for (AssetLock assetLock : assetLocks) {
            deleteAssetLocks(documentNumber, assetLock.getLockingInformation());
        }

        getBusinessObjectService().save(assetLocks);

        return true;
    }

    /**
     * To get blocking document types for given document type. If given document type is FP, blocking documents will be CAMS payment
     * change documents. If given document type is CAMs maintain related, the blocking documents are all CAMs doc excluding FP and
     * PURAP. For other cases, returning null which will block all other documents.
     *
     * @param documentTypeName
     * @return
     */
    protected Collection getBlockingDocumentTypes(String documentTypeName) {
        // FP document should be blocked by CAMS Payment change documents.
        if (FINANCIAL_DOC_TYPE_MAP.containsKey(documentTypeName)) {
            return ASSET_PMT_CHG_DOC_TYPE_MAP.values();
        }
        // CAMS maintain docs
        else if (ASSET_MAINTAIN_DOC_TYPE_MAP.containsKey(documentTypeName)) {
            List financialDocTypes = new ArrayList<String>();
            financialDocTypes.addAll(ASSET_MAINTAIN_DOC_TYPE_MAP.values());
            financialDocTypes.addAll(ASSET_PMT_CHG_DOC_TYPE_MAP.values());
            return financialDocTypes;
        }
        // FP blocking documents
        if (CamsConstants.DocumentTypeName.ASSET_FP_INQUIRY.equals(documentTypeName)) {
            return FINANCIAL_DOC_TYPE_MAP.values();
        }

        // PREQ blocking documents
        if (CamsConstants.DocumentTypeName.ASSET_PREQ_INQUIRY.equals(documentTypeName)) {
            List fpAndPurApDocTypes = new ArrayList<String>();
            fpAndPurApDocTypes.add(CabConstants.PREQ);
            fpAndPurApDocTypes.add(CabConstants.CM);
            return fpAndPurApDocTypes;
        }

        // For CAMs payment change document, any doc type can be the blocker, return null for this case
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#deleteLocks(java.lang.String, java.lang.String)
     */

    public void deleteAssetLocks(String documentNumber, String lockingInformation) {
        if (StringUtils.isBlank(documentNumber)) {
            return;
        }
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(CabPropertyConstants.CapitalAssetLock.DOCUMENT_NUMBER, documentNumber);
        if (StringUtils.isNotBlank(lockingInformation)) {
            fieldValues.put(CabPropertyConstants.CapitalAssetLock.LOCKING_INFORMATION, lockingInformation);
        }
        getBusinessObjectService().deleteMatching(AssetLock.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService#generateAssetLocks(java.util.Collection,
     *      java.lang.String, java.lang.String, java.lang.String)
     */

    public List<AssetLock> buildAssetLockHelper(List<Long> assetNumbers, String documentNumber, String documentType, String lockingInformation) {
        List<AssetLock> assetLocks = new ArrayList<AssetLock>();

        for (Long assetNumber : assetNumbers) {
            if (assetNumber != null) {
                AssetLock newLock = new AssetLock(documentNumber, assetNumber, lockingInformation, documentType);
                assetLocks.add(newLock);
            }
        }
        return assetLocks;
    }


    /**
     * Generating error messages and doc links for blocking documents.
     * 
     * @param blockingDocuments
     */
    protected void addBlockingDocumentErrorMessage(Collection<String> blockingDocuments, String documentTypeName) {
        for (String blockingDocId : blockingDocuments) {
            // build the link URL for the blocking document. Better to use DocHandler because this could be
            // a maintenance document or tDoc.
            Properties parameters = new Properties();
            parameters.put(KRADConstants.PARAMETER_DOC_ID, blockingDocId);
            parameters.put(KRADConstants.PARAMETER_COMMAND, KRADConstants.METHOD_DISPLAY_DOC_SEARCH_VIEW);

            String blockingUrl = UrlFactory.parameterizeUrl(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.WORKFLOW_URL_KEY) + "/" + KRADConstants.DOC_HANDLER_ACTION, parameters);
            if (LOG.isDebugEnabled()) {
                LOG.debug("blockingUrl = '" + blockingUrl + "'");
                LOG.debug("Record: " + blockingDocId + "is locked.");
            }

            // post an error about the locked document
            String[] errorParameters = { blockingUrl, blockingDocId };
            if (FINANCIAL_DOC_TYPE_MAP.containsKey(documentTypeName)) {
                // display a different error message for lock request from FP document.
                GlobalVariables.getMessageMap().putError(KRADConstants.GLOBAL_ERRORS, CamsKeyConstants.AssetLock.ERROR_ASSET_LOCKED, errorParameters);
            }
            else {
                GlobalVariables.getMessageMap().putError(KRADConstants.GLOBAL_ERRORS, CamsKeyConstants.AssetLock.ERROR_ASSET_MAINTENANCE_LOCKED, errorParameters);
            }
        }
    }

    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    /**
     * @see org.kuali.kfs.module.cam.service.AssetLockService#isAssetLockedByDocument(java.lang.String, java.lang.String)
     */
  
    public boolean isAssetLockedByCurrentDocument(String documentNumber, String lockingInformation) {
        if (StringUtils.isBlank(documentNumber)) {
            return false;
        }
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(CabPropertyConstants.CapitalAssetLock.DOCUMENT_NUMBER, documentNumber);
        if (StringUtils.isNotBlank(lockingInformation)) {
            fieldValues.put(CabPropertyConstants.CapitalAssetLock.LOCKING_INFORMATION, lockingInformation);
        }
        Collection<AssetLock> assetLocks = getBusinessObjectService().findMatching(AssetLock.class, fieldValues);
        return assetLocks != null && !assetLocks.isEmpty();
    }

    /**
     * Based on the given documentTypeName, it decides what document types could block it.
     *
     * @see org.kuali.kfs.module.cam.service.AssetLockService#isAssetLocked(java.util.List, java.lang.String, java.lang.String)
     */

    public boolean isAssetLocked(List<Long> assetNumbers, String documentTypeName, String excludingDocumentNumber) {
        if (assetNumbers == null || assetNumbers.isEmpty()) {
            return false;
        }
        if (!isPurApDocument(documentTypeName)) {
            List<String> lockingDocumentNumbers = getAssetLockingDocuments(assetNumbers, documentTypeName, excludingDocumentNumber);
            if (lockingDocumentNumbers != null && !lockingDocumentNumbers.isEmpty()) {
                addBlockingDocumentErrorMessage(lockingDocumentNumbers, documentTypeName);
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.cam.service.AssetLockService#getAssetLockingDocuments(java.util.List, java.lang.String,
     *      java.lang.String)
     */
  
    public List<String> getAssetLockingDocuments(List<Long> assetNumbers, String documentTypeName, String excludingDocumentNumber) {
        Collection blockingDocumentTypes = getBlockingDocumentTypes(documentTypeName);
        List<String> lockingDocumentNumbers = getCapitalAssetLockDao().getLockingDocumentNumbers(assetNumbers, blockingDocumentTypes, excludingDocumentNumber);
        return lockingDocumentNumbers;
    }


}

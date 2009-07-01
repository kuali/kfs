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
package org.kuali.kfs.sys.document.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.LedgerPostingDocument;
import org.kuali.kfs.sys.document.LedgerPostingMaintainable;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentTypeService;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;

/**
 * Default implementation of the FinancialSystemDocumentTypeService
 */
public class FinancialSystemDocumentTypeServiceImpl implements FinancialSystemDocumentTypeService {
    private DataDictionaryService dataDictionaryService;
    private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

    /**
     * Makes sure the doc type represented by the code either is or is a child of the
     * root doc type for the application (in the distribution, this is "KFS").
     * @see org.kuali.kfs.coa.service.AccountDelegateService#isFinancialSystemDocumentType(java.lang.String)
     */
    public boolean isFinancialSystemDocumentType(String documentTypeCode) {
        return isActiveCurrentChildDocumentType(documentTypeCode, KFSConstants.ROOT_DOCUMENT_TYPE, new WorkflowInfo());
    }
    
    /**
     * Checks if the given document type code is either the active, current version of the parent document type code, or an active, current child of the
     * given parent document type code
     * @param documentTypeCode the document type to check
     * @param parentDocumentTypeCode the parent document type code that the documentTypeCode should either represent or be an active, current child of
     * @param workflowInfo a workflowInfo object to help us
     * @return true if the doc type is a child or represents the parent document type, false otherwise
     */
    protected boolean isActiveCurrentChildDocumentType(String documentTypeCode, String parentDocumentTypeCode, WorkflowInfo workflowInfo) {
        if (StringUtils.isBlank(documentTypeCode)) return false;
        if (documentTypeCode.equals(parentDocumentTypeCode)) return true;
        try {
            if (!workflowInfo.isCurrentActiveDocumentType(documentTypeCode)) return false;

            final DocumentTypeDTO documentType = workflowInfo.getDocType(documentTypeCode);
            if (StringUtils.isBlank(documentType.getDocTypeParentName())) return false;
            return isActiveCurrentChildDocumentType(documentType.getDocTypeParentName(), parentDocumentTypeCode, workflowInfo);
        } catch (WorkflowException we) {
            throw new RuntimeException("Could not retrieve document type "+documentTypeCode, we);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#isAccountingDocumentType(java.lang.String)
     */
    public boolean isCurrentActiveAccountingDocumentType(String documentTypeCode) {
        if (StringUtils.isBlank(documentTypeCode)) return false; // no document type code...so it's not a current, active accounting document
        
        return isLedgerPostingDocumentType(documentTypeCode) || isFinancialSystemLedgerOnlyDocumentType(documentTypeCode);
    }
    
    /**
     * Determines if the given document type code represents a document whose class is a LedgerPostingDocument or a maintainable
     * whose class is a LedgerPostingMaintainable
     * @param documentTypeCode the document type code to check
     * @return true if the document type code represents a LedgerPostingDocument, false otherwise
     */
    protected boolean isLedgerPostingDocumentType(String documentTypeCode) {
        final Class<? extends Document> documentClass = getDataDictionaryService().getDocumentClassByTypeName(documentTypeCode);
        if (documentClass != null) {
            if (MaintenanceDocument.class.isAssignableFrom(documentClass)) {
                Class<? extends LedgerPostingMaintainable> maintainableClass = maintenanceDocumentDictionaryService.getMaintainableClass(documentTypeCode);
                if (maintainableClass != null) {
                    return LedgerPostingMaintainable.class.isAssignableFrom(maintainableClass);
                }
            }
            else {
                return LedgerPostingDocument.class.isAssignableFrom(documentClass);
            }
        }

        return false;
    }
    
    /**
     * Determines if the given document type code is a child of the Financial System Ledger Only document type
     * @param documentTypeCode the document type code to check
     * @return true if the documentTypeCode is a current active child of the Financial System Ledger Only document type, false otherwise
     */
    protected boolean isFinancialSystemLedgerOnlyDocumentType(String documentTypeCode) {
        return isActiveCurrentChildDocumentType(documentTypeCode, KFSConstants.FINANCIAL_SYSTEM_LEDGER_ONLY_ROOT_DOCUMENT_TYPE, new WorkflowInfo());
    }

    /**
     * Gets the dataDictionaryService attribute. 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
    
    /**
     * Gets the maintenanceDocumentDictionaryService attribute. 
     * @return Returns the maintenanceDocumentDictionaryService.
     */
    protected MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        return maintenanceDocumentDictionaryService;
    }

    /**
     * Sets the maintenanceDocumentDictionaryService attribute value.
     * @param maintenanceDocumentDictionaryService The maintenanceDocumentDictionaryService to set.
     */
    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }
}

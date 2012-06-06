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
package org.kuali.kfs.sys.document.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.service.impl.StringHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.LedgerPostingDocument;
import org.kuali.kfs.sys.document.LedgerPostingMaintainable;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentTypeService;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Default implementation of the FinancialSystemDocumentTypeService
 */
public class FinancialSystemDocumentTypeServiceImpl implements FinancialSystemDocumentTypeService {
    private DataDictionaryService dataDictionaryService;
    private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    protected DocumentTypeService documentTypeService;

    /**
     * Makes sure the doc type represented by the code either is or is a child of the
     * root doc type for the application (in the distribution, this is "KFS").
     * @see org.kuali.kfs.coa.service.AccountDelegateService#isFinancialSystemDocumentType(java.lang.String)
     */
    public boolean isFinancialSystemDocumentType(String documentTypeCode) {
        final DocumentType documentType = documentTypeService.getDocumentTypeByName(documentTypeCode);
        final DocumentType rootDocumentType = documentTypeService.getDocumentTypeByName(KFSConstants.ROOT_DOCUMENT_TYPE);
        if(ObjectUtils.isNull(documentType)) {
            return false;
        }
        return isActiveCurrentChildDocumentType(documentType.getId(), rootDocumentType.getId());
    }
    
    /**
     * Checks if the given document type code is either the active, current version of the parent document type code, or an active, current child of the
     * given parent document type code
     * @param documentTypeCode the document type to check
     * @param parentDocumentTypeCode the parent document type code that the documentTypeCode should either represent or be an active, current child of
     * @return true if the doc type is a child or represents the parent document type, false otherwise
     */
    protected boolean isActiveCurrentChildDocumentType(String documentTypeId, String parentDocumentTypeId) {
        if (StringHelper.isNullOrEmpty(documentTypeId)) return false;
        if (documentTypeId.equals(parentDocumentTypeId)) return true;

        if (!documentTypeService.isActiveById(documentTypeId)) return false;
        final DocumentType documentType = documentTypeService.getDocumentTypeById(documentTypeId);
        String parentId = documentType.getParentId();
        if (StringHelper.isNullOrEmpty(parentId)) return false;
        return isActiveCurrentChildDocumentType(parentId, parentDocumentTypeId);
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
        final DocumentType documentType = documentTypeService.getDocumentTypeByName(documentTypeCode);
        final DocumentType rootDocumentType = documentTypeService.getDocumentTypeByName(KFSConstants.FINANCIAL_SYSTEM_LEDGER_ONLY_ROOT_DOCUMENT_TYPE);
        
        if (ObjectUtils.isNull(documentType)) {
            return false;
        }
        return isActiveCurrentChildDocumentType(documentType.getId(), rootDocumentType.getId());
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

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }
    
    
}

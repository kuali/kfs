/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.document.service.TransactionArchiveService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class is the dao implementation for the TransactionArchiveServiceImpl.
 */
public class TransactionArchiveServiceImpl implements TransactionArchiveService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.TransactionArchiveService#getAllTransactionArchives()
     */
    public Collection<TransactionArchive> getAllTransactionArchives() {
        Collection<TransactionArchive> transactionArchives = new ArrayList();
        
        transactionArchives = businessObjectService.findAll(TransactionArchive.class);
        
        return transactionArchives;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.TransactionArchiveService#getByPrimaryKey(String, int, String)
     */
    public TransactionArchive getByPrimaryKey(String documentNumber, int lineNumber, String lineTypeCode) {
        TransactionArchive transactionArchive = null;
        
        Map primaryKeys = new HashMap();
        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_DOCUMENT_NUMBER, documentNumber);
        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_NUMBER, lineNumber);
        primaryKeys.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_LINE_TYPE_CODE, lineTypeCode);
        
        transactionArchive = (TransactionArchive) businessObjectService.findByPrimaryKey(TransactionArchive.class, primaryKeys);

        return transactionArchive;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.TransactionArchiveService#getTransactionArchivesByDocumentTypeName(String)
     */
    public Collection<TransactionArchive> getTransactionArchivesByDocumentTypeName(String typeCode) {
        Collection<TransactionArchive> transactionArchives = new ArrayList();
        
        if (StringUtils.isNotBlank(typeCode)) {        
            Map<String, String>  criteria = new HashMap<String, String>();
            
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(TransactionArchive.class, EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE)) {
                typeCode = typeCode.toUpperCase();
            }
            
            criteria.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, typeCode);
            transactionArchives = businessObjectService.findMatching(TransactionArchive.class, criteria);
        }
        
        return transactionArchives;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.TransactionArchiveService#getTransactionArchivesByETranCode(String)
     */
    public Collection<TransactionArchive> getTransactionArchivesByETranCode(String etranCode) {
        Collection<TransactionArchive> transactionArchives = new ArrayList();
        
        if (StringUtils.isNotBlank(etranCode)) {        
            Map<String, String>  criteria = new HashMap<String, String>();
            
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(TransactionArchive.class, EndowPropertyConstants.TRANSACTION_ARCHIVE_ETRAN_CODE)) {
                etranCode = etranCode.toUpperCase();
            }
            
            criteria.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_ETRAN_CODE, etranCode);
            transactionArchives = businessObjectService.findMatching(TransactionArchive.class, criteria);
        }
        
        return transactionArchives;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.TransactionArchiveService#getTransactionArchivesByDocumentTypeNameAndETranCode(String, String)
     */
    public Collection<TransactionArchive> getTransactionArchivesByDocumentTypeNameAndETranCode(String typeCode, String etranCode) {
        Collection<TransactionArchive> transactionArchives = new ArrayList();
        
        if (StringUtils.isNotBlank(etranCode) && StringUtils.isNotBlank(typeCode)) {        
            Map<String, String>  criteria = new HashMap<String, String>();
            
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(TransactionArchive.class, EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE)) {
                typeCode = typeCode.toUpperCase();
            }
            
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(TransactionArchive.class, EndowPropertyConstants.TRANSACTION_ARCHIVE_ETRAN_CODE)) {
                etranCode = etranCode.toUpperCase();
            }
            
            criteria.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_TYPE_CODE, typeCode);
            criteria.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_ETRAN_CODE, etranCode);

            transactionArchives = businessObjectService.findMatching(TransactionArchive.class, criteria);
        }
        
        return transactionArchives;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.TransactionArchiveService#getTransactionArchivesByIncomeOrPrincipalIndicator(String)
     */
    public Collection<TransactionArchive> getTransactionArchivesByIncomeOrPrincipalIndicator(String incomeOrPrincipalIndicator) {
        Collection<TransactionArchive> transactionArchives = new ArrayList();
        
        if (StringUtils.isNotBlank(incomeOrPrincipalIndicator)) {        
            Map<String, String>  criteria = new HashMap<String, String>();
            
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(TransactionArchive.class, EndowPropertyConstants.TRANSACTION_ARCHIVE_INCOME_PRINCIPAL_INDICATOR)) {
                incomeOrPrincipalIndicator = incomeOrPrincipalIndicator.toUpperCase();
            }
            
            criteria.put(EndowPropertyConstants.TRANSACTION_ARCHIVE_INCOME_PRINCIPAL_INDICATOR, incomeOrPrincipalIndicator);

            transactionArchives = businessObjectService.findMatching(TransactionArchive.class, criteria);
        }
        
        return transactionArchives;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.TransactionArchiveService#getAllTransactionArchives(String, String, String)
     */
    public Collection<TransactionArchive> getAllTransactionArchives(String typeCode, String etranCode) {
        Collection<TransactionArchive> transactionArchives = new ArrayList();
        
        transactionArchives = getTransactionArchivesByDocumentTypeNameAndETranCode(typeCode, etranCode);
        
        return transactionArchives;
    }
    
    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}

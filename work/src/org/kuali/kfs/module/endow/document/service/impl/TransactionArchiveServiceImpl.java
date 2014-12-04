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

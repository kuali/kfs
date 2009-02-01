/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentTypeCode;
import org.kuali.kfs.sys.service.FinancialSystemDocumentTypeCodeService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.UnknownDocumentTypeException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This is the implementation of {@link FinancialSystemDocumentTypeCodeService} that is used to retrieve financial information about certain
 * documents by utilizing the document type name
 */
public class FinancialSystemDocumentTypeCodeServiceImpl implements FinancialSystemDocumentTypeCodeService {

    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        if (ObjectUtils.isNull(businessObjectService)) {
            this.businessObjectService = KNSServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the dataDictionaryService attribute.
     * 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        if (ObjectUtils.isNull(dataDictionaryService)) {
            this.dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        }
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * @see org.kuali.kfs.sys.service.FinancialSystemDocumentTypeCodeService#getFinancialSystemDocumentTypeCodeByDocumentClass(java.lang.Class)
     */
    public FinancialSystemDocumentTypeCode getFinancialSystemDocumentTypeCodeByTransactionalDocumentClass(Class documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) documentClass");
        }
        if (!Document.class.isAssignableFrom(documentClass)) {
            throw new IllegalArgumentException("invalid (non-Document) documentClass");
        }

        String documentTypeName = getDataDictionaryService().getDocumentTypeNameByClass(documentClass);
        if (StringUtils.isBlank(documentTypeName)) {
            throw new UnknownDocumentTypeException("unable to get documentTypeName for unknown documentClass '" + documentClass.getName() + "'");
        }
        return getFinancialSystemDocumentTypeCodeByDocumentName(documentTypeName);
    }

    /**
     * @see org.kuali.kfs.sys.service.FinancialSystemDocumentTypeCodeService#getFinancialSystemDocumentTypeCodeByDocumentName(java.lang.String)
     */
    public FinancialSystemDocumentTypeCode getFinancialSystemDocumentTypeCodeByDocumentName(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        // call bo service to get FinancialSystemDocumentTypeCode using the 'documentName' criteria option
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("documentTypeName", documentTypeName);
        Collection financialSystemDocumentTypeCodes = getBusinessObjectService().findMatching(FinancialSystemDocumentTypeCode.class, fieldValues);
        if (financialSystemDocumentTypeCodes.size() == 0) {
            throw new RuntimeException("unable to get FinancialSystemDocumentTypeCode for documentTypeName '" + documentTypeName + "'");
        } else if (financialSystemDocumentTypeCodes.size() == 1) {
            return (FinancialSystemDocumentTypeCode) financialSystemDocumentTypeCodes.iterator().next();
        } else {
            throw new RuntimeException("Too many general ledger input types found for documentTypeName '" + documentTypeName + "'. Require only 1 but found " + financialSystemDocumentTypeCodes.size());
        }
    }

    /**
     * @see org.kuali.kfs.sys.service.FinancialSystemDocumentTypeCodeService#getFinancialSystemDocumentTypeCodeByInputTypeCode(java.lang.String)
     */
    public FinancialSystemDocumentTypeCode getFinancialSystemDocumentTypeCodeByPrimaryKey(String financialSystemDocumentTypeCodeCode) {
        if (StringUtils.isBlank(financialSystemDocumentTypeCodeCode)) {
            throw new IllegalArgumentException("invalid (blank) financialSystemDocumentTypeCodeCode");
        }
        // call bo service to get FinancialSystemDocumentTypeCode using the 'inputTypeCode' criteria option
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put("inputTypeCode", financialSystemDocumentTypeCodeCode);
        FinancialSystemDocumentTypeCode financialSystemDocumentTypeCode = (FinancialSystemDocumentTypeCode) getBusinessObjectService().findByPrimaryKey(FinancialSystemDocumentTypeCode.class, primaryKeys);
        if (ObjectUtils.isNull(financialSystemDocumentTypeCode)) {
            throw new RuntimeException("unable to get FinancialSystemDocumentTypeCode for financialSystemDocumentTypeCodeCode '" + financialSystemDocumentTypeCodeCode + "'");
        }
        return financialSystemDocumentTypeCode;
    }

}

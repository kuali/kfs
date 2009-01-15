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
import org.kuali.kfs.sys.businessobject.GeneralLedgerInputType;
import org.kuali.kfs.sys.exception.UnknownGeneralLedgerInputTypeException;
import org.kuali.kfs.sys.service.GeneralLedgerInputTypeService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.UnknownDocumentTypeException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This is the implementation of {@link GeneralLedgerInputTypeService} that is used to retrieve financial information about certain
 * documents by utilizing the document type name
 */
public class GeneralLedgerInputTypeServiceImpl implements GeneralLedgerInputTypeService {

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
     * @see org.kuali.kfs.sys.service.GeneralLedgerInputTypeService#getGeneralLedgerInputTypeByDocumentClass(java.lang.Class)
     */
    public GeneralLedgerInputType getGeneralLedgerInputTypeByDocumentClass(Class documentClass) {
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
        return getGeneralLedgerInputTypeByDocumentName(documentTypeName);
    }

    /**
     * @see org.kuali.kfs.sys.service.GeneralLedgerInputTypeService#getGeneralLedgerInputTypeByDocumentName(java.lang.String)
     */
    public GeneralLedgerInputType getGeneralLedgerInputTypeByDocumentName(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        // call bo service to get GeneralLedgerInputType using the 'documentName' criteria option
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("documentTypeName", documentTypeName);
        Collection generalLedgerInputTypes = getBusinessObjectService().findMatching(GeneralLedgerInputType.class, fieldValues);
        if (generalLedgerInputTypes.size() == 0) {
            throw new UnknownGeneralLedgerInputTypeException("unable to get GeneralLedgerInputType for documentTypeName '" + documentTypeName + "'");
        } else if (generalLedgerInputTypes.size() == 1) {
            return (GeneralLedgerInputType) generalLedgerInputTypes.iterator().next();
        } else {
            throw new RuntimeException("Too many general ledger input types found for documentTypeName '" + documentTypeName + "'. Require only 1 but found " + generalLedgerInputTypes.size());
        }
    }

    /**
     * @see org.kuali.kfs.sys.service.GeneralLedgerInputTypeService#getGeneralLedgerInputTypeByInputTypeCode(java.lang.String)
     */
    public GeneralLedgerInputType getGeneralLedgerInputTypeByInputTypeCode(String generalLedgerInputTypeCode) {
        if (StringUtils.isBlank(generalLedgerInputTypeCode)) {
            throw new IllegalArgumentException("invalid (blank) generalLedgerInputTypeCode");
        }
        // call bo service to get GeneralLedgerInputType using the 'inputTypeCode' criteria option
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put("inputTypeCode", generalLedgerInputTypeCode);
        GeneralLedgerInputType generalLedgerInputType = (GeneralLedgerInputType) getBusinessObjectService().findByPrimaryKey(GeneralLedgerInputType.class, primaryKeys);
        if (ObjectUtils.isNull(generalLedgerInputType)) {
            throw new UnknownGeneralLedgerInputTypeException("unable to get GeneralLedgerInputType for generalLedgerInputTypeCode '" + generalLedgerInputTypeCode + "'");
        }
        return generalLedgerInputType;
    }

}

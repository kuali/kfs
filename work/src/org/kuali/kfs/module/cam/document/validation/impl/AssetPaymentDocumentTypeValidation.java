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
package org.kuali.kfs.module.cam.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.service.impl.KEWModuleService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This class validates payment financial document type code
 */
public class AssetPaymentDocumentTypeValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;

    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Validates financial document type code
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) getAccountingLineForValidation();
        boolean result = true;
        String label;
        if (!StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode())) {
            Map<String, Object> keyToFind = new HashMap<String, Object>();
            keyToFind.put("name", assetPaymentDetail.getExpenditureFinancialDocumentTypeCode());

            final DocumentTypeEBO docType = SpringContext.getBean(KEWModuleService.class).getExternalizableBusinessObject(DocumentTypeEBO.class, keyToFind);
            if (docType == null) {
                label = dataDictionaryService.getAttributeLabel(AssetPaymentDetail.class, CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE);
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
                result = false;
            }
        } else { 
              // implemented because dd is not enforcing required field rule
            label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE).getLabel();
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, KFSKeyConstants.ERROR_REQUIRED,label);
            result = false;
        }
        return result;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}

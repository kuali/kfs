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

import org.kuali.RiceKeyConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.gl.businessobject.UniversityDate;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class validates Fiscal Period
 */
public class AssetPaymentFiscalPeriodValidation extends GenericValidation {

    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;

    private AccountingLine accountingLineForValidation;

    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Validates fiscal period given for the payment
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) getAccountingLineForValidation();
        Map<String, Object> keyToFind = new HashMap<String, Object>();
        keyToFind.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, assetPaymentDetail.getFinancialDocumentPostingYear());
        keyToFind.put(KFSPropertyConstants.UNIVERSITY_FISCAL_ACCOUNTING_PERIOD, assetPaymentDetail.getFinancialDocumentPostingPeriodCode());

        if (businessObjectService.countMatching(UniversityDate.class, keyToFind) == 0) {
            String labelFiscalYear = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR).getLabel();
            String labelFiscalMonth = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH).getLabel();

            //Since these two fields are now read-only I've commented out these two lines
            //GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR, KFSKeyConstants.ERROR_EXISTENCE, labelFiscalYear);
            //GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH, KFSKeyConstants.ERROR_EXISTENCE, labelFiscalMonth);
            
            String[] errorParameters = new String[1];
            errorParameters[0]=labelFiscalYear + " and "+labelFiscalMonth;
            GlobalVariables.getErrorMap().putError(KNSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_EXISTENCE, errorParameters);            
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

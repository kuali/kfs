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

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.gl.businessobject.UniversityDate;

/**
 * This class validates asset payment posting date
 */
public class AssetPaymentPostingDateValidation extends GenericValidation {

    private AccountingLine accountingLineForValidation;
    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private UniversityDateService universityDateService;


    /**
     * Validates asset payment posting date
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) getAccountingLineForValidation();
        Date expenditureFinancialDocumentPostedDate = assetPaymentDetail.getExpenditureFinancialDocumentPostedDate();
        if (expenditureFinancialDocumentPostedDate != null) {
            Calendar documentPostedDate = dateTimeService.getCalendar(expenditureFinancialDocumentPostedDate);

            Map<String, Object> keyToFind = new HashMap<String, Object>();
            keyToFind.put(KFSPropertyConstants.UNIVERSITY_DATE, expenditureFinancialDocumentPostedDate);

            if (businessObjectService.findByPrimaryKey(UniversityDate.class, keyToFind) == null) {
                String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE, KFSKeyConstants.ERROR_EXISTENCE, label);
                result = false;
            }

            // Validating the posted document date is not greater than the current fiscal year.
            Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();
            if (documentPostedDate.get(Calendar.YEAR) > currentFiscalYear.intValue()) {
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE, CamsKeyConstants.Payment.ERROR_INVALID_DOC_POST_DATE);
                result = false;
            }
        }
        return result;
    }

    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    public void setAccountingLineForValidation(AccountingLine accountingLine) {
        this.accountingLineForValidation = accountingLine;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
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

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }


}

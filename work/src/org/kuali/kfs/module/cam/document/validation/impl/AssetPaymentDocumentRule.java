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

import static org.kuali.kfs.sys.KFSConstants.AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_ZERO_AMOUNT;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.document.Document;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.gl.businessobject.UniversityDate;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase;
import org.kuali.kfs.sys.service.OriginationCodeService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * Business rule(s) applicable to Asset Payment.
 */
public class AssetPaymentDocumentRule extends AccountingDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentDocumentRule.class);

    private static AssetService assetService = SpringContext.getBean(AssetService.class);
    private static BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    private static DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) document;

        if (assetService.isAssetLocked(assetPaymentDocument.getDocumentNumber(), assetPaymentDocument.getCapitalAssetNumber())) {
            return false;
        }

        return true;
    }

    /**
     * This method was overrided because the asset payment only uses source and not target lines. Not gl pending entries are needed.
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) document;

        if (assetService.isAssetLocked(assetPaymentDocument.getDocumentNumber(), assetPaymentDocument.getCapitalAssetNumber())) {
            return false;
        }

        // Validating the asset payment document has at least one accounting line.
        return this.isSourceAccountingLinesRequiredNumberForRoutingMet(assetPaymentDocument);
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean result = true;
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) accountingLine;

        // Validating the fiscal year and month because, the object code validation needs to have this a valid fiscal year
        result &= this.validateFiscalPeriod(assetPaymentDetail.getFinancialDocumentPostingYear(), assetPaymentDetail.getFinancialDocumentPostingPeriodCode());

        // Validating document type code
        result &= this.validateDocumentType(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode());

        // Validating origination code
        result &= validateOriginationCode(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode());

        // Validating posting date which must exists in the university date table
        result &= this.validatePostedDate(assetPaymentDetail.getExpenditureFinancialDocumentPostedDate());

        return result;
    }

    /**
     * 
     * Validates the document type really exists
     * 
     * @param expenditureFinancialDocumentTypeCode
     * @return boolean
     */
    public boolean validateDocumentType(String expenditureFinancialDocumentTypeCode) {
        boolean result = true;
        String label;
        if (!StringUtils.isBlank(expenditureFinancialDocumentTypeCode)) {
            Map<String, Object> keyToFind = new HashMap<String, Object>();
            keyToFind.put(KNSPropertyConstants.DOCUMENT_TYPE_CODE, expenditureFinancialDocumentTypeCode);

            if (SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DocumentType.class, keyToFind) == null) {
                label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(DocumentType.class.getName()).getAttributeDefinition(KNSPropertyConstants.DOCUMENT_TYPE_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE, KFSKeyConstants.ERROR_EXISTENCE, label);
                result = false;
            }
        }
        return result;
    }


    /**
     * 
     * Validates that origination code exists
     * 
     * @param originationCode
     * @return boolean
     */
    private boolean validateOriginationCode(String originationCode) {
        boolean result = true;
        if (!StringUtils.isBlank(originationCode)) {
            if (SpringContext.getBean(OriginationCodeService.class).getByPrimaryKey(originationCode) == null) {
                String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(OriginationCode.class.getName()).getAttributeDefinition(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
                result = false;
            }
        }
        return result;
    }

    /**
     * 
     * Validates the existance of a valid fiscal year and fiscal month in the university date table
     * 
     * @param fiscalYear
     * @param fiscalMonth
     * @return boolean
     */
    public boolean validateFiscalPeriod(Integer fiscalYear, String fiscalMonth) {
        boolean result = true;

        Map<String, Object> keyToFind = new HashMap<String, Object>();
        keyToFind.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        keyToFind.put(KFSPropertyConstants.UNIVERSITY_FISCAL_ACCOUNTING_PERIOD, fiscalMonth);
        
        if (SpringContext.getBean(BusinessObjectService.class).countMatching(UniversityDate.class, keyToFind) == 0) {
            String labelFiscalYear = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR).getLabel();
            String labelFiscalMonth = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH).getLabel();

            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR, KFSKeyConstants.ERROR_EXISTENCE, labelFiscalYear);
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH, KFSKeyConstants.ERROR_EXISTENCE, labelFiscalMonth);
            result = false;
        }
        return result;
    }


    /**
     * 
     * This method validates the given posted date exists in the university date table and that the posted date is not greater than
     * the current fiscal year
     * 
     * @param postedDate
     * @return boolean
     */
    public boolean validatePostedDate(Date postedDate) {
        Calendar documentPostedDate = SpringContext.getBean(DateTimeService.class).getCalendar(postedDate);
        
        boolean result = true;
        Map<String, Object> keyToFind = new HashMap<String, Object>();
        keyToFind.put(KFSPropertyConstants.UNIVERSITY_DATE, postedDate);

        if (businessObjectService.findByPrimaryKey(UniversityDate.class, keyToFind) == null) {
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE).getLabel();
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE, KFSKeyConstants.ERROR_EXISTENCE, label);
            result = false;
        }

        // Validating the posted document date is not greater than the current fiscal year.
        Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        if (documentPostedDate.get(Calendar.YEAR) > currentFiscalYear.intValue()) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE, CamsKeyConstants.Payment.ERROR_INVALID_DOC_POST_DATE);
            result = false;
        }
        
        
        
        
        return result;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase#isAmountValid(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        KualiDecimal amount = accountingLine.getAmount();

        // Check for zero amounts
        if (amount.isZero()) { 
            GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ERROR_ZERO_AMOUNT, "an accounting line");
            return false;
        }
        return true;
    }
}

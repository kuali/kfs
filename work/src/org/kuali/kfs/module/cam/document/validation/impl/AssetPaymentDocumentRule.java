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
package org.kuali.module.cams.rules;

import static org.kuali.kfs.KFSConstants.AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ZERO_AMOUNT;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.RicePropertyConstants;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.document.Document;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.OriginationCodeService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.document.AssetPaymentDocument;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.UniversityDate;

/**
 * Business rule(s) applicable to Asset Payment.
 */
public class AssetPaymentDocumentRule extends AccountingDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentDocumentRule.class);

    private static AssetService assetService;

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) document;

        if (getAssetService().isAssetLocked(assetPaymentDocument.getDocumentNumber(), assetPaymentDocument.getCapitalAssetNumber())) {
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

        if (getAssetService().isAssetLocked(assetPaymentDocument.getDocumentNumber(), assetPaymentDocument.getCapitalAssetNumber())) {
            return false;
        }

        // Validating the asset payment document has at least one accounting line.
        return this.isSourceAccountingLinesRequiredNumberForRoutingMet(assetPaymentDocument);
    }

    /**
     * 
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
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
            keyToFind.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, expenditureFinancialDocumentTypeCode);

            if (SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DocumentType.class, keyToFind) == null) {
                label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(DocumentType.class.getName()).getAttributeDefinition(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE).getLabel();
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
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(OriginationCode.class.getName()).getAttributeDefinition(RicePropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE).getLabel();
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
            String labelFiscalYear = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR).getLabel();
            String labelFiscalMonth = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH).getLabel();

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
        
        if (SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(UniversityDate.class, keyToFind) == null) {
            String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE).getLabel();
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
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#isAmountValid(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
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


    
    /**
     * 
     * This method returns the assetService bean
     * 
     * @return AssetService
     */
    public AssetService getAssetService() {
        if (assetService == null) {
            assetService = SpringContext.getBean(AssetService.class);
        }
        return assetService;
    }
}
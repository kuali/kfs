/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.web.struts;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.businessobject.LedgerEntryGLSummary;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.struts.form.LookupForm;

/**
 * The form which holds values used in the GLLaborEntry Summarization Inquiry screen
 */
public class GLLaborEntrySummarizationInquiryForm extends LookupForm {
    private Integer universityFiscalYear;
    private String universityFiscalYearInquiryUrl;
    private String universityFiscalPeriodCode;
    private String universityFiscalPeriodCodeInquiryUrl;
    private String chartOfAccountsCode;
    private String chartOfAccountsCodeInquiryUrl;
    private String accountNumber;
    private String accountNumberInquiryUrl;
    private String subAccountNumber;
    private String subAccountNumberInquiryUrl;
    private String financialObjectCode;
    private String financialObjectCodeInquiryUrl;
    private String financialSubObjectCode;
    private String financialSubObjectCodeInquiryUrl;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private String financialDocumentTypeCode;
    private String financialSystemOriginationCode;
    private String documentNumber;

    @SuppressWarnings("rawtypes")
    private Collection entries;

    protected static volatile String pageTitle;

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getSubAccountNumber() {
        return subAccountNumber;
    }
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }
    public String getDocumentNumber() {
        return documentNumber;
    }
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @SuppressWarnings("rawtypes")
    public Collection getEntries() {
        return entries;
    }
    @SuppressWarnings("rawtypes")
    public void setEntries(Collection entries) {
        this.entries = entries;
    }

    public String getPageTitle() {
        if (pageTitle == null) {
            final BusinessObjectEntry entry = (BusinessObjectEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(LedgerEntryGLSummary.class.getSimpleName());
            pageTitle = entry.getObjectLabel();
        }
        return pageTitle;
    }

    /**
     * @return the inquiry url for the fiscal year property
     */
    public String getUniversityFiscalYearInquiryUrl() {
        return universityFiscalYearInquiryUrl;
    }
    /**
     * @return the inquiry url for the fiscal period code property
     */
    public String getUniversityFiscalPeriodCodeInquiryUrl() {
        return universityFiscalPeriodCodeInquiryUrl;
    }
    /**
     * @return the inquiry url for the chart of accounts code property
     */
    public String getChartOfAccountsCodeInquiryUrl() {
        return chartOfAccountsCodeInquiryUrl;
    }
    /**
     * @return the inquiry url for the account number property
     */
    public String getAccountNumberInquiryUrl() {
        return accountNumberInquiryUrl;
    }
    /**
     * @return the inquiry url for the sub-account number property
     */
    public String getSubAccountNumberInquiryUrl() {
        return subAccountNumberInquiryUrl;
    }
    /**
     * @return the inquiry url for the object code property
     */
    public String getFinancialObjectCodeInquiryUrl() {
        return financialObjectCodeInquiryUrl;
    }
    /**
     * @return the inquiry url for the sub-object code property
     */
    public String getFinancialSubObjectCodeInquiryUrl() {
        return financialSubObjectCodeInquiryUrl;
    }
    /**
     * @return a Map of the field values for each searching
     */
    @Override
    public Map<String, String> getFieldsForLookup() {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, (getUniversityFiscalYear() != null ? getUniversityFiscalYear().toString() : ""));
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, getUniversityFiscalPeriodCode());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, getAccountNumber());
        fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, getSubAccountNumber());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, getFinancialObjectCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, getFinancialSubObjectCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, getFinancialBalanceTypeCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, getFinancialObjectTypeCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, getFinancialDocumentTypeCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, getFinancialSystemOriginationCode());
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        return fieldValues;
    }

    /**
     * Builds the inquiry urls for the set fields - fiscal year, fiscal period, chart, account, sub-account, object, and sub-object
     * @param lookupableHelperService the lookupable helper service which builds the related inquiry urls for us
     */
    protected void buildInquiryUrls(LookupableHelperService lookupableHelperService) {
        final LedgerEntryGLSummary summary = buildBasicLedgerEntrySummary();
        universityFiscalYearInquiryUrl = retrieveInquiryUrlForProperty(summary, lookupableHelperService, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        universityFiscalPeriodCodeInquiryUrl = retrieveInquiryUrlForProperty(summary, lookupableHelperService, KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        chartOfAccountsCodeInquiryUrl = retrieveInquiryUrlForProperty(summary, lookupableHelperService, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        accountNumberInquiryUrl = retrieveInquiryUrlForProperty(summary, lookupableHelperService, KFSPropertyConstants.ACCOUNT_NUMBER);
        subAccountNumberInquiryUrl = retrieveInquiryUrlForProperty(summary, lookupableHelperService, KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        financialObjectCodeInquiryUrl = retrieveInquiryUrlForProperty(summary, lookupableHelperService, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        financialSubObjectCodeInquiryUrl = retrieveInquiryUrlForProperty(summary, lookupableHelperService, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
    }

    /**
     * @return a LedgerEntryGLSummary record filled with the known values from the form
     */
    protected LedgerEntryGLSummary buildBasicLedgerEntrySummary() {
        LedgerEntryGLSummary summary = new LedgerEntryGLSummary();
        summary.setUniversityFiscalYear(getUniversityFiscalYear());
        summary.setUniversityFiscalPeriodCode(getUniversityFiscalPeriodCode());
        summary.setChartOfAccountsCode(getChartOfAccountsCode());
        summary.setAccountNumber(getAccountNumber());
        summary.setSubAccountNumber(getSubAccountNumber());
        summary.setFinancialObjectCode(getFinancialObjectCode());
        summary.setFinancialSubObjectCode(getFinancialSubObjectCode());
        return summary;
    }

    /**
     * Determines the inquiry url for the given property
     * @param summary the LedgerEntryGLSummary populated with values to build inquiry links for
     * @param lookupableHelperService the lookupable helper service to help us determine the inquiry url
     * @param propertyName the property to build the inquiry url for
     * @return the inquiry url, or null if none was built
     */
    protected String retrieveInquiryUrlForProperty(LedgerEntryGLSummary summary, LookupableHelperService lookupableHelperService, String propertyName) {
        final AnchorHtmlData inquiryHtmlData = (AnchorHtmlData)lookupableHelperService.getInquiryUrl(summary, propertyName);
        final String inquiryUrl = (inquiryHtmlData != null && !StringUtils.isBlank(inquiryHtmlData.getHref())) ? inquiryHtmlData.getHref() : null;
        return inquiryUrl;
    }

    public boolean isCanExport() {
        return false;
    }
}

/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.web.struts;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * This class is the action form for balance inquiries.
 */
public class KualiBalanceInquiryReportMenuForm extends KualiForm {
    private static final long serialVersionUID = 1L;

    // parameter fields
    private String referenceOriginCode;
    private String referenceNumber;
    private String referenceTypeCode;
    private String debitCreditCode;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private String subAccountNumber;
    private String financialSubObjectCode;
    private String projectCode;
    private String objectTypeCode;
    private String universityFiscalYear;

    private String docFormKey;
    // need this next attribute b/c the lookup overwrites the docFormKey
    // improperly when it cancels back to the report menu (this)
    // this holds the official docFormKey for the calling TP eDoc
    private String balanceInquiryReportMenuCallerDocFormKey;
    private String backLocation;

    /**
     * @return String
     */
    public String getBackLocation() {
        return backLocation;
    }

    /**
     * @param backLocation
     */
    public void setBackLocation(String backLocation) {
        this.backLocation = backLocation;
    }

    /**
     * @return String
     */
    public String getDocFormKey() {
        return docFormKey;
    }

    /**
     * @param docFormKey
     */
    public void setDocFormKey(String docFormKey) {
        this.docFormKey = docFormKey;
    }

    /**
     * Builds out the lookupParameters for the GLPE balance inquiry.
     * 
     * @return String
     */
    public String getGeneralLedgerPendingEntryBalanceInquiryLookupParameters() {
        return buildGenericBalanceInquiryLookupParameters();
    }

    /**
     * This method builds out the common balance inquiry lookup parameters based upon what was entered in the calling accounting
     * line.
     * 
     * @return String
     */
    private String buildGenericBalanceInquiryLookupParameters() {
        String lookupParameters = "";

        if (StringUtils.isNotBlank(chartOfAccountsCode)) {
            lookupParameters += "chartOfAccountsCode:chartOfAccountsCode";
        }
        if (StringUtils.isNotBlank(accountNumber)) {
            lookupParameters += ",accountNumber:accountNumber";
        }
        if (StringUtils.isNotBlank(subAccountNumber)) {
            lookupParameters += ",subAccountNumber:subAccountNumber";
        }
        if (StringUtils.isNotBlank(financialObjectCode)) {
            lookupParameters += ",financialObjectCode:financialObjectCode";
        }
        if (StringUtils.isNotBlank(financialSubObjectCode)) {
            lookupParameters += ",financialSubObjectCode:financialSubObjectCode";
        }
        if (StringUtils.isNotBlank(objectTypeCode)) {
            lookupParameters += ",objectTypeCode:financialObjectTypeCode";
        }
        if (StringUtils.isNotBlank(debitCreditCode)) {
            lookupParameters += ",debitCreditCode:transactionDebitCreditCode";
        }
        if (StringUtils.isNotBlank(referenceOriginCode)) {
            lookupParameters += ",referenceOriginCode:referenceFinancialSystemOriginationCode";
        }
        if (StringUtils.isNotBlank(referenceTypeCode)) {
            lookupParameters += ",referenceTypeCode:referenceFinancialDocumentTypeCode";
        }
        if (StringUtils.isNotBlank(referenceNumber)) {
            lookupParameters += ",referenceNumber:referenceFinancialDocumentNumber";
        }
        if (StringUtils.isNotBlank(projectCode)) {
            lookupParameters += ",projectCode:projectCode";
        }
        if (StringUtils.isNotBlank(universityFiscalYear)) {
            lookupParameters += ",universityFiscalYear:universityFiscalYear";
        }

        return lookupParameters;
    }

    /**
     * @return String
     */
    public String getGeneralLedgerBalanceBalanceInquiryLookupParameters() {
        return buildGenericBalanceInquiryLookupParameters();
    }

    /**
     * @return String
     */
    public String getGeneralLedgerEntryBalanceInquiryLookupParameters() {
        return buildGenericBalanceInquiryLookupParameters();
    }

    /**
     * @return String
     */
    public String getBalancesByConsolidationBalanceInquiryLookupParameters() {
        return buildGenericBalanceInquiryLookupParameters();
    }

    /**
     * @return String
     */
    public String getAvailableBalancesBalanceInquiryLookupParameters() {
        return buildGenericBalanceInquiryLookupParameters();
    }

    /**
     * @return String
     */
    public String getOpenEncumbrancesBalanceInquiryLookupParameters() {
        return buildGenericBalanceInquiryLookupParameters();
    }

    /**
     * @return String
     */
    public String getCashBalancesBalanceInquiryLookupParameters() {
        return buildGenericBalanceInquiryLookupParameters();
    }

    /**
     * @return String
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * @return String
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * @param chartOfAccountsCode
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @return String
     */
    public String getDebitCreditCode() {
        return debitCreditCode;
    }

    /**
     * @param debitCreditCode
     */
    public void setDebitCreditCode(String debitCreditCode) {
        this.debitCreditCode = debitCreditCode;
    }

    /**
     * @return String
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * @param financialObjectCode
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * @return String
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * @param financialSubObjectCode
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * @return String
     */
    public String getObjectTypeCode() {
        return objectTypeCode;
    }

    /**
     * @param objectTypeCode
     */
    public void setObjectTypeCode(String objectTypeCode) {
        this.objectTypeCode = objectTypeCode;
    }

    /**
     * @return String
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * @param projectCode
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * @return String
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * @param referenceNumber
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * @return String
     */
    public String getReferenceOriginCode() {
        return referenceOriginCode;
    }

    /**
     * @param referenceOriginCode
     */
    public void setReferenceOriginCode(String referenceOriginCode) {
        this.referenceOriginCode = referenceOriginCode;
    }

    /**
     * @return String
     */
    public String getReferenceTypeCode() {
        return referenceTypeCode;
    }

    /**
     * @param referenceTypeCode
     */
    public void setReferenceTypeCode(String referenceTypeCode) {
        this.referenceTypeCode = referenceTypeCode;
    }

    /**
     * @return String
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * @param subAccountNumber
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * @return String
     */
    public String getBalanceInquiryReportMenuCallerDocFormKey() {
        return balanceInquiryReportMenuCallerDocFormKey;
    }

    /**
     * @param balanceInquiryReportMenuCallerDocFormKey
     */
    public void setBalanceInquiryReportMenuCallerDocFormKey(String balanceInquiryReportMenuCallerDocFormKey) {
        this.balanceInquiryReportMenuCallerDocFormKey = balanceInquiryReportMenuCallerDocFormKey;
    }

    /**
     * Gets the universityFiscalYear attribute. 
     * @return Returns the universityFiscalYear.
     */
    public String getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(String universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }
}

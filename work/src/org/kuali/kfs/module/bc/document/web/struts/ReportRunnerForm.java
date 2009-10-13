/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.bc.BudgetConstructionDocumentReportMode;

/**
 * Form class to display document reports and dumps menu
 */
public class ReportRunnerForm extends BudgetExpansionForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportRunnerForm.class);

    // url parameters sent from BCDoc
    private String documentNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;

    private List<BudgetConstructionDocumentReportMode> budgetConstructionDocumentReportModes;

    /**
     * Constructs a ReportRunnerForm.java.
     */
    public ReportRunnerForm() {
        super();
        this.setBudgetConstructionDocumentReportModes(new ArrayList());
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {

        super.populate(request);
        if (this.getBudgetConstructionDocumentReportModes().isEmpty()) {
            this.getBudgetConstructionDocumentReportModes().add(BudgetConstructionDocumentReportMode.DOCUMENT_OBJECT_DETAIL_REPORT);
            this.getBudgetConstructionDocumentReportModes().add(BudgetConstructionDocumentReportMode.DOCUMENT_FUNDING_DETAIL_REPORT);
            this.getBudgetConstructionDocumentReportModes().add(BudgetConstructionDocumentReportMode.DOCUMENT_MONTHLY_DETAIL_REPORT);
            this.getBudgetConstructionDocumentReportModes().add(BudgetConstructionDocumentReportMode.DOCUMENT_ACCOUNT_DUMP);
            this.getBudgetConstructionDocumentReportModes().add(BudgetConstructionDocumentReportMode.DOCUMENT_FUNDING_DUMP);
            this.getBudgetConstructionDocumentReportModes().add(BudgetConstructionDocumentReportMode.DOCUMENT_MONTHLY_DUMP);
        }
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the budgetConstructionDocumentReportModes attribute.
     * 
     * @return Returns the budgetConstructionDocumentReportModes.
     */
    public List<BudgetConstructionDocumentReportMode> getBudgetConstructionDocumentReportModes() {
        return budgetConstructionDocumentReportModes;
    }

    /**
     * Sets the budgetConstructionDocumentReportModes attribute value.
     * 
     * @param budgetConstructionDocumentReportModes The budgetConstructionDocumentReportModes to set.
     */
    public void setBudgetConstructionDocumentReportModes(List<BudgetConstructionDocumentReportMode> budgetConstructionDocumentReportModes) {
        this.budgetConstructionDocumentReportModes = budgetConstructionDocumentReportModes;
    }

}

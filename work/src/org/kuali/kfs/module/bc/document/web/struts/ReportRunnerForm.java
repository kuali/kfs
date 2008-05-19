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
package org.kuali.module.budget.web.struts.form;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.module.budget.BudgetConstructionDocumentReportMode;

/**
 * Form class to display document reports and dumps menu
 */
public class ReportRunnerForm extends KualiForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportRunnerForm.class);
    
    // url parameters sent from BCDoc
    private String returnAnchor;
    private String returnFormKey;
    private String documentNumber;
    private Integer universityFiscalYear;
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
     * @see org.kuali.core.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        // TODO Auto-generated method stub
        super.populate(request);
        if (this.getBudgetConstructionDocumentReportModes().isEmpty()){
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
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute. 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the returnAnchor attribute. 
     * @return Returns the returnAnchor.
     */
    public String getReturnAnchor() {
        return returnAnchor;
    }

    /**
     * Sets the returnAnchor attribute value.
     * @param returnAnchor The returnAnchor to set.
     */
    public void setReturnAnchor(String returnAnchor) {
        this.returnAnchor = returnAnchor;
    }

    /**
     * Gets the returnFormKey attribute. 
     * @return Returns the returnFormKey.
     */
    public String getReturnFormKey() {
        return returnFormKey;
    }

    /**
     * Sets the returnFormKey attribute value.
     * @param returnFormKey The returnFormKey to set.
     */
    public void setReturnFormKey(String returnFormKey) {
        this.returnFormKey = returnFormKey;
    }

    /**
     * Gets the subAccountNumber attribute. 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the universityFiscalYear attribute. 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the budgetConstructionDocumentReportModes attribute. 
     * @return Returns the budgetConstructionDocumentReportModes.
     */
    public List<BudgetConstructionDocumentReportMode> getBudgetConstructionDocumentReportModes() {
        return budgetConstructionDocumentReportModes;
    }

    /**
     * Sets the budgetConstructionDocumentReportModes attribute value.
     * @param budgetConstructionDocumentReportModes The budgetConstructionDocumentReportModes to set.
     */
    public void setBudgetConstructionDocumentReportModes(List<BudgetConstructionDocumentReportMode> budgetConstructionDocumentReportModes) {
        this.budgetConstructionDocumentReportModes = budgetConstructionDocumentReportModes;
    }

}

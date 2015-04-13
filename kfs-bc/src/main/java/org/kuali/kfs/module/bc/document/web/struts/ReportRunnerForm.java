/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

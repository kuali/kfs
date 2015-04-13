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

import org.kuali.kfs.module.bc.BCConstants;

/**
 * Struts action formm for report dumps.
 */
public class ReportExportForm extends BudgetConstructionImportExportForm {
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private boolean orgReport;
    
    /**
     * Constructs a ReportExportForm.java.
     */
    public ReportExportForm() {
        super();
    }
    
    public String getHtmlFormAction() {
        return BCConstants.REPORT_EXPORT_PATH;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public boolean isOrgReport() {
        return orgReport;
    }

    public void setOrgReport(boolean orgReport) {
        this.orgReport = orgReport;
    }

}

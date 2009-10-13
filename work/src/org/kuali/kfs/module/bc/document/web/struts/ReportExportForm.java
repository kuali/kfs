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

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
package org.kuali.kfs.module.bc;

public enum BudgetConstructionDocumentReportMode {
    DOCUMENT_OBJECT_DETAIL_REPORT("DocumentObjectDetailReport", "Account Object Detail Report", "DocumentAccountObjectDetail"),
    DOCUMENT_FUNDING_DETAIL_REPORT("DocumentFundingDetailReport", "Account Salary Detail Report", "DocumentAccountFundingDetail"),
    DOCUMENT_MONTHLY_DETAIL_REPORT("DocumentMonthlyDetailReport", "Account Monthly Detail Report", "DocumentAccountMonthlyDetail"),
    DOCUMENT_ACCOUNT_DUMP("DocumentAccountDump", "Budgeted Revenue/Expenditure Export", true),
    DOCUMENT_FUNDING_DUMP("DocumentFundingDump", "Budgeted Salary Lines Export", true),
    DOCUMENT_MONTHLY_DUMP("DocumentMonthlyDump", "Monthly Budget Export", true); 

    public final String reportModeName;
    public final String reportDesc;
    public final String jasperFileName;
    public final boolean dump;

    private BudgetConstructionDocumentReportMode(String reportModeName, String reportDesc, String jasperFileName){
        this.reportModeName = reportModeName;
        this.reportDesc = reportDesc; 
        this.jasperFileName = jasperFileName;
        this.dump = false;
    }

    private BudgetConstructionDocumentReportMode(String reportModeName, String reportDesc,  boolean dump){
        this.reportModeName = reportModeName;
        this.reportDesc = reportDesc; 
        this.jasperFileName = "";
        this.dump = dump;
    }

    /**
     * Gets the reportDesc attribute. 
     * @return Returns the reportDesc.
     */
    public String getReportDesc() {
        return reportDesc;
    }

    /**
     * Gets the dump attribute. 
     * @return Returns the dump.
     */
    public boolean isDump() {
        return dump;
    }

    /**
     * Gets the jasperFileName attribute. 
     * @return Returns the jasperFileName.
     */
    public String getJasperFileName() {
        return jasperFileName;
    }

    /**
     * Gets the reportModeName attribute. 
     * @return Returns the reportModeName.
     */
    public String getReportModeName() {
        return reportModeName;
    }
}

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

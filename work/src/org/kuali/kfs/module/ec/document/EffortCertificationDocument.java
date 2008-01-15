/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.effort.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;

/**
 * Effort Certification Document Class.
 */
public class EffortCertificationDocument extends TransactionalDocumentBase {
    private String documentNumber;
    private String chartOfAccountsCode;
    private String organizationCode;
    private String effortCertificationReportNumber;
    private String effortCertificationDocumentCode;
    private Integer universityFiscalYear;
    private String emplid;

    private Chart chartOfAccounts;
    private EffortCertificationReportDefinition effortCertificationReportDefinition;
    private Org organization;
    private UniversalUser employee;
    private Options options;

    private List<EffortCertificationDetail> effortCertificationDetailLines;
    
    /**
     * Default constructor.
     */
    public EffortCertificationDocument() {
        effortCertificationDetailLines = new ArrayList<EffortCertificationDetail>(); 
        
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
     * Gets the organizationCode attribute. 
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute value.
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the effortCertificationReportNumber attribute. 
     * @return Returns the effortCertificationReportNumber.
     */
    public String getEffortCertificationReportNumber() {
        return effortCertificationReportNumber;
    }

    /**
     * Sets the effortCertificationReportNumber attribute value.
     * @param effortCertificationReportNumber The effortCertificationReportNumber to set.
     */
    public void setEffortCertificationReportNumber(String effortCertificationReportNumber) {
        this.effortCertificationReportNumber = effortCertificationReportNumber;
    }

    /**
     * Gets the effortCertificationDocumentCode attribute. 
     * @return Returns the effortCertificationDocumentCode.
     */
    public String getEffortCertificationDocumentCode() {
        return effortCertificationDocumentCode;
    }

    /**
     * Sets the effortCertificationDocumentCode attribute value.
     * @param effortCertificationDocumentCode The effortCertificationDocumentCode to set.
     */
    public void setEffortCertificationDocumentCode(String effortCertificationDocumentCode) {
        this.effortCertificationDocumentCode = effortCertificationDocumentCode;
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
     * Gets the emplid attribute. 
     * @return Returns the emplid.
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute value.
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the chartOfAccounts attribute. 
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute value.
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the effortCertificationReportDefinition attribute. 
     * @return Returns the effortCertificationReportDefinition.
     */
    public EffortCertificationReportDefinition getEffortCertificationReportDefinition() {
        return effortCertificationReportDefinition;
    }

    /**
     * Sets the effortCertificationReportDefinition attribute value.
     * @param effortCertificationReportDefinition The effortCertificationReportDefinition to set.
     */
    @Deprecated
    public void setEffortCertificationReportDefinition(EffortCertificationReportDefinition effortCertificationReportDefinition) {
        this.effortCertificationReportDefinition = effortCertificationReportDefinition;
    }

    /**
     * Gets the organization attribute. 
     * @return Returns the organization.
     */
    public Org getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute value.
     * @param organization The organization to set.
     */
    @Deprecated
    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    /**
     * Gets the employee attribute. 
     * @return Returns the employee.
     */
    public UniversalUser getEmployee() {
        return employee;
    }

    /**
     * Sets the employee attribute value.
     * @param employee The employee to set.
     */
    public void setEmployee(UniversalUser employee) {
        this.employee = employee;
    }

    /**
     * Gets the options attribute. 
     * @return Returns the options.
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * @param options The options to set.
     */
    public void setOptions(Options options) {
        this.options = options;
    }

    /**
     * Gets the effortCertificationDetailLines attribute. 
     * @return Returns the effortCertificationDetailLines.
     */
    public List<EffortCertificationDetail> getEffortCertificationDetailLines() {
        return effortCertificationDetailLines;
    }

    /**
     * Sets the effortCertificationDetailLines attribute value.
     * @param effortCertificationDetailLines The effortCertificationDetailLines to set.
     */
    @Deprecated
    public void setEffortCertificationDetailLines(List<EffortCertificationDetail> effortCertificationDetailLines) {
        this.effortCertificationDetailLines = effortCertificationDetailLines;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
}
